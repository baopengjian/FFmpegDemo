package com.cn.ray.player;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Surface;

/**
 * 提供java 进行播放 停止 等函数
 */
public class DNPlayer implements SurfaceHolder.Callback {
    static {
        System.loadLibrary("native-lib");
    }

    private String dataSource;
    private SurfaceHolder holder;

    private OnPrepareListener listener;
    private OnErrorListener onErrorListener;
    private OnProgressListener onProgressListener;

    /**
     * 让使用 设置播放的文件 或者 直播地址
     */
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 准备好要播放的视频
     */
    public void prepare() {
        native_prepare(dataSource);
    }

    /**
     * 开始播放
     */
    public void start() {
        native_start();
    }

    /**
     * native 回调给java 播放进去的
     */
    public void onProgress(int progress) {
        if (null != onProgressListener) {
            onProgressListener.onProgress(progress);
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        native_stop();
    }

    public void release() {
        holder.removeCallback(this);
        native_release();
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        if (null != holder) {
            holder.removeCallback(this);
        }
        holder = surfaceView.getHolder();
        holder.addCallback(this);
    }

    public void onError(int errorCode) {
        if (null == onErrorListener) {
            onErrorListener.onError(errorCode);
        }
        Log.e("FFmpeg", "onError" + errorCode);
    }

    public void onPrepare() {
        if (null != listener) {
            listener.onPrepare();
        }
    }

    public void setOnPrepareListener(OnPrepareListener listener) {
        this.listener = listener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public interface OnPrepareListener {
        void onPrepare();
    }

    public interface OnErrorListener {
        void onError(int error);
    }

    public interface OnProgressListener {
        void onProgress(int progress);
    }

    /**
     * 画布创建好了
     *
     * @param surfaceHolder
     */
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    /**
     * 画布发生了变化（横竖屏切换、按Home键都会回调这个函数）
     *
     * @param surfaceHolder
     * @param i
     * @param i1
     * @param i2
     */
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        native_setSurface(surfaceHolder.getSurface());
    }

    /**
     * 销毁画布 （按了home/退出应用/..）
     *
     * @param surfaceHolder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    public int getDuration() {
        return native_getDuration();
    }

    public void seek(final int progress) {
        new Thread() {
            @Override
            public void run() {
                native_seek(progress);
            }
        }.start();
    }

    native void native_prepare(String dataSource);

    native void native_start();

    native void native_stop();

    native void native_release();

    native void native_setSurface(Surface suface);

    private native int native_getDuration();

    private native void native_seek(int progress);
}
