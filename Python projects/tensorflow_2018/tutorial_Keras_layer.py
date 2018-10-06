# -*- coding: utf-8 -*-

#This code is from Tensorflow tutorial "Custom Layers"
import tensorflow as tf

class MyDenseLayer(tf.keras.layers.Layer):
    def __init__(self,num_outputs):
        super(MyDenseLayer,self).__init__()
        self.num_outputs = num_outputs
        
    def build(self, input_shape):
        self.kernel = self.add_variable("kernel",shape = [input_shape[-1].value,
                                                          self.num_outputs])
    
    def call(self, input):
        return tf.matmul(input, self.kernel)
    

layer = MyDenseLayer(10)
print(layer(tf.zeros([10,5])))
print(layer.variables)
