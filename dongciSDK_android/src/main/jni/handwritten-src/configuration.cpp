#include "ffmpeg_configuration.hpp"
#include "zf_log.h"

namespace dc {
    namespace platform {

        class ConfigurationInitalizer {
        public:
            ConfigurationInitalizer(){
                zf_log_set_tag_prefix("ZPLFFmpeg");
#ifdef DEBUG
                zf_log_set_output_level(ZF_LOG_DEBUG);
#else
                zf_log_set_output_level(ZF_LOG_WARN);
#endif
            }
        };
        static ConfigurationInitalizer s_configuration_initializer;
        
        
        void FfmpegConfiguration::SetLogLevel(FfmpegLogLevel level){
            zf_log_set_output_level((int)level);
        }
    }
}
