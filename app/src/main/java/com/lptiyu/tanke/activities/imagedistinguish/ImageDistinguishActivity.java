package com.lptiyu.tanke.activities.imagedistinguish;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.pojo.UpLoadGameRecord;
import com.lptiyu.tanke.pojo.UploadGameRecordResponse;

import java.util.concurrent.ScheduledExecutorService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.easyar.engine.EasyAR;

public class ImageDistinguishActivity extends MyBaseActivity implements ImagedistinguishContact.ImagedistinguishView {

    private final String FAIL = "什么都没有发现";
    private final String NET_EXCEPTION = "网络错误";
    private final String SUCESS = "找到新线索";

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
    @BindView(R.id.rl_submit_record)
    RelativeLayout rlSubmitRecord;
    @BindView(R.id.tv_is_scanning)
    TextView tv_is_scanning;
    @BindView(R.id.img_waiting)
    ImageView img_waiting;

    private TextView popup_tv_btn;
    private ImageView popup_img_result;
    private TextView popup_tv_result;
    private PopupWindow popupWindow;
    private View popupView;
    private boolean isOK;
    private AnimationDrawable anim;
    private ImagedistinguishPresenter presenter;
    private long gameId;
    private long gameType;
    private Point point;
    private Task task;
    private boolean isPointOver;
    private Thread thread;

    public static native void nativeInitGL();

    public static native void nativeResizeGL(int w, int h);

    public static native void nativeRender();

    private native boolean nativeInit(String[] strings, String[] strings1);

    public static native void nativeStartAr();

    public static native void nativeStopAr();

    private native boolean nativeInit();

    private native void nativeDestory();

    private native void nativeRotationChange(boolean portrait);

    //    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyC2Java.showContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_distinguish);
        ButterKnife.bind(this);

        //打开摄像头前的等待交互，不能直接写在onCreate()中，要用handler
        //        mHandler.postDelayed(new Runnable() {
        //            public void run() {
        //                Animation animation = AnimationUtils.loadAnimation(ImageDistinguishActivity.this, R.anim
        //                        .waiting_for_open_camera);
        //                img_waiting.startAnimation(animation);
        //            }
        //        }, 100);


        presenter = new ImagedistinguishPresenter(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams
                .FLAG_KEEP_SCREEN_ON);

        EasyAR.initialize(this, key);
        //        nativeInit();
        String imgPath = getIntent().getStringExtra(Conf.IMG_DISTINGUISH_URL) + "";
        gameId = getIntent().getLongExtra(Conf.GAME_ID, 0);
        gameType = getIntent().getLongExtra(Conf.GAME_TYPE, 0);
        point = getIntent().getParcelableExtra(Conf.POINT);
        task = getIntent().getParcelableExtra(Conf.CURRENT_TASK);
        isPointOver = getIntent().getBooleanExtra(Conf.IS_POINT_OVER, false);

        nativeInit(new String[]{imgPath}, new String[]{""});

        GLView glView = new GLView(this);
        glView.setRenderer(new Renderer());
        glView.setZOrderMediaOverlay(true);

        ((ViewGroup) findViewById(R.id.preview)).addView(glView, new ViewGroup.LayoutParams(ViewGroup
                .LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        nativeRotationChange(getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0);

        initPopupwindow();
        initAnim();
        initCountDownTimer();

        /**
         * 识别成功回调
         */
        MyC2Java.setOnSuccessDistinguishListener(new MyC2Java.ISuccessDistinguishListener() {
            @Override
            public void onSuccess() {
                isOK = true;
                tv_is_scanning.setText("提交中...");
                startAnim();
                upLoadGameRecord();
            }
        });
    }

    /**
     * 展示成功信息
     */
    private void showSuccessResult() {
        imgStartScan.setEnabled(true);
        isOK = true;
        stopAnim();
        popup_tv_btn.setText("查看");
        popup_img_result.setImageResource(R.drawable.task_result_right);
        popup_tv_result.setText(SUCESS);
        showPopup();
        ImageDistinguishActivity.nativeStopAr();
    }

    /**
     * 展示失败信息
     */
    private void showFailResult() {
        imgStartScan.setEnabled(true);
        isOK = false;
        stopAnim();
        popup_tv_btn.setText("关闭");
        popup_img_result.setImageResource(R.drawable.task_result_wrong);
        popup_tv_result.setText(FAIL);
        showPopup();
        ImageDistinguishActivity.nativeStopAr();
    }

    /**
     * 展示网络异常信息
     */
    private void showNetException() {
        imgStartScan.setEnabled(true);
        isOK = false;
        stopAnim();
        popup_tv_btn.setText("关闭");
        popup_img_result.setImageResource(R.drawable.task_result_wrong);
        popup_tv_result.setText(NET_EXCEPTION);
        showPopup();
        ImageDistinguishActivity.nativeStopAr();
    }


    private void initPopupwindow() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_scan_result, null);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popup_tv_btn = (TextView) popupView.findViewById(R.id.tv_continue_scan);
        popup_img_result = (ImageView) popupView.findViewById(R.id.img_result);
        popup_tv_result = (TextView) popupView.findViewById(R.id.tv_result_tip);

        popup_tv_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOK) {
                    popupWindow.dismiss();
                    Intent intent = new Intent();
                    intent.putExtra(Conf.UPLOAD_RECORD_RESPONSE, resultRecord);
                    ImageDistinguishActivity.this.setResult(ResultCode.IMAGE_DISTINGUISH, intent);
                    finish();
                } else {
                    hidePopup();
                }
            }
        });
    }

    @OnClick({R.id.img_close, R.id.img_startScan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                finish();
                break;
            case R.id.img_startScan:
                ImageDistinguishActivity.nativeStartAr();
                startAnim();
                if (thread == null) {
                    initCountDownTimer();
                }
                thread.start();
                imgStartScan.setEnabled(false);
                break;
        }
    }

    private UploadGameRecordResponse resultRecord;

    /**
     * 上传游戏记录
     */
    private void upLoadGameRecord() {
        UpLoadGameRecord record = new UpLoadGameRecord();
        record.uid = Accounts.getId() + "";
        record.type = gameType + "";
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
        showSuccessResult();
        thread.interrupt();
        thread = null;
        if (response.game_statu == PlayStatus.GAME_OVER) {
            popup_tv_result.setText("游戏完成");
        } else {
            popup_tv_result.setText("找到新线索");
        }
    }

    /**
     * 上传失败回调
     */
    @Override
    public void failUploadRecord() {
        showFailResult();
    }

    /**
     * 网络异常回调
     */
    @Override
    public void netException() {
        showNetException();
    }

    /**
     * 初始化逐帧动画
     */
    private void initAnim() {
        imgAnim.setBackgroundResource(R.drawable.anim_upload_record);
        anim = (AnimationDrawable) imgAnim.getBackground();
    }

    /**
     * 开启动画
     */
    private void startAnim() {
        if (anim != null) {
            anim.start();
        }
        rlSubmitRecord.setVisibility(View.VISIBLE);
    }

    /**
     * 停止动画
     */
    private void stopAnim() {
        if (anim != null) {
            anim.stop();
        }
        rlSubmitRecord.setVisibility(View.GONE);
    }

    /**
     * 显示popupWindow
     */
    private void showPopup() {
        if (popupView != null && popupWindow != null) {
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        }
    }

    /**
     * 隐藏popupWindow
     */
    private void hidePopup() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    /**
     * 初始化计时器
     */
    private void initCountDownTimer() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            thread = null;
                            if (!isOK) {
                                showFailResult();
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        nativeRotationChange(getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nativeDestory();
        if (anim != null) {
            anim.stop();
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
}
