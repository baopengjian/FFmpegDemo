//
//

#ifndef PLAYER_AUDIOCHANNEL_H
#define PLAYER_AUDIOCHANNEL_H


#include "BaseChannel.h"
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

extern "C" {
#include <libswresample/swresample.h>
};

class AudioChannel : public BaseChannel {
public:
    AudioChannel(int id,JavaCallHelper *javaCallHelper, AVCodecContext *avCodecContext, AVRational time_base);

    ~AudioChannel();

    void play();

    void pause();

    void resume();

    void stop();

    void decode();

    void _play();

    int getPcm();

public:
    uint8_t *data = 0;
    int out_channels;
    int out_samplesize;
    int out_sample_rate;
private:
    pthread_t pid_audio_decode;
    pthread_t pid_audio_play;


    /**
     * OpenSL ES
     */
    // 引擎与引擎接口
    SLObjectItf engineObject = 0;
    SLEngineItf engineInterface = 0;
    //混音器
    SLObjectItf outputMixObject = 0;
    //播放器
    SLObjectItf bqPlayerObject = 0;
    //播放器接口
    SLPlayItf bqPlayerInterface = 0;
    //队列结构
    SLAndroidSimpleBufferQueueItf bqPlayerBufferQueueInterface = 0;


    //重采样
    SwrContext *swrContext = 0;

    pthread_cond_t cond;
    pthread_mutex_t mutex;
};


#endif //PLAYER_AUDIOCHANNEL_H
