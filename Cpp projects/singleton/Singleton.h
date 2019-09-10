//Singleton<AnyClass> 
#pragma once
#include "CriticalSection.h"

namespace mySingleton {
	template<class T>
	class Singleton {
	private:
		static CCriticalSection criticalSection;
		static T* singleton;
	public:
		static T* GetInstance() {
			if (!singleton) {
				CCriticalSectionLocker locker(criticalSection);
				if (!singleton) {
					static T instance;
					singleton = &instance;
				}
			}
			return singleton;
		};
	};

	template<class T> CCriticalSection Singleton<T>::criticalSection;
	template<class T> T* Singleton<T>::singleton = NULL;
}
