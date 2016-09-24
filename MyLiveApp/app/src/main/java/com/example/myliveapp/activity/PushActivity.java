package com.example.myliveapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myliveapp.R;
import com.example.myliveapp.util.HWSupportList;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.text.SimpleDateFormat;


public class PushActivity extends AppCompatActivity implements View.OnClickListener, ITXLivePushListener, SeekBar.OnSeekBarChangeListener {
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
    private Button mBtnBitrate;
    private Button mBtnPlay;
    private Button mBtnFaceBeauty;
    private Button mBtnFlashLight;
    private Button mBtnTouchFocus;
    private Button mBtnHWEncode;
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
    private Handler mHandler = new Handler();
    StringBuffer mLogMsg = new StringBuffer("");
    private Bitmap mBitmap;
    private final int mLogMsgLenLimit = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        flayout= (FrameLayout) findViewById(R.id.activity_push);
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
        mScrollView = (ScrollView)findViewById(R.id.scrollview);
        mScrollView.setVisibility(View.GONE);
        //美颜部分
        mFaceBeautyLayout = (LinearLayout)findViewById(R.id.layoutFaceBeauty);
        mBeautySeekBar = (SeekBar) findViewById(R.id.beauty_seekbar);
        mBeautySeekBar.setOnSeekBarChangeListener(this);

        mWhiteningSeekBar = (SeekBar) findViewById(R.id.whitening_seekbar);
        mWhiteningSeekBar.setOnSeekBarChangeListener(this);

        mBtnFaceBeauty = (Button)findViewById(R.id.btnFaceBeauty);
        mBtnFaceBeauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFaceBeautyLayout.setVisibility(mFaceBeautyLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });
        //播放部分
        mBtnPlay = (Button) findViewById(R.id.btnPlay);
        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mVideoPublish) {
                    stopPublishRtmp();
                    mVideoPublish = false;
                } else {
                    FixOrAdjustBitrate();  //根据设置确定是“固定”还是“自动”码率
                    mVideoPublish = startPublishRtmp();
                }
            }
        });
        //log部分
        final Button btnLog = (Button) findViewById(R.id.btnLog);
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLogViewStatus.getVisibility() == View.GONE) {
                    mLogViewStatus.setVisibility(View.VISIBLE);
                    mScrollView.setVisibility(View.VISIBLE);
                    mLogViewEvent.setText(mLogMsg);
                    scroll2Bottom(mScrollView, mLogViewEvent);
                    btnLog.setBackgroundResource(R.drawable.log_hidden);
                } else {
                    mLogViewStatus.setVisibility(View.GONE);
                    mScrollView.setVisibility(View.GONE);
                    btnLog.setBackgroundResource(R.drawable.log_show);
                }
            }
        });
        //切换前置后置摄像头
        final Button btnChangeCam = (Button) findViewById(R.id.btnCameraChange);
        btnChangeCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFrontCamera = !mFrontCamera;

                if (mLivePusher.isPushing()) {
                    mLivePusher.switchCamera();
                } else {
                    mLivePushConfig.setFrontCamera(mFrontCamera);
                }
                btnChangeCam.setBackgroundResource(mFrontCamera ? R.drawable.camera_change : R.drawable.camera_change2);
            }
        });
        //开启硬件加速
        mBtnHWEncode = (Button) findViewById(R.id.btnHWEncode);
        mBtnHWEncode.getBackground().setAlpha(mHWVideoEncode ? 255 : 100);
        mBtnHWEncode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean HWVideoEncode = mHWVideoEncode;
                mHWVideoEncode = !mHWVideoEncode;
                mBtnHWEncode.getBackground().setAlpha(mHWVideoEncode ? 255 : 100);

                if (mHWVideoEncode){
                    if (mLivePushConfig != null) {
                        if(Build.VERSION.SDK_INT < 16){
                            Toast.makeText(PushActivity.this, "硬件加速失败，当前手机API级别过低（最低16）", Toast.LENGTH_SHORT).show();
                            mHWVideoEncode = false;
                        }
                        else{
                            if (HWSupportList.isHWVideoEncodeSupport() == false){
                                HWListConfirmDialog();
                                if (mHWListConfirmDialogResult){
                                    Toast.makeText(PushActivity.this, "尝试硬件编码，推流端/播放端画面可能异常！", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    mHWVideoEncode = false;
                                    Toast.makeText(PushActivity.this, "取消硬件加速", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
                if(HWVideoEncode != mHWVideoEncode){
                    mLivePushConfig.setHardwareAcceleration(mHWVideoEncode);
                    if(mHWVideoEncode == false){
                        Toast.makeText(PushActivity.this, "取消硬件加速", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(PushActivity.this, "开启硬件加速", Toast.LENGTH_SHORT).show();
                    }
                }
                if (mLivePusher != null) {
                    mLivePusher.setConfig(mLivePushConfig);
                }
            }
        });
        //码率自适应部分
        mBtnBitrate = (Button)findViewById(R.id.btnBitrate);
        mBitrateLayout = (LinearLayout)findViewById(R.id.layoutBitrate);
        mBtnBitrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBitrateLayout.setVisibility(mBitrateLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        mRadioGroupBitrate = (RadioGroup)findViewById(R.id.resolutionRadioGroup);
        mRadioGroupBitrate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FixOrAdjustBitrate();
                mBitrateLayout.setVisibility(View.GONE);
            }
        });
        //闪光灯
        mBtnFlashLight = (Button)findViewById(R.id.btnFlash);
        mBtnFlashLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLivePusher == null) {
                    return;
                }

                mFlashTurnOn = !mFlashTurnOn;
                if (!mLivePusher.turnOnFlashLight(mFlashTurnOn)) {
                    Toast.makeText(PushActivity.this,
                            "打开闪光灯失败（1）大部分前置摄像头并不支持闪光灯（2）该接口需要在启动预览之后调用", Toast.LENGTH_SHORT).show();
                }

                mBtnFlashLight.setBackgroundResource(mFlashTurnOn ? R.drawable.flash_off : R.drawable.flash_on);
            }
        });
        //手动对焦/自动对焦
        mBtnTouchFocus = (Button) findViewById(R.id.btnTouchFoucs);
        mBtnTouchFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFrontCamera) {
                    return;
                }

                mTouchFocus = !mTouchFocus;
                mLivePushConfig.setTouchFocus(mTouchFocus);
                v.setBackgroundResource(mTouchFocus ? R.drawable.automatic : R.drawable.manual);

                if (mLivePusher.isPushing()) {
                    mLivePusher.stopCameraPreview(false);
                    mLivePusher.startCameraPreview(mCaptureView);
                }

                Toast.makeText(PushActivity.this, mTouchFocus ? "已开启手动对焦" : "已开启自动对焦", Toast.LENGTH_SHORT).show();
            }
        });
        flayout.setOnClickListener(this);
        mLogViewStatus.setText("Log File Path:"+ Environment.getExternalStorageDirectory().getAbsolutePath()+"/txRtmpLog");
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mCaptureView != null) {
            mCaptureView.onResume();
        }

        if (mVideoPublish && !mLivePusher.isPushing()) {
            mLivePusher.startCameraPreview(mCaptureView);
        }
    }

    @Override
    public void onStop(){
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
            default:
                mFaceBeautyLayout.setVisibility(View.GONE);
                mBitrateLayout.setVisibility(View.GONE);
        }
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
        if (mScrollView.getVisibility() == View.VISIBLE){
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
        }
        else if (event == TXLiveConstants.PUSH_WARNING_HW_ACCELERATION_FAIL) {
            Toast.makeText(PushActivity.this, param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
            mHWVideoEncode = false;
            mLivePushConfig.setHardwareAcceleration(mHWVideoEncode);
            mBtnHWEncode.setBackgroundResource(mHWVideoEncode ? R.drawable.quick : R.drawable.quick2);
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
    private  boolean startPublishRtmp() {
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

        clearLog();
        int[] ver = TXLivePusher.getSDKVersion();
        if (ver != null && ver.length >= 3) {
            mLogMsg.append(String.format("rtmp sdk version:%d.%d.%d ",ver[0],ver[1],ver[2]));
            mLogViewEvent.setText(mLogMsg);
        }

        mBtnPlay.setBackgroundResource(R.drawable.play_pause);

        appendEventLog(0, "点击推流按钮！");

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
        mLivePusher.stopCameraPreview(true);
        mLivePusher.setPushListener(null);
        mLivePusher.stopPusher();
        mCaptureView.setVisibility(View.GONE);
        mBtnPlay.setBackgroundResource(R.drawable.play_start);
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
                mBtnBitrate.setBackgroundResource(R.drawable.fix_bitrate);
                break;
            case 3: /*540p*/
                if (mLivePusher != null) {
                    mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960);
                    mLivePushConfig.setAutoAdjustBitrate(false);
                    mLivePushConfig.setVideoBitrate(1000);
                    mLivePusher.setConfig(mLivePushConfig);
                }
                mBtnBitrate.setBackgroundResource(R.drawable.fix_bitrate);
                break;
            case 2: /*360p*/
                if (mLivePusher != null) {
                    mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640);
                    mLivePushConfig.setAutoAdjustBitrate(false);
                    mLivePushConfig.setVideoBitrate(700);
                    mLivePusher.setConfig(mLivePushConfig);
                }
                mBtnBitrate.setBackgroundResource(R.drawable.fix_bitrate);
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
                mBtnBitrate.setBackgroundResource(R.drawable.auto_bitrate);
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
        }catch (Exception e) {}
    }
    //公用打印辅助函数
    protected String getNetStatusString(Bundle status) {
        String str = String.format("%-14s %-14s %-12s\n%-14s %-14s %-12s\n%-14s %-14s %-12s\n%-14s",
                "CPU:"+status.getString(TXLiveConstants.NET_STATUS_CPU_USAGE),
                "RES:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH)+"*"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT),
                "SPD:"+status.getInt(TXLiveConstants.NET_STATUS_NET_SPEED)+"Kbps",
                "JIT:"+status.getInt(TXLiveConstants.NET_STATUS_NET_JITTER),
                "FPS:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS),
                "ARA:"+status.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE)+"Kbps",
                "QUE:"+status.getInt(TXLiveConstants.NET_STATUS_CODEC_CACHE)+"|"+status.getInt(TXLiveConstants.NET_STATUS_CACHE_SIZE),
                "DRP:"+status.getInt(TXLiveConstants.NET_STATUS_CODEC_DROP_CNT)+"|"+status.getInt(TXLiveConstants.NET_STATUS_DROP_SIZE),
                "VRA:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE)+"Kbps",
                "SVR:"+status.getString(TXLiveConstants.NET_STATUS_SERVER_IP));
        return str;
    }
}
