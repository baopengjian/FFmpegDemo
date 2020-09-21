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

    /**
     * 让使用 设置播放的文件 或者 直播地址
     */
    public void setDataSource(String dataSource){
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
    public void start(){
        native_start();
    }

    /**
     * 停止播放
     */
    public void stop(){
        native_stop();
    }

    public void release(){
        holder.removeCallback(this);
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        holder = surfaceView.getHolder();
        holder.addCallback(this);
    }

    public void onError(int errorCode){
        Log.e("FFmpeg","onError"+errorCode);
    }

    public void onPrepare(){
        if(null != listener){
            listener.onPrepare();
        }
    }

    public void setOnPrepareListener(OnPrepareListener listener){
        this.listener = listener;
    }

    public interface OnPrepareListener{
        void onPrepare();
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
       native_setSurface(surfaceHolder.getSurface()) ;
    }

    /**
     * 销毁画布 （按了home/退出应用/..）
     *
     * @param surfaceHolder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    native void native_prepare(String dataSource);

    native void native_start();

    native void native_stop();

    native void native_setSurface(Surface suface);
}
