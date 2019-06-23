// STL_Test.cpp : main function
//
#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <stdio.h>
#include <string>

#include "MapTestClass.h"

int main()
{
	FILE * fp = fopen("./map_data.txt", "r");
	if (fp == NULL) { return 1; }
	
	int len = 0;
	fscanf(fp,"%d\r\n", &len);

	MapTestClass mapInst;

	const int MAX_LINE_LEN = 1000;
	int tmpkey = 0;
	char tmpchar[MAX_LINE_LEN] = { 0 };
	for (int i = 0; i < len; i++) {
		fscanf(fp, "%s %d\r\n", &tmpchar, &tmpkey);
		//printf("%d", strlen(tmpchar));
		mapInst.add_item(string(tmpchar), tmpkey);
	}

	mapInst.traverse();
}
