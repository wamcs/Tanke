package com.lptiyu.tanke.activities.gameplaying2;

import com.lptiyu.tanke.database.DBGameRecord;
import com.lptiyu.tanke.database.DBGameRecordDao;
import com.lptiyu.tanke.database.DBHelper;
import com.lptiyu.tanke.database.DBPointRecord;
import com.lptiyu.tanke.database.DBTaskRecord;
import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.entity.PointRecord;
import com.lptiyu.tanke.entity.TaskRecord;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.UpLoadGameRecord;
import com.lptiyu.tanke.pojo.UploadGameRecordResponse;

import java.util.List;

import de.greenrobot.dao.query.Query;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Jason on 2016/7/11.
 */
public class GamePlaying2Presenter implements GamePlaying2Contract.IGamePlayingPresenter {
    private GamePlaying2Contract.IGamePlayingView view;

    public GamePlaying2Presenter(GamePlaying2Contract.IGamePlayingView view) {
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
                        if (response.getStatus() != Response.RESPONSE_OK) {
                            view.failDownLoadRecord(response.getInfo());

                        } else {
                            List<PointRecord> record_text = response.getData().record_text;
                            if (record_text != null) {
                            } else {
                            }
                            view.successDownLoadRecord(response.getData());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        view.failDownLoadRecord(throwable.getMessage());
                    }
                });
    }

    /**
     * 查询本地游戏记录数据库
     *
     * @param gameId
     * @return
     */
    @Override
    public DBGameRecord queryGameRecord(long gameId) {
        Query<DBGameRecord> query = DBHelper.getInstance().getDBGameRecordDao().queryBuilder().where(DBGameRecordDao
                .Properties.Game_id.eq
                        (gameId)).build();
        List<DBGameRecord> list = query.list();
        if (list != null && list.size() != 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 提交游戏记录
     *
     * @param record
     */
    @Override
    public void upLoadRecord(final UpLoadGameRecord record) {
        HttpService.getGameService()
                .upLoadGameRecord(Accounts.getId(), Long.parseLong(record.game_id), Long.parseLong(record.point_id),
                        Long.parseLong(record.task_id), PointTaskStatus.FINISHED)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<UploadGameRecordResponse>>() {
                    @Override
                    public void call(Response<UploadGameRecordResponse> response) {
                        //                        Log.i("jason", "上传游戏记录结果:" + response);
                        if (response.getStatus() == Response.RESPONSE_OK) {
                            view.successUpLoadRecord();
                        } else {
                            //                            Log.i("jason", "游戏记录上传失败：" + response.getInfo());
                            //                            view.failUpLoad();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //                        Log.i("jason", "upload records error:" + throwable.getMessage());
                    }
                });
    }

    @Override
    public void insertTask(TaskRecord taskRecord) {
        DBTaskRecord dbTaskRecord = new DBTaskRecord();
        dbTaskRecord.setExp(taskRecord.exp);
        dbTaskRecord.setFtime(taskRecord.ftime);
        dbTaskRecord.setId(taskRecord.id);
        dbTaskRecord.setTaskId(taskRecord.id + "");
        DBHelper.getInstance().getDBTaskDao().insertOrReplace(dbTaskRecord);
    }

    @Override
    public void insertPoint(PointRecord pointRecord) {
        DBPointRecord dbPointRecord = new DBPointRecord();
        dbPointRecord.setId(pointRecord.id);
        dbPointRecord.setPoint_id(pointRecord.id + "");
        dbPointRecord.setStatu(pointRecord.statu);
        DBHelper.getInstance().getDBPointDao().insertOrReplace(dbPointRecord);
    }

    @Override
    public void insertGameRecord(GameRecord gameRecord) {
        DBGameRecord dbGameRecord = new DBGameRecord();
        //        dbGameRecord.setId(Long.parseLong(gameRecord.game_id));
        dbGameRecord.setGame_id(gameRecord.game_id);
        dbGameRecord.setJoin_time(gameRecord.join_time);
        dbGameRecord.setLast_task_ftime(gameRecord.last_task_ftime);
        dbGameRecord.setLine_id(gameRecord.line_id);
        dbGameRecord.setPlay_statu(gameRecord.play_statu);
        dbGameRecord.setRanks_id(gameRecord.ranks_id);
        //        dbGameRecord.setStart_time(gameRecord.start_time);
        //        dbGameRecord.setUid(gameRecord.uid);
        DBHelper.getInstance().getDBGameRecordDao().insertOrReplace(dbGameRecord);
    }

    /**
     * @param gameId
     * @param pointId
     * @param tastCounts
     * @return
     */
    @Override
    public boolean isCurrentPointFinished(long gameId, long pointId, long tastCounts) {
        List<DBGameRecord> list = DBHelper.getInstance().getDBGameRecordDao().queryBuilder().where(DBGameRecordDao
                .Properties.Game_id.eq(gameId)).build().list();
        if (list == null || list.size() == 0) {
            return false;
        }
        List<DBPointRecord> list_dbPointRecord = list.get(0).getRecord_text();
        for (DBPointRecord dbPointRecord : list_dbPointRecord) {
            if (dbPointRecord.getId() == pointId) {
                List<DBTaskRecord> list_dbTaskRecord = dbPointRecord.getTask();
                if (list_dbTaskRecord != null && list_dbTaskRecord.size() > 0 && list_dbTaskRecord.size() ==
                        tastCounts) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param gameId
     * @param pointCounts
     * @return
     */
    @Override
    public boolean isCurrentGameFinished(long gameId, long pointCounts) {
        List<DBGameRecord> list = DBHelper.getInstance().getDBGameRecordDao().queryBuilder().where(DBGameRecordDao
                .Properties.Game_id.eq(gameId)).build().list();
        if (list == null || list.size() == 0) {
            return false;
        }
        List<DBPointRecord> list_dbPointRecord = list.get(0).getRecord_text();
        if (list_dbPointRecord != null && list_dbPointRecord.size() != 0 && list_dbPointRecord.size() == pointCounts) {
            return true;
        }
        return false;
    }
}
