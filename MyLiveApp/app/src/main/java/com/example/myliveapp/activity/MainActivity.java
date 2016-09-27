package com.example.myliveapp.activity;

import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        rbHome = (RadioButton) findViewById(R.id.main_home);
        rbHome = (RadioButton) findViewById(R.id.main_personal);
        ivPush = (ImageView) findViewById(R.id.main_push);
        ivPush.setOnClickListener(this);

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
}
