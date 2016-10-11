package com.example.myliveapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.myliveapp.R;
import com.example.myliveapp.activity.HotActivity;
import com.example.myliveapp.adapter.MyAdapter;
import com.example.myliveapp.bean.ViewPage;
import com.example.myliveapp.net.HttpNet;
import com.example.myliveapp.net.ResponseListener;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 张宁 on 2016/9/24.
 */

public class HotFragment extends Fragment {
    private PullToRefreshListView mPullList;
    private List<ViewPage.ContentBean.ListBean> mList;
    private List<ViewPage.ContentBean.ListBean> mList1 = new ArrayList<ViewPage.ContentBean.ListBean>();
    private List<ViewPage.ContentBean.BannerBean> mBannerList;
    private ILoadingLayout startLayout;
    private int typeNum;
    private MyAdapter adapter;
    private int index;
    //更新适配器
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                init();
                adapter.notifyDataSetChanged();
            }
            mPullList.onRefreshComplete();
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hot_fragment,container,false);
        mPullList = (PullToRefreshListView) view.findViewById(R.id.pull);
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
                Gson g = new Gson();
                ViewPage vp = g.fromJson(s,ViewPage.class);
                ViewPage.ContentBean  cb = vp.getContent();
                mList = cb.getList();
                mBannerList =  cb.getBanner();
                adapter =  new MyAdapter(getActivity(),mList,mBannerList);
                mPullList.setAdapter(adapter);
            }
        });
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPullList.setMode(PullToRefreshBase.Mode.BOTH);
        //设置刷新时显示的文本
        startLayout = mPullList.getLoadingLayoutProxy(true, false);
        startLayout.setPullLabel("正在下拉刷新...");
        startLayout.setRefreshingLabel("正在加载中...");
        startLayout.setReleaseLabel("放开以刷新");
        ILoadingLayout endLayout = mPullList.getLoadingLayoutProxy(false, true);
        endLayout.setPullLabel("正在上拉刷新...");
        endLayout.setRefreshingLabel("正在加载中...");
        endLayout.setReleaseLabel("放开以刷新");
        //加载数据 适配
        init();

        //刷新和加载
        mPullList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadMore();
            }
        });
        mPullList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), HotActivity.class);
                intent.putExtra("video",mList.get(position).getVideo());
                startActivity(intent);
            }
        });
    }
    //刷新
    private void refresh() {
        index = 0;
        mHandler.sendEmptyMessageDelayed(1,1000);

    }
    //加载更多
    private void loadMore() {
        String.valueOf(++index);
        mList1.addAll(mList);
        mHandler.sendEmptyMessageDelayed(1, 2000);
    }

}
