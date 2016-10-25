package com.lptiyu.tanke.database;

import java.util.List;

import com.lptiyu.tanke.database.DaoSession;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * Entity mapped to table "DBGAME_RECORD".
 */
public class DBGameRecord {

    private Long id;
    private String join_time;
    private String start_time;
    private String last_task_ftime;
    private String play_statu;
    private String ranks_id;
    private String game_id;
    private String line_id;
    private String uid;

    /**
     * Used to resolve relations
     */
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    private transient DBGameRecordDao myDao;

    private List<DBPointRecord> record_text;

    public DBGameRecord() {
    }

    public DBGameRecord(Long id) {
        this.id = id;
    }

    public DBGameRecord(Long id, String join_time, String start_time, String last_task_ftime, String play_statu,
                        String ranks_id, String game_id, String line_id, String uid) {
        this.id = id;
        this.join_time = join_time;
        this.start_time = start_time;
        this.last_task_ftime = last_task_ftime;
        this.play_statu = play_statu;
        this.ranks_id = ranks_id;
        this.game_id = game_id;
        this.line_id = line_id;
        this.uid = uid;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDBGameRecordDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJoin_time() {
        return join_time;
    }

    public void setJoin_time(String join_time) {
        this.join_time = join_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getLast_task_ftime() {
        return last_task_ftime;
    }

    public void setLast_task_ftime(String last_task_ftime) {
        this.last_task_ftime = last_task_ftime;
    }

    public String getPlay_statu() {
        return play_statu;
    }

    public void setPlay_statu(String play_statu) {
        this.play_statu = play_statu;
    }

    public String getRanks_id() {
        return ranks_id;
    }

    public void setRanks_id(String ranks_id) {
        this.ranks_id = ranks_id;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getLine_id() {
        return line_id;
    }

    public void setLine_id(String line_id) {
        this.line_id = line_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not
     * persisted, make changes to the target entity.
     */
    public List<DBPointRecord> getRecord_text() {
        if (record_text == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DBPointRecordDao targetDao = daoSession.getDBPointRecordDao();
            List<DBPointRecord> record_textNew = targetDao._queryDBGameRecord_Record_text(id);
            synchronized (this) {
                if (record_text == null) {
                    record_text = record_textNew;
                }
            }
        }
        return record_text;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    public synchronized void resetRecord_text() {
        record_text = null;
    }

    /**
     * Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context.
     */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context.
     */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context.
     */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    @Override
    public String toString() {
        return "DBGameRecord{" +
                "id=" + id +
                ", join_time='" + join_time + '\'' +
                ", start_time='" + start_time + '\'' +
                ", last_task_ftime='" + last_task_ftime + '\'' +
                ", play_statu='" + play_statu + '\'' +
                ", ranks_id='" + ranks_id + '\'' +
                ", game_id='" + game_id + '\'' +
                ", line_id='" + line_id + '\'' +
                ", uid='" + uid + '\'' +
                ", record_text=" + record_text +
                '}';
    }
}
