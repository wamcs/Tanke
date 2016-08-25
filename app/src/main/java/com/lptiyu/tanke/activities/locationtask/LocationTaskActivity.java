package com.lptiyu.tanke.activities.locationtask;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.base.MyBaseActivity;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.Task;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.enums.ResultCode;
import com.lptiyu.tanke.gameplaying.assist.LocateHelper;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.pojo.UpLoadGameRecord;
import com.lptiyu.tanke.pojo.UploadGameRecordResponse;
import com.lptiyu.tanke.utils.DistanceFormatter;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.ToastUtil;

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
    private int DISTANCE_OFFSET = 20;
    private LocationTaskPresenter presenter;
    //    private Handler mHandler = new Handler();
    private String[] split;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_task);
        ButterKnife.bind(this);

        initAnim();
        startAnim();

        presenter = new LocationTaskPresenter(this);

        initData();

        locateHelper = new LocateHelper(this);
        locateHelper.registerLocationListener(this);

        //        mHandler.postDelayed(new Runnable() {
        //            public void run() {
        locateHelper.startLocate();
        //            }
        //        }, 100);
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
        checkLocation();
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
        stopAnim();
        Intent intent = new Intent();
        intent.putExtra(Conf.UPLOAD_RECORD_RESPONSE, response);
        setResult(ResultCode.LOCATION_TASK, intent);
        finish();
    }

    @Override
    public void failUploadRecord() {
        stopAnim();
        ToastUtil.TextToast("位置验证失败");
    }

    @Override
    public void netException() {
        stopAnim();
        ToastUtil.TextToast("网络异常");
    }

    @OnClick(R.id.btn_startLocating)
    public void onClick() {
        startAnim();
        if (locateHelper != null)
            locateHelper.startLocate();
    }
}
