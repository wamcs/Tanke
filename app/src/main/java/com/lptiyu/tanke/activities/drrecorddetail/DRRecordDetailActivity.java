package com.lptiyu.tanke.activities.drrecorddetail;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.PolylineOptions;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.entity.response.DRRecordDetail;
import com.lptiyu.tanke.entity.response.DRRecordEntity;
import com.lptiyu.tanke.entity.response.DirectionRunPoint;
import com.lptiyu.tanke.entity.response.RunLine;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.AMapViewUtils;
import com.lptiyu.tanke.utils.DisplayUtils;
import com.lptiyu.tanke.utils.FileUtils;
import com.lptiyu.tanke.utils.LocationHelper;
import com.lptiyu.tanke.utils.MarkerOptionHelper;
import com.lptiyu.tanke.utils.TimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lptiyu.tanke.utils.AMapViewUtils.parseJingweiToLatLng;

public class DRRecordDetailActivity extends MyBaseActivity implements DRRecordDetailContact.IDRRecordDetailView {
    @BindView(R.id.textureMapView)
    TextureMapView textureMapView;
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
    private DRRecordEntity record;
    private DRRecordDetailPresenter presenter;
    private AMap map;
    private LocationHelper locationHelper;
    private List<DirectionRunPoint> runPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drrecord_detail);
        ButterKnife.bind(this);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        textureMapView.onCreate(savedInstanceState);
        if (map == null) {
            map = textureMapView.getMap();
            map.setMyLocationType(AMap.MAP_TYPE_NORMAL);
            UiSettings uiSettings = map.getUiSettings();
            uiSettings.setZoomControlsEnabled(false);
            uiSettings.setLogoBottomMargin(-200);
            uiSettings.setLogoLeftMargin(-200);
        }
        initData();
        initLocationHelper();
    }

    private void initData() {
        record = getIntent().getParcelableExtra(Conf.DRRecordEntity);
        presenter = new DRRecordDetailPresenter(this);
        if (record != null) {
            presenter.loadDRRecordDetail(record.id);
        }
    }

    @Override
    public void successDownloadFile(File file) {
        if (file != null) {
            //解析文件
            String content = FileUtils.readFileByLine(file);
            List<LatLng> latLngs = parseDRStr(content);
            //TODO 绘制轨迹,如果用这种方法绘制效果不太好，那就用高德的轨迹纠偏的进行轨迹绘制
            drawLine(latLngs);
        }
    }

    private ArrayList<LatLng> parseDRStr(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        String[] splits = content.split("|");
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for (String split : splits) {
            if (!TextUtils.isEmpty(split)) {
                String[] latLngArr = split.split(",");
                if (latLngArr != null && latLngArr.length == 2) {
                    latLngs.add(new LatLng(Double.parseDouble(latLngArr[0]), Double.parseDouble(latLngArr[1])));
                }
            }
        }
        return latLngs;
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

    @Override
    public void successLoadDRRecordDetail(DRRecordDetail detail) {
        if (detail != null) {
            bindData(detail);
            //TODO  下载轨迹文件进行解析，并绘制轨迹路线，下载之前先判断一下本地有没有此文件
            presenter.downloadFile(detail.file);
            presenter.getRunLine(Long.parseLong(detail.game_id));
        }
    }

    @Override
    public void successGetRunLine(RunLine runLine) {
        if (runLine != null && runLine.point_list.size() > 0) {
            runPoints = new ArrayList<>();
            runPoints.addAll(runLine.point_list);
            //将点绘制在地图上
            addPointToMap();
        } else {
            Toast.makeText(this, "暂无乐跑路线数据", Toast.LENGTH_SHORT).show();
        }
    }

    //将乐跑点添加到地图上
    private void addPointToMap() {
        if (runPoints == null || runPoints.size() <= 0) {
            Toast.makeText(this, "暂无任何乐跑点", Toast.LENGTH_SHORT).show();
            return;
        } else {
            ArrayList<LatLng> latLngs = new ArrayList<>();
            for (int i = 0; i < runPoints.size(); i++) {
                DirectionRunPoint point = runPoints.get(i);
                String[] latLngArr = point.jingwei.split(",");
                if (latLngArr == null || latLngArr.length < 2) {
                    Toast.makeText(this, "乐跑点数据错误", Toast.LENGTH_SHORT).show();
                } else {
                    LatLng latLng = new LatLng(Double.parseDouble(latLngArr[0]),
                            Double.parseDouble(latLngArr[1]));
                    AMapViewUtils.addMarker(map, latLng, R.drawable.didian, i + 1 + "", "");
                    latLngs.add(latLng);
                }
            }
            suitableZoomLevel(latLngs);
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

    private void bindData(DRRecordDetail detail) {
        if (detail == null) {
            return;
        }
        tvDistanceValue.setText(detail.distance);
        tvExpValue.setText("+" + detail.exp);
        tvRedWalletValue.setText("+" + Double.parseDouble(detail.extra_money) / 100.0f);
        tvScoreValue.setText("+" + detail.points);
        long peisu = (long) (detail.time / Float.parseFloat(detail.distance));
        String peisuFormat = TimeUtils.parsePeisu(peisu);
        //        tvSpeedValue.setText(String.format(getString(R.string.speed), peisuFormat));
        tvSpeedValue.setText(peisuFormat);
        chronometer.setText(TimeUtils.formatSecond(detail.time));
    }

    @OnClick({R.id.img_show_current_location, R.id.img_show_all_point})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_show_current_location:
                if (locationHelper != null) {
                    locationHelper.startLocation();
                }
                break;
            case R.id.img_show_all_point:
                if (runPoints == null || runPoints.size() <= 0) {
                    Toast.makeText(this, "乐跑点数据为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                LatLngBounds.Builder builder = LatLngBounds.builder();
                for (DirectionRunPoint point : runPoints) {
                    LatLng latLng = parseJingweiToLatLng(point.jingwei);
                    if (latLng != null) {
                        builder.include(latLng);
                    }
                }
                LatLngBounds bounds = builder.build();
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                break;
        }
    }

    //初始化定位服务
    private void initLocationHelper() {
        locationHelper = new LocationHelper(this, new LocationHelper.OnLocationResultListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                moveToLocation(latLng);
                addCurrentPositionMarker(latLng);
            }
        });
        locationHelper.setOnceLocation(true);
        //        locationHelper.setInterval(5000);
        locationHelper.startLocation();
    }

    //地图中心移动到指定位置
    private void moveToLocation(LatLng latLng) {
        float zoom = map.getCameraPosition().zoom;//第一次会默认返回北京天安门的数据,zoom=10.0
        if (zoom > 9.99999 && zoom < 10.00001) {
            zoom = 16f;
        }
        int duration = 1;
        map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, zoom, 0, 0)), duration,
                null);
    }

    //添加当前位置的marker
    public void addCurrentPositionMarker(LatLng latLng) {
        MarkerOptionHelper markerOptionHelper = new MarkerOptionHelper();
        markerOptionHelper.icon(getResources(), R.drawable.locate_orange);
        if (map != null) {
            map.addMarker(markerOptionHelper.position(latLng));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        textureMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        textureMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textureMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        textureMapView.onSaveInstanceState(outState);
    }
}
