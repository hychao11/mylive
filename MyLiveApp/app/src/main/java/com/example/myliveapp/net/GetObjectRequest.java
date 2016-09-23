package com.example.myliveapp.net;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2016/9/7.
 */

public class GetObjectRequest extends Request<String> {

    private ResponseListener mListener ;

    public GetObjectRequest(String url, ResponseListener listener) {
        super(Method.GET,url, listener);
        setShouldCache(false);
        this.mListener = listener;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            String result ;
            String jsonString =
                    new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
            Log.v("zgy", "====jsonString===" + jsonString);
            result = jsonString;
            return Response.success(result,
                    HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String s) {
        mListener.onResponse(s);
    }
}
