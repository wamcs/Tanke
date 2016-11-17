package com.lptiyu.tanke.activities.directionrunshare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.PolylineOptions;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.entity.greendao.LocationResult;
import com.lptiyu.tanke.entity.response.StopRun;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.DBManager;
import com.lptiyu.tanke.utils.DisplayUtils;
import com.lptiyu.tanke.utils.MarkerOptionHelper;
import com.lptiyu.tanke.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lptiyu.tanke.R.id.textureMapView;

public class DirectionRunShareActivity extends MyBaseActivity {
    @BindView(textureMapView)
    TextureMapView mapView;
    @BindView(R.id.chronometer)
    Chronometer chronometer;
    @BindView(R.id.tv_distance_value)
    TextView tvDistanceValue;
    @BindView(R.id.tv_speed_value)
    TextView tvSpeedValue;
    @BindView(R.id.tv_exp_value)
    TextView tvExpValue;
    @BindView(R.id.tv_score_value)
    TextView tvScoreValue;
    @BindView(R.id.tv_red_wallet_value)
    TextView tvRedWalletValue;
    private AMap map;
    private List<LatLng> latLngs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction_run_share);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mapView.onCreate(savedInstanceState);
        if (map == null) {
            map = mapView.getMap();
            map.setMyLocationType(AMap.MAP_TYPE_NORMAL);
            UiSettings uiSettings = map.getUiSettings();
            uiSettings.setZoomControlsEnabled(false);
            uiSettings.setLogoBottomMargin(-200);
            uiSettings.setLogoLeftMargin(-200);
        }

        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        StopRun stopRun = intent.getParcelableExtra(Conf.STOP_RUN);
        if (stopRun != null) {
            chronometer.setText(TimeUtils.formatSecond(stopRun.time));
            tvDistanceValue.setText(stopRun.distance);
            tvExpValue.setText("+" + stopRun.exp);
            tvScoreValue.setText("+" + stopRun.points);
            tvRedWalletValue.setText("+" + Integer.parseInt(stopRun.extra_money) / 100.0f);
            long peisu = (long) (stopRun.time / Float.parseFloat(stopRun.distance));
            String peisuFormat = TimeUtils.parsePeisu(peisu);
            tvSpeedValue.setText(peisuFormat);
        }

        List<LocationResult> list = DBManager.getInstance(this).queryLocationList();
        if (list != null) {
            latLngs = new ArrayList<>();
            for (LocationResult result : list) {
                latLngs.add(new LatLng(result.latitude, result.longitude));
            }
            addStartAndEndMarker(latLngs);
            drawLine(latLngs);
            suitableZoomLevel(latLngs);
        }
    }

    //添加起始点标记
    private void addStartAndEndMarker(List<LatLng> latLngs) {
        if (latLngs == null) {
            return;
        }
        addMarker(latLngs.get(0));
        addMarker(latLngs.get(latLngs.size() - 1));
    }

    //以最大化的视图显示轨迹
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

    //添加marker
    public void addMarker(LatLng latLng) {
        MarkerOptionHelper markerOptionHelper = new MarkerOptionHelper();
        if (map != null) {
            map.addMarker(markerOptionHelper.position(latLng));
        }
    }

    @OnClick(R.id.img_show_all_point)
    public void onClick() {
        if (latLngs != null) {
            suitableZoomLevel(latLngs);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mapView.onResume ()，实现地图生命周期管理
        mapView.onResume();
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

    @Override
    protected void onDestroy() {
        //在activity执行onDestroy时执行mapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        DBManager.getInstance(DirectionRunShareActivity.this).deleteLocationAll();
        DBManager.getInstance(DirectionRunShareActivity.this).deleteDRLocalAll();
        super.onDestroy();
    }
}
