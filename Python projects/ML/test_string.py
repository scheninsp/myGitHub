# -*- coding: utf-8 -*-
import re

def is_valid_email(addr):
    if re.match(r'^[\w.]+@[\w]+.com$',addr):
        return True
    else:
        return False

def name_of_email(addr):
    name = re.match(r'^<*([\w\s]+)>*([\w\s]*)@', addr).groups()
    print(name)
    return name[0]


if __name__ == "__main__":
    str1 = "singleword"
    str2 = str1.lstrip("single")
    str3 = str1.rstrip("word")
    length = len(str3)
    #print(length)

    str4 = r'ABC\-001'  #use 'r' prefix to avoid '\|'
    str5 = 'ABC\\-001'
    #print(True if str4==str5 else False)

    str_list = ["someone@gmail.com","bill.gates@microsoft.com",
                "bob#example.com","mr-bob@example.com"]

    assert is_valid_email(str_list[0])
    assert is_valid_email(str_list[1])
    assert not is_valid_email(str_list[2])
    assert not is_valid_email(str_list[3])

    str_list2 = ["<Tom Paris> tom@voyager.org","bob@example.com"]

    assert name_of_email(str_list2[0]) == 'Tom Paris'
    assert name_of_email(str_list2[1]) == 'bob'