# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
add_library( # Sets the name of the library.
             nativeslib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/wltrace.cpp
             src/main/jni/nativeslib.cpp

             )

# libcurl.
add_library(curl-lib STATIC IMPORTED)
set_property(TARGET curl-lib PROPERTY IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libs/libcurl/${ANDROID_ABI}/libcurl.a)
# Location of header files.
include_directories(${CMAKE_SOURCE_DIR}/libs/libcurl/include)
find_library(curl-lib curl)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              z
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       nativeslib

                       z
                       curl-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )