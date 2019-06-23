#ifndef MAP_TEST_CLASS_H
#define MAP_TEST_CLASS_H

#include <map>
#include <string>

using namespace std;

class MapTestClass {

private:
	map<string,int> m_map;

public:
	void traverse() {
		for (map<string, int>::iterator it = m_map.begin(); it != m_map.end(); it++) {
			printf("%s %d \n", (it->first).c_str(), it->second);
		}
	};
	void add_item(string key, int val) {
		m_map.insert(pair<string, int>(key, val));
	};
	void delete_item(string key) {};
	void modify_item(string key) {};
	void search_key(string key) {};
};

#endif