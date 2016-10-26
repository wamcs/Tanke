package com.lptiyu.tanke.userCenter.viewholder;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.gameplaying2.GamePlaying2Activity;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.GamePlayingEntity;
import com.lptiyu.tanke.pojo.GetGameStatusResponse;
import com.lptiyu.tanke.utils.GameZipUtils;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.GameZipDownloader;
import com.lptiyu.tanke.widget.GradientProgressBar;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * author:wamcs
 * date:2016/6/13
 * email:kaili@hustunique.com
 */
public class GamePlayingViewHolder extends BaseViewHolder<GamePlayingEntity> {

    @BindView(R.id.game_playing_list_item_picture)
    RoundedImageView mItemPicture;

    @BindView(R.id.game_playing_list_item_name)
    TextView mItemName;

    @BindView(R.id.game_playing_list_item_progress)
    GradientProgressBar mItemProgress;

    @BindView(R.id.game_playing_list_item_progress_number)
    TextView mItemProgressNumber;

    @BindView(R.id.game_playing_list_item)
    LinearLayout mItem;
    private String tempGameZipUrl;
    private GamePlayingEntity currentEntity;


    public GamePlayingViewHolder(ViewGroup parent) {
        super(fromResLayout(parent, R.layout.item_game_playing));
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bind(final GamePlayingEntity entity) {
        currentEntity = entity;
        final long gameId = entity.getGameId();
        mItemName.setText(entity.getName());
        mItemProgressNumber.setText(Math.floor(entity.getProgress() * 100) + "%");
        Glide.with(getContext()).load(entity.getImg()).error(R.drawable.default_pic).into(mItemPicture);
        mItemProgress.setProgress(entity.getProgress());
        mItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (entity.getIsdel() != 0)//游戏包已经删除，提示
                {
                    ToastUtil.TextToast("该游戏已经下线");
                    return;
                }
                else if (entity.getStates() == 2 || entity.getStates()==0)//维护中的游戏s
                {
                    ToastUtil.TextToast("该游戏正在维护中，尽请期待");
                    return;
                }

                GameZipUtils gameZipUtils = new GameZipUtils();
                String parsedFilePath = gameZipUtils.isParsedFileExist(gameId);
                if (parsedFilePath != null) {
                    startPlayingGame();
                } else {
                    //游戏包不存在，开始请求游戏状态获取游戏下载链接，下载游戏
                    loadNetWorkData();
                }
            }
        });
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

    private void loadNet() {
        HttpService.getGameService().getGameStatus(Accounts.getId(), currentEntity.getGameId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<GetGameStatusResponse>>() {
                    @Override
                    public void call(Response<GetGameStatusResponse> response) {
                        if (response.getStatus() == Response.RESPONSE_OK) {
                            tempGameZipUrl = response.getData().game_zip;
                            //判断游戏状态
                            switch (response.getData().play_statu) {
                                case PlayStatus.HAVE_ENTERED_bUT_NOT_START_GAME://进入过但没开始游戏，进入到玩游戏界面
                                case PlayStatus.HAVE_STARTED_GAME://进入并且已经开始游戏，进入到玩游戏界面
                                    //进入到玩游戏界面之前，先检测游戏包是否存在，存在则直接进入，否则要先下载游戏包
                                    //检查游戏包是否存在或者游戏解压后为空，判断完后游戏包已经被解压缩，并且已经将文件解析成实体类对象，此时可以直接从内存中取数据了
                                    new GameZipDownloader(getContext(), tempGameZipUrl, currentEntity.getGameId(), new
                                            GameZipDownloader.FinishDownloadCallback() {
                                                @Override
                                                public void onFinishedDownload() {
                                                    startPlayingGame();
                                                }
                                            });
                                    break;
                                case PlayStatus.NEVER_ENTER_GANME://从未玩过游戏，进入到游戏详情界面
                                case PlayStatus.GAME_OVER://游戏结束，暂不考虑
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

    private void startPlayingGame() {
        //        Intent intent = playing Intent(getContext(), GameDetailsActivity.class);
        Intent intent = new Intent(getContext(), GamePlaying2Activity.class);
        intent.putExtra(Conf.GAME_ID, currentEntity.getGameId());
        intent.putExtra(Conf.GAME_PLAYING_ENTITY, currentEntity);
        Log.i("jason", "正在玩的游戏实体类：" + currentEntity);
        getContext().startActivity(intent);
    }
}
