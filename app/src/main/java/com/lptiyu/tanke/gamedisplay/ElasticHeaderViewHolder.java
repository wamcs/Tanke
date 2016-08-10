package com.lptiyu.tanke.gamedisplay;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.gameplaying2.GamePlaying2Activity;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.gamedetails.GameDetailsActivity;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.GameDisplayEntity;
import com.lptiyu.tanke.pojo.GetGameStatusResponse;
import com.lptiyu.tanke.utils.GameZipUtils;
import com.lptiyu.tanke.utils.XUtilsHelper;
import com.lptiyu.tanke.widget.CircularImageView;
import com.lptiyu.tanke.widget.CustomTextView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/1
 * <p/>
 * 控制RecyclerView的首部
 *
 * @author ldx
 */
public class ElasticHeaderViewHolder extends BaseViewHolder<GameDisplayEntity> {

    @BindView(R.id.left_image_view)
    CircularImageView leftImageView;

    @BindView(R.id.middle_image_view)
    CircularImageView middleImageView;

    @BindView(R.id.right_image_view)
    CircularImageView rightImageView;

    @BindView(R.id.left_title)
    CustomTextView leftTitle;

    @BindView(R.id.middle_title)
    CustomTextView middleTitle;

    @BindView(R.id.right_title)
    CustomTextView rightTitle;

    GameDisplayFragment fragment;

    @BindView(R.id.jelly_view)
    ElasticHeaderLayout elasticHeaderLayout;

    ObjectAnimator animator = new ObjectAnimator();

    List<GameDisplayEntity> gameDisplayEntities;
    private ProgressDialog progressDialog;
    private GameDisplayEntity gameDisplayEntity;
    private String tempGameZipUrl;

    //    private GameZipScanner mGameZipScanner;
    private double gameZipDownloadFailedNum = 3;
    //    private String gameZipUrl;

    public ElasticHeaderViewHolder(ViewGroup parent, GameDisplayFragment fragment) {
        super(fromResLayout(parent, R.layout.item_game_display_header));
        ButterKnife.bind(this, itemView);
        this.fragment = fragment;
        init();
    }

    @OnClick(R.id.left_image_view)
    public void left_image_view() {
        jump(1);
    }

    @OnClick(R.id.middle_image_view)
    public void middle_image_view() {
        jump(0);
    }

    @OnClick(R.id.right_image_view)
    public void right_image_view() {
        jump(2);
    }

    public void jump(int index) {
        if (gameDisplayEntities == null || gameDisplayEntities.size() < index + 1 || gameDisplayEntities.get(index)
                == null) {
            return;
        }
        //        Intent intent = playing Intent(getContext(), GameDetailsActivity.class);
        //        intent.putExtra(Conf.GAME_ID, gameDisplayEntities.get(index).getId());
        //        getContext().startActivity(intent);
        startActivity(index);
    }

    private void startActivity(int index) {
        //请求游戏状态接口获取游戏状态，如果是进入过游戏，则直接跳转到玩游戏界面，如果这是第一次玩，则进入到游戏详情界面
        gameDisplayEntity = gameDisplayEntities.get(index);
        HttpService.getGameService().getGameStatus(Accounts.getId(), gameDisplayEntity.getId(),
                gameDisplayEntity.getType().value)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<GetGameStatusResponse>>() {
                    @Override
                    public void call(Response<GetGameStatusResponse> response) {
                        if (response.getStatus() == Response.RESPONSE_OK) {
                            Intent intent = new Intent();
                            //判断游戏状态
                            switch (response.getData().play_statu) {
                                case PlayStatus.NEVER_ENTER_GANME://从未玩过游戏，进入到游戏详情界面
                                    intent.setClass(getContext(), GameDetailsActivity.class);
                                    intent.putExtra(Conf.GAME_ID, gameDisplayEntity.getId());
                                    intent.putExtra(Conf.FROM_WHERE, Conf.ElasticHeaderViewHolder);
                                    getContext().startActivity(intent);
                                    break;
                                case PlayStatus.GAME_OVER://游戏结束，暂不考虑
                                    //TODO 需要进入到游戏完成界面
                                case PlayStatus.HAVE_ENTERED_bUT_NOT_START_GAME://进入过但没开始游戏，进入到玩游戏界面
                                case PlayStatus.HAVE_STARTED_GAME://进入并且已经开始游戏，进入到玩游戏界面
                                    //进入到玩游戏界面之前，先检测游戏包是否存在，存在则直接进入，否则要先下载游戏包
                                    GameZipUtils gameZipUtils = new GameZipUtils();
                                    if (gameZipUtils.isParsedFileExist(gameDisplayEntity.getId()) == null) {
                                        //游戏包不存在，需要下载游戏包
                                        progressDialog = ProgressDialog.show(getContext(), "", "游戏下载中...",
                                                true);
                                        progressDialog.show();
                                        tempGameZipUrl = response.getData().game_zip;
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

    private void init() {
        animator.setInterpolator(new ElasticOutInterpolator());
        animator.setTarget(elasticHeaderLayout);
        animator.setPropertyName("percent");
        animator.setDuration(400);
    }

    public void setPercent(float percent) {
        elasticHeaderLayout.setPercent(percent);
    }

    public float getPercent() {
        return elasticHeaderLayout.getPercent();
    }

    public void smoothBack() {
        if (elasticHeaderLayout.getPercent() == 0 && !animator.isRunning()) {
            return;
        }
        animator.cancel();
        animator.setFloatValues(elasticHeaderLayout.getPercent(), 0);
        animator.start();
    }

    public void bind(List<GameDisplayEntity> entities) {
        if (entities == null || entities.size() == 0) {
            hideHeader();
            return;
        }
        showHeader();
        bindEntity(middleImageView, middleTitle, entities.size() < 1 ? null : entities.get(0));
        bindEntity(leftImageView, leftTitle, entities.size() < 2 ? null : entities.get(1));
        bindEntity(rightImageView, rightTitle, entities.size() < 3 ? null : entities.get(2));
        this.gameDisplayEntities = entities;
    }

    public void bindEntity(ImageView imageView, TextView textView, GameDisplayEntity entity) {
        if (entity == null) {
            return;
        }
        Glide.with(fragment).load(entity.getImg())
                .error(R.mipmap.need_to_remove)
                .into(imageView);
        textView.setText(entity.getTitle());
    }

    @Override
    public void bind(GameDisplayEntity entity) {

    }

    private void hideHeader() {
        if (middleImageView != null) {
            middleImageView.setVisibility(View.GONE);
        }
        if (middleTitle != null) {
            middleTitle.setVisibility(View.GONE);
        }
        if (leftImageView != null) {
            leftImageView.setVisibility(View.GONE);
        }
        if (leftTitle != null) {
            leftTitle.setVisibility(View.GONE);
        }
        if (rightImageView != null) {
            rightImageView.setVisibility(View.GONE);
        }
        if (rightTitle != null) {
            rightTitle.setVisibility(View.GONE);
        }
    }

    private void showHeader() {
        if (middleImageView != null) {
            middleImageView.setVisibility(View.VISIBLE);
        }
        if (middleTitle != null) {
            middleTitle.setVisibility(View.VISIBLE);
        }
        if (leftImageView != null) {
            leftImageView.setVisibility(View.VISIBLE);
        }
        if (leftTitle != null) {
            leftTitle.setVisibility(View.VISIBLE);
        }
        if (rightImageView != null) {
            rightImageView.setVisibility(View.VISIBLE);
        }
        if (rightTitle != null) {
            rightTitle.setVisibility(View.VISIBLE);
        }
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
                String parsedFilePath = gameZipUtils.isParsedFileExist(gameDisplayEntity.getId());
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
                Log.i("jason", "进度：%" + current * 100 / total);

            }

            @Override
            public void finished() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        });

    }

    private void initGPS() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setMessage("为了定位更加精确，请打开GPS");
            dialog.setPositiveButton("确定",
                    new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            fragment.startActivityForResult(intent, 0); // 设置完成后返回到原来的界面

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
            startPlayingGame();
        }
    }

    public void startPlayingGame() {
        Intent intent = new Intent(getContext(), GamePlaying2Activity.class);
        intent.putExtra(Conf.GAME_ID, gameDisplayEntity.getId());
        intent.putExtra(Conf.GAME_DISPLAY_ENTITY, gameDisplayEntity);
        fragment.startActivity(intent);
    }
}
