package com.example.myliveapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
<<<<<<< HEAD
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
=======
import android.view.KeyEvent;
>>>>>>> 2859e0960af747ea9cbe1c23c3bb14599bdb6d75
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.myliveapp.R;
import com.example.myliveapp.fragment.MainFragment;
import com.example.myliveapp.fragment.PersonalFragment;
import com.example.myliveapp.util.ManagerNet;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    private RadioButton rbHome;
    private RadioButton rbPersonal;

    private MainFragment mains;
    private PersonalFragment personal;
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
<<<<<<< HEAD
        mains = new MainFragment();
        personal = new PersonalFragment();
        rbHome.setOnClickListener(this);
        rbPersonal.setOnClickListener(this);
        ivPush.setOnClickListener(this);
        getSupportFragmentManager().beginTransaction().add(R.id.rl_content, mains)
                .add(R.id.rl_content, personal).commit();
        hide("mains");
    }
    private void hide(String str) {
        FragmentManager fragment = getSupportFragmentManager();
        FragmentTransaction transaction = fragment.beginTransaction();
        if(str.equals("mains")){
            transaction.show(mains).hide(personal).commit();
        }else if(str.equals("personal")){
            transaction.show(personal).hide(mains).commit();
        }
=======
        ivPush.setOnClickListener(this);
        rbHome.setOnClickListener(this);

>>>>>>> 2859e0960af747ea9cbe1c23c3bb14599bdb6d75
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
            case R.id.main_home:
                rbHome.setChecked(true);
                rbPersonal.setChecked(false);
                hide("mains");
                break;
            case R.id.main_personal:
                rbPersonal.setChecked(true);
                rbHome.setChecked(false);
                hide("personal");
                break;

        }
    }

<<<<<<< HEAD
    
=======
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
>>>>>>> 2859e0960af747ea9cbe1c23c3bb14599bdb6d75
}
