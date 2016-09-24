package com.example.myliveapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.example.myliveapp.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
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
        rbHome= (RadioButton) findViewById(R.id.main_home);
        rbHome= (RadioButton) findViewById(R.id.main_personal);
        ivPush= (ImageView) findViewById(R.id.main_push);
        ivPush.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_push:
                Intent intent=new Intent(this,PushActivity.class);
                startActivity(intent);
                break;

        }
    }
}
