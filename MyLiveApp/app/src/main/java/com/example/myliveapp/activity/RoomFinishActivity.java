package com.example.myliveapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.myliveapp.R;

import java.text.DecimalFormat;

public class RoomFinishActivity extends BaseActivity {
    private TextView tvBack;
    private TextView tvTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_finish);
        tvBack= (TextView) findViewById(R.id.tv_back_room);
        tvTime= (TextView) findViewById(R.id.tv_time);
        long time=getIntent().getLongExtra("time",0);

        String str=formatDuring(time);
        tvTime.setText("直播时长："+str);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public static String formatDuring(long mss) {
        DecimalFormat df  = new DecimalFormat("00");
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        return  df.format(hours) + " : " + df.format(minutes) + " : "
                + df.format(seconds);
    }
}
