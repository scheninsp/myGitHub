# -*- coding: utf-8 -*-
"""
Created on Mon Sep 25 23:11:09 2017

@author: Administrator
"""
import numpy as np
x = np.zeros( (4,3,3) )
x[:,:,0] = np.array([[ 0,  1,  2],
            [ 3,  4,  5],
            [ 6,  7,  8],
            [ 9, 10, 11]],dtype=np.int16)

x[:,:,1] = np.array([[ 12,  13,  14],
            [ 15,  16,  17],
            [ 18,  19,  20],
            [ 21, 22, 23]],dtype=np.int16)

x[:,:,2] = np.array([[ 24,  25,  26],
            [ 27,  28,  29],
            [ 30,  31,  32],
            [ 33, 34, 35]],dtype=np.int16)
print(x)

indx = np.array([0.,2.],dtype=np.int16)
indx2 = indx[:,np.newaxis,np.newaxis]
print("----------------------")
print(indx2)
 
'''
print(x[:,:,indx])



x = np.array([[ 0,  1,  2],
            [ 3,  4,  5],
            [ 6,  7,  8],
            [ 9, 10, 11]])

print(  x[  [[0],[3]],  [0,2] ]  )
'''