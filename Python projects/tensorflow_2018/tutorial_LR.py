
import tensorflow as tf
import numpy as np

tfe = tf.contrib.eager
tf.enable_eager_execution()

class Model(object):
    def __init__(self):
        self.W = tfe.Variable(5.0)
        self.b = tfe.Variable(0.0)
    
    def __call__(self,x):
        return self.W * x + self.b


#print(m(1).numpy())

def loss(predicted_y, desired_y):
    return tf.reduce_mean(tf.square(predicted_y - desired_y))

#Samples generate
TRUE_W = 3.0
TRUE_b = 2.0
NUM_EXAMPLES = 1000
inputs = tf.random_normal(shape=[NUM_EXAMPLES])
noise = tf.random_normal(shape=[NUM_EXAMPLES])
outputs = TRUE_W * inputs + TRUE_b

def train(m,x,y,lr):
    with tf.GradientTape() as t:
        current_loss = loss(model(x),y)
    dW,db = t.gradient(current_loss, [m.W, m.b])
    model.W.assign_sub(lr * dW)
    model.b.assign_sub(lr * db)


model = Model()
Ws,bs = [], []
epochs = range(10)
for epoch in epochs:
    Ws.append(model.W.numpy())
    bs.append(model.b.numpy())
    current_loss = loss(model(inputs),outputs)
    
    train(model,inputs,outputs,lr=0.1)
    print('Epoch %2d:W=%1.2f b=%1.2f, loss=%2.5f' %
          (epoch,Ws[-1],bs[-1],current_loss))
