package com.lptiyu.tanke.activities.gamedetail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.directionrun.DirectionRunActivity;
import com.lptiyu.tanke.activities.gamedetailarea.GameDetailAreaActivity;
import com.lptiyu.tanke.activities.gameplaying.GamePlayingActivity;
import com.lptiyu.tanke.entity.BaseEntity;
import com.lptiyu.tanke.entity.eventbus.EnterGame;
import com.lptiyu.tanke.entity.eventbus.LeaveGame;
import com.lptiyu.tanke.entity.eventbus.PlayAgain;
import com.lptiyu.tanke.entity.response.GameDetail;
import com.lptiyu.tanke.entity.response.Ranks;
import com.lptiyu.tanke.enums.GameSort;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.enums.ResultCode;
import com.lptiyu.tanke.enums.SortIndex;
import com.lptiyu.tanke.enums.Where;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.DisplayUtils;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.TimeUtils;
import com.lptiyu.tanke.widget.CircularImageView;
import com.lptiyu.tanke.widget.PullToZoomScrollView;
import com.lptiyu.tanke.widget.dialog.ShareDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lptiyu.tanke.global.AppData.getContext;

public class GameDetailActivity extends MyBaseActivity implements GameDetailContact.IGameDetailView {

    @BindView(R.id.tv_enter_game)
    TextView tvEnterGame;
    @BindView(R.id.rl_enterOrAbandonGame)
    RelativeLayout rlEnterOrAbandonGame;
    @BindView(R.id.image_cover)
    ImageView imageCover;
    @BindView(R.id.tv_game_detail_name)
    TextView tvGameDetailName;
    @BindView(R.id.tv_game_detail_location)
    TextView tvGameDetailLocation;
    @BindView(R.id.tv_game_detail_date)
    TextView tvGameDetailDate;
    @BindView(R.id.textureMapView)
    TextureMapView mapView;
    @BindView(R.id.tv_play_num)
    TextView tvPlayNum;
    @BindView(R.id.ll_ranks_icon)
    LinearLayout llRanksIcon;
    @BindView(R.id.tv_game_detail_introduction)
    TextView tvGameDetailIntroduction;
    @BindView(R.id.pullToZoom)
    PullToZoomScrollView pullToZoom;
    private GameDetailPresenter presenter;
    private long gameId;
    private int type;
    private String fromWhere;
    private ProgressDialog abandonGameDialog;
    private AMap map;
    private BaseEntity entity;
    private GameDetail gameDetail;
    private int status = -1;
    private static final int ENTER_GAME = 0;
    private static final int LEAVE_GAME = 1;
    private static final int PLAY_AGAIN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        ButterKnife.bind(this);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mapView.onCreate(savedInstanceState);
        if (map == null) {
            map = mapView.getMap();
            UiSettings uiSettings = map.getUiSettings();
            uiSettings.setAllGesturesEnabled(false);
            uiSettings.setZoomControlsEnabled(false);
            //隐藏高德地图的logo
            uiSettings.setLogoBottomMargin(-200);
            uiSettings.setLogoLeftMargin(-200);
        }

        presenter = new GameDetailPresenter(this);

        initData();
    }

    private void initData() {
        gameId = RunApplication.gameId;
        type = RunApplication.type;
        Intent intent = getIntent();
        fromWhere = intent.getStringExtra(Conf.FROM_WHERE);
        switch (fromWhere) {
            case Conf.HOME_HOT:
            case Conf.HOME_TAB:
            case Conf.BANNER:
                tvEnterGame.setText("进入游戏");
                status = ENTER_GAME;
                break;
            case Conf.GAME_PLAYing_ACTIVITY:
                if (RunApplication.isGameOver) {
                    tvEnterGame.setText("再玩一次");
                    status = PLAY_AGAIN;
                } else {
                    tvEnterGame.setText("放弃游戏");
                    status = LEAVE_GAME;
                }
                break;
        }
        if (RunApplication.where >= 0) {//如果where值大于等于0，说明是从已完成里面进来的，此时，放弃按钮要隐藏
            rlEnterOrAbandonGame.setVisibility(View.GONE);
        }
        loadGameDetailData();
    }

    private void loadGameDetailData() {
        tvEnterGame.setEnabled(false);
        presenter.getGameDetail(RunApplication.gameId);
    }

    private void abandonGame() {
        if (NetworkUtil.checkIsNetworkConnected()) {
            tvEnterGame.setEnabled(false);
            abandonGameDialog = ProgressDialog.show(this, "", "加载中...", true);
            presenter.leaveGame(gameId);

        } else {
            PopupWindowUtils.getInstance().showNetExceptionPopupwindow(this, new PopupWindowUtils.OnRetryCallback() {
                @Override
                public void onRetry() {
                    abandonGame();
                }
            });
        }
    }

    @Override
    public void successEnterGame() {
        tvEnterGame.setEnabled(true);
        //更改该游戏的状态
        RunApplication.where = Where.GAME_DETAIL;
        RunApplication.recordId = -1;
        if (RunApplication.entity != null) {
            RunApplication.entity.play_status = PlayStatus.HAVE_ENTERED_BUT_NOT_START_GAME;
        }
        EventBus.getDefault().post(new EnterGame());
        if (status == ENTER_GAME) {
        } else if (status == PLAY_AGAIN) {
            EventBus.getDefault().post(new PlayAgain());
        }
        Intent intent = new Intent(GameDetailActivity.this, GamePlayingActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void successLeaveGame() {
        if (abandonGameDialog != null) {
            abandonGameDialog.dismiss();
        }
        //更改该游戏的状态
        if (RunApplication.entity != null) {
            RunApplication.entity.play_status = PlayStatus.NO_STATUS;
        }
        EventBus.getDefault().post(new LeaveGame());
        setResult(ResultCode.LEAVE_GAME);
        finish();
    }

    @Override
    public void successGetGameDetail(GameDetail gameDetail) {
        tvEnterGame.setEnabled(true);
        if (gameDetail != null) {
            this.gameDetail = gameDetail;
            bindData();
        }
    }

    private void bindData() {
        if (gameDetail != null) {
            Glide.with(this).load(gameDetail.pic).error(R.drawable.default_pic).placeholder(R.drawable.default_pic)
                    .into(imageCover);
            tvPlayNum.setText(String.format(getResources().getString(R.string.game_detail_play_num), gameDetail.num +
                    ""));
            tvGameDetailIntroduction.setText(Html.fromHtml(gameDetail.content) + "");
            tvGameDetailLocation.setText(String.format(getResources().getString(R.string.game_detail_area),
                    gameDetail.area + ""));
            tvGameDetailName.setText(gameDetail.title + "");
            String time = TimeUtils.parseTime(this, gameDetail.start_date, gameDetail.end_date, gameDetail
                    .start_time, gameDetail.end_time);
            tvGameDetailDate.setText(String.format(getResources().getString(R.string.game_detail_date_time),
                    time + ""));
            LatLng latLng = new LatLng(Double.parseDouble(gameDetail.latitude), Double.parseDouble(gameDetail
                    .longtitude));
            moveToLocation(latLng);
            addMarker(latLng);

            if (gameDetail.rank_list != null && gameDetail.rank_list.size() > 0) {
                int size = Math.min(gameDetail.rank_list.size(), 5);
                for (int i = 0; i < size; i++) {
                    Ranks ranks = gameDetail.rank_list.get(i);
                    CircularImageView circularImageView = new CircularImageView(this);
                    int widthHeight = DisplayUtils.dp2px(40);
                    circularImageView.setLayoutParams(new LinearLayout.LayoutParams(widthHeight, widthHeight));
                    Glide.with(this).load(ranks.user_avatar).error(R.mipmap.default_avatar).crossFade().into
                            (circularImageView);
                    llRanksIcon.addView(circularImageView);
                }
            }
        }
    }

    private void addMarker(LatLng latLng) {
        if (latLng == null) {
            return;
        }
        if (map != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R
                    .drawable.qi))).position(latLng);
            map.addMarker(markerOptions);
        }
    }

    //地图中心移动到指定位置
    private void moveToLocation(LatLng latLng) {
        if (latLng == null) {
            return;
        }
        if (map != null)
            map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 10, 0, 0)), 1, null);
    }

    @Override
    public void failLeaveGame(String errMsg) {
        tvEnterGame.setEnabled(true);
        if (errMsg != null) {
            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
        }
        if (abandonGameDialog != null) {
            abandonGameDialog.dismiss();
        }
    }

    private void initGPS() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("为了定位更加精确，请打开GPS");
            dialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                        }
                    });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        } else {
            startActivity(new Intent(GameDetailActivity.this, DirectionRunActivity.class));
            finish();
        }
    }

    @OnClick({R.id.img_back, R.id.img_share, R.id.tv_enter_game, R.id.rl_play_num, R.id.view_click_into_detail_area,
            R.id.rl_ranks})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_share:
                intent = new Intent(getContext(), ShareDialog.class);
                intent.putExtra(Conf.SHARE_TITLE, String.format("我正在玩定向AR游戏 %s，一起来探秘吧", gameDetail.title));
                intent.putExtra(Conf.SHARE_TEXT, Html.fromHtml(gameDetail.content).toString());
                intent.putExtra(Conf.SHARE_IMG_URL, gameDetail.pic);
                intent.putExtra(Conf.SHARE_URL, gameDetail.url);
                startActivity(intent);
                overridePendingTransition(R.anim.translate_in_bottom, R.anim.translate_out_bottom);
                break;
            case R.id.tv_enter_game:
                enterGame();
                break;
            case R.id.view_click_into_detail_area:
                if (gameDetail == null || gameDetail.game_zone == null || gameDetail.game_zone.size() < 3) {
                    Toast.makeText(this, "游戏区域为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent = new Intent(GameDetailActivity.this, GameDetailAreaActivity.class);
                intent.putExtra(Conf.GAME_DETAIL, gameDetail);
                startActivity(intent);
                break;
            case R.id.rl_ranks:
                Toast.makeText(this, "即将开放，敬请期待", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void enterGame() {
        entity = RunApplication.entity;
        if (entity == null) {
            return;
        }
        if (gameDetail == null) {
            return;
        }
        //TODO 团队赛事
        if (entity.cid == SortIndex.COMPETITION_ACTIVITY) {
            Toast.makeText(this, "团队赛事即将开放，敬请期待", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (gameDetail.play_status) {
            case PlayStatus.NO_STATUS:
            case PlayStatus.NEVER_ENTER_GANME:
            case PlayStatus.GAME_OVER:
                //直接调用进入游戏的接口
                if (entity.cid == GameSort.DIRECTION_RUN) {//如果是定向乐跑，则直接跳转到定向乐跑界面
                    initGPS();
                } else {
                    if (status == PLAY_AGAIN) {
                        PopupWindowUtils.getInstance().showPlayAgainPopup(this, new PopupWindowUtils
                                .OnClickPopupListener() {
                            @Override
                            public void sure() {
                                tvEnterGame.setEnabled(false);
                                presenter.enterGame(gameId, type);
                            }
                        });
                    } else {
                        tvEnterGame.setEnabled(false);
                        presenter.enterGame(gameId, type);
                    }
                }
                break;
            case PlayStatus.HAVE_ENTERED_BUT_NOT_START_GAME:
            case PlayStatus.HAVE_STARTED_GAME:
                //放弃游戏
                PopupWindowUtils.getInstance().showLeaveGamePopup(this, new PopupWindowUtils.OnClickPopupListener() {
                    @Override
                    public void sure() {
                        abandonGame();
                    }
                });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
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
    public void failLoad(String errMsg) {
        super.failLoad(errMsg);
        tvEnterGame.setEnabled(true);
    }
}
