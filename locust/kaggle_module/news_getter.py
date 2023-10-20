import random
import radar
from newsapi import NewsApiClient
from datetime import date, timedelta, datetime
from newspaper import Article


class NewsGetter:
    def __init__(self, api_key):
        self._today = date.today()
        self._DAYS_BEFORE_TODAY = 31
        self._date_from = self._calculate_date_from()
        self._newsapi = NewsApiClient(api_key=api_key)
        self._all_articles = self._get_all_articles()
        
    def _calculate_date_from(self):
        return self._today - timedelta(days=self._DAYS_BEFORE_TODAY)

    def _get_all_articles(self):
        return self._newsapi.get_everything(
            sources='bbc-news,the-verge',
            domains='bbc.co.uk,techcrunch.com,engadget.com',
            from_param=str(self._date_from),
            language='en',
            sort_by='relevancy')

    def get_random_news(self):
        self._update_articles_list()
        while True:
            article = random.choice(list(self._all_articles["articles"]))
            if article["content"] != "The latest five minute news bulletin from BBC World Service.":
                news = Article(article["url"])

                news.download()
                news.parse()
                # news_date = datetime.strptime(article["publishedAt"], "%Y-%m-%dT%H:%M:%SZ").date()
                # print(str(news_date))
                news_date = radar.random_datetime().date()
                return {"title": article["title"], "date": str(news_date), "text": news.text}

    def _update_articles_list(self):
        today = date.today()
        if today != self._today:
            self._today = today
            self._date_from = self._calculate_date_from()
            self._get_all_articles()
