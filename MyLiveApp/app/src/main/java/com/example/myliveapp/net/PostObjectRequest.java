package com.example.myliveapp.net;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by 郝颖超 on 2016/9/3.
 */
public class PostObjectRequest extends Request<String> {
    /**
     * 正确数据的时候回掉用
     */
    private ResponseListener mListener ;
    /*用来解析 json 用的*/
    //private Gson mGson ;
    /*在用 gson 解析 json 数据的时候，需要用到这个参数*/
   // private Type mClazz ;
    /*请求 数据通过参数的形式传入*/
    private Map<String,String> mParams;
    //需要传入参数，并且请求方式不能再为 get，改为 post
    public PostObjectRequest(String url, Map<String,String> params, ResponseListener listener) {
        super(Method.POST, url, listener);
        this.mListener = listener ;
        setShouldCache(false);
        mParams = params ;
    }

    /**
     * 这里开始解析数据
     * @param response Response from the network
     * @return
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String result ;
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.v("zgy", "====jsonString===" + jsonString);
            result = jsonString;
            return Response.success(result,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String s) {
        mListener.onResponse(s);
    }

    /**
     * 回调正确的数据
     *
     */
//    @Override
//    protected void deliverResponse(T response) {
//        mListener.onResponse(response);
//    }

    //关键代码就在这里，在 Volley 的网络操作中，如果判断请求方式为 Post 则会通过此方法来获取 param，所以在这里返回我们需要的参数，
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }
}
