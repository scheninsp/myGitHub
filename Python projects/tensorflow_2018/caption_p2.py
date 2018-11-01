# -*- coding: utf-8 -*-
"""
Created on Thu Nov  1 14:04:07 2018

@author: infimi
"""

import tensorflow as tf
import numpy as np
#from tensorflow.keras.models import load_model

tf.enable_eager_execution()

import matplotlib.pyplot as plt

from sklearn.model_selection import train_test_split
from sklearn.utils import shuffle

import re  #match string pattern
import os
import time
import json
from glob import glob #file search
from PIL import Image
import pickle #serialize object into file for saving on disk

annotation_file = "C:/Myworks/COCO-data/annotations_trainval2014/captions_train2014.json"
PATH = "C:/Myworks/COCO-data/train2014/"

with open(annotation_file,'r') as f:
    annotations = json.load(f)
    
all_captions = []
all_img_name_vector = []

for annot in annotations['annotations']:
    caption = '<start>' + annot['caption'] + '<end>'
    image_id = annot['image_id']
    full_coco_image_path = PATH + 'COCO_train2014_' + '%012d.jpg' % (image_id)
    
    all_img_name_vector.append(full_coco_image_path)
    all_captions.append(caption)
    
train_captions, img_name_vector = shuffle(all_captions,
                                          all_img_name_vector,
                                          random_state = 1)
#random_state is a random seed.

#------------Load image and preprocess with InceptionV3 ----------------
num_examples = 3000
train_captions = train_captions[:num_examples]
img_name_vector = img_name_vector[:num_examples]
#
#def load_image(image_path):
#    img = tf.read_file(image_path)
#    img = tf.image.decode_jpeg(img, channels=3)
#    #reshape image for inception_v3
#    img = tf.image.resize_images(img,(299,299))
#    #limit image range to -1,1 for inception_v3
#    img = tf.keras.applications.inception_v3.preprocess_input(img)
#    return img, image_path
#
###debug load_image
#test_img, test_image_path = load_image(img_name_vector[-1])
#test_img_show = (test_img+1)/2
#print(np.min(test_img_show))
#imgplot = plt.imshow(test_img_show)
#print(img_name_vector[-1])
#print(train_captions[-1])
#
##Use the InceptionV3 to extract features from original images
#image_model = tf.keras.applications.InceptionV3(include_top = False,
#                                                weights = 'imagenet')
#
##this file might only be weights so load_model won't work ?   
##image_model = load_model("C:/Myworks/downloaded_models/inception_v3_weights_tf_dim_ordering_tf_kernels_notop.h5")
##put this file into the keras downloaded path for models (on Windows,
## it's in /Users/YOUR_NAME/.keras/models)
#
#new_input = image_model.input
#hidden_layer = image_model.layers[-1].output  
##8*8*2048, last convolutional layer in inceptionV3
#
#image_features_extract_model = tf.keras.Model(new_input, hidden_layer)
#
##Caching features extracted by InceptionV3
#encode_train = sorted(set(img_name_vector))
##list, len 2962, set() keeps only unique elements, because 1 pic has multiple captions?
#
##Note
#image_dataset = tf.data.Dataset.from_tensor_slices(
#        encode_train).map(load_image).batch(3)
## nBatch =16 will trigger OOM on Mipro
##python.data.ops.dataset_ops.BatchDataset
#
##debug image_dataset
##testvar1_iter = image_dataset.make_one_shot_iterator()
##testvar1_im, testvar1_label = testvar1_iter.next()
##print(tf.shape(testvar1_im))
#
#for img,path in image_dataset:
#    batch_features = image_features_extract_model(img) 
#    # img nBatch(3)*299*299*3
#    # batch_features: nBatch(3)*8*8*2048
#    
#    batch_features = tf.reshape(batch_features,
#                                (batch_features.shape[0], -1, batch_features.shape[3]))
#    # batch_features: 1*64*2048, shape=(3,)
#    
#    for bf, p in zip(batch_features,path):
#        #zip function returns a tuple combining each pair in given lists
#        path_of_feature = p.numpy().decode("utf-8")
#        #np.save(path_of_feature, bf.numpy())
#        
##generated 2962 .npy files in /train2014

#--------------------------Tokenize Captions -----------------------
# tokenize captions
def calc_max_length(tensor):
    return max(len(t) for t in tensor)

top_k = 5000   #size of vocabulary
tokenizer = tf.keras.preprocessing.text.Tokenizer(num_words = top_k,
                                                  oov_token = "<unk>",
                                                  filters = '!"#$%&()*+.,-/:;=?@[\]^_`{|}~ ')
tokenizer.fit_on_texts(train_captions)  #generate vocabulary
train_seqs = tokenizer.texts_to_sequences(train_captions)  #code 'train_captions', len=414113 list [3,28.3139,101,7,124,4,1656]
#this step generate the full vocabulary

tokenizer.word_index = {key:value for key,value in tokenizer.word_index.items() if value <= top_k}
tokenizer.word_index[tokenizer.oov_token] = top_k + 1  #oov_token was used for changing words outside vocabulary
tokenizer.word_index['<pad>'] = 0 

train_seqs = tokenizer.texts_to_sequences(train_captions)  #code 'train_captions' again ??? [3,352,617,1,290,4,1,92,382,2]

index_word = {value:key for key, value in tokenizer.word_index.items()}

cap_vector = tf.keras.preprocessing.sequence.pad_sequences(train_seqs, padding = 'post')

max_length = calc_max_length(train_seqs)

#--------------------------Split data into train and test -----------------------

img_name_train, img_name_val, cap_train, cap_val = train_test_split(
        img_name_vector,
        cap_vector,
        test_size = 0.2,
        random_state = 0)

print(len(img_name_train), len(cap_train), len(img_name_val), len(cap_val))

#--------------------------Create tf.data.Dataset  -----------------------
BATCH_SIZE = 8
BUFFER_SIZE = 125  
embedding_dim = 256
units = 512
vocab_size = len(tokenizer.word_index)
#shape of vector extracted by InceptionV3 is (64,2048)
features_shape = 2048
attention_features_shape = 64

#loading the numpy(.np) files saved before
def map_func(img_name, cap):
    img_tensor = np.load(img_name.decode('utf-8')+'.npy')
    return img_tensor, cap

dataset = tf.data.Dataset.from_tensor_slices((img_name_train, cap_train))

#use map to load images in parallel
#py_func
#Wraps a python function and uses it as a TensorFlow op.
dataset = dataset.map(lambda item1, item2: tf.py_func(
        map_func, [item1, item2], [tf.float32,tf.int32]), num_parallel_calls=4)

#shuffle and batch
dataset = dataset.shuffle(BUFFER_SIZE)
dataset = dataset.batch(BATCH_SIZE)
# https://www.tensorflow.org/api_docs/python/tf/contrib/data/batch_and_drop_remainder
dataset = dataset.prefetch(1)
#
##debug: iterator of dataset
## create the iterator
#iter = dataset.make_one_shot_iterator()
#el = iter.get_next()  #el is (64,64,2048)
##because it's in eagerTensor so no need to use sess.run

    

#----------------Build Model --------------------                                  
def gru(units):
  # If you have a GPU, we recommend using the CuDNNGRU layer (it provides a 
  # significant speedup).
#  if tf.test.is_gpu_available():
    return tf.keras.layers.CuDNNGRU(units, 
                                    return_sequences=True, 
                                    return_state=True, 
                                    recurrent_initializer='glorot_uniform')
#  else:
#    return tf.keras.layers.GRU(units, 
#                               return_sequences=True, 
#                               return_state=True, 
#                               recurrent_activation='sigmoid', 
#                               recurrent_initializer='glorot_uniform')
    

class BahdanauAttention(tf.keras.Model):
  def __init__(self, units):
    super(BahdanauAttention, self).__init__()
    self.W1 = tf.keras.layers.Dense(units)
    self.W2 = tf.keras.layers.Dense(units)
    self.V = tf.keras.layers.Dense(1)
  
  def call(self, features, hidden):
    # features(CNN_encoder output) shape == (batch_size, 64, embedding_dim)
    
    # hidden shape == (batch_size, hidden_size)
    # hidden_with_time_axis shape == (batch_size, 1, hidden_size)
    hidden_with_time_axis = tf.expand_dims(hidden, 1)  
    #tf.expand_dims(x,1) input shape: 1, output shape: [2,1]

    # score shape == (batch_size, 64, hidden_size)
    score = tf.nn.tanh(self.W1(features) + self.W2(hidden_with_time_axis))
    
    # attention_weights shape == (batch_size, 64, 1)
    # we get 1 at the last axis because we are applying score to self.V
    attention_weights = tf.nn.softmax(self.V(score), axis=1)
    
    # context_vector shape after sum == (batch_size, hidden_size)
    context_vector = attention_weights * features
    context_vector = tf.reduce_sum(context_vector, axis=1)
    
    return context_vector, attention_weights


class CNN_Encoder(tf.keras.Model):
    # Since we have already extracted the features and dumped it using pickle
    # This encoder passes those features through a Fully connected layer
    def __init__(self, embedding_dim):
        super(CNN_Encoder, self).__init__()
        # shape after fc == (batch_size, 64, embedding_dim)
        self.fc = tf.keras.layers.Dense(embedding_dim)
        
    def call(self, x):
        x = self.fc(x)
        x = tf.nn.relu(x)
        return x
    
class RNN_Decoder(tf.keras.Model):
  def __init__(self, embedding_dim, units, vocab_size):
    super(RNN_Decoder, self).__init__()
    self.units = units

    self.embedding = tf.keras.layers.Embedding(vocab_size, embedding_dim)
    self.gru = gru(self.units)
    self.fc1 = tf.keras.layers.Dense(self.units)
    self.fc2 = tf.keras.layers.Dense(vocab_size)
    
    self.attention = BahdanauAttention(self.units)
        
  def call(self, x, features, hidden):
    # defining attention as a separate model
    context_vector, attention_weights = self.attention(features, hidden)
    
    # x shape after passing through embedding == (batch_size, 1, embedding_dim)
    x = self.embedding(x)
    
    # x shape after concatenation == (batch_size, 1, embedding_dim + hidden_size)
    x = tf.concat([tf.expand_dims(context_vector, 1), x], axis=-1)
    
    # passing the concatenated vector to the GRU
    output, state = self.gru(x)
    
    # shape == (batch_size, max_length, hidden_size)
    x = self.fc1(output)
    
    # x shape == (batch_size * max_length, hidden_size)
    x = tf.reshape(x, (-1, x.shape[2]))
    
    # output shape == (batch_size * max_length, vocab)
    x = self.fc2(x)

    return x, state, attention_weights

  def reset_state(self, batch_size):
    return tf.zeros((batch_size, self.units))


#build full model
encoder = CNN_Encoder(embedding_dim)
decoder = RNN_Decoder(embedding_dim, units, vocab_size)

optimizer = tf.train.AdamOptimizer()

# We are masking the loss calculated for padding
def loss_function(real, pred):
    mask = 1 - np.equal(real, 0)
    loss_ = tf.nn.sparse_softmax_cross_entropy_with_logits(labels=real, logits=pred) * mask
    return tf.reduce_mean(loss_)


#------------Training -----------------------------------
# adding this in a separate cell because if you run the training cell 
# many times, the loss_plot array will be reset
loss_plot = []

EPOCHS = 20

for epoch in range(EPOCHS):
    start = time.time()
    total_loss = 0
    
    for (batch, (img_tensor, target)) in enumerate(dataset):
        loss = 0
        
        # initializing the hidden state for each batch
        # because the captions are not related from image to image
        hidden = decoder.reset_state(batch_size=target.shape[0])

        dec_input = tf.expand_dims([tokenizer.word_index['<start>']] * BATCH_SIZE, 1)
        
        with tf.GradientTape() as tape:
            features = encoder(img_tensor)
            
            for i in range(1, target.shape[1]):
                # passing the features through the decoder
                predictions, hidden, _ = decoder(dec_input, features, hidden)

                loss += loss_function(target[:, i], predictions)
                
                # using teacher forcing
                dec_input = tf.expand_dims(target[:, i], 1)
        
        total_loss += (loss / int(target.shape[1]))
        
        variables = encoder.variables + decoder.variables
        
        gradients = tape.gradient(loss, variables) 
        
        optimizer.apply_gradients(zip(gradients, variables), tf.train.get_or_create_global_step())
        
        if batch % 100 == 0:
            print ('Epoch {} Batch {} Loss {:.4f}'.format(epoch + 1, 
                                                          batch, 
                                                          loss.numpy() / int(target.shape[1])))
    # storing the epoch end loss value to plot later
    loss_plot.append(total_loss / len(cap_vector))
    
    print ('Epoch {} Loss {:.6f}'.format(epoch + 1, 
                                         total_loss/len(cap_vector)))
    print ('Time taken for 1 epoch {} sec\n'.format(time.time() - start))
    
    # Model cannot be saved for input_shape undefined ???
#    #save model at each epoch
#    encoder.save('./caption_model/caption_encoder.h5')
#    decoder.save('./caption_model/caption_decoder.h5')


#plt whole training
plt.plot(loss_plot)
plt.xlabel('Epochs')
plt.ylabel('Loss')
plt.title('Loss Plot')
plt.show()