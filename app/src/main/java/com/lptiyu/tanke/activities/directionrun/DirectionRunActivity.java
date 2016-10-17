package com.lptiyu.tanke.activities.directionrun;

import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.entity.response.DirectionRunPoint;
import com.lptiyu.tanke.entity.response.RunLine;
import com.lptiyu.tanke.entity.response.RunSignUp;
import com.lptiyu.tanke.entity.response.StartRun;
import com.lptiyu.tanke.entity.response.StopRun;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.DistanceFormatUtils;
import com.lptiyu.tanke.utils.LocationHelper;
import com.lptiyu.tanke.utils.TimeFormatUtils;
import com.lptiyu.tanke.utils.TraceAsset;
import com.lptiyu.tanke.utils.TracerHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DirectionRunActivity extends MyBaseActivity implements DirectionRunContact.IDirectionRunView {

    @BindView(R.id.textureMapView)
    TextureMapView textureMapView;
    @BindView(R.id.tv_duration_value)
    TextView tvDurationValue;
    @BindView(R.id.btn_start_or_stop_run)
    Button btnStartOrStopRun;
    @BindView(R.id.tv_distance_value)
    TextView tvDistanceValue;
    @BindView(R.id.btn_signUp)
    Button btnSignUp;
    @BindView(R.id.rl_distance)
    RelativeLayout rlDistance;
    @BindView(R.id.rl_bottom)
    LinearLayout rlBottom;
    @BindView(R.id.img_show_current_location)
    ImageView imgShowCurrentLocation;
    @BindView(R.id.img_show_next_task)
    ImageView imgShowNextTask;
    @BindView(R.id.chronometer)
    Chronometer chronometer;
    private AMap map;
    private LocationHelper locationHelper;
    private MarkerOptions currentMarkerOptions;
    private DirectionRunPresenter presenter;
    private ArrayList<DirectionRunPoint> runPoints;
    private final float DISTANCE_DELTA = 200.0f;
    private ArrayList<Integer> list_distance_delta = new ArrayList<>();
    private DirectionRunPoint startedPoint;
    private boolean isNearByStartPoint = false;
    private TracerHelper tracerHelper;
    private ConcurrentMap<Integer, TraceOverlay> mOverlayList = new ConcurrentHashMap();
    private List<TraceLocation> mTraceList;
    private int mSequenceLineID = 1000;
    private int mCoordinateType = LBSTraceClient.TYPE_AMAP;
    private long second;
    private boolean isStarted;
    private StartRun startRun;
    private final double MIN_DEEDED_DISTANCE = 1d;//结束乐跑时，超过1km才有乐跑统计显示
    private DirectionRunPoint nextPoint;
    private boolean isArrivedNextPoint;
    private long distance;
    private boolean isRunFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction_run);
        ButterKnife.bind(this);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        textureMapView.onCreate(savedInstanceState);
        if (map == null) {
            map = textureMapView.getMap();
            map.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);//定位、移动到地图中心点并跟随。
        }
        init();
    }

    private void init() {
        locationHelper = new LocationHelper(this, new LocationHelper.OnLocationResultListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (isArrivedNextPoint) {
                    return;
                }
                LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                //将地图中心移到当前位置
                moveToLocation(latLng);
                //用图标标注当前位置
                addMarker(latLng);
                //判断用户是否在起跑点附近
                if (!isNearByStartPoint) {
                    checkUserIsNearByStartPoint(latLng);
                }
                //如果游戏已经开始，则验证是否到达下一个乐跑点
                if (isStarted) {
                    checkUserIsArrivedNextPoint(latLng);
                }
            }
        });
        locationHelper.setOnceLocation(false);
        locationHelper.setInterval(5000);
        locationHelper.startLocation();

        tracerHelper = new TracerHelper(this, new TracerHelper.TraceCallback() {
            @Override
            public void onRequestFailed(int lineID, String errorInfo) {
                Toast.makeText(DirectionRunActivity.this, "轨迹纠偏失败:" + errorInfo, Toast.LENGTH_SHORT).show();
                if (mOverlayList.containsKey(lineID)) {
                    TraceOverlay overlay = mOverlayList.get(lineID);
                    overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_FAILURE);
                }
            }

            @Override
            public void onTraceProcessing(int lineID, int i1, List<LatLng> segments) {
                if (segments == null) {
                    return;
                }
                if (mOverlayList.containsKey(lineID)) {
                    TraceOverlay overlay = mOverlayList.get(lineID);
                    overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_PROCESSING);
                    overlay.add(segments);
                }
            }

            @Override
            public void onFinished(int lineID, List<LatLng> list, int distance, int watingtime) {
                Toast.makeText(DirectionRunActivity.this, "轨迹纠偏完毕", Toast.LENGTH_SHORT).show();
                if (mOverlayList.containsKey(lineID)) {
                    TraceOverlay overlay = mOverlayList.get(lineID);
                    overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_FINISH);
                    overlay.setDistance(distance);
                    overlay.setWaitTime(watingtime);
                }
            }
        });

        chronometer.setText(TimeFormatUtils.formatSecond(0));
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                chronometer.setText(TimeFormatUtils.formatSecond(second++));
            }
        });

        presenter = new DirectionRunPresenter(this);
        presenter.getRunLine(RunApplication.gameId);
    }

    /**
     * 判断用户是否到达起跑点
     *
     * @param currentLaLng 当前位置的经纬度
     */
    private void checkUserIsNearByStartPoint(LatLng currentLaLng) {
        int min = getMinDistancePoint(currentLaLng);
        if (min == -1) {
            return;
        }
        for (int i = 0; i < list_distance_delta.size(); i++) {
            if (list_distance_delta.get(i).intValue() == min) {
                startedPoint = runPoints.get(i);
                break;
            }
        }
        if (min > DISTANCE_DELTA) {
            Toast.makeText(this, String.format(getString(R.string.not_arrive_any_direction_run_point),
                    DistanceFormatUtils.formatMeter(min)), Toast.LENGTH_SHORT).show();
        } else {
            //可以开始乐跑了
            Toast.makeText(this, "您已到达最近的乐跑点，点击下方开始乐跑按钮开始这段旅程吧", Toast.LENGTH_SHORT).show();
            btnStartOrStopRun.setEnabled(true);
            locationHelper.stopLocation();
            isNearByStartPoint = true;
        }
    }

    private void checkUserIsArrivedNextPoint(LatLng currentLaLng) {
        int min = getMinDistancePoint(currentLaLng);
        if (min == -1) {
            return;
        }
        if (list_distance_delta.size() <= 0) {
            //所有乐跑点都已打卡
            Toast.makeText(this, "恭喜你所有乐跑点全部已打卡", Toast.LENGTH_SHORT).show();
            isRunFinished = true;
            return;
        }
        for (int i = 0; i < list_distance_delta.size(); i++) {
            if (list_distance_delta.get(i).intValue() == min) {
                nextPoint = runPoints.get(i);
                break;
            }
        }
        if (min > DISTANCE_DELTA) {
            Toast.makeText(this, String.format(getString(R.string.not_arrive_next_direction_run_point),
                    DistanceFormatUtils.formatMeter(min)), Toast.LENGTH_SHORT).show();
        } else {
            //可以开始乐跑了
            Toast.makeText(this, "您已到达下一个乐跑点，点击下方打卡按钮签个到吧", Toast.LENGTH_SHORT).show();
            btnSignUp.setEnabled(true);
            locationHelper.stopLocation();
            isArrivedNextPoint = true;
        }
    }

    private int getMinDistancePoint(LatLng currentLaLng) {
        if (currentLaLng == null) {
            Toast.makeText(this, "未获取到当前位置", Toast.LENGTH_SHORT).show();
            return -1;
        }
        if (runPoints == null || runPoints.size() <= 0) {
            return -1;
        }
        list_distance_delta.clear();
        for (DirectionRunPoint point : runPoints) {
            String[] latLng = point.jingwei.split(",");
            if (latLng == null || latLng.length <= 1) {
                Toast.makeText(this, "乐跑点数据错误", Toast.LENGTH_SHORT).show();
            } else {
                LatLng pointLatLng = new LatLng(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]));
                float distance = AMapUtils.calculateLineDistance(currentLaLng, pointLatLng);
                list_distance_delta.add((int) distance);
            }
        }
        return Collections.min(list_distance_delta).intValue();
    }

    //地图中心移动到指定位置
    private void moveToLocation(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition(latLng, 13, 0, 0);
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);
    }

    //添加marker,带文字显示
    private void addMarker(LatLng latLng, String title, String snippet) {
        if (currentMarkerOptions == null)
            currentMarkerOptions = new MarkerOptions();
        currentMarkerOptions.position(latLng).title("当前位置：" + title).snippet(snippet).draggable(true);
        // 将Marker设置为贴地显示，可以双指下拉看效果
        currentMarkerOptions.setFlat(true);
        if (map != null) {
            Marker marker = map.addMarker(currentMarkerOptions);
            marker.showInfoWindow();
        }
    }

    //添加marker
    private void addMarker(LatLng latLng) {
        if (currentMarkerOptions == null)
            currentMarkerOptions = new MarkerOptions();
        currentMarkerOptions.position(latLng).draggable(true);
        currentMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R
                .drawable.locate_orange)));
        // 将Marker设置为贴地显示，可以双指下拉看效果
        currentMarkerOptions.setFlat(true);
        if (map != null) {
            Marker marker = map.addMarker(currentMarkerOptions);
            marker.showInfoWindow();
        }
    }

    //添加marker
    private void addMarker(LatLng latLng, int imgResource) {
        if (currentMarkerOptions == null)
            currentMarkerOptions = new MarkerOptions();
        currentMarkerOptions.position(latLng).draggable(true);
        currentMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R
                .drawable.ic_launcher)));
        // 将Marker设置为贴地显示，可以双指下拉看效果
        currentMarkerOptions.setFlat(true);
        if (map != null) {
            Marker marker = map.addMarker(currentMarkerOptions);
            marker.showInfoWindow();
        }
    }

    /**
     * 调起一次轨迹纠偏
     */
    private void traceGrasp() {
        if (mTraceList == null) {
            Toast.makeText(this, "轨迹点为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //        Toast.makeText(this, "共有" + mTraceList.size() + "个数据点", Toast.LENGTH_SHORT).show();
        TraceOverlay mTraceOverlay = new TraceOverlay(map);
        List<LatLng> mapList = traceLocationToMap(mTraceList);
        mTraceOverlay.setProperCamera(mapList);
        mOverlayList.put(mSequenceLineID, mTraceOverlay);
        tracerHelper.queryProcessedTrace(mSequenceLineID, mTraceList, mCoordinateType);
    }

    /**
     * 轨迹纠偏点转换为地图LatLng
     *
     * @param traceLocationList
     * @return
     */
    public List<LatLng> traceLocationToMap(List<TraceLocation> traceLocationList) {
        List<LatLng> mapList = new ArrayList();
        for (TraceLocation location : traceLocationList) {
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            mapList.add(latlng);
        }
        return mapList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        textureMapView.onDestroy();
        if (locationHelper != null) {
            locationHelper.onDestroy();
        }
        //TODO 用户强制杀掉进程时需要保存当前数据，以便用户在下次进入app时提示用户是否继续乐跑
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        textureMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        textureMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        textureMapView.onSaveInstanceState(outState);
    }

    @OnClick({R.id.btn_start_or_stop_run, R.id.btn_signUp, R.id.img_show_current_location, R.id.img_show_next_task})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_or_stop_run:
                if (!isStarted) {
                    if (startedPoint == null) {
                        Toast.makeText(this, "未找到起跑点", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    btnStartOrStopRun.setClickable(false);
                    presenter.startRun(RunApplication.gameId, Long.parseLong(startedPoint.id));
                } else {
                    if (startRun != null) {
                        new AlertDialog.Builder(this).setMessage("您尚未完成乐跑，确定结束乐跑吗？").setNegativeButton("我点错了", null)
                                .setPositiveButton("残忍结束", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        btnStartOrStopRun.setClickable(false);
                                        presenter.stopRun(RunApplication.gameId, Long.parseLong(startRun.record_id),
                                                distance);
                                    }
                                }).show();
                    } else {
                        Toast.makeText(this, "未获取到开始乐跑返回的结果", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.btn_signUp:
                btnSignUp.setEnabled(false);
                isArrivedNextPoint = true;
                presenter.runSignUp(RunApplication.gameId, Long.parseLong(nextPoint.id), Long.parseLong(startRun
                        .record_id), distance);
                break;
            case R.id.img_show_current_location:
                if (locationHelper != null) {
                    locationHelper.startLocation();
                }
                break;
            case R.id.img_show_next_task:
                break;
        }
    }

    @Override
    public void successStartRun(StartRun startRun) {
        this.startRun = startRun;
        btnStartOrStopRun.setClickable(true);
        btnStartOrStopRun.setText("结束乐跑");
        isStarted = true;
        isNearByStartPoint = false;
        if (locationHelper != null) {
            locationHelper.setInterval(1000);
            locationHelper.setOnceLocation(false);
            locationHelper.startLocation();
        }
        //TODO 开始绘制轨迹并记录里程数
        //        setTraceDataFromAssets();
        //        traceGrasp();
        // 开始计时
        chronometer.start();
    }

    private void setTraceDataFromAssets() {
        List<TraceLocation> traceLocations = TraceAsset.parseLocationsData(DirectionRunActivity.this.getAssets(),
                "traceRecord" + File.separator + "AMapTrace.txt");
        if (mTraceList == null) {
            mTraceList = new ArrayList<>();
        }
        mTraceList.clear();
        mTraceList.addAll(traceLocations);
    }

    @Override
    public void successGetRunLine(RunLine runLine) {
        if (runLine != null && runLine.point_list.size() > 0) {
            runPoints = new ArrayList<>();
            runPoints.addAll(runLine.point_list);
            //将点绘制在地图上
            addPointToMap();
        } else {
            Toast.makeText(this, "暂无乐跑路线数据", Toast.LENGTH_SHORT).show();
        }
    }

    private void addPointToMap() {
        if (runPoints == null || runPoints.size() <= 0) {
            Toast.makeText(this, "暂无任何乐跑点", Toast.LENGTH_SHORT).show();
            return;
        } else {
            for (DirectionRunPoint point : runPoints) {
                String[] latLng = point.jingwei.split(",");
                if (latLng == null || latLng.length < 2) {
                    Toast.makeText(this, "乐跑点数据错误", Toast.LENGTH_SHORT).show();
                } else {
                    addMarker(new LatLng(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1])), -1);
                }
            }
        }
    }

    @Override
    public void successRunSignUp(RunSignUp runSignUp) {
        Toast.makeText(this, "打卡成功", Toast.LENGTH_SHORT).show();
        btnSignUp.setEnabled(false);
        isArrivedNextPoint = false;
    }

    @Override
    public void successStopRun(StopRun stopRun) {
        Toast.makeText(this, "结束乐跑成功", Toast.LENGTH_SHORT).show();
        if (stopRun != null) {
            if (Double.parseDouble(stopRun.distance) < MIN_DEEDED_DISTANCE) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 500);
            } else {
                //显示乐跑统计结果popupwindow
                Toast.makeText(this, "您一共跑了" + stopRun.distance + "公里", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "结束乐跑返回数据为空", Toast.LENGTH_SHORT).show();
        }
    }

    private Handler handler = new Handler();

    @Override
    public void onBackPressed() {
        //        super.onBackPressed();
        if (isStarted) {
            new AlertDialog.Builder(this).setTitle("").setMessage("无法退出当前界面，您需要先结束乐跑")
                    .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        } else {
            super.onBackPressed();
        }
    }
}
