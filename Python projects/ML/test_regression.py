# -*- coding:UTF-8 -*-

from numpy import *

def loadDataSet(fileName):
    numFeat = len(open(fileName).readline().split('\t'))-1
    dataMat = [];
    labelMat = [];

    fr = open(fileName)
    for line in fr.readlines():
        lineArr = []
        curLine = line.strip().split('\t')
        for i in range(numFeat):
            lineArr.append(float(curLine[i]))
        dataMat.append(lineArr)
        labelMat.append(float(curLine[-1]))
    return dataMat, labelMat


def standRegres(xArr,yArr):
    xMat = mat(xArr)
    yMat = mat(yArr).T
    xTx = xMat.T*xMat
    if linalg.det(xTx) == 0.0:
        print("X is not invertable")
        return
    ws = xTx.I * (xMat.T*yMat)
    return ws

if __name__ == "__main__":
    [dataMat, labelMat] = loadDataSet('./ex0.txt')
    w = standRegres(dataMat,labelMat)
    print(w)

    #debug
    x1_mat=mat(dataMat)
    print('dataMat : ',shape(x1_mat))
    x1=mat(dataMat)[:,1]            #dataMat[:,0] is intersect
    print('dataMat[:,1] : ',size(x1))
    y=mat(labelMat)
    print('labelMat[:,1] : ',size(y))

    print('matrix object attributes:' , dir(x1.flatten()))

    #for line plot
    xCopy = x1_mat.copy()
    xCopy.sort(0)
    yHat = xCopy*w   #a sorted (x,y)

    #check residual
    err = y.T - x1_mat*w
    print('err shape : ' , err.shape)
    print('err: \n' , err)

    #plot scatter with regression line
    import matplotlib.pyplot as plt
    fig = plt.figure()
    ax = fig.add_subplot(111)
    ax.scatter(x1_mat[:, 1].flatten().A[0], y.T[:, 0].flatten().A[0])
    ax.plot(xCopy[:,1],yHat)
    plt.show()
