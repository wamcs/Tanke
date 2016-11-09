package com.lptiyu.tanke.activities.gameplaying;

import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

/**
 * Created by Jason on 2016/7/11.
 */
public class GamePlayingContract {
    interface IGamePlaying2View extends IBaseView {

        void successDownLoadRecord(GameRecord gameRecord);

        void successReloadRecord(GameRecord gameRecord);

    }

    interface IGamePlaying2Presenter extends IBasePresenter {

        void downLoadGameRecord(long gameId, long teamId, long recordId);

        void reloadGameRecord(long gameId, long teamId, long recordId);
    }
}
