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
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolygonOptions;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.gamedetail.GameDetailActivity;
import com.lptiyu.tanke.activities.pointtasknew.PointTaskV2Activity;
import com.lptiyu.tanke.adapter.PointListAdapter;
import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.eventbus.NotifyPointTaskV2RefreshData;
import com.lptiyu.tanke.entity.response.HomeGameList;
import com.lptiyu.tanke.entity.response.Recommend;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.enums.RequestCode;
import com.lptiyu.tanke.enums.ResultCode;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.interfaces.OnRecyclerViewItemClickListener;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.LocationHelper;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.widget.CustomTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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

    private ArrayList<Point> totallist;

    private double currentLatitude;
    private double currentLongitude;
    private LocationHelper locationHelper;

    private LatLng WUHAN = new LatLng(30.525252, 114.313131);
    private MarkerOptions currentMarkerOptions;
    private GamePlayingV2Presenter presenter;
    private long gameId;
    private String title;
    private PointListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_playing_v2);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        presenter = new GamePlayingV2Presenter(this);

        textureMapView.onCreate(savedInstanceState);
        aMap = textureMapView.getMap();
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setAllGesturesEnabled(true);//支持手势
        uiSettings.setZoomControlsEnabled(false);

        gamePlayingTitle.setText("加载中...");

        //初始化游戏区域的四个点
        initLatlng();
        initLocation();
        //将地图中心移动到武汉市
        moveToLocation(WUHAN);

        //获取游戏数据和游戏记录
        initData();
        loadNetData();
    }

    private void initData() {
        Intent intent = getIntent();
        gameId = intent.getLongExtra(Conf.GAME_ID, Conf.TEMP_GAME_ID);
        HomeGameList homeGameList = intent.getParcelableExtra(Conf.HOME_TAB_ENTITY);
        Recommend recommend = intent.getParcelableExtra(Conf.RECOMMEND);
        //还有其他更多的入口需要考虑，比如已完成的游戏界面进来的，正在进行的游戏界面进来的
        if (homeGameList != null) {
            title = homeGameList.title;
        }
        if (recommend != null) {
            title = recommend.title;
        }
    }

    private void loadNetData() {
        if (NetworkUtil.checkIsNetworkConnected()) {
            presenter.downLoadGameRecord(gameId);
        } else {
            PopupWindowUtils.getInstance().showNetExceptionPopupwindow(getContext(), new PopupWindowUtils
                    .OnRetryCallback() {
                @Override
                public void onRetry() {
                    loadNetData();
                }
            });
        }
    }

    private void setAdapter() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new PointListAdapter(this, totallist);
        recyclerView.setAdapter(adapter);
        adapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(int position) {
                RunApplication.currentPointIndex = position;
                if (totallist.get(position).status == PointTaskStatus.UNSTARTED) {
                    Toast.makeText(GamePlayingV2Activity.this, "未解锁", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(GamePlayingV2Activity.this, PointTaskV2Activity.class);
                GamePlayingV2Activity.this.startActivity(intent);
            }

            @Override
            public void onLongClick(int position) {

            }
        });
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

    /*无论在哪个线程发送都在主线程接收*/
    @Subscribe
    public void onEventMainThread(NotifyPointTaskV2RefreshData result) {
        //刷新数据
        loadNetData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        textureMapView.onDestroy();
        if (locationHelper != null) {
            locationHelper.onDestroy();
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        textureMapView.onResume();
        if (adapter != null) {
            adapter.refreshData();
        }
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
                Intent intent = new Intent(GamePlayingV2Activity.this, GameDetailActivity.class);
                intent.putExtra(Conf.FROM_WHERE, Conf.GAME_PLAYing_V2_ACTIVITY);
                startActivityForResult(intent, RequestCode.LEAVE_GAME);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.LEAVE_GAME && resultCode == ResultCode.LEAVE_GAME) {
            finish();
        }
    }

    @Override
    public void successUpLoadRecord() {

    }

    @Override
    public void successDownLoadRecord(GameRecord gameRecord) {
        gamePlayingTitle.setText(title);
        if (gameRecord != null) {
            RunApplication.gameRecord = gameRecord;
            if (RunApplication.gameRecord != null) {
                if (RunApplication.gameRecord.game_detail.point_list != null) {
                    totallist = RunApplication.gameRecord.game_detail.point_list;
                    checkPointStatus();
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

    private int max_index = -1;//第一个未解锁的point的角标

    private void checkPointStatus() {
        for (int i = 0; i < totallist.size(); i++) {
            if (totallist.get(i).status == PointTaskStatus.UNSTARTED) {
                max_index = i;
                break;
            }
        }
        if (max_index == -1) {//所有point都已完成
        } else {
            totallist.get(max_index).status = PointTaskStatus.PLAYING;
        }
    }
}
