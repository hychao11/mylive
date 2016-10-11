package com.example.myliveapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.myliveapp.R;
import com.example.myliveapp.activity.HotActivity;
import com.example.myliveapp.adapter.NewsAdapter;
import com.example.myliveapp.bean.ImageList;
import com.example.myliveapp.net.HttpNet;
import com.example.myliveapp.net.ResponseListener;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import java.util.List;

/**
 * Created by 张宁 on 2016/9/24.
 */

public class NewestFragment extends Fragment {
    private PullToRefreshGridView mGridView;
    private List<ImageList.ContentBean.ListBean> mList;
    private NewsAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.newest_fragment,container,false);
        mGridView = (PullToRefreshGridView) view.findViewById(R.id.pull_grid);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String url = "http://live.jufan.tv/cgi/hall/get?sign=4E28973E05FB19007E8E0D786AC05C1BB7436F09&userid=500056489&type=new&r=lggt&page=0";
        HttpNet.getObjectApi(url, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getActivity(),"错误",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(String s) {
                Toast.makeText(getActivity(),"正确",Toast.LENGTH_LONG).show();
                Gson gson = new Gson();
                ImageList img = gson.fromJson(s,ImageList.class);
                ImageList.ContentBean imgContent = img.getContent();
                mList = imgContent.getList();
                mAdapter = new NewsAdapter(getActivity(),mList);
                mGridView.setAdapter(mAdapter);
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), HotActivity.class);
                intent.putExtra("video",mList.get(position).getVideo());
                startActivity(intent);
            }
        });
    }


}
