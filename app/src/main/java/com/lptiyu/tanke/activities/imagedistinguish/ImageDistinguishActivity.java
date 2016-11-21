package com.lptiyu.tanke.activities.imagedistinguish;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.gameover.GameOverActivity;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.Task;
import com.lptiyu.tanke.entity.UploadGameRecord;
import com.lptiyu.tanke.entity.eventbus.GamePointTaskStateChanged;
import com.lptiyu.tanke.entity.response.UpLoadGameRecordResult;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.ScanNothingHelper;
import com.lptiyu.tanke.utils.TaskResultHelper;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.VibratorHelper;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.easyar.engine.EasyAR;

import static com.lptiyu.tanke.RunApplication.currentPoint;
import static com.lptiyu.tanke.RunApplication.currentTask;

public class ImageDistinguishActivity extends MyBaseActivity implements ImagedistinguishContact.ImagedistinguishView {
    /*
    * Steps to create the key for this sample:
    *  1. login www.easyar.com
    *  2. create app with
    *      Name: HelloARVideo
    *      Package Name: cn.easyar.samples.helloarvideo
    *  3. find the created item_home_display in the list and show key
    *  4. set key string bellow
    */
    static String key =
            "3tDDVYPVBvQ3sFL6qZYE208oShLmwE701HnisoQOtM1CHBw3a78VlWRF6YFgCjwijLdUdPAzlUfU7PspcMsueAXRoPfgLktEhoeG7e719f320a1b6dfd44356f8c4de9d7d8ARkKDZE562C3BwYoNN3mvUrW5KuxSn4hEV1AW0WmlffE1pIfsJhN9MSnvrEJOrTNjQW2";

    static {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            System.loadLibrary("EasyAR");
            System.loadLibrary("HelloARVideoNative");
        }
    }

    @BindView(R.id.img_startScan)
    ImageView imgStartScan;
    @BindView(R.id.rl_tip)
    RelativeLayout rlScanningTip;

    private ImagedistinguishPresenter presenter;
    private long gameId;
    private Point point;
    private Task task;
    private boolean isPointOver;
    private MyCountDownTimer timer;
    private TaskResultHelper taskResultHelper;
    private int index;
    private boolean isGameOver;
    private int countDownInterval = 1000;
    private int millisInFuture = 5000;
    private ScanNothingHelper scanNothingHelper;
    private android.support.v7.app.AlertDialog permissionDialog;
    private String[] imgArr;

    public static native void nativeInitGL();

    public static native void nativeResizeGL(int w, int h);

    public static native void nativeRender();

    private native boolean nativeInit(String[] strings, String[] strings1);

    public static native void nativeStartAr();

    public static native void nativeStopAr();

    private native void nativeDestory();

    private native void nativeRotationChange(boolean portrait);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyC2Java.showContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_distinguish);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        init();
    }

    protected void init() {
        presenter = new ImagedistinguishPresenter(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams
                .FLAG_KEEP_SCREEN_ON);
        //有可能有多张图片，是用逗号隔开的
        imgArr = getIntent().getStringArrayExtra(Conf.IMG_DISTINGUISH_ARRAY);
        if (isImgArrNull()) {
            return;
        }

        if (currentPoint == null || currentTask == null) {
            return;
        }
        task = currentTask;
        gameId = RunApplication.gameId;
        point = currentPoint;
        isPointOver = RunApplication.isPointOver;
        index = getIntent().getIntExtra(Conf.INDEX, -1);

        initEasyAR();

        taskResultHelper = new TaskResultHelper(this, new TaskResultHelper.TaskResultCallback() {
            @Override
            public void onSuccess() {
                if (!isGameOver) {
                    setActivityResult();
                }
            }
        });

        if (AppData.isFirstInImageDistinguishActivity()) {
            getWindow().getDecorView().postDelayed(new Runnable() {
                public void run() {
                    PopupWindowUtils.getInstance().showTaskGuide(ImageDistinguishActivity.this,
                            "这是个识图任务，将摄像头对准目标对象，点击蓝色按钮开始识别，识别成功即可通关", null);
                }
            }, Conf.POST_DELAY);
        }

        /**
         * 识别成功回调
         */
        MyC2Java.setOnSuccessDistinguishListener(new MyC2Java.ISuccessDistinguishListener() {
            @Override
            public void onSuccess() {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                upload();
            }
        });

    }

    private boolean isImgArrNull() {
        if (imgArr == null || imgArr.length <= 0) {
            Toast.makeText(this, "未发现目标图片", Toast.LENGTH_SHORT).show();
            return true;
        }
        boolean isImgPathExist = false;
        for (String imgPath : imgArr) {
            if (imgPath != null && !imgPath.equals("")) {
                isImgPathExist = true;
            }
        }
        if (!isImgPathExist) {
            Toast.makeText(this, "未发现目标图片", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void initEasyAR() {
        if (isCameraCanUse()) {
            EasyAR.initialize(this, key);
            nativeInit(imgArr, new String[]{""});
            GLView glView = new GLView(this);
            glView.setRenderer(new Renderer());
            glView.setZOrderMediaOverlay(true);

            ((ViewGroup) findViewById(R.id.preview)).addView(glView, new ViewGroup.LayoutParams(ViewGroup
                    .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            nativeRotationChange(getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0);
        } else {
            showPermissionFailTip();
            return;
        }

    }

    /**
     * 返回true 表示可以使用  返回false表示不可以使用
     */
    public boolean isCameraCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isCanUse;
    }

    private void showPermissionFailTip() {
        if (permissionDialog == null) {
            permissionDialog = new android.support.v7.app.AlertDialog.Builder(this).setMessage
                    ("此功能需要您授予摄像头权限，请前往“设置”->“应用管理”，选择“步道探秘”进行授权设置")
                    .setPositiveButton("前往设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PackageManager pm = getPackageManager();
                            PackageInfo info = null;
                            try {
                                info = pm.getPackageInfo(getPackageName(), 0);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.MAIN");
                            intent.setClassName("com.android.settings", "com.android.settings" +
                                    ".ManageApplications");
                            intent.putExtra("extra_package_uid", info.applicationInfo.uid);
                            try {
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(ImageDistinguishActivity.this, "前往失败，请手动前往设置->应用管理授权", Toast
                                        .LENGTH_SHORT).show();
                            }
                            finish();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ImageDistinguishActivity.this.finish();
                        }
                    }).setCancelable(false).show();
        }
    }

    private void upload() {
        if (NetworkUtil.checkIsNetworkConnected()) {
            //            taskResultHelper.startSubmitting();
            upLoadGameRecord();
        } else {
            PopupWindowUtils.getInstance().showNetExceptionPopupwindow(this, new PopupWindowUtils.OnRetryCallback() {
                @Override
                public void onRetry() {
                    upload();
                }
            });
        }
    }

    private void initTimerTask() {
        timer = new MyCountDownTimer(millisInFuture, countDownInterval, new MyCountDownTimer.ICountDownTimerListener() {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                stopScan();
                if (scanNothingHelper == null) {
                    scanNothingHelper = new ScanNothingHelper(ImageDistinguishActivity.this, new ScanNothingHelper
                            .DismissCallback() {
                        @Override
                        public void onDismisss() {
                            isScanning = !isScanning;
                        }
                    });
                }
                scanNothingHelper.show();
            }
        });
    }


    private void setActivityResult() {
        //发通知销毁PointTaskV2Activity，GamePlayingV2Activity刷新数据
        EventBus.getDefault().post(new GamePointTaskStateChanged());
        finish();
    }

    @OnClick({R.id.img_close, R.id.img_startScan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                finish();
                break;
            case R.id.img_startScan:
                //                if (Accounts.getPhoneNumber() != null && Accounts.getPhoneNumber().equals
                // ("18272164317")) {
                //                    stopScan();
                //                    upload();
                //                    return;
                //                }
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
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ImageDistinguishActivity.nativeStopAr();
        }
        imgStartScan.setImageResource(R.drawable.img_start_distinguish);
        rlScanningTip.setVisibility(View.GONE);
    }

    private void startScan() {
        if (timer == null) {
            initTimerTask();
        }
        timer.start();

        ImageDistinguishActivity.nativeStartAr();
        imgStartScan.setImageResource(R.drawable.img_distinguish);
        rlScanningTip.setVisibility(View.VISIBLE);
    }

    private boolean isScanning = false;

    private UpLoadGameRecordResult resultRecord;

    /**
     * 上传游戏记录
     */
    private void upLoadGameRecord() {
        UploadGameRecord record = new UploadGameRecord();
        record.uid = Accounts.getId() + "";
        record.point_id = point.id + "";
        record.game_id = gameId + "";
        if (isPointOver)
            record.point_statu = PointTaskStatus.FINISHED + "";
        else
            record.point_statu = PointTaskStatus.UNFINISHED + "";
        record.task_id = task.id + "";
        presenter.uploadRecord(record);
    }

    /**
     * 上传记录成功回调
     *
     * @param response
     */
    @Override
    public void successUploadRecord(UpLoadGameRecordResult response) {
        resultRecord = response;
        resultRecord.index = this.index;
        taskResultHelper.showSuccessResult(response);
        taskResultHelper.stopSubmitting();
        stopScan();
        if (response.game_statu == PlayStatus.GAME_OVER) {//游戏通关，需要弹出通关视图，弹出通关视图
            isGameOver = true;
            taskResultHelper.dismiss();
            startActivity(new Intent(ImageDistinguishActivity.this, GameOverActivity.class));
            finish();
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
        taskResultHelper.stopSubmitting();
        ImageDistinguishActivity.nativeStopAr();
    }

    /**
     * 网络异常回调
     */
    @Override
    public void netException() {
        taskResultHelper.showNetException();
        taskResultHelper.stopSubmitting();
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
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            new AlertDialog.Builder(this).setMessage("您当前的手机系统暂不支持此功能").setNegativeButton("确定", new DialogInterface
                    .OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).create().show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            nativeDestory();
        }
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
