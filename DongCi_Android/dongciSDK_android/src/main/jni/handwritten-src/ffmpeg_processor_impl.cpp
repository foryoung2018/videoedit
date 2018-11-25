//
//  ffmpeg_processor_impl.cpp
//  ZPLFFmpeg
//
//  Created by Xiao Zhang on 4/6/16.
//  Copyright © 2016 Zepp Technology Inc. All rights reserved.
//

#include "ffmpeg_processor_impl.hpp"
#include "ffmpeg.h"
#ifdef __ANDROID__
#include "log.hpp"
#endif
#include <sstream>
#include "scope_guard.hpp"
#include "file_manager.h"
#include "log.hpp"
#include "media_info.hpp"

#define CMD_BUFFER_SIZE 1024*256
//#define REDIRECT_ANDROID_STDOUT

namespace dc {namespace platform {
    
    std::shared_ptr<FfmpegProcessor> FfmpegProcessor::CreateFfmpegProcessor() {
        return std::make_shared<FFmpegProcessorImpl>();
    }
    
    FFmpegProcessorImpl::FFmpegProcessorImpl()
    {
#if defined(__ANDROID__) && defined(REDIRECT_ANDROID_STDOUT)
        freopen("/storage/emulated/0/Movies/stderr.txt", "w", stderr);
        freopen("/storage/emulated/0/Movies/stdout.txt", "w", stdout);
#endif
        _listener = nullptr;
    }

    FFmpegProcessorImpl::~FFmpegProcessorImpl()
    {
    }

        bool FFmpegProcessorImpl::GetImageFromVideo(const std::string & video, float time, const std::string & output)
    {
        return _Execute("-ss %.2f -i %s -f image2 -vframes 1 -y  %s", time, video.c_str(), output.c_str());
    }
    
    bool FFmpegProcessorImpl::GenerateVideoWithImage(const std::string & image, int32_t duration, const std::string & output)
    {
        return _Execute("-ar 44100 -ac 1 -f s16le -i /dev/zero -loop 1 -i %s -c:v libx264 -t %d -pix_fmt yuv420p -r 30 -y -loglevel error %s", image.c_str(), duration, output.c_str());
    }
    
    bool FFmpegProcessorImpl::AddWatermarks(const std::string & video, const std::vector<FfmpegWatermark> & watermarks, const std::string & output)
    {
        std::string overlayFiles = "";
        std::string overlayOps = "";
        for(int i = 0; i < watermarks.size(); ++i)
        {
            const FfmpegWatermark &watermark = watermarks[i];
            overlayFiles += " -i " + watermark.image;
            if(watermark.from_time < 0)
            {
                overlayOps += "overlay=" + StringUtil::ToString(watermark.x) + ":" + StringUtil::ToString(watermark.y);
            }
            else
            {
                overlayOps += "overlay=" + StringUtil::ToString(watermark.x) + ":" + StringUtil::ToString(watermark.y) + ":enable='between(t," + StringUtil::ToString(watermark.from_time) + "," + StringUtil::ToString(watermark.from_time + watermark.duration) + ")'";
            }
            if(i != watermarks.size() - 1)
            {
                overlayOps += ",";
            }
        }
        
        return _Execute("-i %s %s -filter_complex %s -c:v libx264 -c:a copy -preset ultrafast -y -loglevel error %s", video.c_str(), overlayFiles.c_str(), overlayOps.c_str(), output.c_str());
    }
    
    bool FFmpegProcessorImpl::GenerateLoopAudio(const std::string & audio, int32_t loop_count, const std::string & output)
    {
        std::string audioList = "concat:";
        for(int i = 0; i < loop_count; ++i)
        {
            audioList.append(audio);
            if(i < loop_count - 1)
            {
                audioList.append("|");
            }
        }
        return _Execute("-i %s -c copy -y %s", audioList.c_str(), output.c_str());
    }

    bool FFmpegProcessorImpl::GenerateLoopAudioWithDuration(const std::string &audio,
                                                            int32_t loop_count, int32_t duration_ms,
                                                            const std::string &output)
    {
        std::string audioList = "concat:";
        for(int i = 0; i < loop_count; ++i)
        {
            audioList.append(audio);
            if(i < loop_count - 1)
            {
                audioList.append("|");
            }
        }
        return _Execute("-i %s -c copy -t %.3f -y %s", audioList.c_str(), duration_ms/1000.f, output.c_str());
    }

    bool FFmpegProcessorImpl::ExtractAudioFromVideo(const std::string & video, int32_t vol, const std::string & output)
    {
        float volume = vol / 100.f;
        return _Execute("-i %s -vn -af volume=%.1f -strict -2 -y %s", video.c_str(), volume, output.c_str());
    }

    bool FFmpegProcessorImpl::MixAudio(const std::string & audio0, const std::string & audio1, const std::string & output)
    {
        // dropout_transition=100 确保时间短的audio结束时,另一个audio的音量不会突然变大。
        return _Execute("-i %s -i %s -filter_complex amix=inputs=2:duration=first:dropout_transition=100 -y %s", audio0.c_str(), audio1.c_str(), output.c_str());
    }

    bool FFmpegProcessorImpl::GenerateFadeOutAudio(const std::string & audio, float fade_out_time, const std::string & output)
    {
        MediaInfo audioInfo(audio);
        float audioDuration = audioInfo.duration();
        return _Execute("-i %s -af afade=t=out:st=%.3f:d=%.3f -strict -2 -y %s", audio.c_str(), audioDuration - fade_out_time, fade_out_time, output.c_str());
    }

    bool FFmpegProcessorImpl::MuxVideoAndAudio(const std::string & video, const std::string & audio, const std::string & output)
    {
        return _Execute("-i %s -i %s -c:v copy -c:a copy -bsf:a aac_adtstoasc -map 0:v -map 1:a -shortest -strict -2 -y %s", video.c_str(), audio.c_str(), output.c_str());
    }

    bool FFmpegProcessorImpl::MoveMoovFlgToBeginning(const std::string & video, const std::string & output)
    {
        return _Execute("-i %s -c:v copy -c:a copy -movflags +faststart -y -loglevel error %s", video.c_str(), output.c_str());
    }

    bool FFmpegProcessorImpl::GetTs(const std::string & video, const std::string & output)
    {
        return _Execute("-i %s -y -c copy -bsf:v h264_mp4toannexb -f mpegts -loglevel error %s", video.c_str(), output.c_str());
    }

    bool FFmpegProcessorImpl::ConcatVideos(const std::vector<std::string> & videos, const std::string & output)
    {
        std::string input = "concat:";
        for(int i = 0; i < videos.size(); ++i)
        {
            input += videos[i];
            if(i < videos.size() - 1)
            {
                input += "|";
            }
        }
        return _Execute("-i %s -c:v copy -c:a copy -bsf:a aac_adtstoasc -y -loglevel error %s", input.c_str(), output.c_str());
    }

    bool FFmpegProcessorImpl::GenerateBlurVideo(const std::string & video, int32_t blur, const std::string & output)
    {
        return _Execute("-i %s -filter_complex boxblur=%d -c:v libx264 -c:a copy -preset ultrafast -y -loglevel error %s", video.c_str(), blur, output.c_str());
    }

    bool FFmpegProcessorImpl::TrimVideo(const std::string & video, float from_time, float duration, const std::string & output)
    {
        if(duration < 0) {
           return _Execute("-ss %f -i %s  -c copy  -y -loglevel error %s", from_time, video.c_str(), output.c_str());
        }
        return _Execute("-ss %f -t %f -i %s  -c copy  -y -loglevel error %s", from_time, duration, video.c_str(), output.c_str());
    }

    bool FFmpegProcessorImpl::MakeAudioSilent(const std::string & video, const std::string & output)
    {
        return _Execute("-ar 44100 -ac 1 -f s16le -i /dev/zero -i %s -shortest -c:v copy -c:a aac -strict -2 -map 0:a -map 1:v -preset ultrafast -y -loglevel error %s", video.c_str(), output.c_str());
    }
    
    bool FFmpegProcessorImpl::TrimVideoWithWatermark(const std::string & video, float from_time, float duration, const FfmpegWatermark & watermark, const std::string & output)
    {
        return _Execute("-ss %f -t %f -i %s -i %s -filter_complex overlay=%d:%d -preset ultrafast -y -loglevel error %s", from_time, duration, video.c_str(), watermark.image.c_str(), watermark.x, watermark.y, output.c_str());
    }
    
    bool FFmpegProcessorImpl::TimeScale(const std::string & video, float time_scale, const std::string & output)
    {
        return _Execute("-i %s -filter:v setpts=%f*PTS -preset ultrafast -y -loglevel error %s", video.c_str(), time_scale, output.c_str());
    }
    
    bool FFmpegProcessorImpl::ConcatVideosWithDirectory(const std::string & directory, const std::vector<std::string> & videos, const std::string & output)
    {
        if(videos.size() == 0)
        {
            return false;
        }
        std::vector<std::string> tmpVideos;
        
        ON_SCOPE_EXIT([&](){
            FileManager::DeleteFiles(tmpVideos);
        });
        for(int i = 0; i < videos.size(); ++i)
        {
            std::string ts = FileManager::GenerateUniquePath(directory, "_ts"+StringUtil::ToString(i)+".ts");
            GetTs(videos[i], ts);
            tmpVideos.push_back(ts);
        }
        std::string concat = FileManager::GenerateUniquePath(directory, "_tc.mp4");
        ConcatVideos(tmpVideos, concat);
        tmpVideos.push_back(concat);
        
        MediaInfo mediaInfo(concat);
        float duration = mediaInfo.duration();
        if (duration > 0)
        {
            return TrimVideo(concat, 0.05, duration, output);
        }
        return false;
    }
    
    bool FFmpegProcessorImpl::VideoCopy(const std::string & video, const std::string & output)
    {
        return _Execute("-i %s -c copy -y -loglevel error %s", video.c_str(), output.c_str());
    }
    
    bool FFmpegProcessorImpl::CropVideo(const std::string & video, int32_t x, int32_t y, int32_t width, int32_t height, const std::string & output)
    {
        return _Execute("-i %s -filter:v crop=%d:%d:%d:%d -c:a copy -y %s", video.c_str(), width, height, x, y, output.c_str());
    }
        bool FFmpegProcessorImpl::TranscodeVideo(const std::string & video, int32_t width, int32_t height, int32_t fps, int32_t bitrate, const std::string & output)
        {
            return _Execute("-i %s -filter:v scale=%d:%d  -r %d -b:v %dk -preset ultrafast -c:a copy -y %s", video.c_str(), width, height, fps, bitrate, output.c_str());
        }

        bool FFmpegProcessorImpl::TranscodeAudio(const std::string & video, int32_t samplerate, int32_t channels, int32_t bitrate,const std::string & output)
        {
            return _Execute("-i %s  -c:v copy -c:a aac -ar %d -ac %d -b:a %d -movflags +faststart -y -loglevel error %s", video.c_str(), samplerate,channels, bitrate,output.c_str());
        }

        bool FFmpegProcessorImpl::RotateVideo(const std::string & video, const std::string & output)
        {
            return _Execute("-i %s  -vf rotate=0 -metadata:s:v rotate=0 -c:v libx264  -preset ultrafast -c:a copy -movflags +faststart -y -loglevel error %s", video.c_str(), output.c_str());
        }
    
    bool FFmpegProcessorImpl::Execute(const std::string & cmd)
    {
        return _Execute2(cmd.c_str());
    }
    
    int FFmpegProcessorImpl::ParseCmd(std::vector<std::string> &cmdArgs, std::unique_ptr<char*[]> &argv)
    {
        const int argc = (int)cmdArgs.size() + 1;
        argv.reset(new char*[argc + 1]);
        for(int i = 1; i < argc; ++i)
        {
            argv[i] = const_cast<char*>(cmdArgs[i - 1].c_str());
        }
        return argc;
    }
    
    bool FFmpegProcessorImpl::_Execute(const char *cmd, ...)
    {
        va_list args;

        va_start(args, cmd);

        std::unique_ptr<char[]> buf(new char[CMD_BUFFER_SIZE]);

        vsprintf(buf.get(), cmd, args);

        va_end(args);

        ZF_LOGD("FFmpeg cmd: %s\n", buf.get());
        
        return _Execute2(buf.get());
    }

        void FFmpegProcessorImpl::SetTranscodeListener(const std::shared_ptr<TranscodeListener> & listener)
        {
            this->_listener = listener;
        }
    
    bool FFmpegProcessorImpl::_Execute2(const char *buf)
    {

        std::stringstream stream(buf);

        std::string value;

        std::vector<std::string> cmdArgs;

        while(getline(stream, value, ' '))
        {
            if(value.compare("") != 0)
            {
                cmdArgs.push_back(value);
            }
        }


        
        std::unique_ptr<char*[]> argv;

        int argc = ParseCmd(cmdArgs, argv);

        int result = 0;


        result = execute(argc, const_cast<char**>(argv.get()),callback_f,this->_listener.get());

        return result == 0;
    }
}}



extern "C"
void callback_f(void * p, float percent) // wrapper function
{
    if(!p) {
        return;
    }

    return ((dc::platform::TranscodeListener *)p)->OnProcess(percent);

}
