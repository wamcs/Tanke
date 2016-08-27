package com.lptiyu.tanke.userCenter.viewholder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.gameplaying2.GamePlaying2Activity;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.GameFinishedEntity;
import com.lptiyu.tanke.pojo.GetGameStatusResponse;
import com.lptiyu.tanke.utils.GameZipUtils;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.TimeUtils;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.XUtilsDownloader;
import com.lptiyu.tanke.widget.CustomTextView;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/14
 *
 * @author ldx
 */
public class UserGameFinishedHolder extends BaseViewHolder<GameFinishedEntity> {

    @BindView(R.id.image_view)
    RoundedImageView mItemPicture;

    @BindView(R.id.item_finished_title)
    CustomTextView title;

    @BindView(R.id.item_finished_type)
    CustomTextView type;

    @BindView(R.id.item_finished_complete_time)
    CustomTextView completeTime;

    @BindView(R.id.item_finished_consuming_time)
    CustomTextView consumingTime;

    @BindView(R.id.item_finished_exp)
    CustomTextView exp;

    @BindView(R.id.game_finished_list_item)
    RelativeLayout mItem;
    private String tempGameZipUrl;
    private GameFinishedEntity currentEntity;
    private ProgressDialog progressDialog;

    public UserGameFinishedHolder(ViewGroup parent) {
        super(fromResLayout(parent, R.layout.item_game_finished));
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bind(final GameFinishedEntity entity) {
        if (entity == null || mItemPicture == null) {
            return;
        }
        currentEntity = entity;
        final long gameId = entity.getGameId();
        Glide.with(getContext()).load(entity.getImg()).error(R.drawable.default_pic).into(mItemPicture);
        title.setText(entity.getName());
        type.setText("");
        String ftime = entity.getEndTime();
        completeTime.setText(ftime.substring(0, ftime.lastIndexOf(":")) + "完成");
        //        Date startTimeDate = TimeUtils.parseDate(entity.getStartTime(), TimeUtils.totalFormat);
        //        Date endTimeDate = TimeUtils.parseDate(entity.getEndTime(), TimeUtils.totalFormat);
        //        if (startTimeDate == null) {
        //            startTimeDate = playing Date();
        //        }
        //        if (endTimeDate == null) {
        //            endTimeDate = playing Date();
        //        }
        //        long consumeTime = endTimeDate.getTime() - startTimeDate.getTime();
        consumingTime.setText("用时" + TimeUtils.parseSecondToHourAndMinutes(Long.parseLong(entity.getTotalTime())));
        exp.setText(String.format(getContext().getString(R.string.user_game_finished_get_exp_formatter), entity
                .getExpPoints()));

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
                                case PlayStatus.GAME_OVER://游戏结束，暂不考虑
                                    new XUtilsDownloader(getContext(), tempGameZipUrl, currentEntity.getGameId(), new
                                            XUtilsDownloader.FinishDownloadCallback() {
                                                @Override
                                                public void onFinishedDownload() {
                                                    startPlayingGame();
                                                }
                                            });
                                    break;
                                case PlayStatus.NEVER_ENTER_GANME://从未玩过游戏，进入到游戏详情界面
                                case PlayStatus.HAVE_ENTERED_bUT_NOT_START_GAME://进入过但没开始游戏，进入到玩游戏界面
                                case PlayStatus.HAVE_STARTED_GAME://进入并且已经开始游戏，进入到玩游戏界面
                                    //进入到玩游戏界面之前，先检测游戏包是否存在，存在则直接进入，否则要先下载游戏包
                                    //检查游戏包是否存在或者游戏解压后为空，判断完后游戏包已经被解压缩，并且已经将文件解析成实体类对象，此时可以直接从内存中取数据了
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
        intent.putExtra(Conf.GAME_FINISHED_ENTITY, currentEntity);
        getContext().startActivity(intent);
    }
}
