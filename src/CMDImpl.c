
#include <time.h>
#include <stdio.h>

#include "src_CmdRegister.h"

JNIEXPORT void JNICALL Java_src_CmdRegister_getLocalTime(JNIEnv * env, jobject obj, jobject ds){
    jclass cls = (*env)->GetObjectClass(env, obj);
    jfieldID fid;
    fid = (*env)->GetFieldID(env, cls, "time", "I");
    if (fid == 0) {
        return;
    }
    (*env)->SetIntField(env, obj, fid, time(NULL));
    fid = (*env)->GetFieldID(env, cls, "valid", "C");
    (*env)->SetCharField(env, obj, fid, 1);
}

JNIEXPORT jstring JNICALL Java_src_CmdRegister_getLocalOS(JNIEnv * env, jobject obj, jobject ds){
    jclass cls = (*env)->GetObjectClass(env, obj);
    jfieldID fid;
    fid = (*env)->GetFieldID(env, cls, "valid", "C");
    (*env)->SetCharField(env, obj, fid, 1);
    char msg[16] = "Linux";
    jstring result = (*env)->NewStringUTF(env,msg);
    return result;
}