//CriticalSection implementation , instead of MFC CCriticalSection
#pragma once

#include <Windows.h>
namespace mySingleton {
	class CCriticalSection {
	public:
		CCriticalSection() { ::InitializeCriticalSection(&m_cs); };
		virtual ~CCriticalSection() { ::DeleteCriticalSection(&m_cs); };
	public:
		void Lock() { ::EnterCriticalSection(&m_cs); };
		void Unlock() { ::LeaveCriticalSection(&m_cs); };
	private:
		CRITICAL_SECTION m_cs;
	};


	class CCriticalSectionLocker {
	public:
		CCriticalSectionLocker(CCriticalSection& cs)
			: m_cs(cs) {
			m_cs.Lock();
		};
		virtual ~CCriticalSectionLocker() { m_cs.Unlock(); };
	private:
		CCriticalSection& m_cs;
	};
}
