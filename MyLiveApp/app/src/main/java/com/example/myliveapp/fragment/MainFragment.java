package com.example.myliveapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.example.myliveapp.R;

import java.util.ArrayList;

/**
 * Created by 郝颖超 on 2016/9/24.
 */

public class MainFragment extends Fragment implements View.OnClickListener{
    private ArrayList<Fragment> mList;
    private RadioButton mAttRB;
    private RadioButton mHotRB;
    private RadioButton mNesestRB;
    private ViewPager mViewPager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_view,container,false);
        mAttRB = (RadioButton) view.findViewById(R.id.main_rb_attention);
        mHotRB = (RadioButton) view.findViewById(R.id.main_rb_hot);
        mNesestRB = (RadioButton) view.findViewById(R.id.main_rb_newest);
        mViewPager = (ViewPager) view.findViewById(R.id.main_vp);
        mAttRB.setOnClickListener(this);
        mHotRB.setOnClickListener(this);
        mNesestRB.setOnClickListener(this);
        init();
        return view;
    }

    private void init() {
        mList = new ArrayList<Fragment>();
        AttentionFragment attention = new AttentionFragment();
        HotFragment hot = new HotFragment();
        NewestFragment newest = new NewestFragment();
        mList.add(attention);
        mList.add(hot);
        mList.add(newest);
       mViewPager.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
           @Override
           public Fragment getItem(int position) {
               return mList.get(position);
           }

           @Override
           public int getCount() {
               return mList.size();
           }
       });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position==0){
                    mAttRB.setChecked(true);
                }else if(position==1){
                    mHotRB.setChecked(true);
                }else if(position==2){
                    mNesestRB.setChecked(true);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_rb_attention :
                mViewPager.setCurrentItem(0);
                mAttRB.setChecked(true);
                break;
            case R.id.main_rb_hot :
                mViewPager.setCurrentItem(1);
                mHotRB.setChecked(true);
                break;
            case R.id.main_rb_newest :
                mViewPager.setCurrentItem(2);
                mNesestRB.setChecked(true);
                break;
            default:
                break;
        }
    }
}
