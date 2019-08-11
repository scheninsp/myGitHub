#include<thread>
//basic thread 

void myfunc(int x) {

	for (int i = 0; i < 10; i++) {
		printf("x: %d\n", x);
	}	
}

void myfunc2(int y) {
	printf("y: %d\n", y);
}


void main(){

	int input_x = 10;
	int input_y = 1;

	std::thread p1 (myfunc, input_x);

	for (int i = 0; i < 10; i++) {
		myfunc2(input_y);
	}

	if (p1.joinable()) {
		p1.join();
	}

	getchar();
}