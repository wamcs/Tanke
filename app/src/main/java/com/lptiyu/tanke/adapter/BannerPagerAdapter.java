package com.lptiyu.tanke.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.bannerdetail.BannerDetailActivity;
import com.lptiyu.tanke.activities.gamedetail.GameDetailActivity;
import com.lptiyu.tanke.activities.gameplaying.GamePlayingActivity;
import com.lptiyu.tanke.entity.GetGameStatusResponse;
import com.lptiyu.tanke.entity.response.Banner;
import com.lptiyu.tanke.enums.BannerType;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.net.HttpService;
import com.lptiyu.tanke.net.Response;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Jason on 2016/8/12.
 */
public class BannerPagerAdapter extends BasePagerAdapter<Banner> {
    private long gameId;
    private String tempGameZipUrl;
    private String title;

    public BannerPagerAdapter(Context context, List<Banner> list) {
        super(list, context);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        final Banner banner = list.get(position);
        Glide.with(context).load(banner.image).error(R.drawable.default_pic).crossFade().into(imageView);
        container.addView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (banner.type == BannerType.OPEN_URL) {
                    Intent intent = new Intent(context, BannerDetailActivity.class);
                    intent.putExtra(Conf.CONTENT, banner.content);
                    context.startActivity(intent);
                }
                if (banner.type == BannerType.ENTER_GAME) {
                    //判断游戏包存不存在
                    String[] split = banner.content.split(":");
                    gameId = Integer.parseInt(split[0]);
                    title = split[1];
                    RunApplication.gameId = gameId;
                    RunApplication.type = banner.type;
                    RunApplication.entity = banner;
                    RunApplication.entity.title = title;
                    loadNetWorkData();
                }
            }
        });
        return imageView;
    }

    private void loadNetWorkData() {
        if (NetworkUtil.checkIsNetworkConnected()) {
            getGameStatus();
        } else {
            showNetUnConnectDialog();
        }
    }

    // 网络异常对话框
    private void showNetUnConnectDialog() {
        PopupWindowUtils.getInstance().showNetExceptionPopupwindow(context, new PopupWindowUtils.OnRetryCallback() {
            @Override
            public void onRetry() {
                loadNetWorkData();
            }
        });
    }

    private void getGameStatus() {
        HttpService.getGameService().getGameStatus(Accounts.getId(), gameId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<GetGameStatusResponse>>() {
                    @Override
                    public void call(Response<GetGameStatusResponse> response) {
                        if (response.getStatus() == Response.RESPONSE_OK) {
                            tempGameZipUrl = response.getData().game_zip;
                            //判断游戏状态
                            switch (response.getData().play_statu) {
                                case PlayStatus.NEVER_ENTER_GANME://从未玩过游戏，进入到游戏详情界面
                                    Intent intent = new Intent();
                                    intent.setClass(context, GameDetailActivity.class);
                                    intent.putExtra(Conf.FROM_WHERE, Conf.BANNER);
                                    context.startActivity(intent);
                                    break;
                                case PlayStatus.GAME_OVER://游戏结束，暂不考虑
                                case PlayStatus.HAVE_ENTERED_BUT_NOT_START_GAME://进入过但没开始游戏，进入到玩游戏界面
                                case PlayStatus.HAVE_STARTED_GAME://进入并且已经开始游戏，进入到玩游戏界面
                                    context.startActivity(new Intent(context, GamePlayingActivity.class));
                                    break;
                            }
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

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
