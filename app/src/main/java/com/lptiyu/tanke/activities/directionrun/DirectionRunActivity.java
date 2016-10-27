package com.lptiyu.tanke.activities.directionrun;

import android.content.DialogInterface;
import android.graphics.Color;
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
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.entity.greendao.LocationResult;
import com.lptiyu.tanke.entity.response.DirectionRunPoint;
import com.lptiyu.tanke.entity.response.RunLine;
import com.lptiyu.tanke.entity.response.RunSignUp;
import com.lptiyu.tanke.entity.response.StartRun;
import com.lptiyu.tanke.entity.response.StopRun;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.AMapViewUtils;
import com.lptiyu.tanke.utils.DBHelper;
import com.lptiyu.tanke.utils.DBManager;
import com.lptiyu.tanke.utils.DistanceFormatUtils;
import com.lptiyu.tanke.utils.LocationHelper;
import com.lptiyu.tanke.utils.LogUtils;
import com.lptiyu.tanke.utils.MarkerOptionHelper;
import com.lptiyu.tanke.utils.TimeFormatUtils;
import com.lptiyu.tanke.utils.TracerHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lptiyu.tanke.RunApplication.gameId;
import static com.lptiyu.tanke.utils.AMapViewUtils.parseJingweiToLatLng;

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
    @BindView(R.id.img_show_all_point)
    ImageView imgShowNextTask;
    @BindView(R.id.chronometer)
    Chronometer chronometer;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    private AMap map;
    private LocationHelper locationHelper;
    private DirectionRunPresenter presenter;
    private ArrayList<DirectionRunPoint> runPoints;
    private final float DISTANCE_DELTA = 50.0f;//起跑点以及打卡点误差范围
    private ArrayList<Integer> list_distance_delta = new ArrayList<>();
    private DirectionRunPoint startedPoint;
    private DirectionRunPoint currentPoint;
    private boolean isNearByStartPoint = false;
    private List<TraceLocation> traceLocationList;
    private int traceLineID = 1000;
    private int coordinateType = LBSTraceClient.TYPE_AMAP;
    private long second;
    private boolean isStarted;
    private boolean isFinished;
    private StartRun startRun;
    private final double MIN_DEEDED_DISTANCE = 1d;//结束乐跑时，超过1km才有乐跑统计显示
    private boolean isArrivedNextPoint;
    private double runDistance = 1d;//单位：公里
    private MarkerOptionHelper markerOptionHelper;
    private Marker currentMarker;
    private DirectionRunPoint previousPoint;
    private LatLng currentLatLng;
    private LatLng previousLatLng;
    private final int INTERVAL = 1000;
    private TraceOverlay traceOverlay;
    private TracerHelper tracerHelper;

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
                previousLatLng = currentLatLng;
                currentLatLng = latLng;
                if (isStarted) {
                    //将定位点存储到数据库中
                    DBHelper.insertDataToDB(DirectionRunActivity.this, aMapLocation);
                    // 添加轨迹数据源
                    addTraceData(aMapLocation);
                    //开始绘制轨迹并记录里程数
                    startTrace();
                    drawLine();
                }
                //将地图中心移到当前位置
                moveToLocation(latLng);
                //用图标标注当前位置
                if (currentMarker != null) {
                    currentMarker.remove();
                }
                addCurrentPositionMarker(latLng);
                //判断用户是否在起跑点附近
                if (!isNearByStartPoint) {
                    checkUserIsNearByStartPoint(latLng);
                }
                //如果游戏已经开始，则验证是否到达下一个乐跑点
                if (isStarted && !isArrivedNextPoint) {
                    checkUserIsArrivedNextPoint(latLng);
                }
            }
        });
        locationHelper.setOnceLocation(false);
        locationHelper.setInterval(INTERVAL);
        locationHelper.startLocation();

        tracerHelper = new TracerHelper(this, new TracerHelper.TraceCallback() {
            @Override
            public void onRequestFailed(int lineID, String errorInfo) {
                LogUtils.i(errorInfo);
                //                if (traceOverlay != null) {
                //                    traceOverlay.setTraceStatus(TraceOverlay.TRACE_STATUS_FAILURE);
                //                }
            }

            @Override
            public void onTraceProcessing(int lineID, int index, List<LatLng> segments) {
                if (segments == null) {
                    return;
                }
                //                if (traceOverlay != null) {
                //                    traceOverlay.setTraceStatus(TraceOverlay.TRACE_STATUS_PROCESSING);
                //添加此行代码即可显示高德地图绘制的轨迹
                //                    traceOverlay.add(segments);
                //                }
            }

            @Override
            public void onFinished(int lineID, List<LatLng> list, int distance, int watingtime) {
                LogUtils.i("onFinished:有效点" + list.size() + ",路程：" + distance + "米");
                //                if (traceOverlay != null) {
                //                    traceOverlay.setTraceStatus(TraceOverlay.TRACE_STATUS_FINISH);
                //                    traceOverlay.setDistance(distance);
                //                    traceOverlay.setWaitTime(watingtime);
                //                }
                //                drawLine(list);
                DirectionRunActivity.this.runDistance = distance;
                tvDistanceValue.setText(DistanceFormatUtils.formatMeterToKiloMeter(distance) + "");
            }
        });

        markerOptionHelper = new MarkerOptionHelper();
        markerOptionHelper.icon(getResources(), R.drawable.locate_orange);

        chronometer.setText(TimeFormatUtils.formatSecond(0));
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                chronometer.setText(TimeFormatUtils.formatSecond(second++));
            }
        });

        presenter = new DirectionRunPresenter(this);
        presenter.getRunLine(gameId);
    }

    private void addTraceData(AMapLocation aMapLocation) {
        TraceLocation traceLocation = new TraceLocation();
        traceLocation.setBearing(aMapLocation.getBearing());
        traceLocation.setTime(aMapLocation.getTime());
        traceLocation.setLatitude(aMapLocation.getLatitude());
        traceLocation.setLongitude(aMapLocation.getLongitude());
        traceLocation.setSpeed(aMapLocation.getSpeed());
        if (traceLocationList == null) {
            traceLocationList = new ArrayList<>();
        }
        traceLocationList.add(traceLocation);
    }

    private void drawLine() {
        if (currentLatLng == null || previousLatLng == null) {
            return;
        }
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(5).color(Color.argb(255, 255, 0, 0));
        polylineOptions.add(previousLatLng);
        polylineOptions.add(currentLatLng);
        map.addPolyline(polylineOptions);
    }

    private void drawLine(List<LatLng> latLngs) {
        if (latLngs == null || latLngs.size() <= 1) {
            return;
        }
        for (LatLng latLng : latLngs) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.width(10).color(Color.argb(255, 255, 0, 0));
            polylineOptions.add(latLng);
            map.addPolyline(polylineOptions);
        }
    }

    //添加marker
    public void addCurrentPositionMarker(LatLng latLng) {
        if (markerOptionHelper == null) {
            markerOptionHelper = new MarkerOptionHelper();
        }
        if (map != null) {
            currentMarker = map.addMarker(markerOptionHelper.position(latLng));
            currentMarker.showInfoWindow();
        }
    }

    /**
     * 判断用户是否到达起跑点
     *
     * @param currentLaLng 当前位置的经纬度
     */
    private void checkUserIsNearByStartPoint(LatLng currentLaLng) {
        int min = getMinDistance(currentLaLng, runPoints);
        if (min == -1) {
            return;
        }
        for (int i = 0; i < list_distance_delta.size(); i++) {
            if (list_distance_delta.get(i).intValue() == min) {
                startedPoint = runPoints.get(i);
                currentPoint = runPoints.get(i);
                break;
            }
        }
        if (min > DISTANCE_DELTA) {
            tvTip.setText(String.format(getString(R.string.not_arrive_any_direction_run_point),
                    DistanceFormatUtils.formatMeterToKiloMeter(min)));
            //            Toast.makeText(this, String.format(getString(R.string.not_arrive_any_direction_run_point),
            //                    DistanceFormatUtils.formatMeterToKiloMeter(min)), Toast.LENGTH_SHORT).show();
            btnStartOrStopRun.setEnabled(false);
            isNearByStartPoint = false;
        } else {
            //可以开始乐跑了
            Toast.makeText(this, "您已到达最近的乐跑点，点击下方开始乐跑按钮开始这段旅程吧", Toast.LENGTH_SHORT).show();
            btnStartOrStopRun.setEnabled(true);
            isNearByStartPoint = true;
        }
    }

    private void checkUserIsArrivedNextPoint(LatLng currentLaLng) {
        int min = getMinDistance(currentLaLng, runPoints);
        if (min == -1) {
            return;
        }
        for (int i = 0; i < list_distance_delta.size(); i++) {
            if (list_distance_delta.get(i).intValue() == min && currentPoint != null && !runPoints.get(i).id.equals
                    (currentPoint.id)) {
                previousPoint = currentPoint;
                currentPoint = runPoints.get(i);
                if (min <= DISTANCE_DELTA) {
                    //可以开始乐跑了
                    Toast.makeText(this, "您已到达下一个乐跑点，点击下方打卡按钮签个到吧", Toast.LENGTH_SHORT).show();
                    btnSignUp.setEnabled(true);
                    isArrivedNextPoint = true;
                } else {
                    isArrivedNextPoint = false;
                    btnSignUp.setEnabled(false);
                }
                return;
            }
        }
        //从剩下的乐跑点中找出距离当前位置最近的那一个点，并计算出距离呈现给用户
        ArrayList<DirectionRunPoint> leftPoints = new ArrayList<>();
        leftPoints.addAll(runPoints);
        leftPoints.remove(currentPoint);
        int leftMin = getMinDistance(currentLaLng, leftPoints);
        tvTip.setText(String.format(getString(R.string.not_arrive_next_direction_run_point),
                DistanceFormatUtils.formatMeterToKiloMeter(leftMin)));
        //        Toast.makeText(this, String.format(getString(R.string.not_arrive_next_direction_run_point),
        //                DistanceFormatUtils.formatMeterToKiloMeter(leftMin)), Toast.LENGTH_SHORT)
        //                .show();
    }

    private int getMinDistance(LatLng currentLaLng, List<DirectionRunPoint> runPoints) {
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
        float zoom = map.getCameraPosition().zoom;//第一次会默认返回北京天安门的数据,zoom=10.0
        if (zoom > 9.99999 && zoom < 10.00001) {
            zoom = 16f;
        }
        map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, zoom, 0, 0)), 1500, null);
    }

    /**
     * 调起轨迹纠偏
     */
    private void startTrace() {
        if (traceLocationList == null || traceLocationList.size() <= 1) {
            return;
        }
        if (traceOverlay == null) {
            traceOverlay = new TraceOverlay(map);
        }
        tracerHelper.queryProcessedTrace(traceLineID, traceLocationList, coordinateType);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        textureMapView.onDestroy();
        if (locationHelper != null) {
            locationHelper.onDestroy();
        }
        //todo 用户强制杀掉进程时需要保存当前数据，以便用户在下次进入app时提示用户是否继续乐跑
        if (!isFinished) {
            if (startedPoint != null) {

            }
        }
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

    @OnClick({R.id.btn_start_or_stop_run, R.id.btn_signUp, R.id.img_show_current_location, R.id.img_show_all_point})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_or_stop_run:
                if (!isStarted) {
                    if (startedPoint == null) {
                        Toast.makeText(this, "未找到起跑点", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    btnStartOrStopRun.setClickable(false);
                    presenter.startRun(gameId, Long.parseLong(startedPoint.id));
                } else {
                    if (startRun != null) {
                        new AlertDialog.Builder(this).setMessage("您尚未完成乐跑，确定结束乐跑吗？").setNegativeButton("我点错了", null)
                                .setPositiveButton("残忍结束", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        btnStartOrStopRun.setClickable(false);
                                        LatLng startLatLng = parseJingweiToLatLng(startedPoint.jingwei);
                                        if (startLatLng == null) {
                                            Toast.makeText(DirectionRunActivity.this, "起跑点数据错误", Toast.LENGTH_SHORT)
                                                    .show();
                                            return;
                                        }
                                        DirectionRunActivity.this.runDistance = AMapUtils.calculateLineDistance
                                                (currentLatLng, startLatLng);
                                        presenter.stopRun(gameId, Long.parseLong(startRun.record_id),
                                                Double.parseDouble(DistanceFormatUtils.formatMeterToKiloMeter
                                                        (runDistance)));
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
                if (currentPoint != null && startRun != null) {
                    LatLng currentLatLng = parseJingweiToLatLng(currentPoint.jingwei);
                    LatLng previousLatLng = parseJingweiToLatLng(previousPoint.jingwei);
                    if (currentLatLng == null || previousLatLng == null) {
                        Toast.makeText(this, "打卡点数据错误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    this.runDistance = (long) AMapUtils.calculateLineDistance(currentLatLng, previousLatLng);
                    presenter.runSignUp(gameId, Long.parseLong(currentPoint.id), Long.parseLong
                            (startRun.record_id), this.runDistance);
                }
                break;
            case R.id.img_show_current_location:
                if (locationHelper != null) {
                    locationHelper.startLocation();
                }
                break;
            case R.id.img_show_all_point:
                if (runPoints == null || runPoints.size() <= 0) {
                    Toast.makeText(this, "乐跑点数据为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                LatLngBounds.Builder builder = LatLngBounds.builder();
                for (DirectionRunPoint point : runPoints) {
                    LatLng latLng = parseJingweiToLatLng(point.jingwei);
                    if (latLng != null) {
                        builder.include(latLng);
                    }
                }
                LatLngBounds bounds = builder.build();
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

                break;
        }
    }

    private void addPointToMap() {
        if (runPoints == null || runPoints.size() <= 0) {
            Toast.makeText(this, "暂无任何乐跑点", Toast.LENGTH_SHORT).show();
            return;
        } else {
            for (int i = 0; i < runPoints.size(); i++) {
                DirectionRunPoint point = runPoints.get(i);
                String[] latLng = point.jingwei.split(",");
                if (latLng == null || latLng.length < 2) {
                    Toast.makeText(this, "乐跑点数据错误", Toast.LENGTH_SHORT).show();
                } else {
                    AMapViewUtils.addMarker(map, new LatLng(Double.parseDouble(latLng[0]),
                            Double.parseDouble(latLng[1])), R.drawable.diand, i + 1 + "", "");
                }
            }
        }
    }

    private void setTraceDataFromDB() {
        //从数据库中获取轨迹点
        List<LocationResult> locationResults = DBManager.getInstance(this).queryLocationList();
        //将轨迹点转化为轨迹纠偏所需要的数据
        List<TraceLocation> traceLocations = parseLocationResultToTraceLocation(locationResults);
        if (traceLocations == null) {
            return;
        }
        if (traceLocationList == null) {
            traceLocationList = new ArrayList<>();
        }
        traceLocationList.clear();
        traceLocationList.addAll(traceLocations);
    }

    private List<TraceLocation> parseLocationResultToTraceLocation(List<LocationResult> locationResults) {
        if (locationResults == null || locationResults.size() <= 0) {
            return null;
        }
        List<TraceLocation> traceLocations = new ArrayList<>();
        for (LocationResult result : locationResults) {
            TraceLocation traceLocation = new TraceLocation();
            traceLocation.setTime(result.time);
            traceLocation.setSpeed(result.speed);
            traceLocation.setLongitude(result.longitude);
            traceLocation.setLatitude(result.latitude);
            traceLocation.setBearing(result.bearing);
            traceLocations.add(traceLocation);
        }
        return traceLocations;
    }

    @Override
    public void successStartRun(StartRun startRun) {
        this.startRun = startRun;
        btnStartOrStopRun.setClickable(true);
        btnStartOrStopRun.setText("结束乐跑");
        isStarted = true;
        // 开始计时
        chronometer.start();
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

    @Override
    public void successRunSignUp(RunSignUp runSignUp) {
        Toast.makeText(this, "打卡成功", Toast.LENGTH_SHORT).show();
        btnSignUp.setEnabled(false);
        isArrivedNextPoint = false;
    }

    @Override
    public void successStopRun(StopRun stopRun) {
        Toast.makeText(this, "结束乐跑成功", Toast.LENGTH_SHORT).show();
        btnStartOrStopRun.setEnabled(false);
        chronometer.stop();
        isStarted = false;
        isFinished = true;
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
        LogUtils.i("数据库查询结果：" + DBManager.getInstance(this).queryLocationList().size() + "条数据");
        DBManager.getInstance(this).deleteLocationAll();
    }

    private Handler handler = new Handler();

    @Override
    public void onBackPressed() {
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
