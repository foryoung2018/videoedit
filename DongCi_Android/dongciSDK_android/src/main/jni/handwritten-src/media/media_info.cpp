//
//  media_info.cpp
//  ZPLFFmpeg
//
//  Created by Xiao Zhang on 8/30/16.
//  Copyright © 2016 Zepp Technology Inc. All rights reserved.
//

#include "media_info.hpp"
#include "scope_guard.hpp"
#include "log.hpp"

using namespace dc::platform;

MediaInfo::MediaInfo(const std::string &filepath)
: _duration(0)
{
    memset(&_video_info, 0, sizeof(_video_info));
    memset(&_audio_info, 0, sizeof(_audio_info));
    init(filepath);
}

MediaInfo::~MediaInfo()
{
    
}

void MediaInfo::init(const std::string &filepath)
{
    av_register_all();
    AVFormatContext *pFormatCtx = avformat_alloc_context();
    if(avformat_open_input(&pFormatCtx, filepath.c_str(), NULL, NULL) != 0)
    {
        ZF_LOGE("Failed to open input file: %s.\n", filepath.c_str());
        return;
    }
    if(NULL == pFormatCtx)
    {
        return;
    }
    ON_SCOPE_EXIT([&](){
        avformat_close_input(&pFormatCtx);
        avformat_free_context(pFormatCtx);
    });
    if(avformat_find_stream_info(pFormatCtx, NULL) < 0)
    {
        ZF_LOGE("Can not find stream info.\n");
        return;
    }
    
    _duration = (float)pFormatCtx->duration / 1000000.0f;
    
    // video info
    AVStream *pVideoStream = findStream(pFormatCtx, AVMEDIA_TYPE_VIDEO);
    if(NULL != pVideoStream)
    {
        _video_info.fps = (float)pVideoStream->avg_frame_rate.num / (float)pVideoStream->avg_frame_rate.den;
        AVCodecContext *pCodecCtx = pVideoStream->codec;
        if(NULL != pCodecCtx)
        {
            _video_info.bit_rate = pCodecCtx->bit_rate;
            _video_info.width = pCodecCtx->width;
            _video_info.height = pCodecCtx->height;
        }
    }
    
    // audio info
    AVStream *pAudioStream = findStream(pFormatCtx, AVMEDIA_TYPE_AUDIO);
    if(NULL != pAudioStream)
    {
        AVCodecContext *pCodecCtx = pAudioStream->codec;
        if(NULL != pCodecCtx)
        {
            _audio_info.bit_rate = pCodecCtx->bit_rate;
            _audio_info.sample_rate = pCodecCtx->sample_rate;
            _audio_info.channels = pCodecCtx->channels;
        }
    }
}

AVStream* MediaInfo::findStream(AVFormatContext *pFormatCtx, AVMediaType type)
{
    int streamIndex = av_find_best_stream(pFormatCtx, type, -1, -1, NULL, 0);
    if(streamIndex < 0)
    {
        ZF_LOGE("Cannot find stream for type %d.\n", type);
        return NULL;
    }
    return pFormatCtx->streams[streamIndex];
}

float MediaInfo::duration()
{
    return _duration;
}

const AudioInfo & MediaInfo::audioInfo()
{
    return _audio_info;
}

const VideoInfo & MediaInfo::videoInfo()
{
    return _video_info;
}
