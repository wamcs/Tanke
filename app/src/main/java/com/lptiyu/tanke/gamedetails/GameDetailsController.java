package com.lptiyu.tanke.gamedetails;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.gameplaying.GamePlayingActivity;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.entity.EnterGameResponse;
import com.lptiyu.tanke.entity.response.GameDetail;
import com.lptiyu.tanke.enums.GameType;
import com.lptiyu.tanke.enums.ResultCode;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.net.HttpService;
import com.lptiyu.tanke.net.Response;
import com.lptiyu.tanke.permission.PermissionDispatcher;
import com.lptiyu.tanke.permission.TargetMethod;
import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.GameZipUtils;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.ProgressDialogHelper;
import com.lptiyu.tanke.utils.TimeUtils;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.xutils3.XUtilsHelper;
import com.lptiyu.tanke.widget.CustomTextView;
import com.lptiyu.tanke.widget.dialog.ShareDialog;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/2
 *
 * @author ldx
 */
public class GameDetailsController extends ActivityController {

    @BindView(R.id.game_detail_title)
    CustomTextView mTextTitle;

    @BindView(R.id.image_cover)
    ImageView mImageCover;

    @BindView(R.id.team_type_text)
    CustomTextView mTextTeamType;

    @BindView(R.id.tv_game_detail_location)
    CustomTextView mTextLocation;

    @BindView(R.id.tv_game_detail_date)
    CustomTextView mTextTime;

    @BindView(R.id.game_detail_intro)
    CustomTextView mTextGameIntro;

    @BindView(R.id.game_detail_rule)
    CustomTextView mTextRule;

    @BindView(R.id.enter_game)
    CustomTextView mTextEnterGame;

    @BindView(R.id.num_playing)
    CustomTextView mTextPeoplePlaying;

    AlertDialog mLoadingDialog;
    ProgressDialog progressDialog;

    private Subscription gameDetailsSubscription;

    //这个变量用来标志下载出错的次数，为0的时候停止重试下载
    private int gameZipDownloadFailedNum = 3;
    private long gameId;
    private String tempGameZipUrl;

    private GameDetail mGameDetailsResponse;
    private final String from_where;

    public GameDetailsController(GameDetailsActivity activity, View view) {
        super(activity, view);
        gameId = getIntent().getLongExtra(Conf.GAME_ID, Integer.MIN_VALUE);
        from_where = getIntent().getStringExtra(Conf.FROM_WHERE);
        ButterKnife.bind(this, view);
        init();
    }

    /**
     * 对整个界面进行初始化的操作，还有根据gameId从服务器获取游戏详情的数据json
     * 获取成功之后用{@link GameDetailsController#bind(GameDetail)}方法来进行数据的绑定操作
     */
    private void init() {
        if (gameDetailsSubscription != null) {
            gameDetailsSubscription.unsubscribe();
            gameDetailsSubscription = null;
        }

        //
        // showLoadingDialog();
        mTextTitle.setText("加载中...");

        //prepare for the network request
        gameDetailsSubscription = HttpService.getGameService().getGameDetails(gameId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Response<GameDetail>, GameDetail>() {
                    @Override
                    public GameDetail call(Response<GameDetail> response) {
                        if (response.getStatus() != Response.RESPONSE_OK || response.getData() == null) {
                            // mLoadingDialog.dismiss();
                            Timber.e("Network Error (%d, %s)", response.getStatus(), response.getInfo());
                            throw new IllegalStateException(response.getInfo());
                        }
                        return response.getData();
                    }
                })
                .subscribe(new Action1<GameDetail>() {
                    @Override
                    public void call(GameDetail gameDetailsEntity) {
                        bind(gameDetailsEntity);
                        // mLoadingDialog.dismiss();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        // mLoadingDialog.dismiss();
                        ToastUtil.TextToast(throwable.getMessage());
                    }
                });
    }

    /**
     * 从服务器上获取到游戏的详情数据之后，在这个方法里面进行一系列的数据绑定操作
     *
     * @param entity
     */
    private void bind(GameDetail entity) {
        this.mGameDetailsResponse = entity;
        mTextTitle.setText(entity.title);
        //        mTextLocation.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        //        mTextLocation.getPaint().setAntiAlias(true);//抗锯齿
        String area = "不限地址";
        if (entity.area != "")
            area = entity.area;
        mTextLocation.setText(area);
        mTextPeoplePlaying.setText(String.valueOf(entity.num));
        parseTime(mTextTime, entity);
        if (entity.type == GameType.INDIVIDUAL_TYPE) {
            mTextTeamType.setVisibility(View.GONE);
        } else {
            mTextTeamType.setText(String.format(getString(R.string.team_game_formatter), Integer.parseInt(entity.min)));
        }
        mTextGameIntro.setText(Html.fromHtml(Html.fromHtml(entity.content) + ""));
        //        mTextRule.setText(Html.fromHtml(Html.fromHtml(entity.getRule()) + ""));
        switch (from_where) {
            case Conf.ELASTIC_HEADER_VIEW_HOLDER: {
                if (entity.states == 3)//游戏已经下线，但还展示在前台
                {
                    mTextEnterGame.setText("游戏已经下线休息了~");
                } else {
                    mTextEnterGame.setText("进入游戏");
                }

                break;
            }
            case Conf.NORMAL_VIEW_HOLDER:
                if (entity.states == 3)//游戏已经下线，但还展示在前台
                {
                    mTextEnterGame.setText("游戏已经下线休息了~");
                } else {
                    mTextEnterGame.setText("进入游戏");
                }
                break;
            case Conf.GAME_PLAYing_V2_ACTIVITY:
                if (entity.states == 3)//游戏已经下线，但还展示在前台
                {
                    mTextEnterGame.setText("游戏已经下线休息了~");
                } else {
                    mTextEnterGame.setText("放弃游戏");
                }

                break;
        }
        Glide.with(getActivity()).load(entity.pic).error(R.drawable.default_pic).centerCrop().into(mImageCover);
    }

    /**
     * 用来解析游戏规定的时间
     *
     * @param textView
     * @param entity
     */
    public void parseTime(final CustomTextView textView, GameDetail entity) {
        Observable.just(entity).map(
                new Func1<GameDetail, String>() {
                    @Override
                    public String call(GameDetail entity) {
                        return TimeUtils.parseTime(getContext(),
                                entity.start_date, entity.end_date,
                                entity.start_time, entity.end_time);
                    }
                })
                .subscribeOn(Schedulers.computation())//CPU密集型计算
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        textView.setText(s);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @OnClick(R.id.back_btn)
    public void goBackClicked() {
        finish();
    }

    @OnClick(R.id.share_btn)
    public void shareClicked() {
        Intent intent = new Intent(getContext(), ShareDialog.class);
        intent.putExtra(Conf.SHARE_TITLE, String.format("我正在玩定向AR游戏 %s，一起来探秘吧", mGameDetailsResponse.title));
        intent.putExtra(Conf.SHARE_TEXT, Html.fromHtml(Html.fromHtml(mGameDetailsResponse.content).toString())
                .toString());

        intent.putExtra(Conf.SHARE_IMG_URL, mGameDetailsResponse.pic);
        intent.putExtra(Conf.SHARE_URL, mGameDetailsResponse.url);
        startActivity(intent);
        overridePendingTransition(R.anim.translate_in_bottom, R.anim.translate_out_bottom);
    }

    //点击范围更广点
    @OnClick(R.id.rl_time_location)
    public void startLocationDetailMap() {

    }


    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, null);
            CustomTextView textView = ((CustomTextView) view.findViewById(R.id.loading_dialog_textview));
            textView.setText("加载中");
            mLoadingDialog = new AlertDialog.Builder(getActivity())
                    .setCancelable(false)
                    .setView(view)
                    .create();
        }
        mLoadingDialog.show();
    }

    @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_LOCATION)
    public void startPlayingGame() {
        Intent intent = new Intent(getContext(), GamePlayingActivity.class);
        intent.putExtra(Conf.GAME_ID, gameId);
        intent.putExtra(Conf.GAME_DETAIL, mGameDetailsResponse);
        //        //从游戏详情进入玩游戏界面，说明这是用户第一次进入到游戏，所以需要传入数据库所需要的字段
        //        intent.putExtra(Conf.JOIN_TIME, System.currentTimeMillis() + "");
        if (mGameDetailsResponse.type == GameType.TEAM_TYPE) {
            //TODO : need pass team id to GamePlayingActivity when the team game open
        }
        startActivity(intent);
        finish();
    }

    // 网络异常对话框
    private void showNetUnConnectDialog() {
        PopupWindowUtils.getInstance().showNetExceptionPopupwindow(getContext(), new PopupWindowUtils.OnRetryCallback
                () {
            @Override
            public void onRetry() {
                loadNet();
            }
        });
    }

    @OnClick(R.id.enter_game)
    public void ensureClicked() {
        if (mGameDetailsResponse == null) {
            ToastUtil.TextToast("获取游戏信息失败");
            return;
        }
        if (mGameDetailsResponse.states == 3)//游戏已经下线，但还展示在前台
        {
            return;
        }

        switch (from_where) {
            case Conf.ELASTIC_HEADER_VIEW_HOLDER:
            case Conf.NORMAL_VIEW_HOLDER:
                if (mGameDetailsResponse.type == GameType.TEAM_TYPE) {
                    ToastUtil.TextToast("团队赛正在开发中");
                    return;
                }
                //请求加入游戏接口，获取游戏包下载链接
                loadNet();
                break;
            case Conf.GAME_PLAYing_V2_ACTIVITY:
                // 放弃游戏
                showPopup();
                break;
        }
    }

    private void loadNet() {
        if (NetworkUtil.checkIsNetworkConnected()) {
            enterGame();
        } else {
            showNetUnConnectDialog();
        }
    }

    private void showPopup() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_abandon_game, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setAnimationStyle(R.style.Popup_Animation);
        popupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 0);

        popupView.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupView.findViewById(R.id.tv_ensure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abandonGamePopup();
            }
        });

    }

    private void abandonGamePopup() {
        if (NetworkUtil.checkIsNetworkConnected()) {
            progressDialog = ProgressDialog.show(getContext(), "", "加载中...", true);
            progressDialog.show();
            abandonGame();
        } else {
            showNetUnConnectDialog();
        }
    }

    private void abandonGame() {
        HttpService.getGameService().leaveGame(Accounts.getId(), gameId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<Void>>() {
                    @Override
                    public void call(Response<Void> response) {
                        //                        Log.i("jason", "放弃游戏结果:" + response);
                        if (response.getStatus() == Response.RESPONSE_OK) {

                            //同时清空该游戏的记录
                            RunApplication.getInstance().setgetPlayingThemeLine(null);
                            getActivity().setResult(ResultCode.LEAVE_GAME);
                            getActivity().finish();
                            //                            RunApplication.getInstance().finishActivity();
                            //                            Toast.makeText(getContext(), "您已成功放弃该游戏", Toast
                            // .LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "放弃该游戏失败", Toast.LENGTH_SHORT).show();
                        }

                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(getContext(), "放弃该游戏失败", Toast.LENGTH_SHORT).show();
                        //                        Log.i("jason", "abandon game error:" + throwable.getMessage());

                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    /**
     * 请求加入游戏，获取游戏下载链接
     */
    private void enterGame() {
        HttpService.getGameService().enterGame(Accounts.getId(), gameId, mGameDetailsResponse.type)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Response<EnterGameResponse>>() {
            @Override
            public void call(Response<EnterGameResponse> response) {
                if (response.getStatus() == Response.RESPONSE_OK) {

                    //                    tempGameZipUrl = response.getData().game_zip;
                    if (new GameZipUtils().isParsedFileExist(gameId) == null) {
                        //根据获取到的游戏包下载链接去下载游戏
                        progressDialog = ProgressDialogHelper.getSpinnerProgressDialog(getContext());
                        progressDialog.show();
                        downloadGameZipFile();
                    } else {
                        startPlayingGame();
                    }
                } else {
                    throw new RuntimeException("请求加入游戏获取游戏包下载地址失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.i("jason", "请求加入游戏获取游戏包下载地址失败:" + throwable.getMessage());
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                PopupWindowUtils.getInstance().showFailLoadPopupwindow(getContext());
            }
        });
    }

    /**
     * 根据游戏下载包链接下载该游戏包
     */
    private void downloadGameZipFile() {
        XUtilsHelper.getInstance().downLoad(tempGameZipUrl, DirUtils.getGameDirectory().getAbsolutePath(), new
                XUtilsHelper.IDownloadCallback() {
            @Override
            public void successs(File file) {
                String zippedFilePath = file.getAbsolutePath();
                GameZipUtils gameZipUtils = new GameZipUtils();
                //解压文件
                gameZipUtils.parseZipFile(zippedFilePath);
                String parsedFilePath = gameZipUtils.isParsedFileExist(gameId);
                if (parsedFilePath != null) {
                    file.delete();
                    startPlayingGame();
                } else {
                    Toast.makeText(getContext(), "游戏包解压失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void progress(long total, long current, boolean isDownloading) {
                //游戏进度
                if (progressDialog != null) {
                    progressDialog.setMessage("加载中" + current * 100 / total + "%");
                }

            }

            @Override
            public void finished() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onError(String errMsg) {
                PopupWindowUtils.getInstance().showFailLoadPopupwindow(getContext());
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (gameDetailsSubscription != null && !gameDetailsSubscription.isUnsubscribed()) {
            gameDetailsSubscription.unsubscribe();
        }
    }
}

