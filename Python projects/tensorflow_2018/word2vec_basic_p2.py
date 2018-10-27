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
#reversed_dictionary - maps codes to words, reversed_dictionary[code] = word
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


# Step 4 : Build and train a skip-gram model
  
batch_size = 120
embedding_size = 128 #dim of embedding vector
skip_window = 1  #how many words are considered left and right
num_skips = 2  #how many times to reuse an input to generate a label
num_sampled = 64 #Number of negative examples to sample
  
#validation parameters only used to show results
#words with low numeric ID = most frequent words
valid_size = 16
valid_window = 100
valid_examples = np.random.choice(valid_window, valid_size, replace=False)
#uniformly choose 'valid_size' number of examples from 'np.aramge(valid_window)' without replacement
  
graph = tf.Graph()
  
with graph.as_default():
      #Input data
      with tf.name_scope('inputs'):
          train_inputs = tf.placeholder(tf.int32, shape=[batch_size])
          train_labels = tf.placeholder(tf.int32, shape=[batch_size, 1])
          valid_dataset = tf.constant(valid_examples, dtype=tf.int32)
          
      #Ops and variables pinned to the CPU because of missing GPU implementation
      with tf.device('/cpu:0'):
          #Look up embeddings for inputs
          with tf.name_scope('embeddings'):
              embeddings = tf.Variable(
                      tf.random_uniform([vocabulary_size, embedding_size],-1.0,1.0))
              embed = tf.nn.embedding_lookup(embeddings, train_inputs)
              
      with tf.name_scope('weights'):
          nce_weights = tf.Variable(tf.truncated_normal(
                  [vocabulary_size,embedding_size],
                  stddev=1.0 / math.sqrt(embedding_size)))
      
      with tf.name_scope('biases'):
          nce_biases = tf.Variable(tf.zeros([vocabulary_size]))
          
      #Compute the averge NCE loss for the batch
      #tf.nce_loss automatically draws a new sample of negative labels
      #each time we evaluate the loss
      #Explanation of the meaning of NCE loss:
      #http://mccormickml.com/2016/04/19/word2vec-tutorial-the-skip-gram-model/
      
      with tf.name_scope('loss'):
          loss = tf.reduce_mean(
                  tf.nn.nce_loss(
                          weights = nce_weights,
                          biases = nce_biases,
                          labels = train_labels,
                          inputs = embed,
                          num_sampled = num_sampled,
                          num_classes = vocabulary_size))
      tf.summary.scalar('loss',loss)
      
      #Construct SGD optimizer
      with tf.name_scope('optimizer'):
          optimizer = tf.train.GradientDescentOptimizer(1.0).minimize(loss)
          
      #Compute the cosine similarity between minibatch examples and all embeddings
      norm = tf.sqrt(tf.reduce_sum(tf.square(embeddings),1,keepdims=True))
      normalized_embeddings = embeddings/norm
      valid_embeddings = tf.nn.embedding_lookup(normalized_embeddings, 
                                                valid_dataset)
      
      similarity = tf.matmul(
              valid_embeddings, normalized_embeddings, transpose_b=True)
      #similarity : (nvalid*nEmbed) * (nvocab*nEmbed)T = nvalid * nvocabulary
      
      #Merge all summaries
      merged = tf.summary.merge_all()
      
      #Add variable initializer
      init = tf.global_variables_initializer()
      
      #Create a saver
      saver = tf.train.Saver()
      
#Step 5 : begin training
num_steps = 1000001

with tf.Session(graph = graph) as session:
    writer = tf.summary.FileWriter(FLAGS.log_dir, session.graph)
    
    #initialize all variables 
    init.run()
    print('Initialized')
    
    average_loss = 0
    for step in xrange(num_steps):
        batch_inputs, batch_labels = generate_batch(batch_size, num_skips,
                                                    skip_window)
        feed_dict = {train_inputs:batch_inputs, train_labels:batch_labels}
        
        #Define metadata variable
        run_metadata = tf.RunMetadata()
        
        #We perform one update step by evaluating the optimizer op
        #also, evaluate the merged op to get all summaries from the returned "summary" variable
        #Feed metadata variable to session for visualizing the graph in TensorBoard
        
        _,summary, loss_val = session.run(
                [optimizer, merged, loss],
                feed_dict = feed_dict,
                run_metadata = run_metadata)
        
        average_loss += loss_val
        
        # add returned summaries to writer 
        writer.add_summary(summary, step)
        
        if step == (num_steps - 1):
            writer.add_run_metadata(run_metadata, 'step%d' %step)
        
        if step % 2000 == 0:
            if step > 0:
                average_loss /= 2000
            print('Average loss at step ', step , ': ', average_loss)
            average_loss = 0
            
        #Note that this is expensive (~%20 slowdown if computed every 500 steps)    
        if step % 10000 == 0:
            sim = similarity.eval()
            for i in xrange(valid_size):
                valid_word = reverse_dictionary[valid_examples[i]]
                top_k = 8 #number of nearest neighbours
                nearest = (-sim[i,:]).argsort()[1:top_k + 1]
                log_str = 'Nearest to %s:' % valid_word
                
                for k in xrange(top_k):
                    close_word = reverse_dictionary[nearest[k]]
                    log_str = '%s %s,' % (log_str, close_word)
                
                print(log_str)
                
    final_embeddings = normalized_embeddings.eval()        
        
    #Write corresponding labels for embeddings
    with open(FLAGS.log_dir + '/metadaa.tsv', 'w') as f:
        for i in xrange(vocabulary_size):
            f.write(reverse_dictionary[i] + '\n')
            
    #Save the model for checkpoints
    saver.save(session, os.path.join(FLAGS.log_dir, 'model.ckpt'))
    
    #Create a configuration for visualizing embeddings with the labels in TensorBoard
    config = projector.ProjectorConfig()
    embedding_conf = config.embeddings.add()
    embedding_conf.tensor_name = embeddings.name
    embedding_conf.metadata_path = os.path.join(FLAGS.log_dir, 'metadata.tsv')
    projector.visualize_embeddings(writer, config)
    
writer.close()


# Step 6: Visualize the embeddings.

# pylint: disable=missing-docstring
# Function to draw visualization of distance between embeddings.
def plot_with_labels(low_dim_embs, labels, filename):
  assert low_dim_embs.shape[0] >= len(labels), 'More labels than embeddings'
  plt.figure(figsize=(18, 18))  # in inches
  for i, label in enumerate(labels):
    x, y = low_dim_embs[i, :]
    plt.scatter(x, y)
    plt.annotate(
        label,
        xy=(x, y),
        xytext=(5, 2),
        textcoords='offset points',
        ha='right',
        va='bottom')

  plt.savefig(filename)


try:
  # pylint: disable=g-import-not-at-top
  from sklearn.manifold import TSNE
  import matplotlib.pyplot as plt

  tsne = TSNE(
      perplexity=30, n_components=2, init='pca', n_iter=5000, method='exact')
  plot_only = 500
  low_dim_embs = tsne.fit_transform(final_embeddings[:plot_only, :])
  labels = [reverse_dictionary[i] for i in xrange(plot_only)]
  plot_with_labels(low_dim_embs, labels, os.path.join(gettempdir(), 'tsne.png'))

except ImportError as ex:
  print('Please install sklearn, matplotlib, and scipy to show embeddings.')
  print(ex)