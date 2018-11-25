#include <jni.h>
#include <string>
#include <sstream>
#include "aos_crc64.h"

extern "C" JNIEXPORT jstring

JNICALL
Java_com_example_ning_myapplication_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}


extern "C" JNIEXPORT jlong

JNICALL
Java_com_example_crclibrary_Crc64_aoscrc64combine(
        JNIEnv *env,
        jobject /* this */,jlong crc1, jlong crc2, jlong len2) {


    return aos_crc64_combine((uint64_t)crc1, (uint64_t)crc2,  (uint64_t)len2);
}

extern "C" JNIEXPORT jstring

JNICALL
Java_com_example_crclibrary_Crc64_aoscrc64(
        JNIEnv *env,
        jobject /* this */,jlong crc, jbyteArray buf, jlong len) {

    jbyte * array = env->GetByteArrayElements(buf, NULL);

     uint64_t  ret = aos_crc64((uint64_t)crc, array,  (uint64_t)len);
     std::ostringstream str;
    str<<ret;
    std::string retstr= str.str();
    env->ReleaseByteArrayElements(buf, array, 0);
    return env->NewStringUTF(retstr.c_str());


}



