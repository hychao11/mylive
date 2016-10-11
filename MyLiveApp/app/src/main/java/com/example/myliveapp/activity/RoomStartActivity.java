package com.example.myliveapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myliveapp.R;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

public class RoomStartActivity extends BaseActivity implements View.OnClickListener{
    private Button btClose;
    private TextView tvStart;
    private UMShareAPI  mShareAPI;
    private SHARE_MEDIA platform;
    private String imageurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_share);

        initView();
    }

    private void initView() {

        btClose= (Button) findViewById(R.id.bt_close);
        tvStart= (TextView) findViewById(R.id.tv_start);

        btClose.setOnClickListener(this);
        tvStart.setOnClickListener(this);
        mShareAPI = UMShareAPI.get( this );

        platform = SHARE_MEDIA.QQ;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).HandleQQError(RoomStartActivity.this,requestCode,umAuthListener);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            imageurl=data.get("profile_image_url");
            Intent intent=new Intent(RoomStartActivity.this,ShareActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText( getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText( getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_start:
                mShareAPI.getPlatformInfo(RoomStartActivity.this, SHARE_MEDIA.QQ, umAuthListener);
                break;
            case R.id.bt_close:
                finish();
                break;

        }
    }



}
