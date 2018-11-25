#pragma once

#include <chrono>

namespace dc{
    namespace platform{
        
        class StopWatch
        {
        public:
            
            StopWatch(){}
            
            void Start(){m_StartPoint = std::chrono::high_resolution_clock::now();}
            
            void Stop(){m_EndPoint = std::chrono::high_resolution_clock::now();}
            
            long GetDuration(){return std::chrono::duration_cast<std::chrono::milliseconds>(m_EndPoint - m_StartPoint).count();}
            
        private:
            std::chrono::time_point<std::chrono::high_resolution_clock> m_StartPoint;
            std::chrono::time_point<std::chrono::high_resolution_clock> m_EndPoint;
            
        };
    }
}
