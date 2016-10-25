package com.lptiyu.tanke.gamedisplay;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.gameplaying2.GamePlaying2Activity;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.enums.GameState;
import com.lptiyu.tanke.enums.GameType;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.gamedetails.GameDetailsActivity;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.permission.PermissionDispatcher;
import com.lptiyu.tanke.permission.TargetMethod;
import com.lptiyu.tanke.pojo.GameDisplayEntity;
import com.lptiyu.tanke.pojo.GetGameStatusResponse;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.TimeUtils;
import com.lptiyu.tanke.utils.XUtilsDownloader;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/1
 *
 * @author ldx
 */
public class NormalViewHolder extends BaseViewHolder<GameDisplayEntity> {

    @BindView(R.id.image_view)
    RoundedImageView imageView;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.team_type)
    ImageView teamType;

    @BindView(R.id.inner_test)
    ImageView innerTest;

    @BindView(R.id.location)
    TextView location;

    @BindView(R.id.time)
    TextView timeView;

    @BindView(R.id.tag)
    TextView tag;

    GameDisplayEntity gameDisplayEntity;

    private GameDisplayFragment fragment;
    //    private int gameZipDownloadFailedNum = 3;
    //    private ProgressDialog progressDialog;
    //    //    private final GameZipHelper gameZipHelper;
    //    //    private GameZipScanner mGameZipScanner;

    NormalViewHolder(ViewGroup parent, GameDisplayFragment fragment) {
        super(fromResLayout(parent, R.layout.item_game_display));
        ButterKnife.bind(this, itemView);
        this.fragment = fragment;
    }

    private void loadNetWorkData() {
        if (NetworkUtil.checkIsNetworkConnected()) {
            loadNet();
        } else {
            showNetUnConnectDialog();
        }
    }

    // 网络异常对话框
    private void showNetUnConnectDialog() {
        PopupWindowUtils.getInstance().showNetExceptionPopupwindow(getContext(), new PopupWindowUtils
                .OnNetExceptionListener() {
            @Override
            public void onClick(View view) {
                loadNetWorkData();
            }
        });
    }

    @OnClick(R.id.item_root)
    public void item_root() {
        if (gameDisplayEntity == null) {
            return;
        }
        int play_statu = gameDisplayEntity.getPlayStatu();
        String tempGameZipUrl = gameDisplayEntity.getGameZipUrl();
        if (play_statu == PlayStatus.NO_STATUS) {
            loadNetWorkData();
        } else if (play_statu != PlayStatus.NEVER_ENTER_GANME && (tempGameZipUrl == null || tempGameZipUrl.isEmpty()
                || tempGameZipUrl.equals(""))) {
            loadNetWorkData();
        } else//如果之前已经请求过状态了，就记录下来，避免每次网络请求
        {
            startGameByPlayStatu(play_statu);
        }
        //        loadNetWorkData();
    }

    @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_LOCATION)
    public void startPlayingGame() {
        Intent intent = new Intent(getContext(), GamePlaying2Activity.class);
        intent.putExtra(Conf.GAME_ID, gameDisplayEntity.getId());
        intent.putExtra(Conf.GAME_DISPLAY_ENTITY, gameDisplayEntity);
        fragment.startActivity(intent);
    }


    public void bind(GameDisplayEntity entity) {
        this.gameDisplayEntity = entity;
        parseImage(entity);
        parseTitle(entity);
        parseLocation(entity);
        parseTime(entity);
        //        parseTag(entity);
        parseTeamType(entity);
        parseInnerTest(entity);
    }

    private void parseImage(GameDisplayEntity entity) {
        Glide.with(fragment).load(entity.getImg())
                .error(R.drawable.default_pic)
                .into(imageView);
    }

    private void parseTitle(GameDisplayEntity entity) {
        title.setText(entity.getTitle());
    }

    private void parseLocation(GameDisplayEntity entity) {
        String area = "不限地址";

        if (entity.getArea() != "") {
            area = entity.getArea();
        }
        location.setText(area);
    }

    private void parseTime(GameDisplayEntity entity) {
        Observable.just(entity).map(
                new Func1<GameDisplayEntity, String>() {
                    @Override
                    public String call(GameDisplayEntity entity) {
                        String[] time = TimeUtils.parseTime(fragment.getContext(),
                                entity.getStartDate(), entity.getEndDate(),
                                entity.getStartTime(), entity.getEndTime());
                        if (time[0] != null && !time[0].equals("")) {
                            return time[0];
                        } else {
                            return time[1];
                        }
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        timeView.setText(s);
                    }
                });

    }

    private void parseTag(GameDisplayEntity entity) {
        switch (entity.getState()) {
            case GameState.NORMAL:
                tag.setText("");
                return;
            case GameState.ALPHA_TEST:
                tag.setText("内测中");
                return;
            case GameState.MAINTAINING:
                tag.setText("维护中");
                return;
            case GameState.FINISHED:
                tag.setText("已结束");
                return;
            default:
        }

    }

    private void parseTeamType(GameDisplayEntity entity) {
        if (entity.getType() == GameType.TEAM_TYPE) {
            teamType.setVisibility(View.VISIBLE);
        } else {
            //个人赛不需要展示标签
            //      teamType.setText(fragment.getString(R.string.team_type_individule));
            teamType.setVisibility(View.GONE);
        }
    }

    private void parseInnerTest(GameDisplayEntity entity) {
        switch (entity.getState()) {
            case GameState.NORMAL:
                innerTest.setVisibility(View.GONE);
                return;
            case GameState.ALPHA_TEST:
                innerTest.setVisibility(View.VISIBLE);
                innerTest.setImageResource(R.drawable.inner_test);
                return;
            case GameState.MAINTAINING:
                innerTest.setVisibility(View.GONE);
                return;
            case GameState.FINISHED:
                innerTest.setVisibility(View.VISIBLE);
                innerTest.setImageResource(R.drawable.have_finished);
                return;
            default:
        }
    }

    private void startGameByPlayStatu(int play_statu) {

        switch (play_statu) {
            case PlayStatus.NEVER_ENTER_GANME://从未玩过游戏，进入到游戏详情界面
                Intent intent = new Intent();
                intent.setClass(getContext(), GameDetailsActivity.class);
                intent.putExtra(Conf.GAME_ID, gameDisplayEntity.getId());
                intent.putExtra(Conf.FROM_WHERE, Conf.NormalViewHolder);
                getContext().startActivity(intent);
                break;
            case PlayStatus.GAME_OVER://游戏结束，暂不考虑
            case PlayStatus.HAVE_ENTERED_bUT_NOT_START_GAME://进入过但没开始游戏，进入到玩游戏界面
            case PlayStatus.HAVE_STARTED_GAME://进入并且已经开始游戏，进入到玩游戏界面
                //进入到玩游戏界面之前，先检测游戏包是否存在，存在则直接进入，否则要先下载游戏包
                //检查游戏包是否存在或者游戏解压后为空，判断完后游戏包已经被解压缩，并且已经将文件解析成实体类对象，此时可以直接从内存中取数据了
                String tempGameZipUrl = gameDisplayEntity.getGameZipUrl();
                if (tempGameZipUrl == null || tempGameZipUrl.equals(""))
                    return;

                new XUtilsDownloader(getContext(), tempGameZipUrl, gameDisplayEntity.getId(), new
                        XUtilsDownloader.FinishDownloadCallback() {
                            @Override
                            public void onFinishedDownload() {
                                startPlayingGame();
                            }
                        });
                break;
            default:
                break;
        }
    }

    private void loadNet() {
        HttpService.getGameService().getGameStatus(Accounts.getId(), gameDisplayEntity.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<GetGameStatusResponse>>() {
                    @Override
                    public void call(Response<GetGameStatusResponse> response) {
                        if (response.getStatus() == Response.RESPONSE_OK) {

                            String tempGameZipUrl = "";
                            if (response.getData().game_zip != null) {
                                tempGameZipUrl = response.getData().game_zip;
                            }

                            gameDisplayEntity.setGameZipUrl(tempGameZipUrl);
                            gameDisplayEntity.setPlayStatu(response.getData().play_statu);
                            startGameByPlayStatu(gameDisplayEntity.getPlayStatu());
                        } else {
                            Log.i("jason", "获取游戏状态错误信息：" + response.getInfo());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i("jason", "获取游戏状态错误信息throw：" + throwable.getMessage());
                    }
                });
    }
}
