# *- coding:utf-8 -*-

from numpy import *
import operator  #for sorting

def createDataSet():
    group = array([[1.0,1.1],[1.0,1.0],[0,0],[0,0.1]])
    labels = ['A','A','B','B']
    return group, labels

def classify0(inX, dataSet, labels, k):
    dataSetSize = dataSet.shape[0]
    diffMat = dataSet - tile(inX,(dataSetSize,1))  # - repmat(inX,[dataSetSize,1])
    sqDiffMat = diffMat**2
    sqDistances = sqDiffMat.sum(axis=1)   #  sum(sqDiffMat,2)
    distances = sqDistances**0.5
    sortedDistIndicies = distances.argsort()  #[~,indx] = sort(distances,'ascend')
    classCount = {}   #create Dict
    for i in range(k):
        voteIlabel = labels[sortedDistIndicies[i]]
        classCount[voteIlabel] = classCount.get(voteIlabel,0) + 1
    sortedClassCount = sorted(classCount.items(),key=operator.itemgetter(1), reverse=True)
    # reverse=True : descend order, key: compared to which element/attribute
    return sortedClassCount[0][0]

if __name__ == "__main__":
    print('directly run for test')
    [group,labels] = createDataSet()
    classLabel = classify0([0,0],group,labels,3)
    str = "[0,0] belongs to class %s" %(classLabel)
    print(str)