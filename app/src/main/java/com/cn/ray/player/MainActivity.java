package com.cn.ray.player;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试url
 * <p>
 * 本地文件有可能申请权限失败，具体看Android高版本申请读取文件权限
 * <p>
 * TODO 1 停止功能
 * 2 卡顿
 * 3 停止后退出闪退
 * 4 重复start，出现多次播放（屏幕闪烁）
 */
public class MainActivity extends AppCompatActivity {

    public static final String[][] SOURCES = {{"CCTV1", "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8"},
            {"CCTV2", "http://ivi.bupt.edu.cn/hls/cctv2hd.m3u8"},
            {"CCTV3", "http://ivi.bupt.edu.cn/hls/cctv3hd.m3u8"},
            {"CCTV6", "http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8"},
            {"CCTV10", "http://ivi.bupt.edu.cn/hls/cctv10hd.m3u8"}
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lv = findViewById(R.id.lv);

        List<Map<String, String>> list = getData();

        lv.setAdapter(new CommonAdapter<Map<String, String>>(MainActivity.this, list, R.layout.item) {
            @Override
            public void convert(ViewHolder helper, final Map<String, String> item) {
                ((TextView) helper.getConvertView()).setText(item.get("name"));
                helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, DnPlayerActivity.class);
                        intent.putExtra("url", item.get("url"));
                        startActivity(intent);
                    }
                });
            }
        });

    }


    public List<Map<String, String>> getData() {
        List<Map<String, String>> list = new ArrayList<>();
        for (String[] s : SOURCES) {
            Map<String, String> map = new HashMap<>();
            map.put("name", s[0]);
            map.put("url", s[1]);
            list.add(map);
        }

        String file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/dx_test.mp4";
        Map<String, String> map = new HashMap<>();
        map.put("name", "读取本地/test/dx_test.mp4");
        map.put("url", file);
        list.add(map);

        return list;
    }
}
