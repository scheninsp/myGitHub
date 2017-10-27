# -*- coding: utf-8 -*-
"""
Created on Fri Oct 27 22:08:46 2017

@author: Administrator
"""

"""Time to play with Python dictionaries!
You're going to work on a dictionary that
stores cities by country and continent.
One is done for you - the city of Mountain 
View is in the USA, which is in North America.

You need to add the cities listed below by
modifying the structure.
Then, you should print out the values specified
by looking them up in the structure.

Cities to add:
Bangalore (India, Asia)
Atlanta (USA, North America)
Cairo (Egypt, Africa)
Shanghai (China, Asia)"""

locations = {'Asia': {'China':['Shanghai'],'India': ['Bangalore']},\
'North America':{'USA' : ['Mountain View']},'Africa':{'Egypt':['Cairo']}}

locations['North America']['USA'].append('Atlanta')

"""Print the following (using "print").
1. A list of all cities in the USA in
alphabetic order.
2. All cities in Asia, in alphabetic
order, next to the name of the country.
In your output, label each answer with a number
so it looks like this:
1
American City
American City
2
Asian City - Country
Asian City - Country"""


usa_cities = sorted(locations['North America']['USA']);
print(1)
for city in usa_cities:
    print(city)

asia_cities = []
for country, city in locations['Asia'].items():
    asia_cities.append(city[0] + " - " + country)
asia_sorted = sorted(asia_cities)
print(2)
for city in asia_sorted:
    print(city)