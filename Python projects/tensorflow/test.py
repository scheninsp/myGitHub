# -*- coding: utf-8 -*-
"""
Created on Tue Dec  5 21:11:08 2017

@author: Administrator
"""
import os

import tensorflow as tf

FLAGS = None

def main(_):
    """
    if tf.gfile.Exists(FLAGS.log_dir):
        tf.gfile.DeleteRecursively(FLAGS.log_dir)
    tf.gfile.MakeDirs(FLAGS.log_dir)
    """
    
if __name__ == '__main__':
    
    tmpstr = '\\tmp'
    os.makedirs(tmpstr)
    
    """
    tmpstr = ''.join([os.path.sep,'tmp'])
    FLAGS.log_dir = os.path.join(os.getenv('TEST_TMPDIR',tmpstr),
                                  'tensorflow/mnist/input_Data')
    print(FLAGS.log_dir)
    """