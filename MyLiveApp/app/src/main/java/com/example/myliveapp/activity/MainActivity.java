package com.example.myliveapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       initView();
    }

    private void initView() {
        rbHome = (RadioButton) findViewById(R.id.main_home);
        rbPersonal = (RadioButton) findViewById(R.id.main_personal);
        ivPush = (ImageView) findViewById(R.id.main_push);
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

    
}
