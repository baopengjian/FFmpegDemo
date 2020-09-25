//
//

#ifndef PLAYER_JAVACALLHELPER_H
#define PLAYER_JAVACALLHELPER_H


#include <jni.h>

class JavaCallHelper {
public:
    JavaCallHelper(JavaVM *vm,JNIEnv* env,jobject instace);
    ~JavaCallHelper();
    //回调java
    void  onError(int thread,int errorCode);
    void onPrepare(int thread);
    void onProgress(int thread, int progress);
private:
    JavaVM *vm;
    JNIEnv *env;
    jobject  instance;
    jmethodID onErrorId;
    jmethodID onPrepareId;
    jmethodID jmid_progress;


};


#endif //PLAYER_JAVACALLHELPER_H
