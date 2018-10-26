# -*- coding: utf-8 -*-
"""
Created on Fri Oct 26 10:55:07 2018

@author: imsheridan
https://github.com/tensorflow/tensorflow/blob/master/tensorflow/examples/tutorials/word2vec/word2vec_basic.py

p2 skip data download
"""

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import collections
import math
import os
import sys
import argparse
import random
from tempfile import gettempdir
import zipfile

import numpy as np
from six.moves import urllib
from six.moves import xrange  # pylint: disable=redefined-builtin
import tensorflow as tf

from tensorflow.contrib.tensorboard.plugins import projector
# Give a folder path as an argument with '--log_dir' to save
# TensorBoard summaries. Default is a log folder in current directory.
current_path = os.path.dirname(os.path.realpath(sys.argv[0]))

parser = argparse.ArgumentParser()
parser.add_argument(
    '--log_dir',
    type=str,
    default=os.path.join(current_path, 'log'),
    help='The log directory for TensorBoard summaries.')
FLAGS, unparsed = parser.parse_known_args()

# Create the directory for TensorBoard variables if there is not.
if not os.path.exists(FLAGS.log_dir):
  os.makedirs(FLAGS.log_dir)

## Step 1: Download the data.
#url = 'http://mattmahoney.net/dc/'
#
#
## pylint: disable=redefined-outer-name
#def maybe_download(filename, expected_bytes):
#  """Download a file if not present, and make sure it's the right size."""
#  local_filename = os.path.join(gettempdir(), filename)
#  if not os.path.exists(local_filename):
#    local_filename, _ = urllib.request.urlretrieve(url + filename,
#                                                   local_filename)
#  statinfo = os.stat(local_filename)
#  if statinfo.st_size == expected_bytes:
#    print('Found and verified', filename)
#  else:
#    print(statinfo.st_size)
#    raise Exception('Failed to verify ' + local_filename +
#                    '. Can you get to it with a browser?')
#  return local_filename
#
## used 4min to download a 29.8M zip file
## local_filename = C:\Users\infimi\AppData\Local\Temp
#filename = maybe_download('text8.zip', 31344016)
filename = "C:/Myworks/WordEmbed-data/text8.zip"

# Read the data into a list of strings.
def read_data(filename):
  """Extract the first file enclosed in a zip file as a list of words."""
  with zipfile.ZipFile(filename) as f:
    data = tf.compat.as_str(f.read(f.namelist()[0])).split()
  return data


vocabulary = read_data(filename)  # list:nVocab(17005207) * 1
print('Data size', len(vocabulary))

#Step 2 : Build the dictoinary and replace rare words with UNK token
vocabulary_size = 50000

def build_dataset(words, n_words):  
    #words : 
    #n_words : the length of dictionary
    count = [['UNK',-1]]
    count.extend(collections.Counter(words).most_common(n_words-1))
    #count has topN words in given 'words'
    dictionary = dict()
    for word, _ in count:
        dictionary[word] = len(dictionary)
    #build dictionary on topN words in given 'words'    
        
    data = list()
    unk_count = 0
    for word in words:
        index  = dictionary.get(word,0)
        if index == 0:
            unk_count += 1
        data.append(index)
    count[0][1] = unk_count
    
    #reverse_dictionary use values to search words
    reverse_dictionary = dict(zip(dictionary.values(), dictionary.keys()))
    return data, count, dictionary, reverse_dictionary

#Filling 4 global variables
#data - list f codes (integer from 0 to vocabulary_size-1 )
# this is the original text bug words were replaced by their codes
#count - map of words to count of occurrence , vocabulary_size * 2
#dictionary - map of words to their codes
#reversed_dictionary - maps codes to words
data, count, dictionary, reverse_dictionary = build_dataset(
        vocabulary, vocabulary_size)
del vocabulary  #save memory
print('Most common words (+UNK)', count[:5])
print('Sample data', data[:10],[reverse_dictionary[i] for i in data[:10]])

data_index = 0    

#Step3 : generate training batch for skip-gram model
def generate_batch(batch_size, num_skips, skip_window):
    global data_index
    assert batch_size % num_skips == 0
    assert num_skips <= 2* skip_window
    
    batch = np.ndarray(shape=(batch_size),dtype=np.int32)
    labels = np.ndarray(shape=(batch_size,1),dtype=np.int32)
    
    span = 2* skip_window + 1 # [skip_window target skip_window]
    buffer = collections.deque(maxlen=span)
    
    if data_index +span > len(data):
        data_index = 0
    
    buffer.extend(data[data_index : data_index+span])
    data_index += span
    
    for i in range(batch_size // num_skips):
        context_words = [w for w in range(span) if w != skip_window]
        # skip the target word
#        print (context_words)
        word_to_use = random.sample(context_words, num_skips)
        # context words has 2*skip_win but only use num_skips
        # in this example, len(context_words)=2, num_skips = 2, all sampled
        for j, context_word in enumerate(word_to_use):
            batch[i*num_skips + j] = buffer[skip_window]  #target 
            labels[i*num_skips + j, 0] = buffer[context_word]  #one random sampled context word
        
        if data_index == len(data):
            buffer.extend(data[0:span])
            data_index = span
        else:
            buffer.append(data[data_index])
            data_index += 1
    
    #Backtrack a little bit to avoid skipping words in the end of a batch        
    data_index = (data_index + len(data) - span) % len(data)
    return batch, labels

batch, labels = generate_batch(batch_size=8, num_skips=2, skip_window=1)
for i in range(8):
  print(batch[i], reverse_dictionary[batch[i]], '->', labels[i, 0],
        reverse_dictionary[labels[i, 0]])


