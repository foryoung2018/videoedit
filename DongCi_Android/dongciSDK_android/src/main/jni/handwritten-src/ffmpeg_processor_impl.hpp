//
//  ffmpeg_processor_impl.hpp
//  ZPLFFmpeg
//
//  Created by Xiao Zhang on 4/6/16.
//  Copyright © 2016 Zepp Technology Inc. All rights reserved.
//

#pragma once
#include "ffmpeg_processor.hpp"
#include "transcode_listener.hpp"
#include <exception>
#include <vector>
#include <string>

namespace dc {
    namespace platform {
        class FFmpegProcessorImpl final : public FfmpegProcessor {
        public:
            FFmpegProcessorImpl();
            ~FFmpegProcessorImpl();
            virtual bool GetImageFromVideo(const std::string & video, float time, const std::string & output);
            
            virtual bool GenerateVideoWithImage(const std::string & image, int32_t duration, const std::string & output);
            
            virtual bool AddWatermarks(const std::string & video, const std::vector<FfmpegWatermark> & watermarks, const std::string & output);
            
            virtual bool GenerateLoopAudio(const std::string & audio, int32_t loop_count, const std::string & output);

            virtual bool GenerateLoopAudioWithDuration(const std::string & audio, int32_t loop_count, int32_t duration_ms, const std::string & output);

            virtual bool ExtractAudioFromVideo(const std::string & video, int32_t vol, const std::string & output);
            
            virtual bool MixAudio(const std::string & audio0, const std::string & audio1, const std::string & output);
            
            virtual bool GenerateFadeOutAudio(const std::string & audio, float fade_out_time, const std::string & output);
            
            virtual bool MuxVideoAndAudio(const std::string & video, const std::string & audio, const std::string & output);
            
            virtual bool MoveMoovFlgToBeginning(const std::string & video, const std::string & output);
            
            virtual bool GetTs(const std::string & video, const std::string & output);
            
            virtual bool ConcatVideos(const std::vector<std::string> & videos, const std::string & output);
            
            virtual bool ConcatVideosWithDirectory(const std::string & directory, const std::vector<std::string> & videos, const std::string & output);
            
            virtual bool GenerateBlurVideo(const std::string & video, int32_t blur, const std::string & output);
            
            virtual bool TrimVideo(const std::string & video, float from_time, float duration, const std::string & output);
            
            virtual bool MakeAudioSilent(const std::string & video, const std::string & output);
            
            virtual bool TrimVideoWithWatermark(const std::string & video, float from_time, float duration, const FfmpegWatermark & watermark, const std::string & output);
            
            virtual bool TimeScale(const std::string & video, float time_scale, const std::string & output);
            
            virtual bool VideoCopy(const std::string & video, const std::string & output);
            
            virtual bool CropVideo(const std::string & video, int32_t x, int32_t y, int32_t width, int32_t height, const std::string & output);

            virtual bool TranscodeVideo(const std::string & video, int32_t width, int32_t height, int32_t fps, int32_t bitrate, const std::string & output);
            virtual bool TranscodeAudio(const std::string & video, int32_t samplerate, int32_t channels,int32_t bitrate, const std::string & output);
            virtual bool RotateVideo(const std::string & video, const std::string & output);

            virtual void SetTranscodeListener(const std::shared_ptr<TranscodeListener> & listener) ;
            virtual bool Execute(const std::string & cmd);

        private:
            std::shared_ptr<TranscodeListener> _listener;
            int ParseCmd(std::vector<std::string> &cmdArgs, std::unique_ptr<char*[]> &argv);
            
            bool _Execute(const char *cmd, ...);
            bool _Execute2(const char *buf);
        };
    }
}

extern "C" void callback_f(void * p, float seconds);
