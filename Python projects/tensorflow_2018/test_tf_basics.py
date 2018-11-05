# -*- coding: utf-8 -*-
"""
Created on Fri Nov  2 23:40:52 2018

@author: infimi
"""

import tensorflow as tf
import numpy as np

import matplotlib.pyplot as plt
import json

from sklearn.utils import shuffle
from sklearn.model_selection import train_test_split

# ---------input a ndarray------------
#x = np.zeros((8,2))
#row, col = x.shape
#for i in range(row):
#    for j in range(col):
#        x[i][j] = i+j
#print(x)
#
#dataset = tf.data.Dataset.from_tensor_slices(x)
#iterator = dataset.make_one_shot_iterator()
#elem = iterator.get_next()
#
#with tf.Session() as sess:
#    for i in range(row):
#        print(sess.run(elem))

##-----------input with dict-------------
#x = {'a':[1.,2.,3.,4.,5.],'b':np.random.uniform(size=(5,2))}
#
#dataset = tf.data.Dataset.from_tensor_slices(x)
#iterator = dataset.make_one_shot_iterator()
#elem = iterator.get_next()
#
#with tf.Session() as sess:
#    try:
#        while True:
#            print(sess.run(elem))
#    except tf.errors.OutOfRangeError:
#        print('end')



annotation_file = "C:/Myworks/COCO-data/annotations_trainval2014/captions_train2014.json"
PATH = "C:/Myworks/COCO-data/train2014/"

with open(annotation_file,'r') as f:
    annotations = json.load(f)

captions_all = []
image_file_all = []

for annot in annotations["annotations"]:
    cap = "<start>" + annot["caption"] + "<end>"
    img_fileroute = PATH + 'COCO_train2014_' + '%012d' % annot['image_id'] + '.jpg'
    captions_all.append(cap)
    image_file_all.append(img_fileroute)
    
train_caps, train_img_file = shuffle(captions_all, image_file_all, random_state = 1)
    
def load_image(image_file_path):
    #read image according to given file
    img = tf.read_file(image_file_path)
    img = tf.image.decode_jpeg(img, channels=3)
    #reshape image for inception_v3
    img = tf.image.resize_images(img,(299,299))
    #limit image range to -1,1 for inception_v3
    img = tf.keras.applications.inception_v3.preprocess_input(img)
    return img, image_file_path

img,_= load_image(image_file_all[0])
with tf.Session() as sess:
    img_eval = img.eval(session = sess)
    plt.imshow(img_eval)
    print(img_eval.ndim)   #3
    print(img_eval.shape)   #(299,299,3)
    print(img_eval.itemsize)   #4 for float32


#Use the InceptionV3 to extract features from original images
image_model = tf.keras.applications.InceptionV3(include_top = False,
                                                weights = 'imagenet')
#include_top = false means without classifier

new_input = image_model.input
hidden_layer = image_model.layers[-1].output  
#8*8*2048, last convolutional layer in inceptionV3

print(new_input.shape)  #(?,?,?,3)
print(hidden_layer.shape)   #(?,?,?,2048)
#dynamic shape

with tf.Session() as sess:
#    print(sess.run(tf.shape(img)))
    print(tf.shape(img).eval(session=sess))  #equal to line above [299, 299, 3]

##    t = tf.placeholder(dtype=tf.float32,shape=(2,2,3))
##    print(sess.run(tf.shape(t)))  # [2, 2, 3] , can be seen only after running session
##
##    t1 = tf.constant(np.zeros((2,2,3)),dtype=tf.float32)
##    print(sess.run(tf.shape(t1)))
#    
#    print(tf.shape(new_input.eval(feed_dict = {img})))
#    print(sess.run(tf.shape(hidden_layer)))

image_features_extract_model = tf.keras.Model(new_input, hidden_layer) #inputs, outputs

with tf.Session() as sess:
    img_input = np.expand_dims(img.eval(session=sess), axis=0)   #img_input: ndarray
    img_input = tf.convert_to_tensor(img_input)    #convert img_input to tensor
    print(sess.run(tf.shape(img_input)))   #[1, 299, 299, 3]
    
    img_features = image_features_extract_model(img_input)
    print(sess.run(tf.shape(img_features)))   #[1, 8, 8, 2048]


num_examples = 3000
train_caps = train_caps[:num_examples]
train_img_file = train_img_file[:num_examples]

#
##Caching features extracted by InceptionV3
#encode_train = sorted(set(train_img_file))
##list, len 2962, set() keeps only unique elements, because 1 pic has multiple captions?
#
#image_dataset = tf.data.Dataset.from_tensor_slices(
#        encode_train).map(load_image).batch(3)
## nBatch =16 will trigger OOM on Mipro
#
#
##--------- Iterate the Dataset ----------------
#dataset_iter = image_dataset.make_initializable_iterator()
#
#with tf.Session() as sess:
#    sess.run(dataset_iter.initializer)
##    print(image_dataset)  #<BatchDataset shapes: ((?, 299, 299, 3), (?,)), types: (tf.float32, tf.string)>
#    
#    dataset_1st_image, dataset_1st_str = dataset_iter.get_next()   #dataset_1st_image:[3,299,299,3]  dataset_1st_str: 3*1 strings
#
#    img_toshow = (dataset_1st_image[0,]+1)/2   
#    str_toshow = dataset_1st_str
#
#    img_toshow, str_toshow = sess.run([img_toshow,str_toshow])   #simultaneously run iterator
#    plt.imshow(img_toshow)      
#    print(str_toshow)



#--------------------------Tokenize Captions -----------------------
# tokenize captions
def calc_max_length(tensor):
    return max(len(t) for t in tensor)

top_k = 5000   #size of vocabulary
tokenizer = tf.keras.preprocessing.text.Tokenizer(num_words = top_k,
                                                  oov_token = "<unk>",
                                                  filters = '!"#$%&()*+.,-/:;=?@[\]^_`{|}~ ')
tokenizer.fit_on_texts(train_caps)  #generate vocabulary
train_seqs = tokenizer.texts_to_sequences(train_caps)  #code 'train_captions', len=414113 list [3,28.3139,101,7,124,4,1656]
#this step generate the full vocabulary

tokenizer.word_index = {key:value for key,value in tokenizer.word_index.items() if value <= top_k}
tokenizer.word_index[tokenizer.oov_token] = top_k + 1  #oov_token was used for changing words outside vocabulary
tokenizer.word_index['<pad>'] = 0 

train_seqs = tokenizer.texts_to_sequences(train_caps)  #code 'train_captions' again ??? [3,352,617,1,290,4,1,92,382,2]

index_word = {value:key for key, value in tokenizer.word_index.items()}

cap_vector = tf.keras.preprocessing.sequence.pad_sequences(train_seqs, padding = 'post')

max_length = calc_max_length(train_seqs)

#--------------------------Split data into train and test -----------------------

img_name_train, img_name_val, cap_train, cap_val = train_test_split(
        train_img_file,
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
dataset = dataset.prefetch(1)   #prefetch buffer size
#
##debug: iterator of dataset
## create the iterator
#iter = dataset.make_one_shot_iterator()
#el = iter.get_next()  #el is (64,64,2048)
##because it's in eagerTensor so no need to use sess.run

#--------------Build model ----------