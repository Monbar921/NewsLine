from locust import HttpUser, task, between
from kaggle_module.get_news_script import get_news
import json


class QuickstartUser(HttpUser):
    # wait_time = between(1, 5)
    token = None

    @task
    def get_token(self):
        if self.token is None:
            url = "http://localhost:9000/oauth2/token"
            payload = {
                "grant_type": "client_credentials",
                "client_id": "messaging-client",
                "client_secret": "secret",
                "scope": "write"
            }

            response = self.client.post(url, data=payload)
            content = response.content.decode().split(",")

            self.token = (content[0].split(":"))[1].strip("\"")

    # @task
    # def get_all_news(self):
    #     if self.token is not None:
    #         url = "http://localhost:8080/news/get-all-news"
    #         params = {
    #             "current-page": 0,
    #             "news-on-page": 100
    #         }
    #
    #         headers = {
    #             "Authorization": f"Bearer {self.token}"
    #         }
    #         response = self.client.get(url, params=params, headers=headers)
    #         print(response)
    #         self._get_new_token(response)
    #
    # @task
    # def get_all_comments(self):
    #     if self.token is not None:
    #         news_id = "a45b7d5a-800f-4d1e-8257-f50f80db0fcb"
    #         url = "http://localhost:8080/news/{news_id}/comments/get-comments".format(news_id=news_id)
    #
    #         headers = {
    #             "Authorization": f"Bearer {self.token}"
    #         }
    #         response = self.client.get(url, headers=headers)
    #         print(response)
    #         self._get_new_token(response)

    @task
    def add_news(self):
        if self.token is not None:
            url = "http://localhost:8080/news/save-news"

            headers = {
                "Authorization": f"Bearer {self.token}",
                'Content-Type': 'application/json'
            }

            data = get_news()
            # print(data)
            response = self.client.post(url, headers=headers, json=data)
            print(response)
            if self._get_new_token(response):
                self.client.post(url, headers=headers, json=data)

    def _get_new_token(self, response):
        is_expired = False
        if response.status_code == 401:
            # print("get new token")
            self.token = None
            self.get_token()
            response.status_code = 200
            is_expired = True
        return is_expired
