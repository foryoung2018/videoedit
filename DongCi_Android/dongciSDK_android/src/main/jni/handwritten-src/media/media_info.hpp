//
//  media_info.hpp
//  ZPLFFmpeg
//
//  Created by Xiao Zhang on 8/30/16.
//  Copyright © 2016 Zepp Technology Inc. All rights reserved.
//

#pragma once
//#if defined(GNU) || defined(__APPLE__)
extern "C"
{
#include <libavformat/avformat.h>
}
//#endif

#include <string>

namespace dc {
    namespace platform {
        struct VideoInfo
        {
            int64_t bit_rate;
            int width;
            int height;
            float fps;
        };
        
        struct AudioInfo
        {
            int64_t bit_rate;
            int sample_rate;
            int channels;
        };
        
        class MediaInfo
        {
        public:
            MediaInfo(const std::string &filepath);
            ~MediaInfo();
            
            float duration(void);
            
            const AudioInfo & audioInfo(void);
            
            const VideoInfo & videoInfo(void);
        private:
            void init(const std::string &filepath);
            AVStream* findStream(AVFormatContext *fmt_ctx, AVMediaType type);
        private:
            float _duration;
            VideoInfo _video_info;
            AudioInfo _audio_info;
        };
}}
