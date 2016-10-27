package com.lptiyu.tanke.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

/**
 * Created by Jason on 2016/10/18.
 */

public class MarkerOptionHelper {
    private MarkerOptions options;

    public MarkerOptionHelper() {
        options = new MarkerOptions();
        init();
    }

    private void init() {
        options.setFlat(true);
        options.draggable(true);
    }

    public MarkerOptions position(LatLng latLng) {
        return options.position(latLng);
    }

    /*
    * currentMarkerOptions.position(latLng).title("当前位置：" + title).snippet(snippet).draggable(true);
        // 将Marker设置为贴地显示，可以双指下拉看效果
        currentMarkerOptions.setFlat(true);
     */
    public MarkerOptions title(String title) {
        return options.title(title);
    }

    public MarkerOptions snippet(String snippet) {
        return options.snippet(snippet);
    }

    public MarkerOptions draggable(boolean draggable) {
        return options.draggable(draggable);
    }

    public MarkerOptions setFlat(boolean flat) {
        return options.setFlat(flat);
    }

    public MarkerOptions icon(Bitmap bitmap) {
        return options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
    }

    public MarkerOptions icon(Resources resources, int resId) {
        return options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources, resId)));
    }
}
