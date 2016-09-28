package com.example.myliveapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.myliveapp.R;

public class WelcomeActivity extends BaseActivity {
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Intent intent=new Intent(WelcomeActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mHandler.sendEmptyMessageDelayed(1,2000);
    }
}
