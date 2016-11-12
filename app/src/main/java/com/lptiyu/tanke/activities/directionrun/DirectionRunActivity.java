package com.lptiyu.tanke.activities.directionrun;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.imagedistinguish.MyCountDownTimer;
import com.lptiyu.tanke.entity.greendao.DRLocalData;
import com.lptiyu.tanke.entity.greendao.LocationResult;
import com.lptiyu.tanke.entity.response.DirectionRunPoint;
import com.lptiyu.tanke.entity.response.RunLine;
import com.lptiyu.tanke.entity.response.RunSignUp;
import com.lptiyu.tanke.entity.response.StartRun;
import com.lptiyu.tanke.entity.response.StopRun;
import com.lptiyu.tanke.entity.response.UploadDRFile;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.AMapViewUtils;
import com.lptiyu.tanke.utils.DBHelper;
import com.lptiyu.tanke.utils.DBManager;
import com.lptiyu.tanke.utils.DateUtils;
import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.DisplayUtils;
import com.lptiyu.tanke.utils.DistanceFormatUtils;
import com.lptiyu.tanke.utils.FileHelper;
import com.lptiyu.tanke.utils.LocationHelper;
import com.lptiyu.tanke.utils.LogUtils;
import com.lptiyu.tanke.utils.MarkerOptionHelper;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.TimeUtils;
import com.lptiyu.tanke.utils.TracerHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lptiyu.tanke.RunApplication.gameId;
import static com.lptiyu.tanke.utils.AMapViewUtils.parseJingweiToLatLng;
import static java.lang.System.currentTimeMillis;

public class DirectionRunActivity extends MyBaseActivity implements DirectionRunContact.IDirectionRunView {

    @BindView(R.id.textureMapView)
    TextureMapView textureMapView;
    @BindView(R.id.tv_startrun_or_stoprun_or_signup)
    TextView tvStartStopSignup;
    @BindView(R.id.tv_distance_value)
    TextView tvDistanceValue;
    @BindView(R.id.rl_distance)
    RelativeLayout rlDistance;
    @BindView(R.id.chronometer)
    Chronometer chronometer;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    private AMap map;
    private LocationHelper locationHelper;
    private DirectionRunPresenter presenter;
    private ArrayList<DirectionRunPoint> runPoints;
    private ArrayList<Integer> list_distance_delta = new ArrayList<>();
    ArrayList<DirectionRunPoint> leftPoints = new ArrayList<>();
    private DirectionRunPoint startedPoint;
    private DirectionRunPoint currentPoint;
    private DirectionRunPoint previousPoint;
    private boolean isNearByStartPoint = false;
    private List<TraceLocation> traceLocationList;
    private int coordinateType = LBSTraceClient.TYPE_AMAP;
    private long second;
    private boolean isStarted = false;
    private boolean isFinished;
    private StartRun startRun;
    private boolean isArrivedNextPoint;
    private MarkerOptionHelper markerOptionHelper;
    private Marker currentMarker;
    private LatLng currentLatLng;
    private LatLng previousLatLng;
    private TraceOverlay traceOverlay;
    private TracerHelper tracerHelper;
    private boolean isFirstEnter = true;
    private MyCountDownTimer saveDataTimer;
    private int traceLineID = 1000;
    private final int INTERVAL = 3000;//定位间隔时间，单位：毫秒
    private final float GPS_ACCURACY = 11.0f;//GPS定位精度，值越小越精确，单位：米
    private final float WIFI_ACCURACY = 100.0f;//wifi定位数据精度，值越小越精确，单位：米
    private final float CELL_ACCURACY = 500.0f;//基站定位数据精度，值越小越精确，单位：米
    private double totalDistance;//总里程数，单位：米
    private final double MIN_DEEDED_DISTANCE = 1000d;//结束乐跑时，超过1000m才有乐跑统计显示，单位：米
    private final float DISTANCE_DELTA = 100.0f;//起跑点以及打卡点误差范围
    private final int millisInFuture = 6000;//保存本地数据的总时间//TODO 正式版需要改为60000
    private final int countDownInterval = 1000;//保存本地数据的时间间隔
    private final String START_RUN = "开始乐跑";
    private final String STOP_RUN = "结束乐跑";
    private final String SIGN_UP = "打卡";
    private final String ArriveStartPointTip = "你已在打卡点范围，可以开始乐跑";
    private final String CanSignUp = "你已在打卡点范围，可以打卡";
    private final String NotArriveStartPointTip = "当前位置不在打卡点范围,请前往任一打卡点";
    private final String TooShortToSaveData = "距离过短，数据无法保存";
    private final String StopRunAndSaveData = "结束并保存数据";
    //    private int minLeftIndexDynamic = -1;//下一个打卡点的角标，根据定位结果实时变化
    //    private int minStartIndexDynamic = -1;//起跑点的角标，根据定位结果实时变化
    private String timestamp = "123456789,123456789,123456789";//上传轨迹文件时要传的时间戳（每走一公里插入一个时间戳）
    private float time_stamp_distance = 1000;//每个一公里插入一个时间戳
    private StringBuilder timeStampBuilder = new StringBuilder();
    private long startTime;
    private LatLng lastLatLngOnLocalData;//从数据恢复中的最后一个点
    private DRLocalData drLocalData;//从本地恢复的数据
    private boolean isLocationErrorToastShow = false;
    private boolean isLastDataAccuracy;//上一次定位点是否精确
    private String lastPointId;
    private String recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction_run);
        ButterKnife.bind(this);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        textureMapView.onCreate(savedInstanceState);
        if (map == null) {
            map = textureMapView.getMap();
            map.setMyLocationType(AMap.MAP_TYPE_NORMAL);
            UiSettings uiSettings = map.getUiSettings();
            uiSettings.setZoomControlsEnabled(false);
            uiSettings.setLogoBottomMargin(-200);
            uiSettings.setLogoLeftMargin(-200);
        }
        init();
    }

    private void init() {
        recoveryDataFromLocal();
        initSaveDataTimer();
        initLocationHelper();
        initTraceHelper();
        initMarkerOptionHelper();
        initChronometer();

        presenter = new DirectionRunPresenter(this);
        presenter.getRunLine(gameId);
    }

    //从本地恢复数据
    private void recoveryDataFromLocal() {
        boolean isRecovery = getIntent().getBooleanExtra(Conf.RECOVERY, false);
        if (isRecovery) {
            List<DRLocalData> list = DBManager.getInstance(this).queryDRLocalData();
            if (list != null && list.size() > 0) {
                drLocalData = list.get(0);
                //读取本地轨迹文件，在地图上绘制出来，
                List<LocationResult> locationList = DBManager.getInstance(this).queryLocationList();
                if (locationList != null && locationList.size() > 0) {
                    ArrayList<LatLng> latLngs = new ArrayList<>();
                    for (LocationResult result : locationList) {
                        latLngs.add(new LatLng(result.latitude, result.longitude));
                        //恢复轨迹和里程，用于进行轨迹纠偏
                        addTraceData(result);
                    }
                    //从数据恢复中的最后一个点
                    lastLatLngOnLocalData = latLngs.get(latLngs.size() - 1);
                    drawLine(latLngs);
                }
                //恢复显示时间和路程值
                totalDistance = Double.parseDouble(drLocalData.totalDistance);
                tvDistanceValue.setText(DistanceFormatUtils.formatMeterToKiloMeter(totalDistance) + "");
                second = (System.currentTimeMillis() - Long.parseLong(drLocalData.startTime)) / 1000;
                chronometer.start();
                RunApplication.gameId = Long.parseLong(drLocalData.game_id);
                tvStartStopSignup.setText(STOP_RUN);
                isStarted = true;
                recordId = drLocalData.record_id;
                lastPointId = drLocalData.previousPointId;
                setPreviousPoint();

            }
        }
    }

    private void setPreviousPoint() {
        if (runPoints != null && !TextUtils.isEmpty(lastPointId)) {
            for (DirectionRunPoint point : runPoints) {
                if (point.id.equals(lastPointId)) {
                    previousPoint = point;
                    break;
                }
            }
        }
    }

    //初始化用户用时计时器
    private void initChronometer() {
        chronometer.setText(TimeUtils.formatSecond(second));
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                chronometer.setText(TimeUtils.formatSecond(second++));
            }
        });
    }

    //初始化Marker
    private void initMarkerOptionHelper() {
        markerOptionHelper = new MarkerOptionHelper();
        markerOptionHelper.icon(getResources(), R.drawable.locate_orange);
    }

    //初始化自定义轨迹纠偏对象
    private void initTraceHelper() {
        tracerHelper = new TracerHelper(this, new TracerHelper.TraceCallback() {
            @Override
            public void onRequestFailed(int lineID, String errorInfo) {
                LogUtils.i(errorInfo);
            }

            @Override
            public void onTraceProcessing(int lineID, int index, List<LatLng> segments) {
            }

            @Override
            public void onFinished(int lineID, List<LatLng> list, int distance, int watingtime) {
                //每个一公里插入一个时间戳，用于上传轨迹文件时的参数
                if (distance >= time_stamp_distance) {
                    timeStampBuilder.append(currentTimeMillis() / 1000).append(",");
                    time_stamp_distance = 2 * time_stamp_distance;
                }
                DirectionRunActivity.this.totalDistance = distance;
                tvDistanceValue.setText(DistanceFormatUtils.formatMeterToKiloMeter(distance) + "");
            }
        });
    }

    //初始化定位服务
    private void initLocationHelper() {
        locationHelper = new LocationHelper(this, new LocationHelper.OnLocationResultListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                LogUtils.i("accuracy", "定位数据精度：" + aMapLocation.getAccuracy() + ", ErrorCode:" + aMapLocation
                        .getErrorCode() + ", ErrorInfo:" + aMapLocation.getErrorInfo() + ", 定位类型：" + aMapLocation
                        .getLocationType());
                //错误码对照表请见http://lbs.amap.com/api/android-location-sdk/guide/utilities/errorcode/#v2
                if (aMapLocation.getErrorCode() != AMapLocation.LOCATION_SUCCESS) {
                    if (!isLocationErrorToastShow) {//防止重复提醒
                        Toast.makeText(DirectionRunActivity.this, "定位异常，已停止定位", Toast.LENGTH_SHORT).show();
                        isLocationErrorToastShow = true;
                    }
                    return;
                }
                /*
 0 	定位失败 	请通过AMapLocation.getErrorCode()方法获取错误码，并参考错误码对照表进行问题排查。
1 	GPS定位结果 	通过设备GPS定位模块返回的定位结果，精度较高，在10米－100米左右
2 	前次定位结果 	网络定位请求低于1秒、或两次定位之间设备位置变化非常小时返回，设备位移通过传感器感知。
4 	缓存定位结果 	返回一段时间前设备在同样的位置缓存下来的网络定位结果
5 	Wifi定位结果 	属于网络定位，定位精度相对基站定位会更好，定位精度较高，在5米－200米之间。
6 	基站定位结果 	纯粹依赖移动、连通、电信等移动网络定位，定位精度在500米-5000米之间。
8 	离线定位结果
                * */
                //只取GPS定位结果
                switch (aMapLocation.getLocationType()) {
                    case AMapLocation.LOCATION_TYPE_GPS://GPS定位结果
                        if (aMapLocation.getAccuracy() > GPS_ACCURACY) {
                            isLastDataAccuracy = false;
                            return;
                        }
                        isLastDataAccuracy = true;
                        break;
                    case AMapLocation.LOCATION_TYPE_WIFI://wifi定位结果
                        //                        if (aMapLocation.getAccuracy() > WIFI_ACCURACY) {
                        //                            isLastDataAccuracy = false;
                        //                            return;
                        //                        }
                        //                        isLastDataAccuracy = true;
                        //                        break;
                    case AMapLocation.LOCATION_TYPE_CELL://基站定位结果
                        //基站定位结果直接忽略
                        return;
                    case AMapLocation.LOCATION_TYPE_SAME_REQ://上一次定位结果
                        if (!isLastDataAccuracy) {
                            return;
                        }
                        isLastDataAccuracy = true;
                        break;

                }
                isLocationErrorToastShow = false;
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
                //第一次进入时将地图中心移到当前位置
                if (isFirstEnter) {
                    moveToLocation(latLng);
                }
                //用图标标注当前位置
                if (currentMarker != null) {
                    currentMarker.remove();
                }
                addCurrentPositionMarker(latLng);
                //不管准不准确，都要先判断是否到达起跑点或者到达下一个打卡点
                //判断用户是否在起跑点附近
                if (!isStarted) {
                    checkUserIsNearByStartPoint(latLng);
                } else {
                    //如果游戏已经开始，则验证是否到达下一个乐跑点
                    checkUserIsArrivedNextPoint(latLng);
                }
            }
        });
        locationHelper.setOnceLocation(false);
        locationHelper.setInterval(INTERVAL);
        locationHelper.startLocation();
    }

    /**
     * 判断用户是否到达起跑点
     *
     * @param currentLaLng 当前位置的经纬度
     */
    private void checkUserIsNearByStartPoint(LatLng currentLaLng) {
        if (runPoints == null) {
            return;
        }
        //获取乐跑点中离当前位置最近的点，计算出其距离
        int min = getMinDistance(currentLaLng, runPoints);
        if (min == -1) {
            return;
        }
        for (int i = 0; i < list_distance_delta.size(); i++) {
            if (list_distance_delta.get(i).intValue() == min) {
                startedPoint = runPoints.get(i);
                break;
            }
        }
        LogUtils.i("距离最近的起跑点：" + min + "米");
        if (min > DISTANCE_DELTA) {
            isNearByStartPoint = false;
            tvTip.setVisibility(View.GONE);
        } else {
            //已到达起跑点范围内，可以开始乐跑了
            tvTip.setVisibility(View.VISIBLE);
            tvTip.setText(ArriveStartPointTip);
            isNearByStartPoint = true;
        }
    }

    private void checkUserIsArrivedNextPoint(LatLng currentLaLng) {
        if (runPoints == null || previousPoint == null) {
            return;
        }
        leftPoints.clear();
        leftPoints.addAll(runPoints);
        //移除上一个打卡点（当前打卡点不能跟上一个打卡点相同）
        for (DirectionRunPoint point : leftPoints) {
            if (point.id == previousPoint.id) {
                leftPoints.remove(point);
                break;
            }
        }
        float minDistance = Float.MAX_VALUE;
        for (int i = 0; i < leftPoints.size(); i++) {
            String[] latLng = leftPoints.get(i).jingwei.split(",");
            if (latLng != null && latLng.length == 2) {
                LatLng pointLatLng = new LatLng(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]));
                float distance = AMapUtils.calculateLineDistance(currentLaLng, pointLatLng);
                if (distance < minDistance) {
                    minDistance = distance;
                    currentPoint = leftPoints.get(i);
                }
            }
        }
        LogUtils.i("距离最近的打卡点：" + minDistance + "米");
        if (minDistance <= DISTANCE_DELTA) {
            //已到达下一个打卡点附近，可以打卡了
            tvTip.setVisibility(View.VISIBLE);
            tvTip.setText(CanSignUp);
            tvStartStopSignup.setText(SIGN_UP);
            isArrivedNextPoint = true;
        } else {
            tvTip.setVisibility(View.GONE);
            tvStartStopSignup.setText(STOP_RUN);
            isArrivedNextPoint = false;
        }
    }

    private int getMinDistance(LatLng currentLaLng, List<DirectionRunPoint> runPoints) {
        if (currentLaLng == null) {
            return -1;
        }
        if (runPoints == null || runPoints.size() <= 0) {
            return -1;
        }
        list_distance_delta.clear();
        for (DirectionRunPoint point : runPoints) {
            String[] latLng = point.jingwei.split(",");
            if (latLng != null && latLng.length == 2) {
                LatLng pointLatLng = new LatLng(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]));
                float distance = AMapUtils.calculateLineDistance(currentLaLng, pointLatLng);
                list_distance_delta.add((int) distance);
            }
        }
        return Collections.min(list_distance_delta).intValue();
    }

    //初始化一个计时器，每个一分钟保存一次本地数据
    private void initSaveDataTimer() {
        saveDataTimer = new MyCountDownTimer(millisInFuture, countDownInterval, new MyCountDownTimer
                .ICountDownTimerListener() {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                saveDataToLocal();
                initSaveDataTimer();
                saveDataTimer.start();
            }
        });
    }

    //添加轨迹纠偏的数据源
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

    //添加轨迹纠偏的数据源
    private void addTraceData(LocationResult locationResult) {
        TraceLocation traceLocation = new TraceLocation();
        traceLocation.setBearing(locationResult.getBearing());
        traceLocation.setTime(locationResult.getTime());
        traceLocation.setLatitude(locationResult.getLatitude());
        traceLocation.setLongitude(locationResult.getLongitude());
        traceLocation.setSpeed(locationResult.getSpeed());
        if (traceLocationList == null) {
            traceLocationList = new ArrayList<>();
        }
        traceLocationList.add(traceLocation);
    }

    //根据两个点绘制线条作为运动轨迹
    private void drawLine() {
        if (currentLatLng == null || previousLatLng == null) {
            return;
        }
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(DisplayUtils.dp2px(6)).color(Color.argb(255, 255, 0, 0));
        polylineOptions.add(previousLatLng);
        polylineOptions.add(currentLatLng);
        map.addPolyline(polylineOptions);
    }

    //根据点集合绘制线条作为运动轨迹
    private void drawLine(List<LatLng> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(DisplayUtils.dp2px(6)).color(Color.argb(255, 255, 0, 0));
        polylineOptions.addAll(list);
        map.addPolyline(polylineOptions);
    }

    //添加当前位置的marker
    public void addCurrentPositionMarker(LatLng latLng) {
        if (markerOptionHelper == null) {
            markerOptionHelper = new MarkerOptionHelper();
        }
        if (map != null) {
            currentMarker = map.addMarker(markerOptionHelper.position(latLng));
            currentMarker.showInfoWindow();
        }
    }

    //地图中心移动到指定位置
    private void moveToLocation(LatLng latLng) {
        float zoom = map.getCameraPosition().zoom;//第一次会默认返回北京天安门的数据,zoom=10.0
        if (zoom > 9.99999 && zoom < 10.00001) {
            zoom = 16f;
        }
        int duration = 1;
        if (isFirstEnter) {
            duration = 1;
            isFirstEnter = false;
        } else {
            duration = 1000;
        }
        map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, zoom, 0, 0)), duration,
                null);
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


    /*
    * 数据保存，要保存的数据有：id,game_id,总用时，总路程，轨迹文件，previousPoint的一些信息;
    * */
    private void saveDataToLocal() {
        DBManager.getInstance(DirectionRunActivity.this).deleteDRLocalAll();
        if (isStarted && !isFinished) {
            DRLocalData data = new DRLocalData();
            data.game_id = gameId + "";
            if (previousPoint != null) {
                data.previousPointId = previousPoint.id;
                data.previousPointLatitude = parseJingweiToLatLng(previousPoint.jingwei).latitude + "";
                data.previousPointLongitude = parseJingweiToLatLng(previousPoint.jingwei).longitude + "";
            }
            if (currentPoint != null) {
                data.lastPointLatitude = parseJingweiToLatLng(currentPoint.jingwei).latitude + "";
                data.lastPointLongitude = parseJingweiToLatLng(currentPoint.jingwei).longitude + "";
            }
            data.totalDistance = totalDistance + "";
            data.record_id = recordId;
            data.startTime = startTime + "";
            String fileName = saveFile();
            data.fileName = fileName;
            long id = DBManager.getInstance(DirectionRunActivity.this).insertDRLocalData(data);
            if (id == DBManager.ERROR_CODE) {
                Accounts.setHaveDRData(false);
            } else {
                Accounts.setHaveDRData(true);
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
    protected void onDestroy() {
        super.onDestroy();
        textureMapView.onDestroy();

        if (locationHelper != null) {
            locationHelper.onDestroy();
        }
        if (saveDataTimer != null) {
            saveDataTimer.cancel();
            saveDataTimer = null;
        }
        System.gc();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        textureMapView.onSaveInstanceState(outState);
    }

    @OnClick({R.id.tv_startrun_or_stoprun_or_signup, R.id.img_show_current_location, R.id.img_show_all_point})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_startrun_or_stoprun_or_signup:
                if (!isStarted) {
                    //开始乐跑
                    startRun();
                } else {
                    if (isArrivedNextPoint) {
                        //打卡
                        signUp();
                    } else {
                        //结束乐跑
                        stopRun();
                    }
                }
                break;
            case R.id.img_show_current_location:
                if (currentLatLng != null) {
                    moveToLocation(currentLatLng);
                } else {
                    Toast.makeText(this, "正在定位当前准确的位置...", Toast.LENGTH_SHORT).show();
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

    //开始乐跑
    private void startRun() {
        if (isNearByStartPoint) {
            presenter.startRun(gameId, Long.parseLong(startedPoint.id));
        } else {
            //弹出未到达起跑点的提示框
            new AlertDialog.Builder(this).setMessage(NotArriveStartPointTip).setNegativeButton("确定",
                    null).show();
        }
    }

    @Override
    public void successUploadFile(UploadDRFile fileUrl) {
        if (recordId != null) {
            presenter.stopRun(gameId, Long.parseLong(recordId),
                    Double.parseDouble(DistanceFormatUtils.formatMeterToKiloMeter(totalDistance)));
        }
    }

    // 结束乐跑
    private void stopRun() {
        if (totalDistance < MIN_DEEDED_DISTANCE) {
            directlyStop();
        } else {
            safelyStop();
        }
        List<LocationResult> list = DBManager.getInstance(DirectionRunActivity.this).queryLocationList();
        int size = list == null ? 0 : list.size();
        LogUtils.i("查询结果数量：" + size);
    }

    //直接结束
    private void directlyStop() {
        new AlertDialog.Builder(DirectionRunActivity.this).setMessage(TooShortToSaveData)
                .setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                releaseData();
                Accounts.setHaveDRData(false);
                DBManager.getInstance(DirectionRunActivity.this).deleteLocationAll();
                DBManager.getInstance(DirectionRunActivity.this).deleteDRLocalAll();
                DirectionRunActivity.this.finish();
            }
        }).show();
    }

    //保存数据结束
    private void safelyStop() {
        new AlertDialog.Builder(DirectionRunActivity.this).setMessage(StopRunAndSaveData)
                .setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //先将数据库的数据写入文件，然后上传到服务器
                if (recordId != null) {
                    String fileName = saveFile();
                    if (fileName == null) {
                        return;
                    }
                    timestamp = timeStampBuilder.toString();
                    if (!TextUtils.isEmpty(timestamp)) {
                        timestamp = timestamp.substring(0, timestamp.lastIndexOf(',') - 1);//去掉最后的逗号
                        presenter.uploadDRFile(Long.parseLong(recordId), timestamp, new File(fileName));
                    } else {
                        Toast.makeText(DirectionRunActivity.this, "结束失败，时间戳为空", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).show();
    }

    //保存轨迹点到本地文件
    @Nullable
    private String saveFile() {
        List<LocationResult> list = DBManager.getInstance(DirectionRunActivity.this).queryLocationList();
        if (list == null || list.size() <= 0) {
            return null;
        }
        final String fileName = DirUtils.getDirectionRunDirectory().getAbsolutePath() + File.separator + DateUtils
                .getCurrentTime() + "_" + recordId + ".txt";
        String content = "";
        StringBuilder builder = new StringBuilder();
        for (LocationResult result : list) {
            builder.append(result.latitude).append(",").append(result.longitude).append("|");
        }
        content = builder.toString();
        //去掉最后的“|”
        content = content.substring(0, content.lastIndexOf('|') - 1);
        final String finalContent = content;
        //IO操作放到子线程中
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileHelper.textToFile(fileName, finalContent);
            }
        }).start();
        return fileName;
    }

    //打卡
    private void signUp() {
        if (currentPoint != null && recordId != null) {
            LatLng currentLatLng = parseJingweiToLatLng(currentPoint.jingwei);
            LatLng previousLatLng = parseJingweiToLatLng(previousPoint.jingwei);
            if (currentLatLng == null || previousLatLng == null) {
                Toast.makeText(this, "打卡点数据错误", Toast.LENGTH_SHORT).show();
                return;
            }
            //应该根据轨迹纠偏的路程来计算
            //long distance = (long) AMapUtils.calculateLineDistance(currentLatLng, previousLatLng);
            presenter.runSignUp(gameId, Long.parseLong(currentPoint.id), Long.parseLong(recordId), totalDistance);
        }
    }

    //将乐跑点添加到地图上
    private void addPointToMap() {
        if (runPoints == null || runPoints.size() <= 0) {
            Toast.makeText(this, "暂无任何乐跑点", Toast.LENGTH_SHORT).show();
            return;
        } else {
            ArrayList<LatLng> latLngs = new ArrayList<>();
            for (int i = 0; i < runPoints.size(); i++) {
                DirectionRunPoint point = runPoints.get(i);
                String[] latLngArr = point.jingwei.split(",");
                if (latLngArr == null || latLngArr.length < 2) {
                    Toast.makeText(this, "乐跑点数据错误", Toast.LENGTH_SHORT).show();
                } else {
                    LatLng latLng = new LatLng(Double.parseDouble(latLngArr[0]),
                            Double.parseDouble(latLngArr[1]));
                    AMapViewUtils.addMarker(map, latLng, R.drawable.didian, i + 1 + "", "");
                    latLngs.add(latLng);
                }
            }
            suitableZoomLevel(latLngs);
        }
    }

    private void suitableZoomLevel(List<LatLng> latLngs) {
        if (latLngs == null || latLngs.size() <= 0) {
            return;
        }
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (LatLng latLng : latLngs) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }

    //从数据库中查询轨迹点并在地图上进行绘制
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
        currentPoint = startedPoint;
        previousPoint = currentPoint;
        isStarted = true;
        tvTip.setVisibility(View.GONE);
        tvStartStopSignup.setText(STOP_RUN);
        if (startRun != null) {
            this.startRun = startRun;
            recordId = startRun.record_id;
            // 开始计时
            chronometer.start();
            PopupWindowUtils.getInstance().showDRSignUpPopupWindow(this, startRun.exp, startRun.extra_points + "",
                    startRun.extra_money);
            startTime = System.currentTimeMillis();
        }
        //启动保存数据定时器
        saveDataTimer.start();
    }

    @Override
    public void successGetRunLine(RunLine runLine) {
        if (runLine != null && runLine.point_list.size() > 0) {
            runPoints = new ArrayList<>();
            runPoints.addAll(runLine.point_list);
            //将点绘制在地图上
            addPointToMap();
            setPreviousPoint();
        } else {
            Toast.makeText(this, "暂无乐跑路线数据", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void successRunSignUp(RunSignUp runSignUp) {
        previousPoint = currentPoint;
        isArrivedNextPoint = false;
        tvTip.setVisibility(View.GONE);
        tvStartStopSignup.setText(STOP_RUN);
        if (runSignUp != null) {
            PopupWindowUtils.getInstance().showDRSignUpPopupWindow(this, runSignUp.exp, runSignUp.extra_points + "",
                    runSignUp.extra_money);
        }
    }

    @Override
    public void successStopRun(StopRun stopRun) {
        releaseData();
        Accounts.setHaveDRData(false);
        if (stopRun != null) {
            //TODO 显示乐跑统计结果popupwindow
            String meter = "您一共跑了" + DistanceFormatUtils.formatMeterToKiloMeter(Double.parseDouble(stopRun
                    .distance)) + "公里";
            new AlertDialog.Builder(this).setMessage(meter).setPositiveButton("确定", new DialogInterface
                    .OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    DBManager.getInstance(DirectionRunActivity.this).deleteLocationAll();
                    DBManager.getInstance(DirectionRunActivity.this).deleteDRLocalAll();
                    finish();
                }
            }).show();
        }
    }

    private void releaseData() {
        tvStartStopSignup.setEnabled(false);
        if (chronometer != null) {
            chronometer.stop();
        }
        isStarted = false;
        isFinished = true;
        if (locationHelper != null) {
            locationHelper.stopLocation();
        }
    }

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
