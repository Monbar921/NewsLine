import torch
import torch.nn as nn
import torch.nn.functional as F
import re
import pandas as pd
import numpy as np
import seaborn as sns

if torch.cuda.is_available():
    device = torch.device("cuda")
else:
    device = torch.device("cpu")

vocab_obj=sports_vocab_obj.token2index #vocabulary
loss_function=nn.CrossEntropyLoss() #Loss Function
model=init_model(vocab_obj,device,layers=1,init_func="xavier").to(device) #Model
optimizer = torch.optim.AdamW(model.parameters(),lr=0.001)  #optimiser
epochs=40

def convert_token_to_text(result, reverse_dict):
    text_list = None
    for sent in results[:, 1:].tolist():
        text_list = []
        for token in sent:
            text_list.append(reverse_dict[token])
    return " ".join(text_list)


def greedy_inference_noPrompt(model, vocab_dict, batch_size=5, sent_max_len=8, inner_device=device):
    start_batch = torch.full((batch_size, 1), vocab_dict['<START>']).to(inner_device)
    current_tokens = start_batch
    req_len = 1
    while (req_len <= sent_max_len):
        output = model(current_tokens)
        predict_tokens = torch.argmax(output[:, -1, :], dim=-1).reshape(batch_size, 1)
        current_tokens = torch.cat((current_tokens, predict_tokens), 1)
        req_len = req_len + 1
    return current_tokens


def greedy_inference_withPrompt(model, prompt, vocab_dict, sent_max_len=8, inner_device=device):
    prompt = convert_token_2_ints(prompt, vocab_dict)
    prompt.insert(0, vocab_dict['<START>'])
    current_tokens = torch.LongTensor(prompt).reshape(1, -1).to(inner_device)
    req_len = 1
    while req_len <= sent_max_len:
        output = model(current_tokens)
        predict_tokens = torch.argmax(output[:, -1, :], dim=-1).reshape(1, 1)
        current_tokens = torch.cat((current_tokens, predict_tokens), 1)
        req_len = req_len + 1
    return current_tokens


def nucleus_sampling(vocab_distrib, p=1.0, T=1):
    neg_inf = -float('inf')
    vocab_distrib_temp = F.softmax(vocab_distrib / T, dim=-1)
    sorted_vals, indices = torch.sort(vocab_distrib_temp, descending=True)
    temp, total = 0, p
    main_indices = []
    for val, index in zip(sorted_vals, indices):
        temp = temp + val
        if temp > total:
            break
        main_indices.append(index)

    inf_list = [i.item() for i in indices[len(main_indices):]]
    for ind in inf_list:
        vocab_distrib[ind] = neg_inf

    return F.softmax(vocab_distrib / T, dim=-1)


def nucleus_sampling_noPrompt(model, vocab_dict, p=1.0, T=1, sent_max_len=10, device=device):
    start_batch = torch.full((1, 1), vocab_dict['<START>']).to(device)
    vocab_size = len(vocab_dict)
    current_tokens = start_batch
    req_len = 1
    while (req_len <= sent_max_len):
        output = model(current_tokens)
        vocab_distrib = output[0, -1, :].reshape(-1)
        sampled_vocab = nucleus_sampling(vocab_distrib, p, T)
        sampled_vocab = sampled_vocab.reshape(1, vocab_size)

        predict_tokens = torch.multinomial(sampled_vocab, 1)
        current_tokens = torch.cat((current_tokens, predict_tokens), 1)
        req_len = req_len + 1

    return current_tokens


def convert_token_2_ints(text, token_dict):
    if (text.strip()):
        text = split_func(text)
        text_int = [token_dict[token] if token in token_dict else token_dict['<UNK>'] for token in text.split()]
        return text_int
    return []


def split_func(text):
    text=text.lower()
    if ":" in text:
        text=text.split(':')[1]
    text=re.sub(r'[^a-zA-Z0-9\']', ' ', text)
    return text

prompt = "indian cricket team"
results = greedy_inference_withPrompt(model, prompt, sports_vocab_obj.token2index)
convert_token_to_text(results, sports_vocab_obj.index2token)
