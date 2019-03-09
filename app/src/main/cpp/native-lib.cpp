#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_tamaraoldham_smarttoastv2_MainMenu_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
