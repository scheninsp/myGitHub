# -*- coding: utf-8 -*-
"""
Created on Fri Nov  2 23:40:52 2018

@author: infimi
"""

import tensorflow as tf
import numpy as np

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

#-----------input with dict-------------
x = {'a':[1.,2.,3.,4.,5.],'b':np.random.uniform(size=(5,2))}

dataset = tf.data.Dataset.from_tensor_slices(x)
iterator = dataset.make_one_shot_iterator()
elem = iterator.get_next()

with tf.Session() as sess:
    try:
        while True:
            print(sess.run(elem))
    except tf.errors.OutOfRangeError:
        print('end')
        