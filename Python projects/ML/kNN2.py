# *- coding:utf-8 -*-
# a self defined version, Chen 2018.4.12

from numpy import *
import operator  #for sorting

def createDataSet():
    group = array([[1.0,1.1],[1.0,1.0],[0,0],[0,0.1]])
    labels = ['A','A','B','B']
    return group, labels

def classify0(inX, dataSet, labels, k):
#INPUT:
#inX : 1*nFeat
#dataSet : nSamples * nFeat
#labels : nSamples * 1, binary
#k : nearest neighbour count

#OUTPUT:
# a binary value indicating class inX belings to

    nCategory = unique(labels).size

    diffMat = dataSet - tile(inX,[size(dataSet,0),1])
    dist = sum(diffMat**2,axis=1)
    dist = dist**0.5
    sortedDistIndx = dist.argsort()
    classLabel_to = []
    for i in range(k):
        classLabel_to.append(labels[sortedDistIndx[i]])

    classNums=zeros((nCategory,1))
    for i in range(nCategory):
        classNums[i] = classLabel_to.count(i)

    return argmax(classNums)

if __name__ == "__main__":
    print('directly run for test')
    [group,labels] = createDataSet()
    classLabel = classify0([0,0],group,labels,3)
    str = "[0,0] belongs to class %s" %(classLabel)
    print(str)