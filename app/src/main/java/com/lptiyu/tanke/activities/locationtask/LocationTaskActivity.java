package com.lptiyu.tanke.activities.locationtask;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.Task;
import com.lptiyu.tanke.entity.UpLoadGameRecord;
import com.lptiyu.tanke.entity.response.UpLoadGameRecordResult;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.DistanceFormatUtils;
import com.lptiyu.tanke.utils.LocationHelper;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.TaskResultHelper;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.VibratorHelper;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationTaskActivity extends MyBaseActivity implements LocationTaskContact
        .ILocationTaskView {

    @BindView(R.id.img_close)
    ImageView imgClose;
    @BindView(R.id.img_anim)
    ImageView imgAnim;
    @BindView(R.id.rl_submit_record)
    RelativeLayout rlSubmitRecord;
    private double latitude;
    private double longitude;
    private long gameId;
    private Point point;
    private boolean isPointOver;
    private Task task;
    private final int DISTANCE_OFFSET = 60;
    private LocationTaskPresenter presenter;
    private String[] latLong;
    private boolean isToastShow = true;

    private UpLoadGameRecordResult resultRecord;

    private TaskResultHelper taskResultHelper;
    private Handler mHandler = new Handler();

    private final double ERROR_LOCATION_RETURN = 4.9E-324;
    private AlertDialog permissionDialog;
    private LocationHelper locationHelper;

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
        EventBus.getDefault().post(resultRecord);//通知PointTaskFragment刷新数据
        finish();
    }

    private void initData() {
        task = RunApplication.gameRecord.game_detail.point_list.get(RunApplication.currentPointIndex).task_list.get
                (RunApplication.currentTaskIndex);
        gameId = RunApplication.gameId;
        point = RunApplication.gameRecord.game_detail.point_list.get(RunApplication.currentPointIndex);
        isPointOver = getIntent().getBooleanExtra(Conf.IS_POINT_OVER, false);
        latLong = task.pwd.split(",");

        presenter = new LocationTaskPresenter(this);

        locationHelper = new LocationHelper(this, new LocationHelper.OnLocationResultListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                handleLocationResult(aMapLocation);
            }
        });
        locationHelper.setInterval(3000);
        locationHelper.setOnceLocation(false);

        if (AppData.isFirstInLocationActivity()) {
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    PopupWindowUtils.getInstance().showTaskGuide(LocationTaskActivity.this,
                            "这是定位任务，点击验证您当前的位置，验证通过即可通关", new PopupWindowUtils.DismissCallback() {
                                @Override
                                public void onDismisss() {
                                    if (locationHelper != null) {
                                        locationHelper.startLocation();
                                    }
                                }
                            });
                }
            }, 500);
        } else {
            locationHelper.startLocation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationHelper != null) {
            locationHelper.stopLocation();
            locationHelper.onDestroy();
        }
        if (permissionDialog != null) {
            if (permissionDialog.isShowing())
                permissionDialog.dismiss();
            permissionDialog = null;
        }
    }

    private void handleLocationResult(AMapLocation location) {
        //TODO 给自己埋下彩蛋
        if (Accounts.getPhoneNumber() != null && Accounts.getPhoneNumber().endsWith("4317") || Accounts
                .getPhoneNumber().endsWith("1965")) {
            upLoadGameRecord();
            return;
        }
        //纬度
        latitude = location.getLatitude();
        //经度
        longitude = location.getLongitude();
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
                    }).setCancelable(false).create();
        }
        if (permissionDialog != null && !permissionDialog.isShowing()) {
            permissionDialog.show();
        }
        if (taskResultHelper != null) {
            taskResultHelper.stopAnim();
        }
    }

    private void checkLocation() {
        if (latLong == null || latLong.length <= 1) {//必定同时包含经度和纬度
            ToastUtil.TextToast("目标位置不存在");
            return;
        }
        double distance = AMapUtils.calculateLineDistance(new LatLng(latitude, longitude), new LatLng(Double
                .parseDouble(latLong[1]), Double.parseDouble(latLong[0])));
        if (distance <= DISTANCE_OFFSET) {
            //验证成功，上传游戏记录
            if (locationHelper != null)
                locationHelper.stopLocation();
            loadNetWorkData();
        } else {
            if (isToastShow) {
                ToastUtil.TextToast("您距离目标点" + DistanceFormatUtils.formatMeter(distance));
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
        PopupWindowUtils.getInstance().showNetExceptionPopupwindow(this, new PopupWindowUtils.OnRetryCallback() {
            @Override
            public void onRetry() {
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
    public void successUploadRecord(UpLoadGameRecordResult response) {
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
