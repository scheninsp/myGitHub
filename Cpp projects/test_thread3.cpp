#include<thread>
#include<windows.h>
#include<string.h>
// receive a data buffer in main thread and process in subthread

const int BUFFER_SIZE = 64;

static bool global_flag = false;
static float buffer0[BUFFER_SIZE];
static float buffer1[BUFFER_SIZE];

void myfunc1(float* buff0, float* buff1) {

	getchar();
	memcpy(buff1, buff0, BUFFER_SIZE * sizeof(float));
	float sum = 0;
	for (int i = 0; i < BUFFER_SIZE; i++) {
		sum += buff1[i];
	}
	printf("myfunc1 sum = %f \n", sum);
}

void myfunc2(int cnt) {
	printf("myfunc2 cnt :%d \n", cnt);
}

int main() {

	int cnt = 0;
	global_flag = true;
	for (int i = 0; i < BUFFER_SIZE; i++) {
		buffer0[i] = 1;
	}

	std::thread p1(myfunc1,buffer0,buffer1);

	while (true) {
		if (global_flag == true) {
			Sleep(1000);
			myfunc2(cnt);
			cnt++;
		}
	}
}