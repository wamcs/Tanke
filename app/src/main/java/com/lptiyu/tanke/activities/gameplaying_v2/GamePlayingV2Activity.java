package com.lptiyu.tanke.activities.gameplaying_v2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolygonOptions;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.adapter.PointListAdapter;
import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.ThemeLine;
import com.lptiyu.tanke.entity.response.HomeGameList;
import com.lptiyu.tanke.entity.response.Recommend;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.LocationHelper;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.widget.CustomTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lptiyu.tanke.global.AppData.getContext;

public class GamePlayingV2Activity extends MyBaseActivity implements GamePlayingV2Contract.IGamePlaying2View {

    @BindView(R.id.textureMapView)
    TextureMapView textureMapView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.game_playing_title)
    CustomTextView gamePlayingTitle;
    private AMap aMap;

    private List<Point> totallist;

    private double currentLatitude;
    private double currentLongitude;
    private LocationHelper locationHelper;

    private LatLng WUHAN = new LatLng(30.525252, 114.313131);
    private MarkerOptions currentMarkerOptions;
    private GamePlayingV2Presenter presenter;
    private long gameId;
    private String point_count;

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_playing_v2);
        ButterKnife.bind(this);

        presenter = new GamePlayingV2Presenter(this);

        textureMapView.onCreate(savedInstanceState);
        aMap = textureMapView.getMap();
        aMap.getUiSettings().setAllGesturesEnabled(true);//支持手势

        gamePlayingTitle.setText("加载中...");

        //初始化游戏区域的四个点
        initLatlng();
        //获取游戏数据和游戏记录
        initData();
        initLocation();
        //将地图中心移动到武汉市
        moveToLocation(WUHAN);
    }

    private void initLocation() {
        locationHelper = new LocationHelper(this, new LocationHelper.OnLocationResultListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                getLocationResultData(aMapLocation);
            }
        });
    }

    private void initLatlng() {
        List<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(30.525252, 114.313131));
        latLngs.add(new LatLng(30.425252, 114.313131));
        latLngs.add(new LatLng(30.425252, 114.413131));
        latLngs.add(new LatLng(30.525252, 114.413131));
        drawPolygon(latLngs);
    }

    private void initData() {
        Intent intent = getIntent();
        //获取gameId
        gameId = intent.getLongExtra(Conf.GAME_ID, Conf.TEMP_GAME_ID);
        HomeGameList homeGameList = intent.getParcelableExtra(Conf.HOME_TAB_ENTITY);
        Recommend recommend = intent.getParcelableExtra(Conf.HOME_HOT_ENTITY);
        if (homeGameList != null) {
            title = homeGameList.title;
        }
        if (recommend != null) {
            title = recommend.title;
        }
        if (NetworkUtil.checkIsNetworkConnected()) {
            presenter.downLoadGameRecord(gameId);
        } else {
            PopupWindowUtils.getInstance().showNetExceptionPopupwindow(getContext(), new PopupWindowUtils
                    .OnNetExceptionListener() {
                @Override
                public void onClick(View view) {
                    initData();
                }
            });
        }
    }

    private void setAdapter() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        PointListAdapter adapter = new PointListAdapter(this, totallist);
        recyclerView.setAdapter(adapter);
    }

    //绘制一个长方形
    private void drawPolygon(List<LatLng> latLngs) {
        PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngs);
        polygonOptions.fillColor(ContextCompat.getColor(this, R.color.colorPrimary)).strokeWidth(1f).strokeColor
                (Color.BLACK);
        aMap.addPolygon(polygonOptions);
    }

    //开始定位
    private void startLocation() {
        if (locationHelper != null) {
            locationHelper.startLocation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        textureMapView.onDestroy();
        if (locationHelper != null) {
            locationHelper.onDestroy();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        textureMapView.onSaveInstanceState(outState);
    }

    //地图中心移动到指定位置
    private void moveToLocation(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition(latLng, 11, 0, 0);
        aMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);
    }

    //添加marker
    private void addMarker(LatLng latLng, String title, String snippet) {
        if (currentMarkerOptions == null)
            currentMarkerOptions = new MarkerOptions();
        currentMarkerOptions.position(latLng).title("当前位置：" + title).snippet(snippet).draggable(true);
        //        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),
        // R.mipmap
        //                .ic_launcher)));
        // 将Marker设置为贴地显示，可以双指下拉看效果
        currentMarkerOptions.setFlat(true);
        Marker marker = aMap.addMarker(currentMarkerOptions);
        marker.showInfoWindow();
    }

    private void getLocationResultData(AMapLocation aMapLocation) {
        StringBuilder builder = new StringBuilder();
        builder.append(aMapLocation.getLocationType());//获取当前定位结果来源，如网络定位结果，详见定位类型表
        currentLatitude = aMapLocation.getLatitude();
        builder.append(currentLatitude);//获取纬度
        currentLongitude = aMapLocation.getLongitude();
        builder.append(currentLongitude);//获取经度
        builder.append(aMapLocation.getAccuracy());//获取精度信息
        builder.append(aMapLocation.getAddress());
        //地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
        builder.append(aMapLocation.getCountry());//国家信息
        String province = aMapLocation.getProvince();
        builder.append(province);//省信息
        String city = aMapLocation.getCity();
        builder.append(city);//城市信息
        String district = aMapLocation.getDistrict();
        builder.append(district);//城区信息
        String street = aMapLocation.getStreet();
        builder.append(street);//街道信息
        String streetNum = aMapLocation.getStreetNum();
        builder.append(streetNum);//街道门牌号信息
        builder.append(aMapLocation.getCityCode());//城市编码
        builder.append(aMapLocation.getAdCode());//地区编码
        builder.append(aMapLocation.getAoiName());//获取当前定位点的AOI信息
        //获取定位时间
        builder.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(aMapLocation.getTime())));
        moveToLocation(new LatLng(currentLatitude, currentLongitude));
        addMarker(new LatLng(currentLatitude, currentLongitude), province + city + district, street + streetNum);
        Log.i("jason", builder.toString());
    }

    @OnClick({R.id.img_game_detail, R.id.back_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.img_game_detail:
                startLocation();
                break;
        }
    }

    @Override
    public void successUpLoadRecord() {

    }

    @Override
    public void successDownLoadRecord(GameRecord gameRecord) {
        gamePlayingTitle.setText(title);
        if (gameRecord != null) {
            ThemeLine themeLine = gameRecord.game_detail;
            if (themeLine != null) {
                point_count = themeLine.point_count;
                if (themeLine.point_list != null) {
                    totallist = themeLine.point_list;
                    setAdapter();
                } else {
                    Toast.makeText(this, "暂无游戏数据", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "ThemeLine为空", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "gameRecord为空", Toast.LENGTH_SHORT).show();
        }
    }
}
