package com.example.myliveapp.net;


import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.Map;

/**
 * Created by 郝颖超 on 2016/9/3.
 */
public class HttpNet {
    public static Bitmap bitmap;
    public static void postObjectMinongApi(String url, Map<String,String> param, ResponseListener listener){

        Request request = new PostObjectRequest(url,param,listener);
        VolleyUtil.getRequestQueue().add(request) ;
    }

    public static void getObjectApi(String url, ResponseListener listener){

        Request request = new GetObjectRequest(url,listener);
        VolleyUtil.getRequestQueue().add(request) ;
    }


    public static void loadImg(String url, Context context, final ImageView imageView){
        RequestQueue mQueue = VolleyUtil.getRequestQueue();
        ImageRequest imageRequest = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(imageRequest);
    }
    public static void getImg(String url, final Context context){

        RequestQueue mQueue = VolleyUtil.getRequestQueue();
        ImageRequest imageRequest = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                       bitmap = response;
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"参数错误",Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(imageRequest);
    }
}
