package com.example.myliveapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.myliveapp.R;
import com.example.myliveapp.net.HttpNet;
import com.example.myliveapp.net.ResponseListener;

/**
 * Created by 张宁 on 2016/9/24.
 */

public class AttentionFragment extends Fragment {
    private ViewPager mViewPager;
    private ListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attention_fragment,container,false);
        init();
        return view;
    }

    private void init() {
        String url = "http://live.jufan.tv/cgi/hall/get?sign=CE99DAEAF121B2AE26E53026D949351C42BB4A90&r=cdidsfkkq&page=0&type=hot&userid=500146940";
        HttpNet.getObjectApi(url, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getActivity(), "错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String s) {
                Toast.makeText(getActivity(), "正确", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


}
