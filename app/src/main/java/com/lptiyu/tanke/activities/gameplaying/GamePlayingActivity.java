package com.lptiyu.tanke.activities.gameplaying;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
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
import com.lptiyu.tanke.activities.gamedetail.GameDetailActivity;
import com.lptiyu.tanke.activities.gameover.GameOverActivity;
import com.lptiyu.tanke.activities.pointtask.PointTaskActivity;
import com.lptiyu.tanke.adapter.PointListAdapter;
import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.eventbus.GamePointTaskStateChanged;
import com.lptiyu.tanke.entity.response.Jingwei;
import com.lptiyu.tanke.enums.GameSort;
import com.lptiyu.tanke.enums.PlayStatus;
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
import com.lptiyu.tanke.utils.TimeFormatUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lptiyu.tanke.global.AppData.getContext;

public class GamePlayingActivity extends MyBaseActivity implements GamePlayingContract.IGamePlaying2View {

    @BindView(R.id.textureMapView)
    TextureMapView textureMapView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_my_progress)
    TextView tvMyProgress;
    private AMap aMap;

    private ArrayList<Point> totallist;

    private double currentLatitude;
    private double currentLongitude;
    private LocationHelper locationHelper;

    private GamePlayingPresenter presenter;
    private long gameId;
    private String title = "";
    private PointListAdapter adapter;
    private int gameType = 0;
    private int teamId = 0;//个人为0，团队为1
    private GameRecord gameRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_playing);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        presenter = new GamePlayingPresenter(this);

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
        if (RunApplication.entity != null && RunApplication.entity.cid == GameSort.ONLINE_PLAYABLE) {
            textureMapView.setVisibility(View.GONE);
        }
        //获取游戏数据和游戏记录
        initData();
        loadGameRecord();
    }

    private void initData() {
        gameId = RunApplication.gameId;
        gameType = RunApplication.type;
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

    private void loadGameRecord() {
        tvTitle.setText("加载中...");
        if (NetworkUtil.checkIsNetworkConnected()) {
            presenter.downLoadGameRecord(gameId, teamId);//个人游戏传0，团队游戏传1
        } else {
            getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    PopupWindowUtils.getInstance().showNetExceptionPopupwindow(getContext(), new PopupWindowUtils
                            .OnRetryCallback() {
                        @Override
                        public void onRetry() {
                            loadGameRecord();
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
                    Toast.makeText(GamePlayingActivity.this, "未解锁", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(GamePlayingActivity.this, PointTaskActivity.class);
                GamePlayingActivity.this.startActivity(intent);
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(GamePointTaskStateChanged result) {
        //刷新数据
        loadGameRecord();
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

    @OnClick({R.id.img_game_detail, R.id.back_btn, R.id.tv_my_progress})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.img_game_detail:
                Intent intent = new Intent(GamePlayingActivity.this, GameDetailActivity.class);
                intent.putExtra(Conf.FROM_WHERE, Conf.GAME_PLAYing_V2_ACTIVITY);
                startActivityForResult(intent, RequestCode.LEAVE_GAME);
                break;
            case R.id.tv_my_progress:
                startActivity(new Intent(GamePlayingActivity.this, GameOverActivity.class));
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
        tvTitle.setText(title);
        if (gameRecord != null && gameRecord.game_detail != null && gameRecord.game_detail.point_list != null) {
            RunApplication.gameRecord = gameRecord;
            this.gameRecord = gameRecord;
            totallist = gameRecord.game_detail.point_list;
            checkPointStatus();
            setAdapter();
            //绘制游戏区域
            drawPolygon();
        } else {
            Toast.makeText(this, "游戏记录为空", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPointStatus() {
        if (gameRecord != null && Integer.parseInt(gameRecord.play_statu) == PlayStatus.GAME_OVER) {
            String time = TimeFormatUtils.caculateUsingTime(gameRecord.last_task_ftime, gameRecord.start_time);
            tvMyProgress.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            tvMyProgress.setText("已通关，耗时" + time);
            tvMyProgress.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
            tvMyProgress.setEnabled(true);
            tvMyProgress.setClickable(true);
            RunApplication.isGameOver = true;
        } else {
            RunApplication.isGameOver = false;
        }
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
