# -*- coding: utf-8 -*-
import tensorflow as tf
import tensorflow.contrib.eager as tfe
import matplotlib.pyplot as plt

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
    #shuffle=False for checking dataset
    
#features,labels = next(iter(train_dataset))

#print(features)

def pack_features_vector(features,labels):
    features = tf.stack(list(features.values()),axis=1)
    return features,labels

train_dataset = train_dataset.map(pack_features_vector)

features,labels = next(iter(train_dataset))
#
print(features[:5])

#Def model
model = tf.keras.Sequential([
        tf.keras.layers.Dense(10, activation = tf.nn.relu, input_shape = (4,)),
        tf.keras.layers.Dense(10, activation = tf.nn.relu),
        tf.keras.layers.Dense(3)
        ])

#Predict without training
predictions = model(features)
tf.nn.softmax(predictions[:5])

print("Prediction:{}".format(tf.argmax(predictions,axis=1)))
print("Original Labels:{}".format(labels))

#Training
#def loss, grad and optimizer
def loss(model, x, y):
    y_ = model(x)
    return tf.losses.sparse_softmax_cross_entropy(labels=y, logits=y_)

def grad(model, inputs, targets):
    with tf.GradientTape() as tape:
        loss_value = loss(model, inputs, targets)
    return loss_value, tape.gradient(loss_value, model.trainable_variables)

optimizer = tf.train.GradientDescentOptimizer(learning_rate = 0.01)

global_step = tf.train.get_or_create_global_step()

#training loop
train_loss_results = []
train_accuracy_results = []

num_epochs = 201

for epoch in range(num_epochs):
    epoch_loss_avg = tfe.metrics.Mean()
    epoch_accuracy = tfe.metrics.Accuracy()
    
    for x,y in train_dataset:
        loss_value, grads = grad(model, x, y)
        optimizer.apply_gradients(zip(grads,model.variables), global_step)
        
        epoch_loss_avg(loss_value)
        epoch_accuracy(tf.argmax(model(x), axis=1, output_type = tf.int32), y)
        
    #end epoch
    train_loss_results.append(epoch_loss_avg.result())
    train_accuracy_results.append(epoch_accuracy.result())
    
    if epoch%50 == 0:
        print("Epoch {:03d}: Loss:{:.3f} Accuracy:{:.3%}".format(epoch,
              epoch_loss_avg.result(),
              epoch_accuracy.result()))

#visualize loss function
fig, axes = plt.subplots(2,sharex=True, figsize=(12,8))
fig.suptitle('Training Metrics')

axes[0].set_ylabel = ("Loss")
axes[0].plot(train_loss_results)

axes[1].set_ylabel = ("Accuracy")
axes[1].set_xlabel = ("Epoch")
axes[1].plot(train_accuracy_results)

#save trained model ?
#load trained model ?

#testing
data_fileroute = "C:/Users/infimi/.keras/datasets/iris_test.csv"

test_dataset_fp = data_fileroute
test_dataset = tf.contrib.data.make_csv_dataset(
    test_dataset_fp,
    batch_size,
    column_names = column_names,
    label_name = label_name,
    num_epochs = 1,
    shuffle=False)

test_dataset = test_dataset.map(pack_features_vector)

test_accuracy = tfe.metrics.Accuracy()

for x,y in test_dataset:
    logits = model(x)
    prediction = tf.argmax(logits, axis=1, output_type = tf.int32)
    test_accuracy(prediction, y)
    
print("Test accuracy: {:.3%}".format(test_accuracy.result()))