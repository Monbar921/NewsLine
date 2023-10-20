import csv
import random

file_name = "./kaggle_module/datasets/uci-news-aggregator.csv"

with open(file_name, 'r') as file:
    csvreader = csv.reader(file)
    content = list(tuple(line) for line in csvreader)

content_length = len(content)


def _get_random_record():
    index = random.randint(1, content_length - 1)
    return content[index]


def read_random_title():
    return _get_random_record()[1]

