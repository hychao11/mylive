package com.example.myliveapp.activity;

import android.app.Activity;
import android.app.Application;

import com.example.myliveapp.net.VolleyUtil;
import com.umeng.socialize.PlatformConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 郝颖超 on 2016/9/26.
 */

public class MyApp extends Application {
    public List<Activity> list;
    @Override
    public void onCreate() {

        super.onCreate();
        VolleyUtil.initialize(getApplicationContext());
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
        list=new ArrayList<Activity>();
    }
    public void close(){
        for (Activity a:list){
            a.finish();
        }
        System.exit(0);
    }
}
