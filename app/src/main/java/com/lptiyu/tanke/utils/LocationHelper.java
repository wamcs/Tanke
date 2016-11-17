package com.lptiyu.tanke.utils;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * Created by Jason on 2016/9/28.
 */

public class LocationHelper implements AMapLocationListener {
    private Context context;

    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mOption;
    private OnLocationResultListener listener;

    public LocationHelper(Context context, OnLocationResultListener listener) {
        this.listener = listener;
        this.context = context;
        initLocation();
    }

    public void startLocation() {
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
    }

    public void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
    }

    public void onDestroy() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.unRegisterLocationListener(this);
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(context.getApplicationContext());
        mOption = new AMapLocationClientOption();
        //设置设备模式（总共有三种模式）
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //        //优先返回GPS定位信息，高精度定位模式下才会有用，默认关闭
        //        mOption.setGpsFirst(true);
        //        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。模式为仅设备模式(Device_Sensors)时无效
        //        mOption.setHttpTimeOut(20000);
        //退出时是否杀死service，默认false
        //        mOption.setKillProcess(false);
        //设置是否定位一次
        mOption.setOnceLocation(true);
        //获取三秒内精度最高的一次定位结果，如果设置为true，则setOnceLocation()也将设置为true
        mOption.setOnceLocationLatest(true);
        //定位时间间隔
        mOption.setInterval(3000);
        //是否返回地址信息
        mOption.setNeedAddress(true);
        //设置是否强制刷新WIFI，默认为强制刷新。每次定位主动刷新WIFI模块会提升WIFI定位精度，但相应的会多付出一些电量消耗。
        mOption.setWifiActiveScan(false);
        //是否允许模拟软件Mock位置结果
        mOption.setMockEnable(false);
        //设置网络请求方式
        mOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);
        //为定位客户端设置定位参数
        mLocationClient.setLocationOption(mOption);
        mLocationClient.setLocationListener(this);
    }

    public void setOnceLocation(boolean flag) {
        if (mOption != null) {
            mOption.setOnceLocation(flag);
            mOption.setOnceLocationLatest(flag);
            if (mLocationClient != null) {
                mLocationClient.setLocationOption(mOption);
            }
        }
    }

    public void setInterval(long interval) {
        if (mOption != null) {
            mOption.setInterval(interval);
            if (mLocationClient != null) {
                mLocationClient.setLocationOption(mOption);
            }
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (listener != null) {
            listener.onLocationChanged(aMapLocation);
        }
    }

    public interface OnLocationResultListener {
        void onLocationChanged(AMapLocation aMapLocation);
    }
}
