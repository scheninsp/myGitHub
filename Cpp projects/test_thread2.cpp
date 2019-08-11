#include<thread>
#include<windows.h>
// subthread receive a keyboard input and actiavte

static int global_flag = false;

void myfunc1() {
	//wait until char input or timeout
	getchar();
	printf("myfunc1 activated\n");
}

void myfunc2(int x) {
	//main thread cycle
	printf("myfunc2 x : %d \n", x);
}


void main() {

	int cnt = 0;
	global_flag = true;

	std::thread p1(myfunc1);

	while (true) {
		if (global_flag == true) {
			Sleep(1000);
			myfunc2(cnt);
			cnt++;
		}
	}
}