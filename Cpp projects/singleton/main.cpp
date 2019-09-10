#include <stdio.h>
#include "Singleton.h"

class TestSingleton{
private:
	int data;
};

int main() {
	TestSingleton* mySing1 = mySingleton::Singleton<TestSingleton>::GetInstance();
	TestSingleton* mySing2 = mySingleton::Singleton<TestSingleton>::GetInstance();

	printf("mySing1 : %p \n", mySing1);
	printf("mySing2 : %p \n", mySing2);

	getchar();
	return 0;
}
