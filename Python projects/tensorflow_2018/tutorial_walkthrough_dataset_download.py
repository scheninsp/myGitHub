# -*- coding: utf-8 -*-

import tensorflow as tf
import os

train_dataset_url = "http://download.tensorflow.org/data/iris_test.csv"

train_dataset_fp = tf.keras.utils.get_file(fname=os.path.basename(train_dataset_url),
                                           origin=train_dataset_url)

print("Local copy of the dataset file: {}".format(train_dataset_fp))

print(train_dataset_fp)
type(train_dataset_fp)
