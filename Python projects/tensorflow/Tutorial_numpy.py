# -*- coding: utf-8 -*-
"""
Created on Fri Oct 27 14:31:53 2017

@author: Administrator
"""

import numpy as np

x = np.array([np.arange(1,10),np.arange(11,20),np.arange(21,30)])
y = x[:,0:10:2]
print(y)