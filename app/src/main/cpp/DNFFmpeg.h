//
//

#ifndef PLAYER_DNFFMPEG_H
#define PLAYER_DNFFMPEG_H

#include "JavaCallHelper.h"
#include "AudioChannel.h"
#include "VideoChannel.h"

extern "C" {
#include <libavformat/avformat.h>
}


class DNFFmpeg {
public:
    DNFFmpeg(JavaCallHelper *callHelper, const char *dataSource);

    ~DNFFmpeg();

    void prepare();

    void _prepare();

    void start();

    void _start();

    void stop();

    void setRenderFrameCallback(RenderFrameCallback callback);

    int getDuration() {
        return duration;
    }

    void seek(int i);

public:
    char *dataSource;
    pthread_t pid;
    pthread_t pid_play = 0;
    pthread_t pid_stop = 0;

    pthread_mutex_t seekMutex;
    AVFormatContext *formatContext = 0;
    JavaCallHelper *callHelper = 0;
    AudioChannel *audioChannel = 0;
    VideoChannel *videoChannel = 0;
    RenderFrameCallback callback;
    bool isPlaying;
    int duration;
    bool isSeek = 0;
};


#endif //PLAYER_DNFFMPEG_H
