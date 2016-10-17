package com.lptiyu.tanke.activities.gamedetail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.directionrun.DirectionRunActivity;
import com.lptiyu.tanke.activities.gameplaying_v2.GamePlayingV2Activity;
import com.lptiyu.tanke.entity.eventbus.NotifyHomeRefreshData;
import com.lptiyu.tanke.entity.response.HomeGameList;
import com.lptiyu.tanke.entity.response.Recommend;
import com.lptiyu.tanke.enums.GameSort;
import com.lptiyu.tanke.enums.GameType;
import com.lptiyu.tanke.enums.ResultCode;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameDetailActivity extends MyBaseActivity implements GameDetailContact.IGameDetailView {

    @BindView(R.id.tv_enter_game)
    TextView tvEnterGame;
    private GameDetailPresenter presenter;

    private long gameId;
    private int type;
    private String fromWhere;
    private ProgressDialog abandonGameDialog;
    private HomeGameList homeTabEntity;
    private Recommend recommend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        ButterKnife.bind(this);

        presenter = new GameDetailPresenter(this);

        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        fromWhere = intent.getStringExtra(Conf.FROM_WHERE);
        switch (fromWhere) {
            case Conf.HOME_HOT:
                tvEnterGame.setText("进入游戏");
                gameId = intent.getLongExtra(Conf.GAME_ID, -1);
                recommend = intent.getParcelableExtra(Conf.RECOMMEND);
                type = intent.getIntExtra(Conf.GAME_TYPE, GameType.INDIVIDUAL_TYPE);
                break;
            case Conf.HOME_TAB:
                tvEnterGame.setText("进入游戏");
                gameId = intent.getLongExtra(Conf.GAME_ID, -1);
                homeTabEntity = intent.getParcelableExtra(Conf.HOME_TAB_ENTITY);
                type = intent.getIntExtra(Conf.GAME_TYPE, GameType.INDIVIDUAL_TYPE);
                break;
            case Conf.GAME_PLAYing_V2_ACTIVITY:
                tvEnterGame.setText("放弃游戏");
                gameId = RunApplication.gameId;
                break;
        }
    }

    @OnClick(R.id.tv_enter_game)
    public void onClick() {
        switch (fromWhere) {
            case Conf.HOME_HOT:
                if (recommend.cid == GameSort.DIRECTION_RUN) {//如果是定向乐跑，则直接跳转到定向乐跑界面
                    startActivity(new Intent(GameDetailActivity.this, DirectionRunActivity.class));
                    finish();
                } else {
                    presenter.enterGame(gameId, type);
                }
                break;
            case Conf.HOME_TAB:
                if (homeTabEntity.cid == GameSort.DIRECTION_RUN) {//如果是定向乐跑，则直接跳转到定向乐跑界面
                    startActivity(new Intent(GameDetailActivity.this, DirectionRunActivity.class));
                } else {
                    presenter.enterGame(gameId, type);
                }
                break;
            case Conf.GAME_PLAYing_V2_ACTIVITY:
                //放弃游戏
                PopupWindowUtils.getInstance().showLeaveGamePopup(this, new PopupWindowUtils.OnClickPopupListener() {
                    @Override
                    public void onClick(View view) {
                        abandonGame();
                    }
                });
                break;
        }
    }

    private void abandonGame() {
        if (NetworkUtil.checkIsNetworkConnected()) {
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
        Intent intent = new Intent(GameDetailActivity.this, GamePlayingV2Activity.class);
        intent.putExtra(Conf.GAME_ID, gameId);
        startActivity(intent);
        EventBus.getDefault().post(new NotifyHomeRefreshData());
        finish();
    }

    @Override
    public void successLeaveGame() {
        if (abandonGameDialog != null) {
            abandonGameDialog.dismiss();
        }
        EventBus.getDefault().post(new NotifyHomeRefreshData());
        setResult(ResultCode.LEAVE_GAME);
        finish();
    }

    @Override
    public void failLeaveGame(String errMsg) {
        if (errMsg != null) {
            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
        }
        if (abandonGameDialog != null) {
            abandonGameDialog.dismiss();
        }
    }
}
