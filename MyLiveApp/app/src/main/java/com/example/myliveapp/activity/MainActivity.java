package com.example.myliveapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.myliveapp.R;
import com.tencent.rtmp.TXLivePusher;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int[] sdkver = TXLivePusher.getSDKVersion();
        if (sdkver != null && sdkver.length >= 3) {
            Log.d("rtmpsdk","rtmp sdk version is:" + sdkver[0] + "." + sdkver[1] + "." + sdkver[2]);
        }
    }
}
