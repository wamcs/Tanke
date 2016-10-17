package com.lptiyu.tanke.activities.gameplaying;

import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.net.HttpService;
import com.lptiyu.tanke.net.Response;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Jason on 2016/7/11.
 */
public class GamePlayingPresenter implements GamePlayingContract.IGamePlayingPresenter {
    private GamePlayingContract.IGamePlayingView view;

    public GamePlayingPresenter(GamePlayingContract.IGamePlayingView view) {
        this.view = view;
    }

    /**
     * 从服务器请求游戏记录
     */
    @Override
    public void downLoadGameRecord(long gameId) {
        HttpService.getGameService()
                .getGameRecord(Accounts.getId(), gameId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<GameRecord>>() {
                    @Override
                    public void call(Response<GameRecord> response) {
                        //                        Log.i("jason", "请求游戏记录结果：" + response);
                        if (response.getStatus() != Response.RESPONSE_OK) {
                            //                            Log.i("jason", "请求游戏记录失败：" + response.getInfo());
                            view.failDownLoadRecord();

                        } else {
                            //                            List<PointRecord> record_text = response.getData()
                            // .record_text;
                            //                            if (record_text != null) {
                            //                                //                                Log.i("jason",
                            // "请求游戏记录数目：" + record_text.size());
                            //                            } else {
                            //                                //                                Log.i("jason",
                            // "请求游戏记录数目为0");
                            //                            }
                            view.successDownLoadRecord(response.getData());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //                        Log.i("jason", "请求游戏记录失败throwable：" + throwable.getMessage());
                    }
                });
    }
}
