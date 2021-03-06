// AUTOGENERATED FILE - DO NOT MODIFY!
// This file generated by Djinni from ffmpeg.djinni

#pragma once

#include "ffmpeg_watermark.hpp"
#include "transcode_listener.hpp"
#include <cstdint>
#include <memory>
#include <string>
#include <vector>

namespace dc { namespace platform {

class FfmpegProcessor {
public:
    virtual ~FfmpegProcessor() {}

    virtual bool GetImageFromVideo(const std::string & video, float time, const std::string & output) = 0;

    virtual bool GenerateVideoWithImage(const std::string & image, int32_t duration, const std::string & output) = 0;

    virtual bool AddWatermarks(const std::string & video, const std::vector<FfmpegWatermark> & watermarks, const std::string & output) = 0;

    virtual bool GenerateLoopAudio(const std::string & audio, int32_t loop_count, const std::string & output) = 0;

    virtual bool GenerateLoopAudioWithDuration(const std::string & audio, int32_t loop_count, int32_t duration_ms, const std::string & output) = 0;

    virtual bool ExtractAudioFromVideo(const std::string & video, int32_t vol, const std::string & output) = 0;

    virtual bool MixAudio(const std::string & audio0, const std::string & audio1, const std::string & output) = 0;

    virtual bool GenerateFadeOutAudio(const std::string & audio, float fade_out_time, const std::string & output) = 0;

    virtual bool MuxVideoAndAudio(const std::string & video, const std::string & audio, const std::string & output) = 0;

    virtual bool MoveMoovFlgToBeginning(const std::string & video, const std::string & output) = 0;

    virtual bool GetTs(const std::string & video, const std::string & output) = 0;

    virtual bool ConcatVideos(const std::vector<std::string> & videos, const std::string & output) = 0;

    virtual bool ConcatVideosWithDirectory(const std::string & directory, const std::vector<std::string> & videos, const std::string & output) = 0;

    virtual bool GenerateBlurVideo(const std::string & video, int32_t blur, const std::string & output) = 0;

    virtual bool TrimVideo(const std::string & video, float from_time, float duration, const std::string & output) = 0;

    virtual bool MakeAudioSilent(const std::string & video, const std::string & output) = 0;

    virtual bool TrimVideoWithWatermark(const std::string & video, float from_time, float duration, const FfmpegWatermark & watermark, const std::string & output) = 0;

    virtual bool TimeScale(const std::string & video, float time_scale, const std::string & output) = 0;

    virtual bool VideoCopy(const std::string & video, const std::string & output) = 0;

    virtual bool Execute(const std::string & cmd) = 0;

    virtual bool CropVideo(const std::string & video, int32_t x, int32_t y, int32_t width, int32_t height, const std::string & output) = 0;

    virtual bool TranscodeVideo(const std::string & video, int32_t width, int32_t height, int32_t fps, int32_t bitrate, const std::string & output) = 0;

    virtual bool TranscodeAudio(const std::string & video, int32_t samplerate, int32_t channels,int32_t bitrate, const std::string & output) = 0;
    virtual bool RotateVideo(const std::string & video, const std::string & output) = 0;
    virtual void SetTranscodeListener(const std::shared_ptr<TranscodeListener> & listener) = 0;
    static std::shared_ptr<FfmpegProcessor> CreateFfmpegProcessor();
};

} }  // namespace dc::platform
