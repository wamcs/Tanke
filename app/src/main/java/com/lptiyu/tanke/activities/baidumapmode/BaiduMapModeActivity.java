package com.lptiyu.tanke.activities.baidumapmode;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.TextureMapView;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.gameplaying.assist.ConsoleHelper;
import com.lptiyu.tanke.gameplaying.assist.LocateHelper;
import com.lptiyu.tanke.gameplaying.assist.MapHelper;
import com.lptiyu.tanke.gameplaying.assist.TimingTaskHelper;
import com.lptiyu.tanke.gameplaying.assist.zip.GameZipHelper;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.gameplaying.records.RecordsHandler;
import com.lptiyu.tanke.gameplaying.records.RecordsUtils;
import com.lptiyu.tanke.pojo.GameDetailsEntity;
import com.lptiyu.tanke.trace.bean.HistoryTrackData;
import com.lptiyu.tanke.trace.history.HistoryTrackCallback;
import com.lptiyu.tanke.trace.history.HistoryTrackHelper;
import com.lptiyu.tanke.trace.history.IHistoryTrackHelper;
import com.lptiyu.tanke.trace.tracing.ITracingHelper;
import com.lptiyu.tanke.trace.tracing.TracingCallback;
import com.lptiyu.tanke.trace.tracing.TracingHelper;
import com.lptiyu.tanke.utils.TimeUtils;
import com.lptiyu.tanke.widget.BaseSpotScrollView;
import com.lptiyu.tanke.widget.dialog.TextDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaiduMapModeActivity extends AppCompatActivity implements
        BDLocationListener,
        TracingCallback,
        MapHelper.OnMapMarkerClickListener,
        BaseSpotScrollView.OnSpotItemClickListener,
        HistoryTrackCallback {

    @BindView(R.id.map_view)
    TextureMapView mapView;
    @BindView(R.id.start_locate)
    ImageView startLocate;
    @BindView(R.id.move_to_target)
    ImageView moveToTarget;
    @BindView(R.id.game_data)
    ImageView gameData;
    @BindView(R.id.zoom_out)
    ImageView zoomOut;
    @BindView(R.id.zoom_in)
    ImageView zoomIn;

    Context context = this;

    boolean isReachedAttackPoint = false;
    boolean isGameFinished = false;

    long gameId;
    long teamId;
    String entityName;
    long queryHistoryTrackStartTime;
    long queryHistoryTrackEndTime;

    int currentAttackPointIndex = 0;
    Point currentAttackPoint;
    List<Point> mPoints;
    GameDetailsEntity mGameDetailsEntity;

    MapHelper mapHelper;
    LocateHelper locateHelper;
    ConsoleHelper consoleHelper;
    GameZipHelper gameZipHelper;
    ITracingHelper mTracingHelper;
    IHistoryTrackHelper mHistoryTrackHelper;
    TimingTaskHelper mTimingTaskHelper;

    AlertDialog mErrorDialog;
    AlertDialog mAlertDialog;
    AlertDialog mLoadingDialog;
    TextDialog mTextDialog;

    private static final int START_QUERY_TRACK = 1;
    private static final int STOP_QUERT_TRACK = 2;
    private static final long QUERY_HISTORY_TRACK_DELAY = TimeUtils.ONE_MINUTE_TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_map_mode);
        ButterKnife.bind(this);
    }

    private void init(View view) {

//        gameZipHelper = new GameZipHelper();
//
//        Intent intent = getIntent();
//        gameId = intent.getLongExtra(Conf.GAME_ID, Conf.TEMP_GAME_ID);
//        mGameDetailsEntity = intent.getParcelableExtra(Conf.GAME_DETAIL);
//        queryHistoryTrackEndTime = System.currentTimeMillis() / TimeUtils.ONE_SECOND_TIME;
//        teamId = intent.getLongExtra(Conf.TEAM_ID, Conf.TEMP_TEAM_ID);
//        entityName = Conf.makeUpTrackEntityName(gameId, Accounts.getId());
//        if (!gameZipHelper.checkAndParseGameZip(gameId) || gameZipHelper.getmPoints().size() == 0) {
//            mLoadingDialog.dismiss();
//            return;
//        }
//        mPoints = gameZipHelper.getmPoints();
        mapHelper = new MapHelper(context, mapView);
        mapHelper.bindData(mPoints);
        mapHelper.setmMapMarkerClickListener(this);
        consoleHelper = new ConsoleHelper(this, view, mPoints);
        consoleHelper.setOnSpotClickListener(this);
        mTracingHelper = new TracingHelper(getApplicationContext(), this);
        mTracingHelper.entityName(entityName);
        mHistoryTrackHelper = new HistoryTrackHelper(getApplicationContext(), this);
        locateHelper = new LocateHelper(getApplicationContext());
        locateHelper.registerLocationListener(this);


        //初始化游戏记录
        RecordsUtils.initRecordsHandler(new RecordsHandler.Builder(gameId, teamId).build());
    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.move_to_target:
                moveToTarget();
                break;
        }
    }

    private void moveToTarget() {
        if (mapHelper != null) {
            mapHelper.animateCameraToCurrentTarget();
        }
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {

    }

    @Override
    public void onRequestFailedCallback(String s) {

    }

    @Override
    public void onQueryHistoryTrackCallback(HistoryTrackData trackData) {

    }

    @Override
    public void onMarkerClicked(Point point) {

    }

    @Override
    public void onSpotItemClick(View view, int position) {

    }

    @Override
    public void onTraceStart() {

    }

    @Override
    public void onTracePush(byte b, String s) {

    }

    @Override
    public void onTraceStop() {

    }
}
