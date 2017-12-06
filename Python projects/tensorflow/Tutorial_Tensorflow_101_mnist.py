# -*- coding: utf-8 -*-
"""
Created on Tue Dec  5 20:44:48 2017

@author: Administrator
"""

"""
implements inference(), loss(), training()

this file is used by the various "fully_connected_*.py" files and not meant
to be run.
"""
"""
from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
"""
import math

import tensorflow as tf

#MNIST dataset has 10 classes, representing digit 0 - 9
NUM_CLASSES = 10

#MNIST images are 28*28 pixels
IMAGE_SIZE = 28
IMAGE_PIXELS = IMAGE_SIZE * IMAGE_SIZE

def inference(images, hidden1_units, hidden2_units):
    """
    Args:
        images: Image placeholder
        hidden1_units : Size of the first hidden layer
        hidden2_units : Size of the second hidden layer
    
    Returns:
        softmax_linear: Output tensor with the computed logits
    """
    
    #Hidden 1
    with tf.name_scope('hidden1'):
        weights = tf.Variable(
                tf.truncated_normal([IMAGE_PIXELS, hidden1_units],
                                    stddev=1.0/math.sqrt(float(IMAGE_PIXELS))),
                                    name = 'weights')
        biases = tf.Variable(tf.zeros([hidden1_units]),
                             name = 'biases')
        hidden1 = tf.nn.relu(tf.matmul(images,weights) + biases)
        
    #Hidden 2
    with tf.name_scope('hidden2'):
        weights = tf.Variable(
                tf.truncated_normal([hidden1_units, hidden2_units],
                                    stddev=1.0/math.sqrt(float(hidden1_units))),
                                    name = 'weights')
        biases = tf.Variable(tf.zeros([hidden2_units]),
                             name = 'biases')
        hidden2 = tf.nn.relu(tf.matmul(hidden1,weights) + biases)
    
    #Linear
    with tf.name_scope('softmax_linear'):
        weights = tf.Variable(
                tf.truncated_normal([hidden2_units,NUM_CLASSES],
                                    stddev=1.0/ math.sqrt(float(hidden2_units))),
                                    name = 'weights')
        
        biases = tf.Variable(tf.zeros([NUM_CLASSES]),
                            name = 'biases')
        logits = tf.matmul(hidden2,weights) + biases
        
        return logits
    
def loss(logits, labels):
    """
    Args:
        logits: Logits tensor, float - [batch_size, NUM_CLASSES]
        labels: labels tensor, int32 - [batch_size]

    Returns:
        loss: Loss tensor of type float
    """

    labels = tf.to_int64(labels)        
    cross_entropy = tf.nn.sparse_softmax_cross_entropy_with_logits(
            labels=labels, logits=logits, name='xentropy')
    return tf.reduce_mean(cross_entropy, name='xentropy_mean')

 
def training(loss, learning_rate):
     """
     Args:
         loss: loss tensor, from loss()
         learning_rate
        
     Returns:
         train_op: the Op for training.
     """
     
     tf.summary.scalar('loss',loss)
     
     optimizer = tf.train.GradientDescentOptimizer(learning_rate)
     
     #Create a varible to track the global step
     global_step = tf.Variable(0,name='global_step', trainable=False)
     
     train_op = optimizer.minimize(loss, global_step = global_step)
     return train_op
     
def evaluation(logits, labels):
     """
     Args:
         logits: logits tensor, float - [batch_size, NUM_CLASSES].
         labels: Labels tensor, int32 - [batch_size], with values in the range
         [0, NUM_CLASSES]
         
     Returns:
         A scalar int32 tensor with numbers of examples predicted correctly
     """
     
     correct = tf.nn.in_top_k(logits,labels,1)
     return tf.reduce_sum(tf.cast(correct, tf.int32))