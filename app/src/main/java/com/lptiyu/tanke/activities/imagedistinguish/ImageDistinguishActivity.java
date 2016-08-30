package com.lptiyu.tanke.activities.imagedistinguish;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.base.MyBaseActivity;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.Task;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.enums.ResultCode;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.pojo.UpLoadGameRecord;
import com.lptiyu.tanke.pojo.UploadGameRecordResponse;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.TaskResultHelper;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.VibratorHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.easyar.engine.EasyAR;

public class ImageDistinguishActivity extends MyBaseActivity implements ImagedistinguishContact.ImagedistinguishView {
    /*
    * Steps to create the key for this sample:
    *  1. login www.easyar.com
    *  2. create app with
    *      Name: HelloARVideo
    *      Package Name: cn.easyar.samples.helloarvideo
    *  3. find the created item in the list and show key
    *  4. set key string bellow
    */
    static String key =
            "3tDDVYPVBvQ3sFL6qZYE208oShLmwE701HnisoQOtM1CHBw3a78VlWRF6YFgCjwijLdUdPAzlUfU7PspcMsueAXRoPfgLktEhoeG7e719f320a1b6dfd44356f8c4de9d7d8ARkKDZE562C3BwYoNN3mvUrW5KuxSn4hEV1AW0WmlffE1pIfsJhN9MSnvrEJOrTNjQW2";

    static {
        System.loadLibrary("EasyAR");
        System.loadLibrary("HelloARVideoNative");
    }

    @BindView(R.id.img_startScan)
    ImageView imgStartScan;
    @BindView(R.id.img_anim)
    ImageView imgAnim;
    @BindView(R.id.img_waiting)
    ImageView img_waiting;
    @BindView(R.id.tv_scanning_tip)
    TextView tvScanningTip;
    @BindView(R.id.rl_submit_record)
    RelativeLayout rlSubmitRecord;

    private boolean isOK = false;
    private ImagedistinguishPresenter presenter;
    private long gameId;
    private Point point;
    private Task task;
    private boolean isPointOver;
    private String[] imgArr;
    private MyCountDownTimer timer;
    private TaskResultHelper taskResultHelper;

    public static native void nativeInitGL();

    public static native void nativeResizeGL(int w, int h);

    public static native void nativeRender();

    private native boolean nativeInit(String[] strings, String[] strings1);

    public static native void nativeStartAr();

    public static native void nativeStopAr();

    private native boolean nativeInit();

    private native void nativeDestory();

    private native void nativeRotationChange(boolean portrait);

    private Handler mHandler = new Handler();

    private boolean isInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyC2Java.showContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_distinguish);
        ButterKnife.bind(this);
    }

    protected void init() {
        //如果初始化过就直接返回
        if (isInit)
            return;

        isInit = true;

        presenter = new ImagedistinguishPresenter(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams
                .FLAG_KEEP_SCREEN_ON);

        EasyAR.initialize(this, key);
        String parent_dir = getIntent().getStringExtra(Conf.PARENT_DIR);
        String imgPath = getIntent().getStringExtra(Conf.IMG_DISTINGUISH_URL) + "";//有可能有多张图片，是用逗号隔开的
        String[] split = imgPath.split(",");
        if (split == null) {
            imgArr = new String[]{parent_dir + "/" + imgPath};
        } else {
            for (int i = 0; i < split.length; i++) {
                split[i] = parent_dir + "/" + split[i];
            }
            imgArr = split;
        }
        gameId = getIntent().getLongExtra(Conf.GAME_ID, 0);
        point = getIntent().getParcelableExtra(Conf.POINT);
        task = getIntent().getParcelableExtra(Conf.CURRENT_TASK);
        isPointOver = getIntent().getBooleanExtra(Conf.IS_POINT_OVER, false);

        nativeInit(imgArr, new String[]{""});

        GLView glView = new GLView(this);
        glView.setRenderer(new Renderer());
        glView.setZOrderMediaOverlay(true);

        ((ViewGroup) findViewById(R.id.preview)).addView(glView, new ViewGroup.LayoutParams(ViewGroup
                .LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        nativeRotationChange(getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0);

        taskResultHelper = new TaskResultHelper(this, rlSubmitRecord, imgAnim, new TaskResultHelper
                .TaskResultCallback() {
            @Override
            public void onSuccess() {
                setActivityResult();
            }
        });

        /**
         * 识别成功回调
         */
        MyC2Java.setOnSuccessDistinguishListener(new MyC2Java.ISuccessDistinguishListener() {
            @Override
            public void onSuccess() {
                isOK = true;
                taskResultHelper.startAnim();
                timer.cancel();
                timer = null;
                mHandler = null;
                loadNetWorkData();
            }
        });

        if (AppData.isFirstInImageDistinguishActivity()) {
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    PopupWindowUtils.getInstance().showTaskGuide(ImageDistinguishActivity.this,
                            "这是个识图任务，将摄像头对准目标对象，点击蓝色按钮开始识别，识别成功即可通关");
                }
            }, 800);
        }


        img_waiting.setVisibility(View.GONE);
    }

    private void loadNetWorkData() {
        if (NetworkUtil.checkIsNetworkConnected()) {
            upLoadGameRecord();
        } else {
            showNetUnConnectDialog();
        }
    }

    // 网络异常对话框
    private void showNetUnConnectDialog() {
        PopupWindowUtils.getInstance().showNetExceptionPopupwindow(this, new PopupWindowUtils
                .OnNetExceptionListener() {
            @Override
            public void onClick(View view) {
                loadNetWorkData();
            }
        });
    }

    private void initTimerTask() {
        timer = new MyCountDownTimer(7000, 1000, new MyCountDownTimer.ICountDownTimerListener() {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                ToastUtil.TextToast("什么都没有发现，继续努力哦！");
                if (!isOK) {
                    if (timer == null) {
                        initTimerTask();
                    }
                    timer.start();
                }
            }
        });
    }


    private void setActivityResult() {
        Intent intent = new Intent();
        intent.putExtra(Conf.UPLOAD_RECORD_RESPONSE, resultRecord);
        ImageDistinguishActivity.this.setResult(ResultCode.IMAGE_DISTINGUISH, intent);
        finish();
    }

    @OnClick({R.id.img_close, R.id.img_startScan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                finish();
                break;
            case R.id.img_startScan:
                if (!isScanning) {
                    startScan();
                } else {
                    stopScan();
                }
                isScanning = !isScanning;
                break;
        }
    }

    private void stopScan() {
        if (timer != null) {
            timer.cancel();
        }
        ImageDistinguishActivity.nativeStopAr();
        imgStartScan.setImageResource(R.drawable.img_start_distinguish);
        tvScanningTip.setVisibility(View.GONE);
    }

    private void startScan() {
        if (timer == null) {
            initTimerTask();
        }
        timer.start();

        ImageDistinguishActivity.nativeStartAr();
        imgStartScan.setImageResource(R.drawable.img_distinguish);
        tvScanningTip.setVisibility(View.VISIBLE);
    }

    private boolean isScanning = false;

    private UploadGameRecordResponse resultRecord;

    /**
     * 上传游戏记录
     */
    private void upLoadGameRecord() {
        UpLoadGameRecord record = new UpLoadGameRecord();
        record.uid = Accounts.getId() + "";
        record.point_id = point.id + "";
        record.game_id = gameId + "";
        if (isPointOver)
            record.point_statu = PointTaskStatus.FINISHED + "";
        else
            record.point_statu = PointTaskStatus.PLAYING + "";
        record.task_id = task.id + "";
        presenter.uploadRecord(record);
    }

    /**
     * 上传记录成功回调
     *
     * @param response
     */
    @Override
    public void successUploadRecord(UploadGameRecordResponse response) {
        resultRecord = response;
        taskResultHelper.showSuccessResult();
        taskResultHelper.stopAnim();
        ImageDistinguishActivity.nativeStopAr();
        if (response.game_statu == PlayStatus.GAME_OVER) {
            taskResultHelper.popup_tv_result.setText("游戏完成");
        } else {
            taskResultHelper.popup_tv_result.setText("找到新线索");
        }
        //震动提示
        VibratorHelper.startVibrator(this);
    }

    /**
     * 上传失败回调
     */
    @Override
    public void failUploadRecord(String errorMsg) {
        ToastUtil.TextToast(errorMsg);
        taskResultHelper.showFailResult();
        taskResultHelper.stopAnim();
        ImageDistinguishActivity.nativeStopAr();
    }

    /**
     * 网络异常回调
     */
    @Override
    public void netException() {
        taskResultHelper.showNetException();
        taskResultHelper.stopAnim();
        ImageDistinguishActivity.nativeStopAr();
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        nativeRotationChange(getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isInit)
            init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nativeDestory();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EasyAR.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EasyAR.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopScan();
    }
}
