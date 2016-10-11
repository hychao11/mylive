package com.example.myliveapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myliveapp.R;
import com.example.myliveapp.net.HttpNet;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

public class ShareActivity extends BaseActivity  implements View.OnClickListener{
    private Button btCancle;
    private Button btCommit;
    private EditText et;
    private TextView tvContent;
    private ImageView iv;
    private String imageurl;
    private UMImage image;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        initView();
        sp=getSharedPreferences("name",MODE_PRIVATE);
        imageurl=sp.getString("imgUrl","");

        HttpNet.loadImg(imageurl,this,iv);
        tvContent.setText("聚范直播平台\n我在直播！你还不快来！");
        image = new UMImage(ShareActivity.this, imageurl);//网络图片


    }



    private void initView() {
        btCancle= (Button) findViewById(R.id.bt_menu_cancle);
        btCommit= (Button) findViewById(R.id.bt_menu_commit);
        tvContent= (TextView) findViewById(R.id.tv_menu);
        et= (EditText) findViewById(R.id.et_menu);
        iv= (ImageView) findViewById(R.id.iv_menu);
        btCancle.setOnClickListener(this);
        btCommit.setOnClickListener(this);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get( this ).onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_menu_cancle:
                Intent intent=new Intent(this,PushActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.bt_menu_commit:
                new ShareAction(ShareActivity.this).withText(tvContent.getText().toString())
                        .setPlatform(SHARE_MEDIA.QQ)
                        .setCallback(umShareListener)
                        .withMedia(image)
                        .withTitle(et.getText().toString())
                        .share();

                break;
        }
    }
    private UMShareListener umShareListener=new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(ShareActivity.this,platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            Intent intent2=new Intent(ShareActivity.this,PushActivity.class);
            startActivity(intent2);
            finish();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(ShareActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(ShareActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent2=new Intent(ShareActivity.this,PushActivity.class);
            startActivity(intent2);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
