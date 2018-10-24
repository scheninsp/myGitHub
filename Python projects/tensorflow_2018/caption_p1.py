# -*- coding: utf-8 -*-

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

num_examples = 3000
train_captions = train_captions[:num_examples]
img_name_vector = img_name_vector[:num_examples]

def load_image(image_path):
    img = tf.read_file(image_path)
    img = tf.image.decode_jpeg(img, channels=3)
    #reshape image for inception_v3
    img = tf.image.resize_images(img,(299,299))
    #limit image range to -1,1 for inception_v3
    img = tf.keras.applications.inception_v3.preprocess_input(img)
    return img, image_path

##debug load_image
test_img, test_image_path = load_image(img_name_vector[-1])
test_img_show = (test_img+1)/2
print(np.min(test_img_show))
imgplot = plt.imshow(test_img_show)
print(img_name_vector[-1])
print(train_captions[-1])

#Use the InceptionV3 to extract features from original images
image_model = tf.keras.applications.InceptionV3(include_top = False,
                                                weights = 'imagenet')

#this file might only be weights so load_model won't work ?   
#image_model = load_model("C:/Myworks/downloaded_models/inception_v3_weights_tf_dim_ordering_tf_kernels_notop.h5")
#put this file into the keras downloaded path for models (on Windows,
# it's in /Users/YOUR_NAME/.keras/models)

new_input = image_model.input
hidden_layer = image_model.layers[-1].output  
#8*8*2048, last convolutional layer in inceptionV3

image_features_extract_model = tf.keras.Model(new_input, hidden_layer)

#Caching features extracted by InceptionV3
encode_train = sorted(set(img_name_vector))
#list, len 2962, set() keeps only unique elements, because 1 pic has multiple captions?

#Note
image_dataset = tf.data.Dataset.from_tensor_slices(
        encode_train).map(load_image).batch(3)
# nBatch =16 will trigger OOM on Mipro
#python.data.ops.dataset_ops.BatchDataset

#debug image_dataset
#testvar1_iter = image_dataset.make_one_shot_iterator()
#testvar1_im, testvar1_label = testvar1_iter.next()
#print(tf.shape(testvar1_im))

for img,path in image_dataset:
    batch_features = image_features_extract_model(img) 
    # img nBatch(3)*299*299*3
    # batch_features: nBatch(3)*8*8*2048
    
    batch_features = tf.reshape(batch_features,
                                (batch_features.shape[0], -1, batch_features.shape[3]))
    # batch_features: 1*64*2048, shape=(3,)
    
    for bf, p in zip(batch_features,path):
        #zip function returns a tuple combining each pair in given lists
        path_of_feature = p.numpy().decode("utf-8")
        #np.save(path_of_feature, bf.numpy())
        
#generated 2962 .npy files in /train2014