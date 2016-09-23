package com.example.myliveapp.net;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by 郝颖超 on 2016/9/3.
 */
public class VolleyUtil {
    private static RequestQueue mRequestQueue ;

    public static void initialize(Context context){
        if (mRequestQueue == null){
            synchronized (VolleyUtil.class){
                if (mRequestQueue == null){
                    mRequestQueue = Volley.newRequestQueue(context,new HHurlStack()) ;
                }
            }
        }
        mRequestQueue.start();
    }

    public static RequestQueue getRequestQueue(){
        if (mRequestQueue == null)
            throw new RuntimeException("请先初始化mRequestQueue") ;
        return mRequestQueue ;
    }
}
