package com.example.myliveapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myliveapp.R;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private Button btWeixin;
    private Button btQQ;
    private Button btPhone;
    private Button btWeibo;
    private UMShareAPI mShareAPI;
    private String imgUrl;
    private String name;
    private boolean isFirst;
    private SharedPreferences sp ;
    private SharedPreferences.Editor et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("name", MODE_PRIVATE);
        isFirst=sp.getBoolean("isFirst",false);
        if (isFirst){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_login);
        initView();

        et = sp.edit();
        mShareAPI = UMShareAPI.get(this);

    }



    private void initView() {
        btPhone = (Button) findViewById(R.id.bt_login_phone);
        btQQ = (Button) findViewById(R.id.bt_login_qq);
        btWeibo = (Button) findViewById(R.id.bt_login_weibo);
        btWeixin = (Button) findViewById(R.id.bt_login_weixin);
        btQQ.setOnClickListener(this);
        btPhone.setOnClickListener(this);
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            mShareAPI.getPlatformInfo(LoginActivity.this, platform, new UMAuthListener() {
                @Override
                public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                    imgUrl = map.get("profile_image_url");
                    name = map.get("screen_name");
                    et.putString("imgUrl", imgUrl);
                    et.putString("name", name);
                    et.putBoolean("isFirst",true);
                    et.commit();
                }
                @Override
                public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

                }
                @Override
                public void onCancel(SHARE_MEDIA share_media, int i) {

                }
            });

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login_qq:
                SHARE_MEDIA platform = SHARE_MEDIA.QQ;
                mShareAPI.doOauthVerify(this, platform, umAuthListener);
                break;
            case R.id.bt_login_phone:

                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }
}
