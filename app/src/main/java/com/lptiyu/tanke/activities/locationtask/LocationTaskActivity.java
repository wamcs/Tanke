package com.lptiyu.tanke.activities.locationtask;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
import com.lptiyu.tanke.utils.TaskResultHelper;
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
    @BindView(R.id.rl_submit_record)
    RelativeLayout rlSubmitRecord;
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
    private boolean isToastShow = true;

    private UploadGameRecordResponse resultRecord;

    private TaskResultHelper taskResultHelper;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_task);
        ButterKnife.bind(this);

        taskResultHelper = new TaskResultHelper(this, rlSubmitRecord, imgAnim, new TaskResultHelper
                .TaskResultCallback() {
            @Override
            public void onSuccess() {
                setActivityResult();
            }
        });
        taskResultHelper.startAnim();
        initData();
    }

    private void setActivityResult() {
        Intent intent = new Intent();
        intent.putExtra(Conf.UPLOAD_RECORD_RESPONSE, resultRecord);
        LocationTaskActivity.this.setResult(ResultCode.LOCATION_TASK, intent);
        finish();
    }

    private void initData() {
        task = getIntent().getParcelableExtra(Conf.CURRENT_TASK);
        gameId = getIntent().getLongExtra(Conf.GAME_ID, 0);
        point = getIntent().getParcelableExtra(Conf.POINT);
        isPointOver = getIntent().getBooleanExtra(Conf.IS_POINT_OVER, false);
        split = task.pwd.split(",");

        presenter = new LocationTaskPresenter(this);

        locateHelper = new LocateHelper(this);
        locateHelper.registerLocationListener(this);

        if (AppData.isFirstInLocationActivity()) {
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    PopupWindowUtils.getInstance().showTaskGuide(LocationTaskActivity.this,
                            "这是定位任务，点击验证您当前的位置，验证通过即可通关", new PopupWindowUtils.DismissCallback() {
                                @Override
                                public void onDismisss() {
                                    locateHelper.startLocate();
                                }
                            });
                }
            }, 500);
        } else {
            locateHelper.startLocate();
        }
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
        checkLocation();
    }

    private void checkLocation() {
        if (split == null || split.length <= 1) {//必定同时包含经度和纬度
            ToastUtil.TextToast("目标位置不存在");
            return;
        }
        double distance = DistanceUtil.getDistance(new LatLng(latitude, longitude), new LatLng(Double.parseDouble
                (split[1]), Double.parseDouble(split[0])));
        if (distance <= DISTANCE_OFFSET) {
            //验证成功，上传游戏记录
            if (locateHelper != null)
                locateHelper.stopLocate();
            loadNetWorkData();
        } else {
            if (isToastShow) {
                ToastUtil.TextToast("您距离目标点" + DistanceFormatter.formatMeter(distance));
            }
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
        taskResultHelper.showSuccessResult();
        taskResultHelper.stopAnim();
        if (response.game_statu == PlayStatus.GAME_OVER) {
            taskResultHelper.popup_tv_result.setText("游戏完成");
        } else {
            taskResultHelper.popup_tv_result.setText("找到新线索");
        }
        //震动提示
        VibratorHelper.startVibrator(this);
    }

    @Override
    public void failUploadRecord(String errorMsg) {
        ToastUtil.TextToast(errorMsg);
        taskResultHelper.showFailResult();
        taskResultHelper.stopAnim();
    }

    @Override
    public void netException() {
        taskResultHelper.showNetException();
        taskResultHelper.stopAnim();
    }

    @OnClick(R.id.btn_startLocating)
    public void onClick() {
        taskResultHelper.startAnim();
        if (locateHelper != null)
            locateHelper.startLocate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isToastShow = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isToastShow = false;
    }
}
