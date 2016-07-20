package com.lptiyu.tanke.activities.gameplaying2;

import android.app.Activity;

import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.pojo.GameDetailsEntity;
import com.lptiyu.tanke.pojo.UpLoadGameRecord;

import java.util.List;

/**
 * Created by Jason on 2016/7/11.
 */
public class GamePlayingContract {
    interface IGamePlayingView {

        void successDownLoadRecord(GameRecord gameRecord);

        void failLoadRecord();

        void successUpLoadRecord();

        void gameOver();

        void checkZipExistOver();

        void getData(List<Point> list_points, String unZippedDir, GameRecord gameRecord, long gameId, long
                gameType, String gameName, GameDetailsEntity mGameDetailsEntity);

        void successEnterGame();
    }

    interface IGamePlayingPresenter {

        void downLoadRecord();

        void upLoadRecord(UpLoadGameRecord record);

        void gameOver(UpLoadGameRecord record);

        void initData(Activity activity);

        void insertTask(long taskId, long exp);

        void insertPoint(long pointId, long state);

        void insertGameRecord(long gameId, long status);

        boolean isCurrentPointFinished(Point point);

        boolean isCurrentGameFinished(long pointCounts);

        void enterGame();

    }
}
