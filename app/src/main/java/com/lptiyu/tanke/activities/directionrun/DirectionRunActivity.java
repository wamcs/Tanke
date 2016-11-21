package com.lptiyu.tanke.activities.directionrun;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.directionrunshare.DirectionRunShareActivity;
import com.lptiyu.tanke.activities.imagedistinguish.MyCountDownTimer;
import com.lptiyu.tanke.broadcastreceiver.GpsStatusReceiver;
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
import com.lptiyu.tanke.utils.FileUtils;
import com.lptiyu.tanke.utils.IOHelper;
import com.lptiyu.tanke.utils.LocationHelper;
import com.lptiyu.tanke.utils.LogUtils;
import com.lptiyu.tanke.utils.MarkerOptionHelper;
import com.lptiyu.tanke.utils.NetworkUtil;
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

public class DirectionRunActivity extends MyBaseActivity implements DirectionRunContact.IDirectionRunView, AMap
        .OnMarkerClickListener, AMap.InfoWindowAdapter, AMap.OnInfoWindowClickListener, AMap.OnMapClickListener {

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
    private final float ACCURACY = 80.0f;//定位数据精度，值越小越精确，单位：米
    private double totalDistance;//总里程数，单位：米
    private final double MIN_DEEDED_DISTANCE = 200d;//结束乐跑时，超过1000m才有乐跑统计显示，单位：米
    private float time_stamp_distance = 200;//每200米插入一个时间戳
    private final float DISTANCE_DELTA = 100.0f;//起跑点以及打卡点误差范围
    private final int millisInFuture = 30000;//保存本地数据的时间间隔
    private final int countDownInterval = 1000;
    private final String START_RUN = "开始乐跑";
    private final String STOP_RUN = "结束乐跑";
    private final String SIGN_UP = "打卡";
    private final String ArriveStartPointTip = "你已在打卡点范围，可以开始乐跑";
    private final String CanSignUp = "你已在打卡点范围，可以打卡";
    private final String NotArriveStartPointTip = "当前位置不在打卡点范围,请前往任一打卡点";
    private final String TooShortToSaveData = "距离过短，数据无法保存";
    private final String StopRunAndSaveData = "结束并保存数据";
    private StringBuilder timeStampBuilder = new StringBuilder();//上传轨迹文件时要传的时间戳（每200米插入一个时间戳）
    private long startTime;
    private LatLng lastLatLngOnLocalData;//从数据恢复中的最后一个点
    private DRLocalData drLocalData;//从本地恢复的数据
    private String lastPointId;
    private String recordId;
    private String fileName;
    private boolean isLocationFailToastShow;
    private final String PLEASE_GO_OUTDOR = "您当前可能处于室内，请到室外空旷区域";
    private final String UNCONNECT_NET = "网络连接断开，请检查网络";
    private final String NO_PERMISSION = "定位失败，请检查是否获得GPS定位权限";
    private final String INVALID_KEY = "定位失败，请检查key是否正确";
    private final String NO_SIM_CARD = "定位失败，请检查是否安装SIM卡";
    private final String FAIL_INIT_LOCATION_CLIENT = "初始化定位失败，请退出重新初始化";
    private final String EXCEPTION_PARAMETER = "定位参数错误，数据可能被篡改，请确保当前网络安全";
    private final String SINGLE_WIFI_AND_NO_CELL = "定位失败，由于仅扫描到单个wifi，且没有基站信息";
    private final String FAIL_LOCATION = "定位失败";
    private boolean isStartFollow;//地图中心是否跟随当前位置移动
    private boolean isDoingNetWork;
    private StringBuilder latlngBuilder = new StringBuilder();
    private double times = 0;
    private List<LatLng> accurateLatLngs = new ArrayList<>();
    private double ACCURATE_DISTANCE = 20;//计算最准确的点的范围标准
    private LatLng accurateLatLng;//开始乐跑后十秒钟内最准确的点
    private int valid = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction_run);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        registerGpsMonitor();
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        textureMapView.onCreate(savedInstanceState);
        if (map == null) {
            map = textureMapView.getMap();
            map.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
            UiSettings uiSettings = map.getUiSettings();
            uiSettings.setZoomControlsEnabled(false);
            uiSettings.setCompassEnabled(true);
            uiSettings.setLogoBottomMargin(-200);
            uiSettings.setLogoLeftMargin(-200);
        }
        init();
    }

    //注册GPS状态监听
    private void registerGpsMonitor() {
        GpsStatusReceiver.register(this);
    }

    private void init() {
        recoveryDataFromLocal();
        initSaveDataTimer();
        initLocationHelper();
        initTraceHelper();
        initMarkerOptionHelper();
        initChronometer();
        initListener();

        startLocation();

        presenter = new DirectionRunPresenter(this);
        presenter.getRunLine(gameId);
    }

    private void initListener() {
        map.setOnMarkerClickListener(this);
        map.setInfoWindowAdapter(this);
        map.setOnInfoWindowClickListener(this);
        map.setOnMapClickListener(this);
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
                second = (currentTimeMillis() - Long.parseLong(drLocalData.startTime)) / 1000;
                startChronome();
                RunApplication.gameId = Long.parseLong(drLocalData.game_id);
                tvStartStopSignup.setText(STOP_RUN);
                isStarted = true;
                recordId = drLocalData.record_id;
                lastPointId = drLocalData.previousPointId;
                timeStampBuilder.append(drLocalData.timeStamp).append(",");
                setPreviousPoint();
            }
        }
    }

    //数据恢复时获取最后一个打卡点
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
                LogUtils.i("轨迹纠偏结果：共" + list.size() + "个有效点，里程：" + distance + "米");
                //每个一公里插入一个时间戳（单位：秒），用于上传轨迹文件时的参数
                if (totalDistance < distance) {
                    DirectionRunActivity.this.totalDistance = distance;
                }
                if (distance >= time_stamp_distance) {
                    timeStampBuilder.append(currentTimeMillis() / 1000).append(",");
                    time_stamp_distance = 2 * time_stamp_distance;
                }
                tvDistanceValue.setText(DistanceFormatUtils.formatMeterToKiloMeter(totalDistance) + "");
            }
        });
    }

    //初始化定位服务
    private void initLocationHelper() {
        locationHelper = new LocationHelper(this, new LocationHelper.OnLocationResultListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                handleLocationResult(aMapLocation);
            }
        });
        locationHelper.setOnceLocation(false);
        locationHelper.setInterval(INTERVAL);
    }

    //处理定位返回信息
    private void handleLocationResult(AMapLocation aMapLocation) {
        double latitude = aMapLocation.getLatitude();
        double longitude = aMapLocation.getLongitude();
        int locationCode = aMapLocation.getErrorCode();
        LogUtils.i("accuracy", "定位数据精度：" + aMapLocation.getAccuracy() + ", ErrorCode:" + locationCode + ", " +
                "ErrorInfo:" + aMapLocation.getErrorInfo() + ", 定位类型：" + aMapLocation
                .getLocationType() + ",经纬度：" + latitude + "," + longitude + ",GPS信号强度：" + aMapLocation
                .getGpsAccuracyStatus() + ",卫星数量：" + aMapLocation.getSatellites());
        latlngBuilder.append(latitude).append(",").append(longitude).append("\r\n");
        //错误码对照表请见http://lbs.amap.com/api/android-location-sdk/guide/utilities/errorcode/#v2
        if (locationCode != AMapLocation.LOCATION_SUCCESS) {
            if (!isLocationFailToastShow) {
                handlerFailLocationResult(locationCode);
                isLocationFailToastShow = true;
            }
            return;
        }
        if (aMapLocation.getAccuracy() > ACCURACY) {
            return;
        }
        if (isDoingNetWork) {
            return;
        }
        isLocationFailToastShow = false;
        LatLng latLng = new LatLng(latitude, longitude);
        previousLatLng = currentLatLng;
        currentLatLng = latLng;
        if (isStarted) {
            //将定位点存储到数据库中
            DBHelper.insertDataToDB(DirectionRunActivity.this, aMapLocation);
            //开始绘制轨迹
            drawLine(previousLatLng, currentLatLng);
            //记录里程数
            caculateDistance();
            if (isStartFollow) {
                //将地图中心移到当前位置
                moveToLocation(currentLatLng);
            }
            //验证是否到达下一个乐跑点
            checkUserIsArrivedNextPoint(latLng);
        } else {
            //判断是否到达起跑点
            checkUserIsNearByStartPoint(latLng);
        }
        //如果是第一次进入Activity，则将地图中心移到当前位置
        if (isFirstEnter) {
            moveToLocation(latLng);
            isFirstEnter = false;
        }
        //用图标标注当前位置
        if (currentMarker != null) {
            currentMarker.remove();
        }
        addCurrentPositionMarker(latLng);
    }

    private LatLng getAccurateLatLng() {
        int max = 0;
        int maxIndex = 0;
        if (accurateLatLngs == null) {
            return null;
        }
        for (int i = 0; i < accurateLatLngs.size(); i++) {
            LatLng latLng = accurateLatLngs.get(i);
            int count = 0;
            for (int j = 0; j < accurateLatLngs.size(); j++) {
                if (AMapUtils.calculateLineDistance(latLng, accurateLatLngs.get(j)) < ACCURATE_DISTANCE) {
                    count++;
                }
            }
            if (count > max) {
                max = count;
                maxIndex = i;
            }
        }
        return accurateLatLngs.get(maxIndex);
    }

    private void handlerFailLocationResult(int locationCode) {
        if (!NetworkUtil.checkIsNetworkConnected()) {
            Toast.makeText(DirectionRunActivity.this, UNCONNECT_NET, Toast.LENGTH_SHORT).show();
        } else {
            switch (locationCode) {
                case AMapLocation.ERROR_CODE_FAILURE_AUTH:
                    Toast.makeText(DirectionRunActivity.this, INVALID_KEY, Toast.LENGTH_SHORT).show();
                    break;
                case AMapLocation.ERROR_CODE_FAILURE_CELL:
                    Toast.makeText(DirectionRunActivity.this, NO_SIM_CARD, Toast.LENGTH_SHORT).show();
                    break;
                case AMapLocation.ERROR_CODE_FAILURE_CONNECTION:
                    Toast.makeText(DirectionRunActivity.this, UNCONNECT_NET, Toast.LENGTH_SHORT).show();
                    break;
                case AMapLocation.ERROR_CODE_FAILURE_INIT:
                    Toast.makeText(DirectionRunActivity.this, FAIL_INIT_LOCATION_CLIENT, Toast.LENGTH_SHORT).show();
                    break;
                case AMapLocation.ERROR_CODE_FAILURE_LOCATION:
                    Toast.makeText(DirectionRunActivity.this, FAIL_LOCATION, Toast.LENGTH_SHORT).show();
                    break;
                case AMapLocation.ERROR_CODE_FAILURE_LOCATION_PARAMETER:
                    Toast.makeText(DirectionRunActivity.this, EXCEPTION_PARAMETER, Toast.LENGTH_SHORT).show();
                    break;
                case AMapLocation.ERROR_CODE_FAILURE_LOCATION_PERMISSION:
                    Toast.makeText(DirectionRunActivity.this, NO_PERMISSION, Toast.LENGTH_SHORT).show();
                    break;
                case AMapLocation.ERROR_CODE_FAILURE_NOENOUGHSATELLITES:
                    Toast.makeText(DirectionRunActivity.this, PLEASE_GO_OUTDOR, Toast.LENGTH_SHORT).show();
                    break;
                case AMapLocation.ERROR_CODE_FAILURE_NOWIFIANDAP:
                    Toast.makeText(DirectionRunActivity.this, NO_PERMISSION, Toast.LENGTH_SHORT).show();
                    break;
                case AMapLocation.ERROR_CODE_FAILURE_PARSER:
                    Toast.makeText(DirectionRunActivity.this, EXCEPTION_PARAMETER, Toast.LENGTH_SHORT).show();
                    break;
                case AMapLocation.ERROR_CODE_FAILURE_WIFI_INFO:
                    Toast.makeText(DirectionRunActivity.this, SINGLE_WIFI_AND_NO_CELL, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(DirectionRunActivity.this, FAIL_LOCATION, Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    }

    private void caculateDistance() {
        if (previousLatLng != null && currentLatLng != null) {
            totalDistance += AMapUtils.calculateLineDistance(previousLatLng, currentLatLng);
            if (totalDistance >= time_stamp_distance) {
                timeStampBuilder.append(currentTimeMillis() / 1000).append(",");
                time_stamp_distance = 2 * time_stamp_distance;
            }
            tvDistanceValue.setText(DistanceFormatUtils.formatMeterToKiloMeter(totalDistance) + "");
        }
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
            tvTip.setText("");
        } else {
            //已到达起跑点范围内，可以开始乐跑了
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
            tvTip.setText(CanSignUp);
            tvStartStopSignup.setText(SIGN_UP);
            isArrivedNextPoint = true;
        } else {
            tvTip.setText("");
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
    private void drawLine(LatLng previousLatLng, LatLng currentLatLng) {
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
        markerOptionHelper.icon(getResources(), R.drawable.my_location_dr);
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
        map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, zoom, 0, 0)), 1000, null);
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
            data.timeStamp = timeStampBuilder.toString();
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
        GpsStatusReceiver.initGPS(this);
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
        GpsStatusReceiver.unregister(this);

        if (latlngBuilder != null) {
            IOHelper.writeTextFile(latlngBuilder.toString(), DirUtils.getDirectionRunDirectory().getAbsolutePath() +
                    File.separator + TimeUtils.getCurrentTime() + "_latlngs.txt");
        }
    }

    @Override
    public void failLoad(String errMsg) {
        super.failLoad(errMsg);
        isDoingNetWork = false;
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
                isStartFollow = true;
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
                isStartFollow = true;
                if (currentLatLng != null) {
                    moveToLocation(currentLatLng);
                } else {
                    Toast.makeText(this, "正在定位当前准确的位置...", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.img_show_all_point:
                isStartFollow = false;
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
            isDoingNetWork = true;
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
                    if (TextUtils.isEmpty(fileName)) {
                        fileName = saveFile();
                    }
                    if (TextUtils.isEmpty(fileName)) {
                        return;
                    }
                    String timestamp = timeStampBuilder.toString();
                    if (timestamp.startsWith(",")) {
                        timestamp = timestamp.substring(timestamp.indexOf(',') + 1, timestamp
                                .length());
                    }
                    if (!TextUtils.isEmpty(timestamp)) {
                        // 去掉最后的逗号
                        timestamp = timestamp.substring(0, timestamp.lastIndexOf(',') - 1);
                        isDoingNetWork = true;
                        presenter.uploadDRFile(Long.parseLong(recordId), timestamp, new File(fileName));
                    } else {
                        Toast.makeText(DirectionRunActivity.this, "结束失败", Toast.LENGTH_SHORT).show();
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
                FileUtils.textToFile(fileName, finalContent);
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
            //如果误差太大，可以根据轨迹纠偏的路程来计算
            isDoingNetWork = true;
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
                LatLng latLng = parseJingweiToLatLng(point.jingwei);
                if (latLng != null) {
                    AMapViewUtils.addMarker(map, latLng, R.drawable.dian, point.cover_url, point.address + "");
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

    //将定位数据转化为轨迹纠偏的数据
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
        isDoingNetWork = false;
        currentPoint = startedPoint;
        previousPoint = currentPoint;
        isStarted = true;
        tvTip.setText("");
        tvStartStopSignup.setText(STOP_RUN);
        if (startRun != null) {
            this.startRun = startRun;
            recordId = startRun.record_id;
            // 开始计时
            chronometer.start();
            PopupWindowUtils.getInstance().showDRSignUpPopupWindow(this, startRun.exp, startRun.extra_points + "",
                    startRun.extra_money);
            startTime = currentTimeMillis();
        }
        saveDataToLocal();
        //启动保存数据定时器
        saveDataTimer.start();
    }

    @Override
    public void successGetRunLine(RunLine runLine) {
        isDoingNetWork = false;
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

    //启动定位
    private void startLocation() {
        if (locationHelper != null) {
            locationHelper.startLocation();
        }
    }

    //停止定位
    private void stopLocation() {
        if (locationHelper != null) {
            locationHelper.stopLocation();
        }
    }

    //开始计时
    private void startChronome() {
        if (chronometer != null) {
            chronometer.start();
        }
    }

    //结束计时
    private void stopChronome() {
        if (chronometer != null) {
            chronometer.stop();
        }
    }

    @Override
    public void successRunSignUp(RunSignUp runSignUp) {
        isDoingNetWork = false;
        previousPoint = currentPoint;
        isArrivedNextPoint = false;
        tvTip.setText("");
        tvStartStopSignup.setText(STOP_RUN);
        if (runSignUp != null) {
            PopupWindowUtils.getInstance().showDRSignUpPopupWindow(this, runSignUp.exp, runSignUp.extra_points + "",
                    runSignUp.extra_money);
        }
    }

    @Override
    public void successStopRun(StopRun stopRun) {
        isDoingNetWork = false;
        releaseData();
        Accounts.setHaveDRData(false);
        if (stopRun != null) {
            Intent intent = new Intent(DirectionRunActivity.this, DirectionRunShareActivity.class);
            intent.putExtra(Conf.STOP_RUN, stopRun);
            startActivity(intent);
            finish();
        }
    }

    //清除数据
    private void releaseData() {
        tvStartStopSignup.setEnabled(false);
        stopChronome();
        isStarted = false;
        isFinished = true;
        stopLocation();
        //删除本地轨迹文件
        if (!TextUtils.isEmpty(fileName)) {
            FileUtils.deleteFile(fileName);
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!marker.isInfoWindowShown()) {
            marker.showInfoWindow();
        }
        return true;//设置为true后该marker点不会成为地图的中心
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View infoWindow = getLayoutInflater().inflate(R.layout.info_window, null);
        TextView tvDistance = (TextView) infoWindow.findViewById(R.id.tv_distance);
        TextView tvAddress = (TextView) infoWindow.findViewById(R.id.tv_address);
        ImageView img = (ImageView) infoWindow.findViewById(R.id.img);
        if (currentLatLng != null) {
            tvDistance.setText("距您" + DistanceFormatUtils.formatMeterToKiloMeter(AMapUtils.calculateLineDistance
                    (currentLatLng, marker.getPosition())) + "公里");
        }
        tvAddress.setText(marker.getTitle());
        Glide.with(this).load(marker.getSnippet()).error(R.drawable.default_pic).into(img);
        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View infoWindow = getLayoutInflater().inflate(R.layout.info_window, null);
        TextView tvDistance = (TextView) infoWindow.findViewById(R.id.tv_distance);
        TextView tvAddress = (TextView) infoWindow.findViewById(R.id.tv_address);
        ImageView img = (ImageView) infoWindow.findViewById(R.id.img);
        if (currentLatLng != null) {
            tvDistance.setText(DistanceFormatUtils.formatMeterToKiloMeter(AMapUtils.calculateLineDistance(currentLatLng,
                    marker.getPosition())));
        }
        tvAddress.setText(marker.getSnippet());
        Glide.with(this).load(marker.getSnippet()).error(R.drawable.default_pic).into(img);
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        marker.hideInfoWindow();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        List<Marker> markers = map.getMapScreenMarkers();
        if (markers != null) {
            for (Marker marker : markers) {
                if (marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                }
            }
        }
    }
}
