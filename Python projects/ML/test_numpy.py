# *- coding:utf-8 -*-

print("Hello World")

from numpy import *
import copy  #for deepcopy
import operator

# array1 = array1+ random.rand(4,4)

array1 = array([[1,0,0,0],[0,2,0,0],[0,0,3,0],[0,0,0,4]])  #the numpy object 'ndarray'
mat1 = mat(array1)  #the numpy object 'numpy.matrixlib.defmatrix.matrix'
mat1 = mat1.I    #inverse of matrix

indx1 = arange(1,10,2)   #the numpy object 'ndarray'

#usage of tile:
indx2 = indx1[:,newaxis]  #change to column vector
indx2_tile = tile(indx2,2) #repmat(indx2,1,2)
indx1_tile = tile(indx1,2) #repmat(indx1,1,2)
#print(indx1_tile)
#print(indx2_tile)

#test list
indx3 = [1,2,1,3,1,4]
indx3.remove(3)
#print(indx3)

#test dict
dict1 = {'Tom':1080,'Mary':1090,'Bob':1030}
dict2 = {(1,2,1):10201,(1,3,2):10302}
#dict3 = {(1,2,[1,2]):10201020, (1,3,[1,3]):10301030}  #error:unhashable type: 'list'
dict3 = {(1,2,1):10301,(1,4,2):10402}
#dict4 = dict2   #shallow copy
dict4 = copy.deepcopy(dict2)
dict4.update(dict3)
#print(dict4)
# for k,v in dict4.items():
#     print(k,v)
#print(len(dict4))
# print(['dict2',dict2])
# print(['dict4',dict4])
dict1_sortedlist = sorted(dict1.items(),key=operator.itemgetter(1))
#operator.itemgetter(1) is a operator to equal to 'lambda s : s[1]'
#sort dictionary by its [1] items, ([0] is key, [1] is the 1st value, etc..)
print(dict1_sortedlist)

print("Finished")