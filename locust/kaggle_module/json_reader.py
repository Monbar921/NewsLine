import json
import random
import radar
file_name = "./kaggle_module/datasets/arxiv-metadata-oai-snapshot.json"


class JsonReader:
    def __init__(self, path):
        self._content_length = 0

        with open(path, 'r') as file:
            for line in file:
                self._content_length += 1

    def read_random_news(self):
        index = 0
        desired_index = random.randint(0, self._content_length - 1)
        with open(file_name, 'r') as file:
            for line in file:
                if index == desired_index:
                    data = json.loads(line)
                    title = data["title"]
                    text = data["abstract"]
                    article_date = radar.random_datetime().date()
                    return {"title": title, "date": str(article_date), "text": text}
                index += 1



