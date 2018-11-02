# -*- coding: utf-8 -*-

tuple = ('a','b','c')
print(len(tuple))

slist = [1,2,3,4,6]
slist.insert(4,5)
print(slist)  #[1, 2, 3, 4, 5, 6]
slist.pop()
print(slist) #[1, 2, 3, 4, 5]
slist.pop(0)
print(slist) #[2, 3, 4, 5]
slist[0] = 1
print(slist) #[1, 3, 4, 5]
slist[:2] = [0,1,2]
print(slist) #[0,1,2,4,5]
print(slist[:2])   
slist2 = range(5)
print(slist2)   #range type
for i in slist2:
    print("%02d, " % i,end='')

fp = './testdata2.txt'
#with open(fp,'r') as ft:
#    #x = ft.read()  #read all file as a string
#    #for line in ft:  # ft is iterable, line is line
#    lines = ft.readlines() #form a multiple string lines, with '\n' ending each element   
#    print("Type: {0} , Value: {1}".format(type(lines),lines))
    
with open (fp,'r') as ft:
    data= ft.read()

rows = data.split('\n')
full_data = []
for row in rows:
    split_row = row.split(" ")
    for i in range(len(split_row)):
        split_row[i] = int(split_row[i]) 
    full_data.append(split_row)
    

import numpy as np

A1 = np.zeros((3,3))
print(A1)

row, col = A1.shape
print((row,col))  #(3,3)

for i in range(row):
    for j in range(col):
        if i > j:    
            A1[i][j] = 2
        elif i < j:
            A1[i][j] = 1/2

print(A1)        
print(np.transpose(A1))

B1 = np.zeros((row,col),dtype=np.int32)  
# default type cannot invert
for i in range(row):
    B1[i][i] = 3
B1 = np.mat(B1)  #change ndarray to matrix
B2 = B1.I    #only matrix is invertable
print(B1)
print(B2)
print(B1.dot(B2))
