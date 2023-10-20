from transformers import pipeline

generation_model = pipeline('text-generation', model='EleutherAI/gpt-neo-125M')


def get_news_from_title(title):
    generated_result = generation_model(title, max_length=90, do_sample=True, temperature=0.9)
    return (generated_result[0])["generated_text"]
