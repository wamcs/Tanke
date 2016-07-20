package com.lptiyu.tanke.activities.gameplaying2;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.lptiyu.tanke.database.DBHelper;
import com.lptiyu.tanke.database.DBPointRecord;
import com.lptiyu.tanke.database.DBTaskRecord;
import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.enums.GameRecordAndPointStatus;
import com.lptiyu.tanke.enums.GameType;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.gameplaying.assist.zip.GameZipHelper;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.EnterGameResponse;
import com.lptiyu.tanke.pojo.GameDetailsEntity;
import com.lptiyu.tanke.pojo.GameDisplayEntity;
import com.lptiyu.tanke.pojo.UpLoadGameRecord;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Jason on 2016/7/11.
 */
public class GamePlayingPresenter implements GamePlayingContract.IGamePlayingPresenter {

    private GamePlayingContract.IGamePlayingView view;
    private int gameType;
    private String gameName;
    //    private GameDisplayEntity gameDisplayEntity;

    public GamePlayingPresenter(GamePlayingContract.IGamePlayingView view) {
        this.view = view;
    }

    private long gameId;
    //    private GameDetailsEntity mGameDetailsEntity;
    private GameRecord gameRecord;

    //    public final long FINISHED_STATUS = 1;
    //    public final long PERSONAL_GAME = 2;
    //    public final long PLAY_STATUS = 2;

    /**
     * 从服务器请求游戏记录
     */
    @Override
    public void downLoadRecord() {
        HttpService.getGameService()
                .getGameRecord(Accounts.getId(), gameId, gameType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<GameRecord>>() {
                    @Override
                    public void call(Response<GameRecord> response) {
                        Log.i("jason", "游戏记录：" + response);
                        if (response.getStatus() != Response.RESPONSE_OK) {
                            Log.i("jason", "请求游戏记录失败：" + response.getInfo());
                        }
                        view.successDownLoadRecord(response.getData());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i("jason", "获取游戏记录失败throwable：" + throwable.getMessage());
                        view.failLoadRecord();
                    }
                });
    }


    /**
     * 提交游戏记录
     *
     * @param record
     */
    @Override
    public void upLoadRecord(final UpLoadGameRecord record) {
        HttpService.getGameService()
                .upLoadGameRecord(Accounts.getId(), gameId, record.point_id, record.task_id, (long)
                                (gameType),
                        GameRecordAndPointStatus.FINISHED)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<Void>>() {
                    @Override
                    public void call(Response<Void> voidResponse) {
                        if (voidResponse.getStatus() == Response.RESPONSE_OK) {
                            Timber.d(AppData.globalGson().toJson(record));
                            view.successUpLoadRecord();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.e("upload records error");
                    }
                });
    }

    /**
     * 游戏结束时执行此方法上传游戏记录（多传一个游戏结束的标志位参数）
     *
     * @param record
     */
    @Override
    public void gameOver(final UpLoadGameRecord record) {
        HttpService.getGameService()
                .gameOver(Accounts.getId(), gameId, record.point_id, record.task_id, GameType.INDIVIDUAL_TYPE,
                        GameRecordAndPointStatus.FINISHED, PlayStatus.GAME_OVER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<Void>>() {
                    @Override
                    public void call(Response<Void> voidResponse) {
                        if (voidResponse.getStatus() == Response.RESPONSE_OK) {
                            Timber.d(AppData.globalGson().toJson(record));
                            view.gameOver();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.e("upload records error");
                    }
                });
    }

    @Override
    public void initData(Activity activity) {
        GameZipHelper gameZipHelper = new GameZipHelper();

        /*
        玩游戏界面可以从两个入口进来，游戏列表和游戏详情，要分开考虑
         */
        Intent intent = activity.getIntent();
        //获取gameId
        gameId = intent.getLongExtra(Conf.GAME_ID, Conf.TEMP_GAME_ID);
        //获取游戏列表实体类（从游戏列表进来）
        GameDisplayEntity gameDisplayEntity = intent.getParcelableExtra(Conf.GAME_DISPLAY_ENTITY);
        //获取游戏详情实体类（从游戏详情进来）
        GameDetailsEntity mGameDetailsEntity = intent.getParcelableExtra(Conf.GAME_DETAIL);
        if (gameDisplayEntity != null) {
            gameType = gameDisplayEntity.getType().value;
            gameName = gameDisplayEntity.getTitle();
        }
        if (mGameDetailsEntity != null) {
            gameType = mGameDetailsEntity.getType().value;
            gameName = mGameDetailsEntity.getTitle();
        }

        //检查游戏包是否存在或者游戏解压后为空，判断完后游戏包已经被解压缩，并且已经将文件解析成实体类对象，此时可以直接从内存中取数据了
        if (!gameZipHelper.checkAndParseGameZip(gameId) || gameZipHelper.getmPoints().size() == 0) {
            view.checkZipExistOver();
            return;
        }
        //获取游戏攻击点集合
        List<Point> list_points = gameZipHelper.getmPoints();

        /**
         /storage/emulated/0/Android/data/com.lptiyu.tanke/files/temp/39_33_1467790230
         */
        String unZippedDir = gameZipHelper.unZippedDir;
        Log.i("jason", "游戏包绝对路径：" + unZippedDir);

        view.getData(list_points, unZippedDir, gameRecord, gameId, gameType, gameName,mGameDetailsEntity);
    }

    @Override
    public void insertTask(long taskId, long exp) {
        long timeStamp = System.currentTimeMillis();
        DBHelper.getInstance().getDBTaskDao().insertOrReplace(new DBTaskRecord(taskId, timeStamp + "", taskId + "",
                exp + ""));
    }

    /**
     * 当前点的状态
     *
     * @param pointId
     * @param state
     */
    @Override
    public void insertPoint(long pointId, long state) {
        DBHelper.getInstance().getDBPointDao().insertOrReplace(new DBPointRecord(pointId, state + "", pointId + ""));
    }

    @Override
    public void insertGameRecord(long gameId, long status) {
//        DBHelper.getInstance().getDBGameRecordDao().insertOrReplace((new DBGameRecord(gameId, status + "", gameId +
//                "")));
    }

    @Override
    public boolean isCurrentPointFinished(Point point) {
        if (point == null) {
            throw new RuntimeException("null point");
        }
        List<DBTaskRecord> dbTasks = DBHelper.getInstance().getDBTaskDao().loadAll();
        Log.i("jason", "task表存储结果：" + dbTasks);
        if (dbTasks != null && dbTasks.size() != 0 && dbTasks.size() == point.getTaskMap().size()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isCurrentGameFinished(long pointCounts) {
        List<DBPointRecord> dbPoints = DBHelper.getInstance().getDBPointDao().loadAll();
        if (dbPoints != null && dbPoints.size() != 0 && dbPoints.size() == pointCounts) {
            return true;
        }
        return false;
    }

    @Override
    public void enterGame() {
        HttpService.getGameService()
                .enterGame(Accounts.getId(), gameId, (long) (gameType))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<EnterGameResponse>>() {
                    @Override
                    public void call(Response<EnterGameResponse> response) {
                        if (response.getStatus() == Response.RESPONSE_OK) {
                            game_zip = response.getData().game_zip;
                            view.successEnterGame();
                        } else {
                            Log.i("jason", "进入游戏失败信息：" + response.getInfo());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (throwable != null)
                            Log.i("jason", "进入游戏失败信息throwable：" + throwable.getMessage());
                        else
                            Log.i("jason", "服务器异常");
                    }
                });
    }

    private String game_zip;
}
