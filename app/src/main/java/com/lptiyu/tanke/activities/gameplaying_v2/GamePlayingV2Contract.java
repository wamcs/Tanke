package com.lptiyu.tanke.activities.gameplaying_v2;

import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.entity.UpLoadGameRecord;
import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

/**
 * Created by Jason on 2016/7/11.
 */
public class GamePlayingV2Contract {
    interface IGamePlaying2View extends IBaseView {

        void successUpLoadRecord();

        void successDownLoadRecord(GameRecord gameRecord);

    }

    interface IGamePlaying2Presenter extends IBasePresenter {

        //        DBGameRecord queryGameRecord(long gameId);

        void downLoadGameRecord(long gameId);

        void upLoadRecord(UpLoadGameRecord record);

        //        void insertTask(TaskRecord taskRecord);
        //
        //        void insertPoint(PointRecord pointRecord);
        //
        //        void insertGameRecord(GameRecord pameRecord);

        //        boolean isCurrentPointFinished(long gameId, long pointId, long tastCounts);
        //
        //        boolean isCurrentGameFinished(long gameId, long pointCounts);
    }
}
