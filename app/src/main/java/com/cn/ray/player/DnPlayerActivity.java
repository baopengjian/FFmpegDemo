package com.cn.ray.player;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;

public class DnPlayerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private DNPlayer dnPlayer;
    private SurfaceView surfaceView;

    private SeekBar seekBar;

    private int progress;
    private boolean isTouch;
    private boolean isSeek;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager
                .LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_play);
        getPermission();
        surfaceView = findViewById(R.id.surface_view);
        dnPlayer = new DNPlayer();
        dnPlayer.setSurfaceView(surfaceView);
        String url = getIntent().getStringExtra("url");


        File file = new File(url);
        if (file.isFile() && !file.exists()) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        dnPlayer.setDataSource(url);
        dnPlayer.setOnPrepareListener(new DNPlayer.OnPrepareListener() {
            @Override
            public void onPrepare() {
                //获得时间
                int duration = dnPlayer.getDuration();
                //直播： 时间就是0
                if (duration != 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //显示进度条
                            seekBar.setVisibility(View.VISIBLE);
                        }
                    });
                }
                dnPlayer.start();
            }
        });

        dnPlayer.setOnErrorListener(new DNPlayer.OnErrorListener() {
            @Override
            public void onError(int error) {

            }
        });

        dnPlayer.setOnProgressListener(new DNPlayer.OnProgressListener() {

            @Override
            public void onProgress(final int progress2) {
                if (!isTouch) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int duration = dnPlayer.getDuration();
                            //如果是直播
                            if (duration != 0) {
                                if (isSeek) {
                                    isSeek = false;
                                    return;
                                }
                                //更新进度 计算比例
                                seekBar.setProgress(progress2 * 100 / duration);
                            }
                        }
                    });
                }
            }
        });

        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
    }


    public void start(View view) {
        dnPlayer.prepare();
    }

    public void stop(View view) {
          dnPlayer.stop();
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isTouch = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isSeek = true;
        isTouch = false;
        progress = dnPlayer.getDuration() * seekBar.getProgress() / 100;
        //进度调整
        dnPlayer.seek(progress);
    }

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private void getPermission() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }

            ActivityCompat.requestPermissions(this,
                    PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }

        while ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) != PackageManager.PERMISSION_GRANTED) {
        }
    }
}
