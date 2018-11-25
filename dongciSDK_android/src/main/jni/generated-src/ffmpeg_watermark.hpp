// AUTOGENERATED FILE - DO NOT MODIFY!
// This file generated by Djinni from ffmpeg.djinni

#pragma once

#include <cstdint>
#include <string>
#include <utility>

namespace dc { namespace platform {

struct FfmpegWatermark final {
    std::string image;
    int32_t x;
    int32_t y;
    float from_time;
    float duration;

    FfmpegWatermark(std::string image,
                    int32_t x,
                    int32_t y,
                    float from_time,
                    float duration)
    : image(std::move(image))
    , x(std::move(x))
    , y(std::move(y))
    , from_time(std::move(from_time))
    , duration(std::move(duration))
    {}
    FfmpegWatermark() = default;
};

} }  // namespace dc::platform
