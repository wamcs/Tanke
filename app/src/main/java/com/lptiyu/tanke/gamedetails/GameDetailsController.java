package com.lptiyu.tanke.gamedetails;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.gameplaying2.GamePlaying2Activity;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.permission.PermissionDispatcher;
import com.lptiyu.tanke.permission.TargetMethod;
import com.lptiyu.tanke.pojo.EnterGameResponse;
import com.lptiyu.tanke.pojo.GAME_TYPE;
import com.lptiyu.tanke.pojo.GameDetailsEntity;
import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.TimeUtils;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.widget.CustomTextView;
import com.lptiyu.tanke.widget.dialog.ShareDialog;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
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

    @BindView(R.id.game_detail_location)
    CustomTextView mTextLocation;

    @BindView(R.id.game_detail_time)
    CustomTextView mTextTime;

    @BindView(R.id.game_detail_intro)
    CustomTextView mTextGameIntro;

    @BindView(R.id.game_detail_rule)
    CustomTextView mTextRule;

    @BindView(R.id.game_detail_ensure)
    CustomTextView mTextEnsure;

    @BindView(R.id.num_playing)
    CustomTextView mTextPeoplePlaying;

    AlertDialog mLoadingDialog;
    ProgressDialog progressDialog;

    private Subscription gameDetailsSubscription;

    //这个变量用来标志下载出错的次数，为0的时候停止重试下载
    private int gameZipDownloadFailedNum = 3;

    private long gameId;
    private String tempGameZipUrl;
    private ShareDialog shareDialog;

    //    private GameZipScanner mGameZipScanner;

    private GameDetailsEntity mGameDetailsEntity;
    //    private DBGameRecord gameRecord;

    public GameDetailsController(GameDetailsActivity activity, View view) {
        super(activity, view);
        gameId = getIntent().getLongExtra(Conf.GAME_ID, Integer.MIN_VALUE);
        ButterKnife.bind(this, view);
        init();
    }

    /**
     * 对整个界面进行初始化的操作，还有根据gameId从服务器获取游戏详情的数据json
     * 获取成功之后用{@link GameDetailsController#bind(GameDetailsEntity)}方法来进行数据的绑定操作
     */
    private void init() {
        if (gameDetailsSubscription != null) {
            gameDetailsSubscription.unsubscribe();
            gameDetailsSubscription = null;
        }

        showLoadingDialog();
        ////        扫描游戏包中的信息
        //        mGameZipScanner = new GameZipScanner();

        //请求游戏详情数据
        gameDetailsSubscription = HttpService.getGameService().getGameDetails(gameId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Response<GameDetailsEntity>, GameDetailsEntity>() {
                    @Override
                    public GameDetailsEntity call(Response<GameDetailsEntity> response) {
                        if (response.getStatus() != Response.RESPONSE_OK || response.getData() == null) {
                            mLoadingDialog.dismiss();
                            Timber.e("Network Error (%d, %s)", response.getStatus(), response.getInfo());
                            throw new IllegalStateException(response.getInfo());
                        }
                        return response.getData();
                    }
                })
                .subscribe(new Action1<GameDetailsEntity>() {
                    @Override
                    public void call(GameDetailsEntity gameDetailsEntity) {
                        bind(gameDetailsEntity);
                        mLoadingDialog.dismiss();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mLoadingDialog.dismiss();
                        ToastUtil.TextToast(throwable.getMessage());
                    }
                });
    }

    /**
     * 从服务器上获取到游戏的详情数据之后，在这个方法里面进行一系列的数据绑定操作
     *
     * @param entity
     */
    private void bind(GameDetailsEntity entity) {
        this.mGameDetailsEntity = entity;
        mTextTitle.setText(entity.getTitle());
        mTextLocation.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        mTextLocation.getPaint().setAntiAlias(true);//抗锯齿
        mTextLocation.setText(entity.getArea());
        mTextPeoplePlaying.setText(String.valueOf(entity.getPeoplePlaying()));
        parseTime(mTextTime, entity);
        if (entity.getType() == GAME_TYPE.INDIVIDUALS) {
            mTextTeamType.setVisibility(View.GONE);
        } else {
            mTextTeamType.setText(String.format(getString(R.string.team_game_formatter), entity.getMinNum()));
        }
        mTextGameIntro.setText(Html.fromHtml(Html.fromHtml(entity.getGameIntro()).toString()));
        mTextRule.setText(Html.fromHtml(Html.fromHtml(entity.getRule()).toString()));

        Glide.with(getActivity()).load(entity.getImg()).error(R.mipmap.need_to_remove).centerCrop().into(mImageCover);
    }

    /**
     * 用来解析游戏规定的时间
     *
     * @param textView
     * @param entity
     */
    public void parseTime(final CustomTextView textView, GameDetailsEntity entity) {
        Observable.just(entity).map(
                new Func1<GameDetailsEntity, String>() {
                    @Override
                    public String call(GameDetailsEntity entity) {
                        return TimeUtils.parseTime(getContext(),
                                entity.getStartDate(), entity.getEndDate(),
                                entity.getStartTime(), entity.getEndTime());
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


    /**
     * This method is to check whether the game zip has been download
     * and the game zip is out of date or not
     * <p/>
     * If the unix timestamp is not match with server's, then must
     * clean the game records、zip package、game dir etc
     * <p/>
     * 这个是用来扫描本地文件夹，查看该游戏的游戏包是否已经下载过了，如果没有下载过，启动下载；
     * 下载过之后，请求定位权限并进入游戏中
     */
    //    private void checkDiskAndNextStep() {
    //        // first scan the dir and check the zip is exist or not
    //        if (mGameZipScanner == null) {
    //            mGameZipScanner = new GameZipScanner();
    //        }
    //        long timeStamp = mGameZipScanner.getGameZipFileTimeStamp(gameId);
    //        if (timeStamp == GameZipScanner.ZIP_FILE_NOT_FOUND_TIMESTAMP) {
    //            // it means that the game zip is not exist, need download
    //            startGetGameZipUrlAndDownload();
    //        } else {
    //            // the zip is exist, then check is it out of date
    //            //TODO : invoke api to check whether the zip is out of date
    //            initGPS();
    //        }
    //    }

    //    /**
    //     * 检测到本地没有游戏包之后，开始向服务器请求游戏包的下载url
    //     * 并下载相应的zip包保存在本地
    //     * 这里可能出现游戏包第一次下载失败的情况，失败之后再次请求，失败超过三次则停止请求
    //     */
    //    private void startGetGameZipUrlAndDownload() {
    //        mGameDetailsEntity.setGameId(gameId);
    //        progressDialog.show();
    //        HttpService.getGameService()
    //                .getIndividualGameZipUrl(Accounts.getId(),
    //                        Accounts.getToken(),
    //                        mGameDetailsEntity.getGameId())
    //                .flatMap(new Func1<Response<String>, Observable<retrofit2.Response<ResponseBody>>>() {
    //                    @Override
    //                    public Observable<retrofit2.Response<ResponseBody>> call(Response<String> response) {
    //                        if (response.getStatus() != Response.RESPONSE_OK) {
    //                            throw new RuntimeException(response.getInfo());
    //                        }
    //                        String data = response.getData();
    //                        if (data == null || data.length() == 0) {
    //                            Timber.e("当前游戏并未提供下载连接");
    //                            throw new RuntimeException(response.getInfo());
    //                        }
    //                        tempGameZipUrl = response.getData();
    //                        return HttpService.getGameService().downloadGameZip(response.getData());
    //                    }
    //                })
    //                .map(new Func1<retrofit2.Response<ResponseBody>, File>() {
    //                    @Override
    //                    public File call(retrofit2.Response<ResponseBody> response) {
    //                        String url = tempGameZipUrl;
    //                        System.out.println("url = " + url);
    //                        String[] segs = url.split("/");
    //                        if (segs.length == 0) {
    //                            throw new IllegalStateException("Wrong url can not split file name");
    //                        }
    //                        File file = new File(DirUtils.getTempDirectory(), segs[segs.length - 1]);
    //                        try {
    //                            if (!file.exists() && !file.createNewFile()) {
    //                                throw new IOException("Create file failed.");
    //                            }
    //                            BufferedSink sink = Okio.buffer(Okio.sink(file));
    //                            sink.writeAll(response.body().source());
    //                            sink.close();
    //                            return file;
    //                        } catch (IOException e) {
    //                            if (file.exists()) {
    //                                //noinspection ResultOfMethodCallIgnored
    //                                file.delete();
    //                            }
    //                            throw Exceptions.propagate(e);
    //                        }
    //                    }
    //                })
    //                .subscribeOn(Schedulers.io())
    //                .observeOn(AndroidSchedulers.mainThread())
    //                .subscribe(new Action1<File>() {
    //                    @Override
    //                    public void call(File file) {
    //                        //                        mGameZipScanner.reload();
    //                        initGPS();
    //                    }
    //                }, new Action1<Throwable>() {
    //                    @Override
    //                    public void call(Throwable throwable) {
    //                        Timber.e(throwable, "game zip download error.....redownload");
    //                        if (gameZipDownloadFailedNum > 0) {
    //                            startGetGameZipUrlAndDownload();
    //                            gameZipDownloadFailedNum--;
    //                        } else {
    //                            ToastUtil.TextToast("游戏包下载出错");
    //                        }
    //                    }
    //                }, new Action0() {
    //                    @Override
    //                    public void call() {
    //                        progressDialog.dismiss();
    //                    }
    //                });
    //    }
    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, null);
            CustomTextView textView = ((CustomTextView) view.findViewById(R.id.loading_dialog_textview));
            textView.setText(getString(R.string.loading));
            mLoadingDialog = new AlertDialog.Builder(getActivity())
                    .setCancelable(false)
                    .setView(view)
                    .create();
        }
        mLoadingDialog.show();
    }

    //    @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_LOCATION)
    //    public void startPlayingGame() {
    //        Intent intent = new Intent(getContext(), GamePlayingActivity.class);
    //        intent.putExtra(Conf.GAME_ID, gameId);
    //        intent.putExtra(Conf.GAME_DETAIL, mGameDetailsEntity);
    //        if (mGameDetailsEntity.getType() == GAME_TYPE.TEAMS) {
    //        }
    //        startActivity(intent);
    //    }
    @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_LOCATION)
    public void startPlayingGame() {
        Intent intent = new Intent(getContext(), GamePlaying2Activity.class);
        intent.putExtra(Conf.GAME_ID, gameId);
        intent.putExtra(Conf.GAME_DETAIL, mGameDetailsEntity);
        if (mGameDetailsEntity.getType() == GAME_TYPE.TEAMS) {
            //TODO : need pass team id to GamePlayingActivity when the team game open
        }
        startActivity(intent);
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
        if (null == shareDialog) {
            shareDialog = new ShareDialog(getContext());
            shareDialog.setShareContent(
                    String.format("我正在玩定向AR游戏 %s，一起来探秘吧", mGameDetailsEntity.getTitle()),
                    Html.fromHtml(Html.fromHtml(mGameDetailsEntity.getGameIntro()).toString()).toString(),
                    mGameDetailsEntity.getImg(),
                    mGameDetailsEntity.getShareUrl());
        }
        shareDialog.show();
    }

    @OnClick(R.id.game_detail_location)
    public void startLocationDetailMap() {
        Intent intent = new Intent(getActivity(), GameDetailsLocationActivity.class);
        intent.putExtra(Conf.LATITUDE, Double.valueOf(mGameDetailsEntity.getLatitude()));
        intent.putExtra(Conf.LONGITUDE, Double.valueOf(mGameDetailsEntity.getLongitude()));
        startActivity(intent);
    }

    @OnClick(R.id.game_detail_ensure)
    public void ensureClicked() {
        if (mGameDetailsEntity == null) {
            ToastUtil.TextToast("获取游戏信息失败");
            return;
        }
        if (mGameDetailsEntity.getType() == GAME_TYPE.TEAMS) {
            ToastUtil.TextToast("团队赛正在开发中");
            return;
        }
        //        checkDiskAndNextStep();
        //请求加入游戏接口，获取游戏包下载链接
        progressDialog = new ProgressDialog(getContext(), ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        enterGame();
    }

    /**
     * 请求加入游戏，获取游戏下载链接
     */
    private void enterGame() {
        HttpService.getGameService().enterGame(Accounts.getId(), gameId, mGameDetailsEntity.getType().value)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Response<EnterGameResponse>>() {
            @Override
            public void call(Response<EnterGameResponse> response) {
                if (response.getStatus() == Response.RESPONSE_OK) {
                    tempGameZipUrl = response.getData().game_zip;
                    //根据获取到的游戏包下载链接去下载游戏
                    downloadGameZip(tempGameZipUrl);
                } else {
                    throw new RuntimeException("请求加入游戏获取游戏包下载地址失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.i("jason", "请求加入游戏获取游戏包下载地址失败:" + throwable.getMessage());
            }
        });
    }

    /**
     * 根据游戏下载包链接下载该游戏包
     *
     * @param tempGameZipUrl
     */
    private void downloadGameZip(final String tempGameZipUrl) {
        HttpService.getGameService().downloadGameZip(tempGameZipUrl).subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Action1<retrofit2.Response<ResponseBody>>() {
            @Override
            public void call(retrofit2.Response<ResponseBody> response) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                String url = tempGameZipUrl;
                System.out.println("url = " + url);
                String[] segs = url.split("/");
                if (segs.length == 0) {
                    throw new IllegalStateException("Wrong url can not split file name");
                }
                File file = new File(DirUtils.getTempDirectory(), segs[segs.length - 1]);
                try {
                    if (!file.exists() && !file.createNewFile()) {
                        throw new IOException("Create file failed.");
                    }
                    BufferedSink sink = Okio.buffer(Okio.sink(file));
                    sink.writeAll(response.body().source());
                    sink.close();
                    //游戏包下载完毕,检测有用是否开启GPS定位
                    initGPS();
                } catch (IOException e) {
                    if (file.exists()) {
                        file.delete();
                    }
                    throw Exceptions.propagate(e);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if (gameZipDownloadFailedNum > 0) {
                    downloadGameZip(tempGameZipUrl);
                    gameZipDownloadFailedNum--;
                } else {
                    ToastUtil.TextToast("游戏包下载出错");
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                }
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

    /**
     * 判断用户是否打开GPS，如果没有，则进入到设置页面打开GPS
     */
    private void initGPS() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("为了定位更加精确，请打开GPS");
            dialog.setPositiveButton("确定",
                    new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面

                        }
                    });
            dialog.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        } else {
            PermissionDispatcher.startLocateWithCheck(((BaseActivity) getActivity()));
        }
    }
}

