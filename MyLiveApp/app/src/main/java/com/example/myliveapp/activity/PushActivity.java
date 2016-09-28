package com.example.myliveapp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myliveapp.R;
import com.example.myliveapp.net.HttpNet;
import com.example.myliveapp.view.CircleImageView;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.text.SimpleDateFormat;
import java.util.Date;


public class PushActivity extends BaseActivity implements View.OnClickListener, ITXLivePushListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = PushActivity.class.getSimpleName();

    private TXLivePushConfig mLivePushConfig;
    private TXLivePusher mLivePusher;
    private TXCloudVideoView mCaptureView;

    private LinearLayout mBitrateLayout;
    private LinearLayout mFaceBeautyLayout;
    private SeekBar mBeautySeekBar;
    private SeekBar mWhiteningSeekBar;
    private ScrollView mScrollView;
    private RadioGroup mRadioGroupBitrate;
    private Button mBtnFuction;
    private TextView mLogViewStatus;
    private TextView mLogViewEvent;
    private boolean mVideoPublish;
    private boolean mFrontCamera = true;
    private boolean mHWVideoEncode = false;
    private boolean mFlashTurnOn = false;
    private boolean mTouchFocus = true;
    private boolean mHWListConfirmDialogResult = false;
    private int mBeautyLevel = 0;
    private int mWhiteningLevel = 0;
    private FrameLayout flayout;
    private RelativeLayout rlNum;
    private TextView tvNum;
    StringBuffer mLogMsg = new StringBuffer("");
    private Bitmap mBitmap;
    private final int mLogMsgLenLimit = 3000;
    private int num = 3;
    private CircleImageView ivHead;
    private TextView tvName;
    private SharedPreferences sp;
    private Button btClose;
    private Date start;
    private Date end;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            //播放部分
            if (num > 0) {
                tvNum.setText(--num + "");
                mHandler.sendEmptyMessageDelayed(1, 1000);
            }
            if (num == 0) {
                FixOrAdjustBitrate();  //根据设置确定是“固定”还是“自动”码率
                mVideoPublish = true;
                rlNum.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }else{
            mVideoPublish = true;
        }
        sp=getSharedPreferences("name",MODE_PRIVATE);
        initView();

        mHandler.sendEmptyMessageDelayed(1, 1000);
        //码率自适应部分
        mBitrateLayout = (LinearLayout) findViewById(R.id.layoutBitrate);
        mRadioGroupBitrate = (RadioGroup) findViewById(R.id.resolutionRadioGroup);
        mRadioGroupBitrate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FixOrAdjustBitrate();
                mBitrateLayout.setVisibility(View.GONE);
            }
        });

        //手动对焦/自动对焦
        flayout.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 123:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    mVideoPublish = true;
                } else {
                }

                break;
        }
    }

    private void initView() {
        ivHead= (CircleImageView) findViewById(R.id.iv_head);
        tvName= (TextView) findViewById(R.id.tv_name);
        String imgUrl=sp.getString("imgUrl","");
        String name=sp.getString("name","");
        tvName.setText(name);
        HttpNet.loadImg(imgUrl,this,ivHead);
        flayout = (FrameLayout) findViewById(R.id.activity_push);
        mBtnFuction = (Button) findViewById(R.id.btnFuction);
        tvNum = (TextView) findViewById(R.id.push_tv_num);
        rlNum = (RelativeLayout) findViewById(R.id.push_rl_num);
        btClose= (Button) findViewById(R.id.btnClose);
        btClose.setOnClickListener(this);
        mBtnFuction.setOnClickListener(this);
        mLivePusher = new TXLivePusher(this);
        mLivePushConfig = new TXLivePushConfig();
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.watermark);
        mCaptureView = (TXCloudVideoView) findViewById(R.id.video_view);
        mLogViewEvent = (TextView) findViewById(R.id.logViewEvent);
        mLogViewStatus = (TextView) findViewById(R.id.logViewStatus);
        mVideoPublish = false;
        mLogViewStatus.setVisibility(View.GONE);
        mLogViewStatus.setMovementMethod(new ScrollingMovementMethod());
        mLogViewEvent.setMovementMethod(new ScrollingMovementMethod());
        mScrollView = (ScrollView) findViewById(R.id.scrollview);
        mScrollView.setVisibility(View.GONE);
        //美颜部分
        mFaceBeautyLayout = (LinearLayout) findViewById(R.id.layoutFaceBeauty);
        mBeautySeekBar = (SeekBar) findViewById(R.id.beauty_seekbar);
        mBeautySeekBar.setOnSeekBarChangeListener(this);

        mWhiteningSeekBar = (SeekBar) findViewById(R.id.whitening_seekbar);
        mWhiteningSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mVideoPublish) {
            startPublishRtmp();
        }
        if (mCaptureView != null) {
            mCaptureView.onResume();
        }

        if (mVideoPublish && !mLivePusher.isPushing()) {
            mLivePusher.startCameraPreview(mCaptureView);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mCaptureView != null) {
            mCaptureView.onPause();
        }

        mLivePusher.stopCameraPreview(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPublishRtmp();
        if (mCaptureView != null) {
            mCaptureView.onDestroy();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFuction:
                showPopWindow();
                break;
            case R.id.beauty:
                beauty();
                break;
            case R.id.change:
                change();
                break;
            case R.id.light:
                light();
                break;
            case R.id.btnClose:
                stopPublishRtmp();
                Intent intent=new Intent(this,RoomFinishActivity.class);
                intent.putExtra("time",(end.getTime()-start.getTime()));
                startActivity(intent);
                finish();
                break;

            default:
                mFaceBeautyLayout.setVisibility(View.GONE);
                mBitrateLayout.setVisibility(View.GONE);
        }
    }

    //美颜
    private void beauty() {
        mFaceBeautyLayout.setVisibility(mFaceBeautyLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    //镜头切换
    private void change() {
        mFrontCamera = !mFrontCamera;

        if (mLivePusher.isPushing()) {
            mLivePusher.switchCamera();
        } else {
            mLivePushConfig.setFrontCamera(mFrontCamera);
        }
    }

    //闪光灯
    private void light() {
        if (mLivePusher == null) {
            return;
        }

        mFlashTurnOn = !mFlashTurnOn;
        if (!mLivePusher.turnOnFlashLight(mFlashTurnOn)) {
            Toast.makeText(PushActivity.this,
                    "打开闪光灯失败（1）大部分前置摄像头并不支持闪光灯（2）该接口需要在启动预览之后调用", Toast.LENGTH_SHORT).show();
        }

    }

    private void showPopWindow() {
        View v = LayoutInflater.from(this).inflate(R.layout.camera_view, null);
        PopupWindow mPopWindow = new PopupWindow(v);
        mPopWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //获取自身的长宽高
        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupHeight = v.getMeasuredHeight();
        int popupWidth = v.getMeasuredWidth();
        LinearLayout line1 = (LinearLayout) v.findViewById(R.id.beauty);
        LinearLayout line2 = (LinearLayout) v.findViewById(R.id.change);
        LinearLayout line3 = (LinearLayout) v.findViewById(R.id.light);
        line1.setOnClickListener(this);
        line2.setOnClickListener(this);
        line3.setOnClickListener(this);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setFocusable(true);
        mPopWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_icon_pop_background));
        int[] location = new int[2];
        mBtnFuction.getLocationOnScreen(location);

        //在控件上方显示
        mPopWindow.showAtLocation(mBtnFuction, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2)-popupWidth/4, location[1] - popupHeight-10);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.beauty_seekbar) {
            mBeautyLevel = progress;
        } else if (seekBar.getId() == R.id.whitening_seekbar) {
            mWhiteningLevel = progress;
        }

        if (mLivePusher != null) {
            if (!mLivePusher.setBeautyFilter(mBeautyLevel, mWhiteningLevel)) {
                Toast.makeText(PushActivity.this, "当前机型的性能无法支持美颜功能", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onPushEvent(int event, Bundle param) {
        String msg = param.getString(TXLiveConstants.EVT_DESCRIPTION);
        appendEventLog(event, msg);
        if (mScrollView.getVisibility() == View.VISIBLE) {
            mLogViewEvent.setText(mLogMsg);
            scroll2Bottom(mScrollView, mLogViewEvent);
        }
//        if (mLivePusher != null) {
//            mLivePusher.onLogRecord("[event:" + event + "]" + msg + "\n");
//        }
        //错误还是要明确的报一下
        if (event < 0) {
            Toast.makeText(PushActivity.this, param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
        }

        if (event == TXLiveConstants.PUSH_ERR_NET_DISCONNECT) {
            stopPublishRtmp();
            mVideoPublish = false;
        } else if (event == TXLiveConstants.PUSH_WARNING_HW_ACCELERATION_FAIL) {
            Toast.makeText(PushActivity.this, param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
            mHWVideoEncode = false;
            mLivePushConfig.setHardwareAcceleration(mHWVideoEncode);
        }
    }

    @Override
    public void onNetStatus(Bundle status) {
        String str = getNetStatusString(status);
        mLogViewStatus.setText(str);
        Log.d(TAG, "Current status: " + status.toString());
//        if (mLivePusher != null){
//            mLivePusher.onLogRecord("[net state]:\n"+str+"\n");
//        }
    }

    /**
     * 实现EVENT VIEW的滚动显示
     *
     * @param scroll
     * @param inner
     */
    public static void scroll2Bottom(final ScrollView scroll, final View inner) {
        if (scroll == null || inner == null) {
            return;
        }
        int offset = inner.getMeasuredHeight() - scroll.getMeasuredHeight();
        if (offset < 0) {
            offset = 0;
        }
        scroll.scrollTo(0, offset);
    }

    private boolean startPublishRtmp() {
        start=new Date(System.currentTimeMillis());
        String rtmpUrl = "rtmp://2000.livepush.myqcloud.com/live/2000_1f4652b179af11e69776e435c87f075e?bizid=2000";
        if (TextUtils.isEmpty(rtmpUrl) || (!rtmpUrl.trim().toLowerCase().startsWith("rtmp://"))) {
            mVideoPublish = false;
            Toast.makeText(PushActivity.this, "推流地址不合法，目前支持rtmp推流!", Toast.LENGTH_SHORT).show();
            return false;
        }

        mCaptureView.setVisibility(View.VISIBLE);
//        mLivePushConfig.setWatermark(mBitmap, 0, 0);
//        mLivePushConfig.setWatermark(mBitmap, 10, 10);

        int customModeType = 0;

        //【示例代码1】设置自定义视频采集逻辑 （自定义视频采集逻辑不要调用startPreview）
//        customModeType |= TXLiveConstants.CUSTOM_MODE_VIDEO_CAPTURE;
//        mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_1280_720);
//        new Thread() {  //视频采集线程
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        FileInputStream in = new FileInputStream("/sdcard/dump_1280_720.yuv");
//                        int len = 1280 * 720 * 3 / 2;
//                        byte buffer[] = new byte[len];
//                        int count;
//                        while ((count = in.read(buffer)) != -1) {
//                            if (len == count) {
//                                mLivePusher.sendCustomYUVData(buffer);
//                            } else {
//                                break;
//                            }
//                            sleep(50, 0);
//                        }
//                        in.close();
//                    } catch (Exception e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();

        //【示例代码2】设置自定义音频采集逻辑（音频采样位宽必须是16）
//        mLivePushConfig.setAudioSampleRate(44100);
//        mLivePushConfig.setAudioChannels(1);
//        customModeType |= TXLiveConstants.CUSTOM_MODE_AUDIO_CAPTURE;
//        new Thread() {  //音频采集线程
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        FileInputStream in = new FileInputStream("/sdcard/dump.pcm");
//                        int len = 2048;
//                        byte buffer[] = new byte[len];
//                        int count;
//                        while ((count = in.read(buffer)) != -1) {
//                            if (len == count) {
//                                mLivePusher.sendCustomPCMData(buffer);
//                            } else {
//                                break;
//                            }
//                            sleep(10, 0);
//                        }
//                        in.close();
//                    } catch (Exception e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();

        //【示例代码3】设置自定义视频预处理逻辑
//        customModeType |= TXLiveConstants.CUSTOM_MODE_VIDEO_PREPROCESS;
//        String path = this.getActivity().getApplicationInfo().dataDir + "/lib";
//        mLivePushConfig.setCustomVideoPreProcessLibrary(path +"/libvideo.so", "tx_video_process");

        //【示例代码4】设置自定义音频预处理逻辑
//        customModeType |= TXLiveConstants.CUSTOM_MODE_AUDIO_PREPROCESS;
//        String path = this.getActivity().getApplicationInfo().dataDir + "/lib";
//        mLivePushConfig.setCustomAudioPreProcessLibrary(path +"/libvideo.so", "tx_audio_process");


        mLivePushConfig.setCustomModeType(customModeType);

        mLivePusher.setConfig(mLivePushConfig);
        mLivePusher.setPushListener(this);
        mLivePusher.startCameraPreview(mCaptureView);
        mLivePusher.startPusher(rtmpUrl.trim());
        mLivePusher.setLogLevel(TXLiveConstants.LOG_LEVEL_DEBUG);
        int[] ver = TXLivePusher.getSDKVersion();
        if (ver != null && ver.length >= 3) {
            mLogMsg.append(String.format("rtmp sdk version:%d.%d.%d ", ver[0], ver[1], ver[2]));
            mLogViewEvent.setText(mLogMsg);
        }

        return true;
    }

    protected void clearLog() {
        mLogMsg.setLength(0);
        mLogViewEvent.setText("");
        mLogViewStatus.setText("");
    }

    //公用打印辅助函数
    protected void appendEventLog(int event, String message) {
        String str = "receive event: " + event + ", " + message;
        Log.d(TAG, str);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        String date = sdf.format(System.currentTimeMillis());
        while (mLogMsg.length() > mLogMsgLenLimit) {
            int idx = mLogMsg.indexOf("\n");
            if (idx == 0)
                idx = 1;
            mLogMsg = mLogMsg.delete(0, idx);
        }
        mLogMsg = mLogMsg.append("\n" + "[" + date + "]" + message);
    }

    private void stopPublishRtmp() {
        end =new Date(System.currentTimeMillis());
        mLivePusher.stopCameraPreview(true);
        mLivePusher.setPushListener(null);
        mLivePusher.stopPusher();
        mCaptureView.setVisibility(View.GONE);
    }

    public void FixOrAdjustBitrate() {
        if (mRadioGroupBitrate == null || mLivePushConfig == null || mLivePusher == null) {
            return;
        }

        RadioButton rb = (RadioButton) findViewById(mRadioGroupBitrate.getCheckedRadioButtonId());
        int mode = Integer.parseInt((String) rb.getTag());

        switch (mode) {
            case 4: /*720p*/
                if (mLivePusher != null) {
                    mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_720_1280);
                    mLivePushConfig.setAutoAdjustBitrate(false);
                    mLivePushConfig.setVideoBitrate(1500);
                    mLivePusher.setConfig(mLivePushConfig);
                }
                break;
            case 3: /*540p*/
                if (mLivePusher != null) {
                    mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960);
                    mLivePushConfig.setAutoAdjustBitrate(false);
                    mLivePushConfig.setVideoBitrate(1000);
                    mLivePusher.setConfig(mLivePushConfig);
                }
                break;
            case 2: /*360p*/
                if (mLivePusher != null) {
                    mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640);
                    mLivePushConfig.setAutoAdjustBitrate(false);
                    mLivePushConfig.setVideoBitrate(700);
                    mLivePusher.setConfig(mLivePushConfig);
                }
                break;

            case 1: /*自动*/
                if (mLivePusher != null) {
                    mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640);
                    mLivePushConfig.setAutoAdjustBitrate(true);
                    mLivePushConfig.setMaxVideoBitrate(1000);
                    mLivePushConfig.setMinVideoBitrate(500);
                    mLivePushConfig.setVideoBitrate(700);
                    mLivePusher.setConfig(mLivePushConfig);
                }
                break;
            default:
                break;
        }
    }

    protected void HWListConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("警告：当前机型不在白名单中,是否继续尝试硬编码？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mHWListConfirmDialogResult = true;
                throw new RuntimeException();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mHWListConfirmDialogResult = false;
                throw new RuntimeException();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
        try {
            Looper.loop();
        } catch (Exception e) {
        }
    }

    //公用打印辅助函数
    protected String getNetStatusString(Bundle status) {
        String str = String.format("%-14s %-14s %-12s\n%-14s %-14s %-12s\n%-14s %-14s %-12s\n%-14s",
                "CPU:" + status.getString(TXLiveConstants.NET_STATUS_CPU_USAGE),
                "RES:" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH) + "*" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT),
                "SPD:" + status.getInt(TXLiveConstants.NET_STATUS_NET_SPEED) + "Kbps",
                "JIT:" + status.getInt(TXLiveConstants.NET_STATUS_NET_JITTER),
                "FPS:" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS),
                "ARA:" + status.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE) + "Kbps",
                "QUE:" + status.getInt(TXLiveConstants.NET_STATUS_CODEC_CACHE) + "|" + status.getInt(TXLiveConstants.NET_STATUS_CACHE_SIZE),
                "DRP:" + status.getInt(TXLiveConstants.NET_STATUS_CODEC_DROP_CNT) + "|" + status.getInt(TXLiveConstants.NET_STATUS_DROP_SIZE),
                "VRA:" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE) + "Kbps",
                "SVR:" + status.getString(TXLiveConstants.NET_STATUS_SERVER_IP));
        return str;
    }
}
