package com.lptiyu.tanke.gameplaying.assist;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.lang.ref.WeakReference;

import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-23
 *         email: wonderfulifeel@gmail.com
 */
public class LocateHelper implements BDLocationListener {

  private static final String DEFAULT_COOR_TYPE = "bd09ll";
  private static final int DEFAULT_SPAN = 1000;

  private LocationClient mLocationClient;
  private BDLocationListener mBDLocationListener;

  private WeakReference<Context> contextWeakReference;

  public LocateHelper(Context context) {
    contextWeakReference = new WeakReference<>(context);
    mLocationClient = new LocationClient(contextWeakReference.get());
    initLocation();
  }

  @Override
  public void onReceiveLocation(BDLocation location) {
    if (location == null || mBDLocationListener == null) {
      return;
    }
    mBDLocationListener.onReceiveLocation(location);
  }

  public void startLocate() {
    if (mLocationClient != null) {
      if (mLocationClient.isStarted()) {
        Timber.e("locate service is already started");
      } else {
        mLocationClient.start();
      }
    }
  }

  public void stopLocate() {
    if (mLocationClient != null) {
      if (mLocationClient.isStarted()) {
        Timber.e("locate service is already stop");
      } else {
        mLocationClient.stop();
      }
    }
  }

  public void registerLocationListener(BDLocationListener listener) {
    if (listener == null) {
      throw new IllegalStateException("please set a non-null listener");
    }
    mLocationClient.registerLocationListener(this);
    mBDLocationListener = listener;
  }

  public void unRegisterLocationListener(BDLocationListener listener) {
    if (mLocationClient == null) {
      return;
    }
    mBDLocationListener = null;
    mLocationClient.unRegisterLocationListener(this);
  }

  private void initLocation() {
    LocationClientOption option = new LocationClientOption();
    option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
    option.setCoorType(DEFAULT_COOR_TYPE);
    option.setScanSpan(DEFAULT_SPAN);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
    option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
    option.setOpenGps(true);//可选，默认false,设置是否使用gps
    option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
    option.setIsNeedLocationDescribe(false);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
    option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
    option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
    option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
    option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
    mLocationClient.setLocOption(option);
  }

}
