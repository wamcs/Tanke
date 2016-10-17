package com.lptiyu.tanke.activities.gameplaying;

import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

/**
 * Created by Jason on 2016/7/11.
 */
public class GamePlayingContract {
    interface IGamePlayingView extends IBaseView {

        void successUpLoadRecord();

        void successDownLoadRecord(GameRecord gameRecord);

        void failDownLoadRecord();

    }

    interface IGamePlayingPresenter extends IBasePresenter {

        void downLoadGameRecord(long gameId);
    }
}
