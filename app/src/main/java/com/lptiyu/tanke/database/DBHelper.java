package com.lptiyu.tanke.database;

import android.database.sqlite.SQLiteDatabase;

import com.lptiyu.tanke.global.AppData;

/**
 * author:wamcs
 * date:2016/6/2
 * email:kaili@hustunique.com
 */
public class DBHelper {


    private static DaoMaster.DevOpenHelper helper;
    private static SQLiteDatabase db;
    private static DaoMaster master;
    private static DaoSession session;

    public static void init(){
        helper = new DaoMaster.DevOpenHelper(AppData.getContext(),"tanke-db",null);
    }

    public static PushMessageDao getPushMessageDao(){
        db = helper.getWritableDatabase();
        master = new DaoMaster(db);
        session = master.newSession();
        return session.getPushMessageDao();
    }

    public static void recycle(){
        session = null;
        master = null;
        db.close();
    }
}
