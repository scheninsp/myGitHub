# -*- coding: utf-8 -*-
"""
Created on Tue Dec  5 14:16:23 2017

@author: Administrator
"""

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import sys
import os
import time
import argparse

from six.moves import xrange

import tensorflow as tf
from tensorflow.examples.tutorials.mnist import input_data
import Tutorial_Tensorflow_101_mnist as mnist

#Model parameters
FLAGS = None

def placeholder_inputs(batch_size):
    """generate placeholder for inputs
    """
    images_placeholder = tf.placeholder(tf.float32, shape=(batch_size,
                                                           mnist.IMAGE_PIXELS))
    labels_placeholder = tf.placeholder(tf.int32, shape=(batch_size))
    return images_placeholder, labels_placeholder

def fill_feed_dict(data_set, images_pl, labels_pl):
    """ Fills the feed_dict for training 
    """
    images_feed,labels_feed = data_set.next_batch(FLAGS.batch_size,FLAGS.fake_data)
    
    feed_dict = {
            images_pl : images_feed,
            labels_pl: labels_feed,
            }
    
    return feed_dict
    
def do_eval(sess,
            eval_correct,
            images_placeholder,
            labels_placeholder,
            data_set):
    """Run one evaluation against the full epoch of data.
    """
    true_count = 0
    steps_per_epoch = data_set.num_examples // FLAGS.batch_size
    num_examples = steps_per_epoch * FLAGS.batch_size
    for steps in xrange(steps_per_epoch):
        feed_dict = fill_feed_dict(data_set,
                                  images_placeholder,
                                  labels_placeholder)
        true_count += sess.run(eval_correct, feed_dict= feed_dict)
    
    precision = float(true_count) / num_examples
    print('Num examples: %d   Num correct: %d   Precision @ 1: %0.04f' %
          (num_examples, true_count, precision))
    
def run_training():
    """Train MNIST for a number of steps."""
    data_sets = input_data.read_data_sets(FLAGS.input_data_dir, FLAGS.fake_data)
    
    with tf.Graph().as_default():
        images_placeholder, labels_placeholder = placeholder_inputs(FLAGS.batch_size)
        
        #build a graph to compute inference model
        logits = mnist.inference(images_placeholder,
                                 FLAGS.hidden1,
                                 FLAGS.hidden2)
        
        #Add to Graph Ops for loss calculation
        loss = mnist.loss(logits, labels_placeholder)
        
        # Add to Graph Ops to calculate gradients
        train_op = mnist.training(loss, FLAGS.learning_rate)
        
        #Add the Ops to compare logits and labels
        eval_correct = mnist.evaluation(logits, labels_placeholder)
                                 
        #Build summary tensor
        summary = tf.summary.merge_all()
        
        # Add the variable initializer Op
        init =  tf.global_variables_initializer()
        
        # Save data
        saver = tf.train.Saver()
        
        #Create Session
        sess = tf.Session()
        summary_writer = tf.summary.FileWriter(FLAGS.log_dir, sess.graph)
        
        sess.run(init)
        
        for step in xrange(FLAGS.max_steps):
            start_time = time.time()
            
            feed_dict = fill_feed_dict(data_sets.train,
                                       images_placeholder,
                                       labels_placeholder)
            
            _, loss_value = sess.run([train_op, loss], feed_dict = feed_dict)
            
            duration = time.time() - start_time
            
            #Write summary
            if step%100 == 0:
                print('Step %d: loss = %.2f (%.3f sec)' %
                      (step, loss_value, duration))
                summary_str = sess.run(summary, feed_dict= feed_dict)
                summary_writer.add_summary(summary_str,step)
                summary_writer.flush()
                
            #Save a checkpoint and evaluate model
            if (step+1)%1000 == 0 or (step+1) == FLAGS.max_steps:
                checkpoint_file = os.path.join(FLAGS.log_dir,'model.ckpt')
                saver.save(sess, checkpoint_file, global_step=step)
                
                print('Training Data Eval:')
                do_eval(sess,
                        eval_correct,
                        images_placeholder,
                        labels_placeholder,
                        data_sets.train)
                print('Validation Data Eval:')
                do_eval(sess,
                        eval_correct,
                        images_placeholder,
                        labels_placeholder,
                        data_sets.validation)
                print('Test Data Eval:')
                do_eval(sess,
                        eval_correct,
                        images_placeholder,
                        labels_placeholder,
                        data_sets.test)            
            
    
def main(_):
    
    #this is not working on Windows System
    if tf.gfile.Exists(FLAGS.log_dir):
        tf.gfile.DeleteRecursively(FLAGS.log_dir)
    tf.gfile.MakeDirs(FLAGS.log_dir)
    
    #os.makedirs(FLAGS.log_dir)
    run_training()    
    

if __name__ == '__main__':
    
    #tmpstr = ''.join(['E:',os.path.sep,'tmp'])
    #input_data_route = ''.join(['tensorflow',os.path.sep,'mnist',os.path.sep,'input_Data'])
    #log_route = ''.join(['tensorflow',os.path.sep,'mnist',os.path.sep,
    #                    'logs',os.path.sep,'fully_connected_feed'])
    """
    tmpstr = '\\tmp'
    input_data_route = 'tensorflow\\mnist\\input_Data'
    log_route = 'tensorflow\\mnist\\logs\\fully_connected_feed'
    """
    
    #still not working when it needs deletion
    tmpstr = 'E:\\tmp'
    input_data_route = 'tensorflow\\mnist\\input_Data'
    log_route = 'tensorflow\\mnist\\logs\\fully_connected_feed'
    
    
    parser = argparse.ArgumentParser()
    parser.add_argument(
            '--learning_rate',
            type = float,
            default = 0.01,
            help = 'Initial learning rate')
    parser.add_argument(
            '--max_steps',
            type = int,
            default = 2000,
            help = 'Number of steps to run trainer')
    parser.add_argument(
            '--hidden1',
            type=int,
            default=128,
            help = 'Number of units in hidden layer 1')
    parser.add_argument(
            '--hidden2',
            type=int,
            default=32,
            help = 'Number of units in hidden layer 2')
    parser.add_argument(
            '--batch_size',
            type=int,
            default = 100,
            help = 'Batch size')
    parser.add_argument(
            '--input_data_dir',
            type=str,
            default= os.path.join(os.getenv('TEST_TMPDIR',tmpstr),
                                  input_data_route),
            help = 'Directory to the input data'        
            )
    parser.add_argument(
            '--log_dir',
            type = str,
            default = os.path.join(os.getenv('TEST_TMPDIR',tmpstr),
                                   log_route),
            help = 'Directory to the log data'
            )
    parser.add_argument(
            '--fake_data',
            default = False,
            help = 'If true, use fake data for unit testing',
            action = 'store_true'
            )
    
    FLAGS, unparsed = parser.parse_known_args()
    tf.app.run(main = main, argv=[sys.argv[0]] + unparsed)
    
    
    
    
        
    