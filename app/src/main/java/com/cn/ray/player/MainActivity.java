package com.cn.ray.player;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;

/**
 * 测试url
 * http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8
 http://ivi.bupt.edu.cn/hls/cctv3hd.m3u8
 http://ivi.bupt.edu.cn/hls/cctv5hd.m3u8
 http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8
 */
public class MainActivity extends AppCompatActivity {

    private DNPlayer dnPlayer;

    private SurfaceView surfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.surface_view);
        dnPlayer = new DNPlayer();
        dnPlayer.setSurfaceView(surfaceView);
        String url = null;

         url = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";
        dnPlayer.setDataSource(url);
        dnPlayer.setOnPrepareListener(new DNPlayer.OnPrepareListener() {
            @Override
            public void onPrepare() {
                dnPlayer.start();
            }
        });
    }


    public void start(View view) {
        dnPlayer.prepare();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dnPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dnPlayer.release();
    }
}
