package com.lptiyu.tanke.entity.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Jason on 2016/9/28.
 */
@Entity
public class LocationResult {
    @Id(autoincrement = true)
    public Long id;
    public float accuracy;
    public String adCode;
    public String adress;
    public double altitude;
    public String aoiName;
    public float bearing;//方向角
    public String city;
    public String cityCode;
    public String country;
    public String district;
    public int errorCode;
    public String errorInfo;
    public double latitude;
    public String locationDetail;
    public int locationType;
    public double longitude;
    public String poiName;
    public String provider;
    public String province;
    public String street;
    public int satellites;
    public float speed;
    public String streetNum;
    public long time;

    @Generated(hash = 783061630)
    public LocationResult(Long id, float accuracy, String adCode, String adress,
                          double altitude, String aoiName, float bearing, String city,
                          String cityCode, String country, String district, int errorCode,
                          String errorInfo, double latitude, String locationDetail,
                          int locationType, double longitude, String poiName, String provider,
                          String province, String street, int satellites, float speed,
                          String streetNum, long time) {
        this.id = id;
        this.accuracy = accuracy;
        this.adCode = adCode;
        this.adress = adress;
        this.altitude = altitude;
        this.aoiName = aoiName;
        this.bearing = bearing;
        this.city = city;
        this.cityCode = cityCode;
        this.country = country;
        this.district = district;
        this.errorCode = errorCode;
        this.errorInfo = errorInfo;
        this.latitude = latitude;
        this.locationDetail = locationDetail;
        this.locationType = locationType;
        this.longitude = longitude;
        this.poiName = poiName;
        this.provider = provider;
        this.province = province;
        this.street = street;
        this.satellites = satellites;
        this.speed = speed;
        this.streetNum = streetNum;
        this.time = time;
    }

    @Generated(hash = 2003041966)
    public LocationResult() {
    }

    @Override
    public String toString() {
        return "LocationResult{" +
                "id=" + id +
                ", accuracy=" + accuracy +
                ", adCode='" + adCode + '\'' +
                ", adress='" + adress + '\'' +
                ", altitude=" + altitude +
                ", aoiName='" + aoiName + '\'' +
                ", bearing=" + bearing +
                ", city='" + city + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", country='" + country + '\'' +
                ", district='" + district + '\'' +
                ", errorCode=" + errorCode +
                ", errorInfo='" + errorInfo + '\'' +
                ", latitude=" + latitude +
                ", locationDetail='" + locationDetail + '\'' +
                ", locationType=" + locationType +
                ", longtitude=" + longitude +
                ", poiName='" + poiName + '\'' +
                ", provider='" + provider + '\'' +
                ", province='" + province + '\'' +
                ", street='" + street + '\'' +
                ", satellites=" + satellites +
                ", speed=" + speed +
                ", streetNum='" + streetNum + '\'' +
                ", time=" + time +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getAccuracy() {
        return this.accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public String getAdCode() {
        return this.adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getAdress() {
        return this.adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public double getAltitude() {
        return this.altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getAoiName() {
        return this.aoiName;
    }

    public void setAoiName(String aoiName) {
        this.aoiName = aoiName;
    }

    public float getBearing() {
        return this.bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return this.cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDistrict() {
        return this.district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorInfo() {
        return this.errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getLocationDetail() {
        return this.locationDetail;
    }

    public void setLocationDetail(String locationDetail) {
        this.locationDetail = locationDetail;
    }

    public int getLocationType() {
        return this.locationType;
    }

    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPoiName() {
        return this.poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getSatellites() {
        return this.satellites;
    }

    public void setSatellites(int satellites) {
        this.satellites = satellites;
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getStreetNum() {
        return this.streetNum;
    }

    public void setStreetNum(String streetNum) {
        this.streetNum = streetNum;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
