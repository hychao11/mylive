package com.example.myliveapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.myliveapp.R;
import com.example.myliveapp.util.ManagerNet;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private RadioButton rbHome;
    private RadioButton rbPersonal;
    private ImageView ivPush;
    private MyApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        app= (MyApp) getApplication();
        rbHome = (RadioButton) findViewById(R.id.main_home);
        rbPersonal = (RadioButton) findViewById(R.id.main_personal);
        ivPush = (ImageView) findViewById(R.id.main_push);
        ivPush.setOnClickListener(this);
        rbHome.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_push:
                if (ManagerNet.isNetworkConnected(this)) {
                    Intent intent = new Intent(this, RoomStartActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(this,"网络不给力",Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            dialog();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void dialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("确定要退出吗");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                app.close();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
