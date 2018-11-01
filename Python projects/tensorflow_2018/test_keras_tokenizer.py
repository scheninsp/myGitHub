# -*- coding: utf-8 -*-
"""
Created on Thu Nov  1 15:02:55 2018

@author: infimi
"""

import tensorflow as tf

train_captions = []
train_captions.append("A red fox jump over the fence.")
train_captions.append("A man sitting over the wall.")
train_captions.append("A plane is flying in the sky.")

top_k = 5  #size of vocabulary
tokenizer = tf.keras.preprocessing.text.Tokenizer(num_words = top_k,
                                                  oov_token = "<unk>",
                                                  filters = '!"#$%&()*+.,-/:;=?@[\]^_`{|}~ ')
tokenizer.fit_on_texts(train_captions)  #generate vocabulary
train_seqs = tokenizer.texts_to_sequences(train_captions)  #code 'train_captions', len=414113 list [3,28.3139,101,7,124,4,1656]

tokenizer.word_index = {key:value for key,value in tokenizer.word_index.items() if value <= top_k}
tokenizer.word_index[tokenizer.oov_token] = top_k + 1  #oov_token was used for changing words outside vocabulary
tokenizer.word_index['<pad>'] = 0 

train_seqs = tokenizer.texts_to_sequences(train_captions)  #code 'train_captions' again ??? [3,352,617,1,290,4,1,92,382,2]

for x in train_seqs:
    print(x)
    
for key,val in tokenizer.word_index.items():
    print("key: %s, val: %s" % (key,val))    
    

# word_index automaticaly sort words from most frequent one
# texts_to_sequences doesn't directly code each word in the sentence    