# -*- coding:utf-8 -*-

#exp.1 : normalize word formats
# def normalize(name):
#     name = name.capitalize()
#     return name
#
# if __name__ == "__main__":
#
#     str_names = ["adam", "LISA", 'barT']
#     L2 = list(map(normalize, str_names))
#     print(L2)


#exp.2 : change string to float
from functools import reduce

def str2float(s):
    def f(x,y):
        return x*10+y

    [s1,s2]=s.split('.',maxsplit=1)
    rec = len(s2)
    p1 = reduce(f,map(int,s1))
    p2 = reduce(f,map(int,s2))
    p = p1 + p2*10**(-rec)
    return p

if __name__ == "__main__":

    str1 = '123.456'
    print(str2float(str1) - 123.456)
