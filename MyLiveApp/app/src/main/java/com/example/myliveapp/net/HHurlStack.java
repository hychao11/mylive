package com.example.myliveapp.net;

import com.android.volley.toolbox.HurlStack;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 郝颖超 on 2016/9/3.
 */
public class HHurlStack extends HurlStack {
    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        if (url.toString().contains("https")) {
            HTTPSTrustManager.allowAllSSL();
        }
        return super.createConnection(url);
    }
}
