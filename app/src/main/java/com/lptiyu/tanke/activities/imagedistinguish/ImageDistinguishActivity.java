package com.lptiyu.tanke.activities.imagedistinguish;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

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
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.thread;

import java.util.TimerTask;

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
    @BindView(R.id.tv_scanning_tip)
    TextView tvScanningTip;

    private TextView popup_tv_btn;
    private ImageView popup_img_result;
    private TextView popup_tv_result;
    private PopupWindow popupWindow;
    private View popupView;
    private boolean isOK;
    private AnimationDrawable anim;
    private ImagedistinguishPresenter presenter;
    private long gameId;
    //    private long gameType;
    private Point point;
    private Task task;
    private boolean isPointOver;
    private String[] imgArr;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyC2Java.showContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_distinguish);
        ButterKnife.bind(this);

        presenter = new ImagedistinguishPresenter(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams
                .FLAG_KEEP_SCREEN_ON);

        EasyAR.initialize(this, key);
        //        nativeInit();
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
        //        gameType = getIntent().getLongExtra(Conf.GAME_TYPE, 0);
        point = getIntent().getParcelableExtra(Conf.POINT);
        task = getIntent().getParcelableExtra(Conf.CURRENT_TASK);
        isPointOver = getIntent().getBooleanExtra(Conf.IS_POINT_OVER, false);

        //        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager
        //                .PERMISSION_GRANTED) {
        //            if (ActivityCompat.shouldShowRequestPermissionRationale(ImageDistinguishActivity.this,
        //                    Manifest.permission.CAMERA)) {
        //                //如果用户之前拒绝过App使用权限，这个方法就会返回true。
        //                Toast.makeText(this, "上次拒绝了授权", Toast.LENGTH_SHORT).show();
        //            } else {
        //                //当App被永远拒绝获取某权限时（比如你点击了“不再提示”），shouldShowRequestPermissionRationale()就会返回false，
        //                Toast.makeText(this, "权限被永久禁止", Toast.LENGTH_SHORT).show();
        //            }
        //            //申请权限，在回调方法onRequestPermissionsResult()中处理
        //            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        //        } else {
        initAR();
        //        }

        GLView glView = new GLView(this);
        glView.setRenderer(new Renderer());
        glView.setZOrderMediaOverlay(true);

        ((ViewGroup) findViewById(R.id.preview)).addView(glView, new ViewGroup.LayoutParams(ViewGroup
                .LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        nativeRotationChange(getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0);

        initPopupwindow();
        initAnim();

        /**
         * 识别成功回调
         */
        MyC2Java.setOnSuccessDistinguishListener(new MyC2Java.ISuccessDistinguishListener() {
            @Override
            public void onSuccess() {
                isOK = true;
                tv_is_scanning.setText("提交中...");
                startAnim();
                timerHandler = null;
                mHandler = null;
                upLoadGameRecord();
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
    }

    private Handler timerHandler;

    private void initTimerTask() {
        timerHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        if (timerHandler != null)
                            ToastUtil.TextToast("什么都没有发现，继续努力哦！");
                        break;
                }
            }
        };
    }

    private void initAR() {
        try {
            nativeInit(imgArr, new String[]{""});
        } catch (Exception e) {
            Toast.makeText(this, "请确认本应用是否授予摄像头的权限", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    //    @Override
    //    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    //        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    //        switch (requestCode) {
    //            case 100:
    //                // If request is cancelled, the result arrays are empty.
    //                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
    //                    // permission was granted, yay! Do the
    //                    // contacts-related task you need to do.
    //                    Toast.makeText(this, "权限请求成功", Toast.LENGTH_SHORT).show();
    //                    initAR();
    //                } else {
    //                    // permission denied, boo! Disable the
    //                    // functionality that depends on this permission.
    //                    Toast.makeText(this, "权限请求失败", Toast.LENGTH_SHORT).show();
    //                }
    //                break;
    //        }
    //    }

    /**
     * 展示成功信息
     */
    private void showSuccessResult() {
        tvScanningTip.setVisibility(View.GONE);
        //        imgStartScan.setEnabled(true);
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
        tvScanningTip.setVisibility(View.GONE);
        //        imgStartScan.setEnabled(true);
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
        tvScanningTip.setVisibility(View.GONE);
        //        imgStartScan.setEnabled(true);
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
                if (!isScanning) {
                    if (timerHandler == null) {
                        initTimerTask();
                    }
                    timerHandler.sendEmptyMessageDelayed(0, 7000);

                    ImageDistinguishActivity.nativeStartAr();
                    imgStartScan.setImageResource(R.drawable.img_distinguish);
                    tvScanningTip.setVisibility(View.VISIBLE);
                } else {
                    timerHandler = null;
                    ImageDistinguishActivity.nativeStopAr();
                    imgStartScan.setImageResource(R.drawable.img_start_distinguish);
                    tvScanningTip.setVisibility(View.GONE);
                }
                isScanning = !isScanning;
                break;
        }
    }

    private boolean isScanning = false;

    private UploadGameRecordResponse resultRecord;

    /**
     * 上传游戏记录
     */
    private void upLoadGameRecord() {
        UpLoadGameRecord record = new UpLoadGameRecord();
        record.uid = Accounts.getId() + "";
        //        record.type = gameType + "";
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
