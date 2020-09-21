//
//

#include <cstring>
#include <pthread.h>
#include "DNFFmpeg.h"
#include "macro.h"


void *task_prepare(void *args) {
    DNFFmpeg *ffmpeg = static_cast<DNFFmpeg *>(args);
    ffmpeg->_prepare();
    return 0;
}

DNFFmpeg::DNFFmpeg(JavaCallHelper *callHelper, const char *dataSource) {
    this->callHelper = callHelper;
    //防止 dataSource参数 指向的内存被释放
    //strlen 获得字符串的长度 不包括\0
    this->dataSource = new char[strlen(dataSource) + 1];
    strcpy(this->dataSource, dataSource);
}

DNFFmpeg::~DNFFmpeg() {
    //释放
    DELETE(dataSource);
    DELETE(callHelper);
}


void DNFFmpeg::prepare() {
    //创建一个线程
    pthread_create(&pid, 0, task_prepare, this);
}

void DNFFmpeg::_prepare() {
    // 初始化网络 让ffmpeg能够使用网络
    avformat_network_init();
    //1、打开媒体地址(文件地址、直播地址)
    // AVFormatContext  包含了 视频的 信息(宽、高等)
    formatContext = 0;
    //文件路径不对 手机没网
    int ret = avformat_open_input(&formatContext, dataSource, 0, 0);
    //ret不为0表示 打开媒体失败
    if (ret != 0) {
        LOGE("打开媒体失败:%s", av_err2str(ret));
        callHelper->onError(THREAD_CHILD, FFMPEG_CAN_NOT_OPEN_URL);
        return;
    }
    //2、查找媒体中的 音视频流 (给 contxt里的 streams等成员赋)
    ret = avformat_find_stream_info(formatContext, 0);
    // 小于0 则失败
    if (ret < 0) {
        LOGE("查找流失败:%s", av_err2str(ret));
        callHelper->onError(THREAD_CHILD, FFMPEG_CAN_NOT_FIND_STREAMS);
        return;
    }
    //nb_streams :几个流(几段视频/音频)
    for (int i = 0; i < formatContext->nb_streams; ++i) {
        //可能代表是一个视频 也可能代表是一个音频
        AVStream *stream = formatContext->streams[i];
        //包含了 解码 这段流 的各种参数信息(宽、高、码率、帧率)
        AVCodecParameters *codecpar = stream->codecpar;

        //无论视频还是音频都需要干的一些事情（获得解码器）
        // 1、通过 当前流 使用的 编码方式，查找解码器
        AVCodec *dec = avcodec_find_decoder(codecpar->codec_id);
        if (dec == NULL) {
            LOGE("查找解码器失败:%s", av_err2str(ret));
            callHelper->onError(THREAD_CHILD, FFMPEG_FIND_DECODER_FAIL);
            return;
        }
        //2、获得解码器上下文
        AVCodecContext *context = avcodec_alloc_context3(dec);
        if (context == NULL) {
            LOGE("创建解码上下文失败:%s", av_err2str(ret));
            callHelper->onError(THREAD_CHILD, FFMPEG_ALLOC_CODEC_CONTEXT_FAIL);
            return;
        }
        //3、设置上下文内的一些参数 (context->width)
//        context->width = codecpar->width;
//        context->height = codecpar->height;
        ret = avcodec_parameters_to_context(context, codecpar);
        //失败
        if (ret < 0) {
            LOGE("设置解码上下文参数失败:%s", av_err2str(ret));
            callHelper->onError(THREAD_CHILD, FFMPEG_CODEC_CONTEXT_PARAMETERS_FAIL);
            return;
        }
        // 4、打开解码器
        ret = avcodec_open2(context, dec, 0);
        if (ret != 0) {
            LOGE("打开解码器失败:%s", av_err2str(ret));
            callHelper->onError(THREAD_CHILD, FFMPEG_OPEN_DECODER_FAIL);
            return;
        }
        // 单位
        AVRational time_base = stream->time_base;
        //音频
        if (codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
            //0
            audioChannel = new AudioChannel(i,context,time_base);
        } else if (codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            //1
            //帧率： 单位时间内 需要显示多少个图像
            AVRational frame_rate = stream->avg_frame_rate;
            int fps = av_q2d(frame_rate);

            videoChannel = new VideoChannel(i,context,time_base,fps);
            videoChannel->setRenderFrameCallback(callback);
        }
    }
    //没有音视频  (很少见)
    if (!audioChannel && !videoChannel) {
        LOGE("没有音视频");
        callHelper->onError(THREAD_CHILD, FFMPEG_NOMEDIA);
        return;
    }
    // 准备完了 通知java 你随时可以开始播放
    callHelper->onPrepare(THREAD_CHILD);
};

void *play(void *args) {
    DNFFmpeg *ffmpeg = static_cast<DNFFmpeg *>(args);
    ffmpeg->_start();
    return 0;
}


void DNFFmpeg::start() {
    // 正在播放
    isPlaying = 1;

    //启动声音的解码与播放
    if (audioChannel){
        audioChannel->play();
    }
    if (videoChannel){
        videoChannel->setAudioChannel(audioChannel);
        videoChannel->play();
    }
    pthread_create(&pid_play, 0, play, this);
}

/**
 * 专门读取数据包
 */
void DNFFmpeg::_start() {
    //1、读取媒体数据包(音视频数据包)
    int ret;
    while (isPlaying) {
        AVPacket *packet = av_packet_alloc();
        ret = av_read_frame(formatContext, packet);
        //=0成功 其他:失败
        if (ret == 0) {
            //stream_index 这一个流的一个序号
            if (audioChannel && packet->stream_index == audioChannel->id) {
                audioChannel->packets.push(packet);
            } else if (videoChannel && packet->stream_index == videoChannel->id) {
                videoChannel->packets.push(packet);
            }
        } else if (ret == AVERROR_EOF) {
            //读取完成 但是可能还没播放完
        } else {
            //
        }

    }
};

void DNFFmpeg::setRenderFrameCallback(RenderFrameCallback callback){
    this->callback = callback;
}