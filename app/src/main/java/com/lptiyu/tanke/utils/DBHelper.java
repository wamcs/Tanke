package com.lptiyu.tanke.utils;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.lptiyu.tanke.entity.greendao.LocationResult;

/**
 * Created by Jason on 2016/10/19.
 */

public class DBHelper {
    public static void insertDataToDB(Context context, AMapLocation aMapLocation) {
        LocationResult locationResult = new LocationResult();
        locationResult.accuracy = aMapLocation.getAccuracy();
        locationResult.adCode = aMapLocation.getAdCode();
        locationResult.adress = aMapLocation.getAddress();
        locationResult.altitude = aMapLocation.getAltitude();
        locationResult.aoiName = aMapLocation.getAoiName();
        locationResult.bearing = aMapLocation.getBearing();
        locationResult.city = aMapLocation.getCity();
        locationResult.cityCode = aMapLocation.getCityCode();
        locationResult.country = aMapLocation.getCountry();
        locationResult.district = aMapLocation.getDistrict();
        locationResult.errorCode = aMapLocation.getErrorCode();
        locationResult.errorInfo = aMapLocation.getErrorInfo();
        locationResult.latitude = aMapLocation.getLatitude();
        locationResult.locationDetail = aMapLocation.getLocationDetail();
        locationResult.locationType = aMapLocation.getLocationType();
        locationResult.longitude = aMapLocation.getLongitude();
        locationResult.poiName = aMapLocation.getPoiName();
        locationResult.provider = aMapLocation.getProvider();
        locationResult.satellites = aMapLocation.getSatellites();
        locationResult.speed = aMapLocation.getSpeed();
        locationResult.street = aMapLocation.getStreet();
        locationResult.streetNum = aMapLocation.getStreetNum();
        locationResult.time = aMapLocation.getTime();
        locationResult.province = aMapLocation.getProvince();
        DBManager.getInstance(context).insertLocation(locationResult);
    }
}
