package com.cn.ray.player;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试url
 * <p>
 *
 *
 *
 */
public class MainActivity extends AppCompatActivity {

    public static final String[][] SOURCES = {{"CCTV1", "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8"}, {"CCTV3", "http://ivi.bupt.edu.cn/hls/cctv3hd.m3u8"},
            {"CCTV5", "http://ivi.bupt.edu.cn/hls/cctv5hd.m3u8"}, {"CCTV6", "http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8"}};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lv = findViewById(R.id.lv);

        List<Map<String, String>> list = getData();

        lv.setAdapter(new CommonAdapter<Map<String, String>>(MainActivity.this,list,R.layout.item ) {
            @Override
            public void convert(ViewHolder helper, final Map<String, String> item) {
                ((TextView) helper.getConvertView()).setText(item.get("name"));
                helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, DnPlayerActivity.class);
                        intent.putExtra("url",item.get("url"));
                        startActivity(intent);
                    }
                });
            }
        });

    }


    public List<Map<String,String>> getData() {
        List<Map<String,String>> list = new ArrayList<>();
        for(String[] s:SOURCES){
            Map<String,String> map = new HashMap<>();
            map.put("name",s[0]);
            map.put("url",s[1]);
            list.add(map);
        }
        return list;
    }
}
