package com.lptiyu.tanke.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.bannerdetail.BannerDetailActivity;
import com.lptiyu.tanke.activities.gameplaying2.GamePlaying2Activity;
import com.lptiyu.tanke.enums.BannerType;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.gamedetails.GameDetailsActivity;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.GetGameStatusResponse;
import com.lptiyu.tanke.utils.FileUtils;
import com.lptiyu.tanke.utils.GameZipUtils;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.xutils3.XUtilsHelper;
import com.lptiyu.tanke.utils.xutils3.response.Banner;

import java.io.File;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Jason on 2016/8/12.
 */
public class BannerPagerAdapter extends PagerAdapter {
    private List<Banner> list;
    private Context context;
    private long gameId;
    private ProgressDialog progressDialog;
    private String tempGameZipUrl;
    private String title;

    public BannerPagerAdapter(Context context, List<Banner> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        final Banner banner = list.get(position);
        Glide.with(context).load(banner.image).error(R.drawable.default_pic).into(imageView);
        container.addView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO banner点击事件
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
                    loadNetWorkData();
                }
            }
        });
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //        super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
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
        PopupWindowUtils.getInstance().showNetExceptionPopupwindow(context, new PopupWindowUtils
                .OnNetExceptionListener() {
            @Override
            public void onClick(View view) {
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
                                    intent.setClass(context, GameDetailsActivity.class);
                                    intent.putExtra(Conf.GAME_ID, gameId);
                                    intent.putExtra(Conf.FROM_WHERE, Conf.ElasticHeaderViewHolder);
                                    context.startActivity(intent);
                                    break;
                                case PlayStatus.GAME_OVER://游戏结束，暂不考虑
                                    //TODO 需要进入到游戏完成界面
                                case PlayStatus.HAVE_ENTERED_bUT_NOT_START_GAME://进入过但没开始游戏，进入到玩游戏界面
                                case PlayStatus.HAVE_STARTED_GAME://进入并且已经开始游戏，进入到玩游戏界面
                                    //进入到玩游戏界面之前，先检测游戏包是否存在，存在则直接进入，否则要先下载游戏包
                                    GameZipUtils gameZipUtils = new GameZipUtils();
                                    if (gameZipUtils.isParsedFileExist(gameId) == null) {
                                        //游戏包不存在，需要下载游戏包
                                        progressDialog = ProgressDialog.show(context, "", "加载中...", true);
                                        downloadGameZipFile();
                                    } else if (gameZipUtils.isGameUpdated(gameId, tempGameZipUrl
                                            .substring(tempGameZipUrl.lastIndexOf('/') + 1, tempGameZipUrl
                                                    .lastIndexOf('.')))) {
                                        String parsedFileExist = gameZipUtils.isParsedFileExist(gameId);
                                        //删除旧的游戏包
                                        boolean b = FileUtils.deleteDirectory(parsedFileExist);
                                        //下载新的游戏包
                                        progressDialog = ProgressDialog.show(context, "", "加载中...", true);
                                        downloadGameZipFile();
                                    } else {
                                        startPlayingGame();
                                    }
                                    break;
                                default:
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

    //下载游戏包
    private void downloadGameZipFile() {
        XUtilsHelper.getInstance().downLoadGameZip(tempGameZipUrl, new XUtilsHelper.IDownloadGameZipFileListener() {
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
                    Toast.makeText(context, "游戏包解压失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void progress(long total, long current, boolean isDownloading) {
                //游戏进度
                Log.i("jason", "进度：%" + current * 100 / total);

            }

            @Override
            public void finished() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onError(String errMsg) {
                PopupWindowUtils.getInstance().showFailLoadPopupwindow(context);
            }
        });

    }

    private void startPlayingGame() {
        Intent intent = new Intent(context, GamePlaying2Activity.class);
        intent.putExtra(Conf.GAME_ID, gameId);
        intent.putExtra(Conf.BANNER_TITLE, title);
        context.startActivity(intent);
    }
}
