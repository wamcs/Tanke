package com.lptiyu.tanke;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.TextureMapView;
import com.lptiyu.tanke.permission.PermissionDispatcher;
import com.lptiyu.tanke.permission.TargetMethod;
import com.lptiyu.tanke.records.RecordsHandler;
import com.lptiyu.tanke.records.RecordsUtils;
import com.lptiyu.zxinglib.android.CaptureActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements
    BDLocationListener {

  @BindView(R.id.map)
  TextureMapView mapView;

  private BaiduMap map;

  private RecordsHandler recordsHandler;
  private static final String ACTIVITY_ID = "123qweasd";
  private static final int TEAM_ID = 1;

  private LocationClient mLocationClient;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    map = mapView.getMap();
    map.setMyLocationEnabled(true);

    recordsHandler = new RecordsHandler.Builder(ACTIVITY_ID, TEAM_ID, true).build();

    // prepare to locate
    mLocationClient = new LocationClient(getApplicationContext());
    initLocation();
    mLocationClient.registerLocationListener(this);
  }

  @Override
  public void onReceiveLocation(BDLocation location) {
    Timber.e("receive location");
    RecordsUtils.dispatchNormalRecords(recordsHandler, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
  }

  @OnClick(R.id.scanner)
  public void onClick() {
    PermissionDispatcher.showCameraWithCheck(this);
  }

  @OnClick(R.id.start_trace)
  public void onStartTrace() {
    PermissionDispatcher.startLocateWithCheck(this);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    // NOTE: delegate the permission handling to generated method
    PermissionDispatcher.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
  }


  @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_CAMERA)
  public void openCamera() {
    startActivity(new Intent(this, CaptureActivity.class));
  }

  @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_LOCATION)
  public void startLocate() {
    if (!mLocationClient.isStarted()) {
      mLocationClient.start();
    } else {
      Timber.e("already start location");
    }
  }

  private void initLocation() {
    LocationClientOption option = new LocationClientOption();
    option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
    );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
    option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
    int span = 1000;
    option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
    option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
    option.setOpenGps(true);//可选，默认false,设置是否使用gps
    option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
    option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
    option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
    option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
    option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
    option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
    mLocationClient.setLocOption(option);
  }

  @Override
  protected void onDestroy() {
    mapView.onDestroy();
    mLocationClient.unRegisterLocationListener(this);
    super.onDestroy();
  }
}
