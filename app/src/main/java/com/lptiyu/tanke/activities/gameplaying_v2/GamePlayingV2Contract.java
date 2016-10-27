package com.lptiyu.tanke.activities.gameplaying_v2;

import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

/**
 * Created by Jason on 2016/7/11.
 */
public class GamePlayingV2Contract {
    interface IGamePlaying2View extends IBaseView {

        void successDownLoadRecord(GameRecord gameRecord);

    }

    interface IGamePlaying2Presenter extends IBasePresenter {

        void downLoadGameRecord(long gameId, long teamId);
    }
}
