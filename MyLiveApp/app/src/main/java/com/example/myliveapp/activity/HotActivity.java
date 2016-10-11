package com.example.myliveapp.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.myliveapp.R;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import static com.tencent.rtmp.TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN;

public class HotActivity extends BaseActivity {

    private TXLivePlayer mLivePlayer;
    private TXCloudVideoView mPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_hot);
        mPlayerView = (TXCloudVideoView) findViewById(R.id.hot_video_view);
        mLivePlayer = new TXLivePlayer(this);
        mLivePlayer.setPlayerView(mPlayerView);
        Intent intent = getIntent();
        String flvUrl = intent.getStringExtra("video");
        mLivePlayer.startPlay(flvUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV); //推荐FLV
        mLivePlayer.setRenderMode(RENDER_MODE_FULL_FILL_SCREEN);
        mLivePlayer.stopPlay(true);
        mLivePlayer.enableHardwareDecode(true);
        mLivePlayer.startPlay(flvUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mLivePlayer.stopPlay(true);
        mPlayerView.onDestroy();
    }
}
