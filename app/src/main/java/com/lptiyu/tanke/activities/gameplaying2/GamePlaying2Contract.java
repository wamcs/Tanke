package com.lptiyu.tanke.activities.gameplaying2;

import com.lptiyu.tanke.database.DBGameRecord;
import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.entity.PointRecord;
import com.lptiyu.tanke.entity.TaskRecord;
import com.lptiyu.tanke.pojo.UpLoadGameRecord;

/**
 * Created by Jason on 2016/7/11.
 */
public class GamePlaying2Contract {
    interface IGamePlayingView {

        void successUpLoadRecord();

        void successDownLoadRecord(GameRecord gameRecord);

        void failDownLoadRecord(String errMsg);

    }

    interface IGamePlayingPresenter {

        DBGameRecord queryGameRecord(long gameId);

        void downLoadGameRecord(long gameId);

        void upLoadRecord(UpLoadGameRecord record);

        void insertTask(TaskRecord taskRecord);

        void insertPoint(PointRecord pointRecord);

        void insertGameRecord(GameRecord pameRecord);

        boolean isCurrentPointFinished(long gameId, long pointId, long tastCounts);

        boolean isCurrentGameFinished(long gameId, long pointCounts);
    }
}
