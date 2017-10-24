# -*- coding: utf-8 -*-

"""You can use this class to represent how classy someone
or something is.
"Classy" is interchangable with "fancy".
If you add fancy-looking items, you will increase
your "classiness".
Create a function in "Classy" that takes a string as
input and adds it to the "items" list.
Another method should calculate the "classiness"
value based on the items.
The following items have classiness points associated
with them:
"tophat" = 2
"bowtie" = 4
"monocle" = 5
Everything else has 0 points.
Use the test cases below to guide you!"""

class Classy(object):
    def __init__(self):
        self.items = []
        self.classiness = []
        
    def addItem(self, str):
        self.items.append(str)
        if str == "tophat":
            self.classiness.append(2)
        elif str == "bowtie":
            self.classiness.append(4)
        elif str == "monocle":
            self.classiness.append(5)
        else:
            self.classiness.append(0)
    
    def getClassiness(self):
        sum_val=0
        for x in self.classiness:
            sum_val += x
        return sum_val
                

# Test cases
me = Classy()

# Should be 0
print (me.getClassiness())

me.addItem("tophat")
# Should be 2
print (me.getClassiness())

me.addItem("bowtie")
me.addItem("jacket")
me.addItem("monocle")
# Should be 11
print (me.getClassiness())

me.addItem("bowtie")
# Should be 15
print (me.getClassiness())
