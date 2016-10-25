package com.lptiyu.tanke.activities.locationtask;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
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
    private long gameId;
    private Point point;
    private boolean isPointOver;
    private Task task;
    private final int DISTANCE_OFFSET = 60;
    private LocationTaskPresenter presenter;
    private String[] split;
    private boolean isToastShow = true;

    private UploadGameRecordResponse resultRecord;

    private TaskResultHelper taskResultHelper;

    private final double ERROR_LOCATION_RETURN = 4.9E-324;
    private AlertDialog permissionDialog;

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
            getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    PopupWindowUtils.getInstance().showTaskGuide(LocationTaskActivity.this,
                            "这是定位任务，点击验证您当前的位置，验证通过即可通关", new PopupWindowUtils.DismissCallback() {
                                @Override
                                public void onDismisss() {
                                    locateHelper.startLocate();
                                }
                            });
                }
            });
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
        if (permissionDialog != null) {
            if (permissionDialog.isShowing())
                permissionDialog.dismiss();
            permissionDialog = null;
        }
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        locateHelper.stopLocate();
        //        if (Accounts.getPhoneNumber().endsWith("4317")) {
        //            loadNetWorkData();
        //            return;
        //        }
        //纬度
        latitude = bdLocation.getLatitude();
        //经度
        longitude = bdLocation.getLongitude();
        Log.i("jason", "定位信息latitude：" + latitude + ", longitude:" + longitude);
        if (latitude == ERROR_LOCATION_RETURN || longitude == ERROR_LOCATION_RETURN) {
            showPermissionFailTip();
            return;
        } else {
            if (permissionDialog != null) {
                permissionDialog.dismiss();
            }
        }
        //核对位置
        checkLocation();
    }

    private void showPermissionFailTip() {
        if (permissionDialog == null) {
            permissionDialog = new AlertDialog.Builder(this).setMessage("此功能需要您授予定位权限，请前往“设置”->“应用管理”，选择“步道探秘”进行授权设置")
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
                                Toast.makeText(LocationTaskActivity.this, "前往失败，请手动前往设置->应用管理授权", Toast
                                        .LENGTH_SHORT).show();
                            }
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LocationTaskActivity.this.finish();
                        }
                    }).setCancelable(false).setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            locateHelper.startLocate();
                        }
                    }).create();
        }
        if (permissionDialog != null && !permissionDialog.isShowing()) {
            permissionDialog.show();
        }
        //        if (locateHelper != null)
        //            locateHelper.stopLocate();
        //        if (taskResultHelper != null) {
        //            taskResultHelper.stopAnim();
        //        }
    }

    private void checkLocation() {
        if (split == null || split.length <= 1) {//必定同时包含经度和纬度
            ToastUtil.TextToast("目标位置不存在");
            locateHelper.startLocate();
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
            locateHelper.startLocate();
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
        RunApplication.isNeededRefresh = true;
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
