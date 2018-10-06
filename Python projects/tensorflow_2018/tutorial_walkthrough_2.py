# -*- coding: utf-8 -*-
import tensorflow as tf
import tensorflow.contrib.eager as tfe
tf.enable_eager_execution()

data_fileroute = "C:/Users/infimi/.keras/datasets/iris_training.csv"
#data_fileroute = "C:/Users/iris_training.csv"
batch_size = 32

column_names = ['sepal_length','sepal_width','petal_length','petal_width','species']
label_name = column_names[-1]

train_dataset_fp = data_fileroute
train_dataset = tf.contrib.data.make_csv_dataset(
    train_dataset_fp,
    batch_size,
    column_names = column_names,
    label_name = label_name,
    num_epochs = 1)
    
features,labels = next(iter(train_dataset))

print(features)
