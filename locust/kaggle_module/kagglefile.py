import pandas as pd
import os
from sumy.parsers.plaintext import PlaintextParser
from sumy.nlp.tokenizers import Tokenizer
from sumy.summarizers.lex_rank import LexRankSummarizer

from sumy.nlp.stemmers import Stemmer
from sumy.utils import get_stop_words
import nltk

nltk.download('punkt')

classes = os.listdir('./kaggle_module/datasets/bbc news summary/BBC News Summary/News Articles')
art_dir = './kaggle_module/datasets/bbc news summary/BBC News Summary/News Articles/'
sum_dir = './kaggle_module/datasets/bbc news summary/BBC News Summary/Summaries/'

articles = []
summaries = []
file_arr = []
for cla in classes:
    files = os.listdir(art_dir + cla)
    for file in files:
        article_file_path = art_dir + cla + '/' + file
        summary_file_path = sum_dir + cla + '/' + file
        try:
            with open(article_file_path, 'r') as f:
                articles.append('.'.join([line.rstrip() for line in f.readlines()]))
            with open(summary_file_path, 'r') as f:
                summaries.append('.'.join([line.rstrip() for line in f.readlines()]))
            file_arr.append(cla + '/' + file)
        except:
            pass

df = pd.DataFrame({'File_path': file_arr, 'Articles': articles, 'Summaries': summaries})
df.head()


def summarize(text, sentences_count):
    language = 'english'

    parser = PlaintextParser.from_string(text, Tokenizer(language))
    stemmer = Stemmer(language)

    summarizer = LexRankSummarizer(stemmer)

    summarizer.stop_words = get_stop_words(language)

    summary = []
    for sentence in summarizer(parser.document, sentences_count):
        summary.append(str(sentence))

    return " ".join(summary)
