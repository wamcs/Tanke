package com.lptiyu.tanke.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolygonOptions;
import com.lptiyu.tanke.R;

import java.util.List;

/**
 * Created by Jason on 2016/10/14.
 */

public class AMapViewHelper {
    public AMap map;
    public TextureMapView textureMapView;
    private UiSettings uiSettings;
    private MarkerOptions currentMarkerOptions;

    public AMapViewHelper(TextureMapView textureMapView) {
        this.textureMapView = textureMapView;
        if (map == null) {
            textureMapView.getMap();
        }
        initUiSetting();
    }

    public void initUiSetting() {
        if (map != null) {
            uiSettings = map.getUiSettings();
        }
    }

    public void setZoomControlsEnabled(boolean zoomControlsEnabled) {
        if (uiSettings != null) {
            uiSettings.setZoomControlsEnabled(zoomControlsEnabled);
        }
    }

    public void setAllGesturesEnabled(boolean allGesturesEnabled) {
        if (uiSettings != null) {
            uiSettings.setAllGesturesEnabled(allGesturesEnabled);
        }
    }

    public void setCompassEnabled(boolean compassEnabled) {
        if (uiSettings != null) {
            uiSettings.setCompassEnabled(compassEnabled);
        }
    }

    public void setScaleControlsEnabled(boolean scaleControlsEnabled) {
        if (uiSettings != null) {
            uiSettings.setScaleControlsEnabled(scaleControlsEnabled);
        }
    }

    //绘制一个长方形
    public void drawPolygon(Context context, List<LatLng> latLngs) {
        if (map != null) {
            PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngs);
            polygonOptions.fillColor(ContextCompat.getColor(context, R.color.colorPrimary)).strokeWidth(1f).strokeColor
                    (Color.BLACK);
            map.addPolygon(polygonOptions);
        } else {
            LogUtils.i("AMapView-->drawPolygon()-->map为空");
        }
    }

    //地图中心移动到指定位置
    public void moveToLocation(LatLng latLng) {
        if (map != null) {
            CameraPosition cameraPosition = new CameraPosition(latLng, 11, 0, 0);
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);
        } else {
            LogUtils.i("AMapView-->moveToLocation()-->map为空");
        }
    }

    //添加marker
    public void addMarker(LatLng latLng, String title, String snippet) {
        if (map != null) {
            if (currentMarkerOptions == null)
                currentMarkerOptions = new MarkerOptions();
            currentMarkerOptions.position(latLng).title("当前位置：" + title).snippet(snippet).draggable(true);
            //        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),
            // R.mipmap
            //                .ic_launcher)));
            // 将Marker设置为贴地显示，可以双指下拉看效果
            currentMarkerOptions.setFlat(true);
            Marker marker = map.addMarker(currentMarkerOptions);
            marker.showInfoWindow();
        } else {
            LogUtils.i("AMapView-->addMarker()-->map为空");
        }
    }
}
