// AUTOGENERATED FILE - DO NOT MODIFY!
// This file generated by Djinni from ffmpeg.djinni

#pragma once

#include "djinni_support.hpp"
#include "ffmpeg_configuration.hpp"

namespace djinni_generated {

class NativeFfmpegConfiguration final : ::djinni::JniInterface<::dc::platform::FfmpegConfiguration, NativeFfmpegConfiguration> {
public:
    using CppType = std::shared_ptr<::dc::platform::FfmpegConfiguration>;
    using JniType = jobject;

    using Boxed = NativeFfmpegConfiguration;

    ~NativeFfmpegConfiguration();

    static CppType toCpp(JNIEnv* jniEnv, JniType j) { return ::djinni::JniClass<NativeFfmpegConfiguration>::get()._fromJava(jniEnv, j); }
    static ::djinni::LocalRef<JniType> fromCpp(JNIEnv* jniEnv, const CppType& c) { return {jniEnv, ::djinni::JniClass<NativeFfmpegConfiguration>::get()._toJava(jniEnv, c)}; }

private:
    NativeFfmpegConfiguration();
    friend ::djinni::JniClass<NativeFfmpegConfiguration>;
    friend ::djinni::JniInterface<::dc::platform::FfmpegConfiguration, NativeFfmpegConfiguration>;

};

}  // namespace djinni_generated
