# -*- coding: utf-8 -*-
"""
Created on Fri Oct 19 23:02:25 2018

@author: infimi
"""

#import numpy as np

#nr=2
#nc=3
#X=np.random.rand(nr,nc)
#for i in range(nr):
#    for j in range(nc):
#        print("{:.2f}".format(X[i][j]),end=' ')
#    print("\n",end='')

#--------------------------------------
#def print_all(name,age,**kw):
#    print('name:',name,' age:',age,' others:', kw)
#    
#name1 = 'Wang'
#age1 = 24
#dict1 = {'Location':'China','Job':'Engineer'}
#
#print_all(name1,age1,**dict1)

#-----------------------------------------
import numpy as np
import tensorflow as tf

import tensorflow.contrib.eager as tfe
tf.enable_eager_execution()

Ns = 1000
l1 = np.arange(Ns)
l2 = np.random.rand(Ns)
feats={'x':l1,'y':l2}
print(feats["x"][0:10])
input_layer = tf.reshape(feats["x"],[-1,2,2,1])
print(input_layer[1,:,:,:])
