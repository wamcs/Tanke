package com.lptiyu.tanke.database;


import com.lptiyu.tanke.global.AppData;

import de.greenrobot.dao.async.AsyncSession;

/**
 * author:wamcs
 * date:2016/6/2
 * email:kaili@hustunique.com
 */
public class DBHelper {


    private volatile static DBHelper helper;

    public static DBHelper getInstance() {
        if (null == helper) {
            synchronized (DBHelper.class) {
                if (null == helper) {
                    helper = new DBHelper();
                }
            }
        }
        return helper;
    }

    private static final String DB_NAME = "tanke.db";
    private DaoSession daoSession;
    private AsyncSession asyncSession;

    private DBHelper() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(AppData.getContext(), DB_NAME, null);
        DaoMaster master = new DaoMaster(helper.getWritableDatabase());
        daoSession = master.newSession();
        asyncSession = daoSession.startAsyncSession();
    }

    public MessageNotificationDao getPushMessageDao() {
        return daoSession.getMessageNotificationDao();
    }

    public MessageNotificationListDao getMessageListDao() {
        return daoSession.getMessageNotificationListDao();
    }

    public DBGameRecordDao getDBGameRecordDao() {
        return daoSession.getDBGameRecordDao();
    }

    public DBPointRecordDao getDBPointDao() {
        return daoSession.getDBPointRecordDao();
    }

    public DBTaskRecordDao getDBTaskDao() {
        return daoSession.getDBTaskRecordDao();
    }

    //用于处理异步任务
    public AsyncSession getAsyncSession() {
        return asyncSession;
    }
}
