# -*- coding: utf-8 -*-
"""
Created on Fri Oct 19 23:02:25 2018

@author: infimi
"""

import numpy as np

nr=2
nc=3
X=np.random.rand(nr,nc)
for i in range(nr):
    for j in range(nc):
        print("{:.2f}".format(X[i][j]),end=' ')
    print("\n",end='')

    