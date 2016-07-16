package com.lptiyu.tanke.gameplaying2;

/**
 * Created by Jason on 2016/7/11.
 */
public class GamePlayingContract {
    interface IGamePlayingView {
    }

    interface IGamePlayingPresenter {

        void loadNetWork();

        void initData(GamePlaying2Activity activity);
    }
}
