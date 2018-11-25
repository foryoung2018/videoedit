#pragma once

#include <atomic>
#include <chrono>
#include <condition_variable>
#include <memory>
#include <mutex>
#include <thread>

#include "error_handling_util.hpp"
#include "WindowsTypeHelper.h"

namespace dc{
	enum Async_Worker_Status
	{
		Status_Stopped,
		Status_Started,		
		Status_Paused,
		Status_Error
	};

	class AsyncBase 
	{	

	public:
		AsyncBase():
			m_status(Status_Stopped)
		{}

		virtual ~AsyncBase(void)
		{
			MuteAllExceptions([&](){ StopAsyncBase();});
		}


	protected:
		void StartAsyncBase()
		{
			std::lock_guard<std::mutex> lg(m_operationMutex);
			if(m_status == Status_Stopped)
			{
				m_status = Status_Started;
				std::lock_guard<std::mutex> lock(m_threadOpMutex);
                m_workingThread.reset(new std::thread([&]()
				{
					try
					{
						// SetThreadName(AsyncThreadName().c_str());
						Workloop();

						//set the status to stopped;					
					}
					catch(...)
					{
						m_status = Status_Error;
					}
				}));
			}
			else if (m_status==Status_Paused)
			{
				m_status = Status_Started;
				m_loopNotify.notify_all();
			}
		}
		void StopAsyncBase()
		{
			{
				std::lock_guard<std::mutex> lg(m_operationMutex);
				if(m_status == Status_Started || m_status == Status_Paused)
				{
					m_status = Status_Stopped;	

					UnBlockWorkingThread();		

					m_loopNotify.notify_all();
				}
			}

			std::lock_guard<std::mutex> lock(m_threadOpMutex);
			if(m_workingThread)
			{
				if(m_workingThread->joinable())
				{
					m_workingThread->join();		
				}
				m_workingThread.reset();		
			}
		}
		void PauseAsyncBase()
		{
			std::lock_guard<std::mutex> lg(m_operationMutex);
			if(m_status != Status_Started)
			{
				return;
			}
			m_status = Status_Paused;
		}
		void ResumeAsyncBase()
		{
			std::lock_guard<std::mutex> lg(m_operationMutex);
			if(m_status != Status_Paused)
			{
				return;
			}
			m_status = Status_Started;
			m_loopNotify.notify_all();
		}	

		inline bool IsAsyncBaseRunning(){return m_status != Status_Error&& m_status != Status_Stopped;}
		inline bool IsAsyncBaseInError(){return m_status == Status_Error;}
		inline Async_Worker_Status AsyncBaseStatus(){return m_status;}

	protected:

		//
		// de-blocking the working thread here if there is any blocking operation in you workloop.
		//
		virtual void UnBlockWorkingThread() =0;

		virtual bool CanLoopContinue()
		{
			std::unique_lock<std::mutex> lock(m_operationMutex);
			m_loopNotify.wait_for(lock, std::chrono::seconds(INFINITE),[&](){return m_status!=Status_Paused;});
			return (m_status == Status_Started);
		}
		/*
		virtual void Workloop()
		{
		// variables definition here

		// your main loop, should looks like this. the CanLoopContinue will handle Stop, Pause and Resume for you.
		while(CanLoopContinue()) 
		{
		// you looping code 
		}
		}
		*/
		virtual void Workloop() = 0;	
		virtual std::string AsyncThreadName() = 0;

	protected:
		Async_Worker_Status m_status;
        std::unique_ptr<std::thread> m_workingThread;
		std::mutex m_operationMutex;
		std::mutex m_threadOpMutex;
		std::condition_variable m_loopNotify;

	};
} // namespace dc
