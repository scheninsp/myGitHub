#include<thread>
#include<windows.h>
#include<string.h>
#include<mutex>
// receive a data buffer in main thread and process in subthread

const int BUFFER_SIZE = 4000*3000;

static bool global_flag = false;
static float buffer0[BUFFER_SIZE];
static float buffer1[BUFFER_SIZE];

std::mutex some_mutex;

void safecopy(float* buff0, float* buff1) {
	//lock buffer0
	std::lock_guard<std::mutex> guard(some_mutex);  //mutex to myfunc2
	memcpy(buff1, buff0, BUFFER_SIZE * sizeof(float));

	//check buff0 == buff1 data , without mutex , data will be modified by myfunc1
	for (int i = 0; i < BUFFER_SIZE; i++) {
		if (buff0[i] != buff1[i]) {
			printf("buff0 != buff1 !");
		}
	}
}

void myfunc1(float* buff0, float* buff1) {
	getchar();
	safecopy(buff0, buff1);

	float sum = 0;
	for (int i = 0; i < BUFFER_SIZE; i++) {
		sum += buff1[i];
	}
	printf("myfunc1 sum = %f \n", sum);
}

void myfunc2(int cnt) {
	//printf("myfunc2 cnt :%d \n", cnt);
	std::lock_guard<std::mutex> guard(some_mutex);   //mutex to myfunc1
	int n = cnt % BUFFER_SIZE;
	buffer0[n] += 1;
}

int main() {

	int cnt = 0;
	global_flag = true;
	//initialize buffer0
	for (int i = 0; i < BUFFER_SIZE; i++) {
		buffer0[i] = 1;
	}

	std::thread p1(myfunc1,buffer0,buffer1);

	while (true) {
		if (global_flag == true) {
			Sleep(100);
			myfunc2(cnt);
			cnt++;
		}
	}
}