# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

set(PATH_TO_MEDIACORE ${CMAKE_SOURCE_DIR}/src/main/cpp)
set(PATH_TO_JNI_LAYER ${CMAKE_SOURCE_DIR}/src/main/jni)
set(PATH_TO_THIRDPARTY ${PATH_TO_MEDIACORE}/thirdparty)
set(PATH_TO_PRE_BUILT ${PATH_TO_THIRDPARTY}/prebuilt/${ANDROID_ABI})

set(PATH_TO_LIBS_LAYER ${CMAKE_SOURCE_DIR}/../app/libs)

add_definitions("
-DGL_GLEXT_PROTOTYPES
-DEGL_EGLEXT_PROTOTYPES
")


#set(LOCAL_ARM_MODE arm)


include_directories(${PATH_TO_JNI_LAYER}/deps/djinni/)
#include_directories(${PATH_TO_JNI_LAYER}/deps/ffmpeg/armeabi-v7a/include/)
include_directories(${PATH_TO_JNI_LAYER}/deps/ffmpeg/${ANDROID_ABI}/include/)

include_directories(${PATH_TO_JNI_LAYER}/generated-src/)
include_directories(${PATH_TO_JNI_LAYER}/handwritten-src/)
include_directories(${PATH_TO_JNI_LAYER}/handwritten-src/ffmpeg/)
include_directories(${PATH_TO_JNI_LAYER}/handwritten-src/foundation/)
include_directories(${PATH_TO_JNI_LAYER}/handwritten-src/media/)
include_directories(${PATH_TO_JNI_LAYER}/jni/)



file(GLOB FILES_LIB_DJINNI "${PATH_TO_JNI_LAYER}/deps/djinni/*.cpp")
file(GLOB FILES_LIB_GENERATED_SRC "${PATH_TO_JNI_LAYER}/generated-src/*.cpp")
file(GLOB FILES_LIB_HANDWRITTEN_SRC "${PATH_TO_JNI_LAYER}/handwritten-src/*.cpp")

file(GLOB FILES_LIB_HANDWRITTEN_SRC_FFMPEG "${PATH_TO_JNI_LAYER}/handwritten-src/ffmpeg/*.c")

#file(GLOB FILES_LIB_HANDWRITTEN_SRC_FFMPEG "${PATH_TO_JNI_LAYER}/handwritten-src/ffmpeg/ffmpeg_hw.c")
file(GLOB FILES_LIB_HANDWRITTEN_SRC_FOUNDATION "${PATH_TO_JNI_LAYER}/handwritten-src/foundation/*.c")
file(GLOB FILES_LIB_HANDWRITTEN_SRC_MEDIA "${PATH_TO_JNI_LAYER}/handwritten-src/media/*.cpp")
file(GLOB FILES_LIB_JNI "${PATH_TO_JNI_LAYER}/jni/*.cpp")



add_library(ijkffmpeg
             SHARED
             IMPORTED)

set_target_properties(ijkffmpeg
                      PROPERTIES IMPORTED_LOCATION
                      #${PATH_TO_JNI_LAYER}/deps/ffmpeg/armeabi-v7a/libijkffmpeg.so)

                      #${PATH_TO_JNI_LAYER}/deps/ffmpeg/${ANDROID_ABI}/libdijkffmpeg.so)
                      ${PATH_TO_LIBS_LAYER}/${ANDROID_ABI}/libijkffmpeg.so)


add_library(dcffmpeg-native SHARED
        ${FILES_LIB_DJINNI}
        ${FILES_LIB_GENERATED_SRC}
        ${FILES_LIB_HANDWRITTEN_SRC}
        ${FILES_LIB_HANDWRITTEN_SRC_FFMPEG}
        ${FILES_LIB_HANDWRITTEN_SRC_FOUNDATION}
        ${FILES_LIB_HANDWRITTEN_SRC_MEDIA}
        ${FILES_LIB_JNI}
        )

target_link_libraries(dcffmpeg-native
                       # 引入系统的动态库
                       log
                       android
                       GLESv2
                       EGL
                       z
                       OpenSLES
                       # 引入ffmpeg相关静态库
                       ijkffmpeg
                       )

 add_custom_command(
                    TARGET dcffmpeg-native
                    POST_BUILD
                    #COMMAND ${CMAKE_COMMAND} -E copy ${PATH_TO_JNI_LAYER}/deps/ffmpeg/${ANDROID_ABI}/libijkffmpeg.so ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}
                    COMMAND ${CMAKE_COMMAND} -E copy ${PATH_TO_LIBS_LAYER}/${ANDROID_ABI}/libijkffmpeg.so ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}

                    )

