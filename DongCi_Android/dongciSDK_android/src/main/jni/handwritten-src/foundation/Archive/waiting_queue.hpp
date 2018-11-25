#pragma once

#include <memory>
#include <queue>
#include <mutex>
#include <condition_variable>
#include <exception>

namespace dc{
    
    class WaitingQueueCancelledException : public std::exception{
        
    };
    template<class ObjType>
    class WaitingQueue
    {
    public:
        WaitingQueue():is_unblock_(false){}
        ObjType deque()
        {
            std::unique_lock<std::mutex> lock(mutex_);
            if(is_unblock_){throw WaitingQueueCancelledException();}//"Deque is cancled!"
            cv_.wait(lock, [&]()
                     {
                         return is_unblock_ || internal_queue_.size()>0;
                     });
            
            if(is_unblock_){throw WaitingQueueCancelledException();}//"Deque is cancled!"
            
            auto ret = internal_queue_.front();
            internal_queue_.pop();
            return ret;
        }
        void enque(const ObjType& obj)
        {
            std::unique_lock<std::mutex> lock(mutex_);
            internal_queue_.push(obj);
            cv_.notify_all();
        }
        void clear()
        {
            std::queue<ObjType> empty_queue;
            {
                std::unique_lock<std::mutex> lock(mutex_);
                internal_queue_.swap(empty_queue);
            }
        }
        void cancelWaiting()
        {
            std::unique_lock<std::mutex> lock(mutex_);
            is_unblock_ = true;
            cv_.notify_all();
        }
        unsigned long size() const
        {
            std::unique_lock<std::mutex> lock(mutex_);
            return internal_queue_.size();
        }
    private:
        std::queue<ObjType> internal_queue_;
        mutable std::mutex mutex_;
        std::condition_variable cv_;
        bool is_unblock_;
    };
} //namespace dc
