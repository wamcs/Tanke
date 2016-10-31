package com.lptiyu.tanke.activities.gameplaying_v2;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolygonOptions;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.gamedetailv2.GameDetailV2Activity;
import com.lptiyu.tanke.activities.pointtaskv2.PointTaskV2Activity;
import com.lptiyu.tanke.adapter.PointListAdapter;
import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.eventbus.NotifyGamePlayingV2RefreshData;
import com.lptiyu.tanke.entity.response.Jingwei;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.enums.RequestCode;
import com.lptiyu.tanke.enums.ResultCode;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.interfaces.OnRecyclerViewItemClickListener;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.AMapViewUtils;
import com.lptiyu.tanke.utils.LocationHelper;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.widget.CustomTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
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

    private GamePlayingV2Presenter presenter;
    private long gameId;
    private String title = "";
    private PointListAdapter adapter;
    private final int ONLINE_GAME = 1;
    private int gameType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_playing_v2);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        presenter = new GamePlayingV2Presenter(this);

        textureMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = textureMapView.getMap();
        }
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setAllGesturesEnabled(true);//支持手势
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setLogoLeftMargin(-200);
        uiSettings.setLogoBottomMargin(-200);
        //如果是线上游戏，则隐藏地图
        if (RunApplication.entity != null && RunApplication.entity.cid == ONLINE_GAME) {
            textureMapView.setVisibility(View.GONE);
        }
        //获取游戏数据和游戏记录
        initData();
        loadNetData();
    }

    private void initData() {
        gameId = RunApplication.gameId;
        if (RunApplication.entity != null) {
            title = RunApplication.entity.title;
        }
        locationHelper = new LocationHelper(this, new LocationHelper.OnLocationResultListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                getLocationResultData(aMapLocation);
            }
        });
        locationHelper.setOnceLocation(true);
        locationHelper.startLocation();

    }

    private void loadNetData() {
        gamePlayingTitle.setText("加载中...");
        if (NetworkUtil.checkIsNetworkConnected()) {
            presenter.downLoadGameRecord(gameId, gameType);//个人游戏传0
        } else {
            getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    PopupWindowUtils.getInstance().showNetExceptionPopupwindow(getContext(), new PopupWindowUtils
                            .OnRetryCallback() {
                        @Override
                        public void onRetry() {
                            loadNetData();
                        }
                    });
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
                RunApplication.currentPoint = totallist.get(position);
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

    //绘制一个游戏区域
    private void drawPolygon() {
        List<LatLng> latLngs = new ArrayList<>();
        for (Jingwei jingwei : RunApplication.gameRecord.game_zone) {
            latLngs.add(AMapViewUtils.parseJingweiToLatLng(jingwei.jingwei));
        }
        PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngs);
        polygonOptions.fillColor(ContextCompat.getColor(this, R.color.colorPrimary)).strokeWidth(1f).strokeColor
                (Color.BLACK);
        aMap.addPolygon(polygonOptions);
    }

    /*无论在哪个线程发送都在主线程接收
    * 接受任务完成后的通知，刷新数据
    * */
    @Subscribe
    public void onEventMainThread(NotifyGamePlayingV2RefreshData result) {
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
        float zoom = aMap.getCameraPosition().zoom;
        if (zoom > 9.99999 && zoom < 10.00001) {
            zoom = 13;
        }
        CameraPosition cameraPosition = new CameraPosition(latLng, zoom, 0, 0);
        aMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1, null);
    }

    //添加marker
    private void addMarker(LatLng latLng) {
        MarkerOptions currentMarkerOptions = new MarkerOptions();
        currentMarkerOptions.position(latLng).draggable(true);
        currentMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R
                .drawable.locate_orange)));
        // 将Marker设置为贴地显示，可以双指下拉看效果
        currentMarkerOptions.setFlat(true);
        aMap.addMarker(currentMarkerOptions);
    }

    private void getLocationResultData(AMapLocation aMapLocation) {
        currentLatitude = aMapLocation.getLatitude();
        currentLongitude = aMapLocation.getLongitude();
        moveToLocation(new LatLng(currentLatitude, currentLongitude));
        addMarker(new LatLng(currentLatitude, currentLongitude));
    }

    @OnClick({R.id.img_game_detail, R.id.back_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.img_game_detail:
                Intent intent = new Intent(GamePlayingV2Activity.this, GameDetailV2Activity.class);
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
    public void successDownLoadRecord(GameRecord gameRecord) {
        gamePlayingTitle.setText(title);
        if (gameRecord != null) {
            RunApplication.gameRecord = gameRecord;
            if (gameRecord != null) {
                if (gameRecord.game_detail.point_list != null) {
                    totallist = gameRecord.game_detail.point_list;
                    checkPointStatus();
                    setAdapter();
                    //绘制游戏区域
                    drawPolygon();
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


    private void checkPointStatus() {
        int max_index = -1;//第一个未解锁的point的角标
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
