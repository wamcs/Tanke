package com.lptiyu.tanke.gameplaying2;

/**
 * Created by Jason on 2016/7/11.
 */
public class GamePlayingPresenter implements GamePlayingContract.IGamePlayingPresenter {

    private GamePlaying2Activity activity;

    public GamePlayingPresenter(GamePlaying2Activity activity) {
        this.activity = activity;
    }

    @Override
    public void loadNetWork() {

    }

    @Override
    public void initData(GamePlaying2Activity activity) {
    }
}
