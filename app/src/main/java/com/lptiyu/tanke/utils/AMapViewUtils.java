package com.lptiyu.tanke.utils;

import android.graphics.BitmapFactory;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.lptiyu.tanke.R;

import static com.lptiyu.tanke.global.AppData.getResources;

/**
 * Created by Jason on 2016/10/17.
 */

public class AMapViewUtils {
    //添加marker,带文字显示
    public static void addMarker(AMap map, LatLng latLng, String title, String snippet) {
        MarkerOptions currentMarkerOptions = new MarkerOptions();
        currentMarkerOptions.position(latLng).title("当前位置：" + title).snippet(snippet).draggable(true);
        // 将Marker设置为贴地显示，可以双指下拉看效果
        currentMarkerOptions.setFlat(true);
        if (map != null) {
            Marker marker = map.addMarker(currentMarkerOptions);
            marker.showInfoWindow();
        }
    }

    public static void addMarker(AMap map, LatLng latLng) {
        MarkerOptions currentMarkerOptions = new MarkerOptions();
        currentMarkerOptions.position(latLng).draggable(true);
        // 将Marker设置为贴地显示，可以双指下拉看效果
        currentMarkerOptions.setFlat(true);
        if (map != null) {
            map.addMarker(currentMarkerOptions);
        }
    }

    //添加marker
    public static Marker addMarker(AMap map, LatLng latLng, int imgResource, String snippet, String title) {
        MarkerOptions currentMarkerOptions = new MarkerOptions();
        currentMarkerOptions.position(latLng).draggable(true).snippet(snippet).title(title).setFlat(true);
        currentMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), imgResource)));
        if (map != null) {
            return map.addMarker(currentMarkerOptions);
        }
        return null;
    }

    public static void addText(AMap map, LatLng latLng, String text) {
        TextOptions textOptions = new TextOptions();
        textOptions.text(text).position(latLng).fontSize(DisplayUtils.dp2px(16)).fontColor(R.color.colorPrimary);
        if (map != null) {
            Text addText = map.addText(textOptions);
        }
    }

    public static LatLng parseJingweiToLatLng(String jingwei) {
        String[] latLngArr = jingwei.split(",");
        if (latLngArr == null || latLngArr.length <= 1) {
            return null;
        }
        return new LatLng(Double.parseDouble(latLngArr[0]), Double.parseDouble
                (latLngArr[1]));
    }
}
