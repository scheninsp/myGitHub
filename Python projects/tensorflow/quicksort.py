# -*- coding: utf-8 -*-
"""
Created on Fri Oct 27 15:32:14 2017

@author: Administrator
"""

"""Implement quick sort in Python.
Input a list.
Output a sorted list."""
def quicksort(array):
    i = 0
    j = len(array)-1
    quicksort_p(array,i,j)    
    return array

def quicksort_p(array,st,ed):
    i=st
    j=ed
    if (i>=j):
        return 
    else:
        p = int((i+j)/2)
        pval = array[p]
        while(i<j):
            while(array[i] < pval):
                i+=1     
            swap(array,i,p)
            p=i
            i+=1
            while(array[j] > pval):
                j-=1
            swap(array,j,p)
            p=j
            j-=1
            
        quicksort_p(array,st,p-1)
        quicksort_p(array,p+1,ed)
        return 

def swap(array,i,j):
    temp = array[i]
    array[i] = array[j]
    array[j] = temp

#test = [3,2,1,5,5]
test = [21, 4, 1, 3, 9, 20, 25, 6, 21, 14]
print (quicksort(test))
