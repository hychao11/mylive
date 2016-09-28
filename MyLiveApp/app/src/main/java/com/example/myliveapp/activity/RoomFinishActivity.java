package com.example.myliveapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.myliveapp.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

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
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String str=sdf.format(time);
        tvTime.setText("直播时长："+str);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
