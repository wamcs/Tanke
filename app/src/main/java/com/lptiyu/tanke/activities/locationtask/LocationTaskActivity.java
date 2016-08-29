package com.lptiyu.tanke.activities.locationtask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.base.MyBaseActivity;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.Task;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.enums.ResultCode;
import com.lptiyu.tanke.gameplaying.assist.LocateHelper;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.pojo.UpLoadGameRecord;
import com.lptiyu.tanke.pojo.UploadGameRecordResponse;
import com.lptiyu.tanke.utils.DistanceFormatter;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.VibratorHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationTaskActivity extends MyBaseActivity implements BDLocationListener, LocationTaskContact
        .ILocationTaskView {

    @BindView(R.id.img_close)
    ImageView imgClose;
    @BindView(R.id.img_anim)
    ImageView imgAnim;
    @BindView(R.id.tv_locating)
    TextView tvLocating;
    private LocateHelper locateHelper;
    private double latitude;
    private double longitude;
    private AnimationDrawable anim;
    private long gameId;
    private Point point;
    private boolean isPointOver;
    private Task task;
    private int DISTANCE_OFFSET = 60;
    private LocationTaskPresenter presenter;
    private String[] split;

    private TextView popup_tv_btn;
    private ImageView popup_img_result;
    private TextView popup_tv_result;
    private PopupWindow popupWindow;
    private View popupView;
    private boolean isOK;
    private UploadGameRecordResponse resultRecord;

    private final String FAIL = "什么都没有发现";
    private final String NET_EXCEPTION = "网络错误";
    private final String SUCESS = "找到新线索";
    //    private TaskResultHelper taskResultHelper;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_task);
        ButterKnife.bind(this);

        initAnim();
        startAnim();
        initPopupwindow();
        //
        //        taskResultHelper = new TaskResultHelper(this, imgAnim, new TaskResultHelper
        //                .TaskResultCallback() {
        //            @Override
        //            public void onSuccess() {
        //                Intent intent = new Intent();
        //                intent.putExtra(Conf.UPLOAD_RECORD_RESPONSE, resultRecord);
        //                LocationTaskActivity.this.setResult(ResultCode.LOCATION_TASK, intent);
        //                finish();
        //            }
        //        });
        presenter = new LocationTaskPresenter(this);

        initData();

        locateHelper = new LocateHelper(this);
        locateHelper.registerLocationListener(this);
        //        locateHelper.startLocate();

        if (AppData.isFirstInLocationActivity()) {
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    PopupWindowUtils.getInstance().showTaskGuide(LocationTaskActivity.this,
                            "这是定位任务，点击验证您当前的位置，验证通过即可通关", new PopupWindowUtils.DismissCallback() {
                                @Override
                                public void onDismisss() {
                                    locateHelper.startLocate();
                                    //                                    checkLocation();
                                }
                            });
                }
            }, 500);
        } else {
            locateHelper.startLocate();
        }
    }

    private void initData() {
        task = getIntent().getParcelableExtra(Conf.CURRENT_TASK);
        gameId = getIntent().getLongExtra(Conf.GAME_ID, 0);
        point = getIntent().getParcelableExtra(Conf.POINT);
        isPointOver = getIntent().getBooleanExtra(Conf.IS_POINT_OVER, false);
        split = task.pwd.split(",");
    }

    /**
     * 初始化动画
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
        imgAnim.setVisibility(View.VISIBLE);
        tvLocating.setVisibility(View.VISIBLE);
    }

    /**
     * 停止动画
     */
    private void stopAnim() {
        if (anim != null) {
            anim.stop();
        }
        imgAnim.setVisibility(View.GONE);
        tvLocating.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locateHelper != null) {
            locateHelper.stopLocate();
            locateHelper.unRegisterLocationListener(this);
        }
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        //纬度
        latitude = bdLocation.getLatitude();
        //经度
        longitude = bdLocation.getLongitude();
        Log.i("jason", "定位信息latitude：" + latitude + ", longitude:" + longitude);
        //核对位置
        if (!AppData.isFirstInLocationActivity()) {
            checkLocation();
        }
    }

    private void checkLocation() {
        if (split == null || split.length <= 0) {
            ToastUtil.TextToast("目标位置不存在");
            return;
        }
        double distance = DistanceUtil.getDistance(new LatLng(latitude, longitude), new LatLng(Double.parseDouble
                (split[1]), Double
                .parseDouble(split[0])));
        if (distance <= DISTANCE_OFFSET) {
            //验证成功，上传游戏记录
            if (locateHelper != null)
                locateHelper.stopLocate();
            loadNetWorkData();
        } else {
            ToastUtil.TextToast("您距离目标点" + DistanceFormatter.formatMeter(distance));
        }
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

    @OnClick(R.id.img_close)
    public void close() {
        finish();
    }

    @Override
    public void successUploadRecord(UploadGameRecordResponse response) {
        resultRecord = response;
        showSuccessResult();
        if (response.game_statu == PlayStatus.GAME_OVER) {
            popup_tv_result.setText("游戏完成");
        } else {
            popup_tv_result.setText("找到新线索");
        }
        //震动提示
        VibratorHelper.startVibrator(this);

        //                stopAnim();
        //                Intent intent = new Intent();
        //                intent.putExtra(Conf.UPLOAD_RECORD_RESPONSE, response);
        //                setResult(ResultCode.LOCATION_TASK, intent);
        //                finish();
    }

    @Override
    public void failUploadRecord(String errorMsg) {
        //        showFailResult();
        //        stopAnim();
        ToastUtil.TextToast(errorMsg);
    }

    @Override
    public void netException() {
        //        showNetUnConnectDialog();
        showNetException();
    }

    @OnClick(R.id.btn_startLocating)
    public void onClick() {
        startAnim();
        if (locateHelper != null)
            locateHelper.startLocate();
    }

    /**
     * 展示成功信息
     */
    private void showSuccessResult() {
        isOK = true;
        stopAnim();
        popup_tv_btn.setText("查看");
        popup_img_result.setImageResource(R.drawable.task_result_right);
        popup_tv_result.setText(SUCESS);
        showPopup();
    }

    /**
     * 展示失败信息
     */
    private void showFailResult() {
        isOK = false;
        stopAnim();
        popup_tv_btn.setText("关闭");
        popup_img_result.setImageResource(R.drawable.task_result_wrong);
        popup_tv_result.setText(FAIL);
        showPopup();
    }

    /**
     * 展示网络异常信息
     */
    private void showNetException() {
        isOK = false;
        stopAnim();
        popup_tv_btn.setText("关闭");
        popup_img_result.setImageResource(R.drawable.task_result_wrong);
        popup_tv_result.setText(NET_EXCEPTION);
        showPopup();
    }

    private void showPopup() {
        if (popupView != null && popupWindow != null) {
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        }
    }

    private void hidePopup() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private void initPopupwindow() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_scan_result, null);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //        popupWindow.setAnimationStyle(R.style.Popup_Animation);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (isOK) {
                    Intent intent = new Intent();
                    intent.putExtra(Conf.UPLOAD_RECORD_RESPONSE, resultRecord);
                    LocationTaskActivity.this.setResult(ResultCode.LOCATION_TASK, intent);
                    finish();
                } else {
                    hidePopup();
                }
            }
        });

        popup_tv_btn = (TextView) popupView.findViewById(R.id.tv_continue_scan);
        popup_img_result = (ImageView) popupView.findViewById(R.id.img_result);
        popup_tv_result = (TextView) popupView.findViewById(R.id.tv_result_tip);

        popup_tv_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                //                if (isOK) {
                //                    Intent intent = new Intent();
                //                    intent.putExtra(Conf.UPLOAD_RECORD_RESPONSE, resultRecord);
                //                    LocationTaskActivity.this.setResult(ResultCode.LOCATION_TASK, intent);
                //                    finish();
                //                } else {
                //                    hidePopup();
                //                }
            }
        });
    }
}
