# *- coding:utf-8 -*-

from functools import wraps

def singleton(cls):
    instances = {}
    @wraps(cls)
    def getinstance(*args,**kw):
        if cls not in instances:
            instances[cls] = cls(*args,**kw)
        return instances[cls]
    return getinstance

@singleton
class myClass(object):
    a=1

class myClass2(object):
    def __init__(self):
        self.a=1

if __name__ == "__main__" :
 # addr( class1 ) != addr( class2 )
    class1 = myClass()
    print(class1)

    class2 = myClass()
    print(class2)

# addr( class3 ) != addr( class4 )
    class3 = myClass2()
    print(class3)

    class4 = myClass2()
    print(class4)