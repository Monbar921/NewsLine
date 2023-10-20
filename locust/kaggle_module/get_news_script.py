# from datetime import datetime
# import random
# import string
import radar
# from kaggle_module.news_getter import NewsGetter
# from kaggle_module.kagglefile import summarize
# from kaggle_module.news_creator import get_news_from_title
# from kaggle_module.csv_reader import read_random_title
from kaggle_module.json_reader import JsonReader
# getter = NewsGetter("85f1966bd8254a2190edf3a1f36c31c4")
#

file_name = "./kaggle_module/datasets/arxiv-metadata-oai-snapshot.json"
json_reader = JsonReader(file_name)


def get_news():
    # news = getter.get_random_news()
    # desired_sentences = random.randint(8, 10)
    # new_content = summarize(news["text"], desired_sentences)
    # news.update({"text": new_content})

    # title = read_random_title()
    # text = get_news_from_title(title)
    # article_date = radar.random_datetime().date()
    # news = {"title": title, "date": str(article_date), "text": text}
    # return news
    return json_reader.read_random_news()


# def get_random_string(length):
#     # choose from all lowercase letter
#     letters = string.ascii_lowercase
#     return ''.join(random.choice(letters) for i in range(length))
#
#
# def get_random_string_from_array(length):
#     result_str = ""
#     array_length = len(strs_for_title)
#     for i in range(0, length):
#         random_str_index = random.randint(0, array_length - 1)
#         result_str = result_str + strs_for_title[random_str_index] + " "
#     return result_str
