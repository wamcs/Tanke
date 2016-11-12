package com.lptiyu.tanke.activities.gamedetailarea;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.PolygonOptions;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.entity.response.GameDetail;
import com.lptiyu.tanke.entity.response.Jingwei;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.AMapViewUtils;
import com.lptiyu.tanke.widget.CustomTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameDetailAreaActivity extends MyBaseActivity {

    @BindView(R.id.textureMapView)
    TextureMapView mapView;
    @BindView(R.id.default_tool_bar_textview)
    CustomTextView defaultToolBarTextview;
    private AMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail_area);
        ButterKnife.bind(this);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mapView.onCreate(savedInstanceState);
        if (map == null) {
            map = mapView.getMap();
            UiSettings uiSettings = map.getUiSettings();
            uiSettings.setAllGesturesEnabled(true);
            uiSettings.setZoomControlsEnabled(true);
            //隐藏高德地图的logo
            uiSettings.setLogoBottomMargin(-200);
            uiSettings.setLogoLeftMargin(-200);
        }
        defaultToolBarTextview.setText("游戏区域");

        GameDetail gameDetail = getIntent().getParcelableExtra(Conf.GAME_DETAIL);
        if (gameDetail != null && gameDetail.game_zone != null && gameDetail.game_zone.size() >= 3) {//至少三个点才能绘制平面
            ArrayList<LatLng> latLngs = new ArrayList<>();
            for (Jingwei jingwei : gameDetail.game_zone) {
                LatLng latLng = AMapViewUtils.parseJingweiToLatLng(jingwei.jingwei);
                latLngs.add(latLng);
            }
            suitableZoomLevel(latLngs);
            drawPolygon(latLngs);
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

    //绘制一个游戏区域
    private void drawPolygon(List<LatLng> latLngs) {
        if (latLngs == null || latLngs.size() <= 0) {
            return;
        }
        PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngs);
        polygonOptions.fillColor(ContextCompat.getColor(this, R.color.transparent_a)).strokeWidth(1f).strokeColor
                (Color.BLACK);
        map.addPolygon(polygonOptions);
    }

    //地图中心移动到指定位置
    private void moveToLocation(LatLng latLng) {
        float zoom = map.getCameraPosition().zoom;
        if (zoom > 9.99999 && zoom < 10.00001) {
            zoom = 13;
        }
        CameraPosition cameraPosition = new CameraPosition(latLng, zoom, 0, 0);
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mapView.onResume ()，实现地图生命周期管理
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mapView.onPause ()，实现地图生命周期管理
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mapView.onSaveInstanceState(outState);
    }

    @OnClick(R.id.default_tool_bar_imageview)
    public void onClick() {
        finish();
    }
}
