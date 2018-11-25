//
// Created by admin on 2018/9/19.
//

#include <jni.h>
#include "../cpp/wltrace.h"
#ifndef _Included_cutils_CUtilTestVar
#define _Included_cutils_CUtilTestVar
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL
Java_com_example_curllibrary_Curl_trace(
        JNIEnv *env,
        jobject /* this */, jstring url, jstring ua) {
    const char * a = env-> GetStringUTFChars(url,0);
    const char * b = env->GetStringUTFChars(ua,0);
    return trace(a,b);
}



JNIEXPORT jstring JNICALL
Java_com_example_curllibrary_Curl_tracegetres(JNIEnv *env,  jobject /* this */, jint code) {
    char* a = trace_getres(static_cast<CURLcode>(code));
    return env->NewStringUTF(a);
}

JNIEXPORT void JNICALL
Java_com_example_curllibrary_Curl_tracereset(
        JNIEnv *env,
        jobject /* this */) {
    trace_reset();
}

JNIEXPORT jstring JNICALL
Java_com_example_curllibrary_Curl_getfinalip(JNIEnv *env,  jobject /* this */) {
    char* a = trace_get_final_ip();
    if(!a) {
        a = "";
    }
    return env->NewStringUTF(a);
}





#ifdef __cplusplus
}
#endif
#endif

