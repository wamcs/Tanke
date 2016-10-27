package com.lptiyu.tanke.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.lptiyu.tanke.entity.greendao.LocationResult;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import greendao.DaoMaster;

/**
 * Created by Jason on 2016/9/6.
 */
public class DBManager {
    public static final long ERROR_CODE = -1;
    private final static String dbName = "location_db";
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    private DBManager(Context context) {
        this.context = context;
        openHelper = new greendao.DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new greendao.DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new greendao.DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }

    /**
     * 插入单条数据
     *
     * @param locationResult
     */
    public long insertLocation(LocationResult locationResult) {
        greendao.DaoMaster daoMaster = new greendao.DaoMaster(getWritableDatabase());
        greendao.DaoSession daoSession = daoMaster.newSession();
        greendao.LocationResultDao locationDao = daoSession.getLocationResultDao();
        try {
            return locationDao.insert(locationResult);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "插入失败", Toast.LENGTH_SHORT).show();
        }
        return ERROR_CODE;
    }

    /**
     * 插入集合
     *
     * @param locationResults
     */
    public boolean insertLocationResult(List<LocationResult> locationResults) {
        if (locationResults == null || locationResults.isEmpty()) {
            return false;
        }
        greendao.DaoMaster daoMaster = new greendao.DaoMaster(getWritableDatabase());
        greendao.DaoSession daoSession = daoMaster.newSession();
        greendao.LocationResultDao locationDao = daoSession.getLocationResultDao();
        try {
            locationDao.insertInTx(locationResults);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "插入失败", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * 查询列表
     */
    public List<LocationResult> queryLocationList() {
        greendao.DaoMaster daoMaster = new greendao.DaoMaster(getReadableDatabase());
        greendao.DaoSession daoSession = daoMaster.newSession();
        greendao.LocationResultDao locationDao = daoSession.getLocationResultDao();
        QueryBuilder<LocationResult> qb = locationDao.queryBuilder();
        List<LocationResult> list = qb.list();
        return list;
    }

    /**
     * 清空表
     */
    public void deleteLocationAll() {
        greendao.DaoMaster daoMaster = new greendao.DaoMaster(getWritableDatabase());
        greendao.DaoSession daoSession = daoMaster.newSession();
        greendao.LocationResultDao locationDao = daoSession.getLocationResultDao();
        locationDao.deleteAll();
    }
}
