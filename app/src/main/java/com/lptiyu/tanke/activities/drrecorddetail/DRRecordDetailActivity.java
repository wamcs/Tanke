package com.lptiyu.tanke.activities.drrecorddetail;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;

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
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.DisplayUtils;
import com.lptiyu.tanke.utils.FileUtils;
import com.lptiyu.tanke.utils.MarkerOptionHelper;
import com.lptiyu.tanke.utils.TimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    private List<LatLng> latLngs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drrecord_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
            readRecordFileAndDrawLine(file);
        }
    }

    //解析轨迹文件并绘制轨迹
    private void readRecordFileAndDrawLine(File file) {
        String content = FileUtils.readFileByLine(file);
        latLngs = parseDRStr(content);
        //TODO 绘制轨迹,如果用这种方法绘制效果不太好，那就用高德的轨迹纠偏的进行轨迹绘制
        drawLine(latLngs);
        suitableZoomLevel(latLngs);
        addStartAndEndMarker(latLngs);
    }

    //解析轨迹文件内容
    private ArrayList<LatLng> parseDRStr(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        String[] splits = content.split("\\|");
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

    //添加起始点标记
    private void addStartAndEndMarker(List<LatLng> latLngs) {
        if (latLngs == null) {
            return;
        }
        addMarker(latLngs.get(0));
        addMarker(latLngs.get(latLngs.size() - 1));
    }

    //添加marker
    public void addMarker(LatLng latLng) {
        MarkerOptionHelper markerOptionHelper = new MarkerOptionHelper();
        if (map != null) {
            map.addMarker(markerOptionHelper.position(latLng));
        }
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
            String filePath = isRecordFileExist(detail.file);
            if (filePath == null) {
                presenter.downloadFile(detail.file);
            } else {
                readRecordFileAndDrawLine(new File(filePath));
            }
        }
    }

    //本地文件是否存在
    private String isRecordFileExist(String fileUrl) {
        String fileName = FileUtils.getFileNameFromURL(fileUrl);
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        File[] files = DirUtils.getDirectionRunDirectory().listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.getName().equals(fileName) || file.getAbsolutePath().endsWith(fileName)) {
                    return file.getAbsolutePath();
                }
            }
        }
        return null;
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

    //绑定数据
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
        tvSpeedValue.setText(peisuFormat);
        chronometer.setText(TimeUtils.formatSecond(detail.time));
    }

    @OnClick(R.id.img_show_all_point)
    public void onClick(View view) {
        if (latLngs != null) {
            suitableZoomLevel(latLngs);
        }
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
