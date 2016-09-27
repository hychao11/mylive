package com.example.myliveapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.myliveapp.R;

public class RoomFinishActivity extends BaseActivity {
    private TextView tvBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_finish);
        tvBack= (TextView) findViewById(R.id.tv_back_room);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
