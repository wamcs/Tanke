package com.lptiyu.tanke.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.lptiyu.tanke.entity.greendao.DRLocalData;
import com.lptiyu.tanke.entity.greendao.LocationResult;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import greendao.DaoMaster;

/**
 * Created by Jason on 2016/9/6.
 */
public class DBManager {
    public static final long ERROR_CODE = -1;
    private static final String DBNAME = "location_db";
    private static DBManager mInstance;
    private static DaoMaster.DevOpenHelper openHelper;
    private Context context;

    private DBManager(Context context) {
        this.context = context;
        openHelper = new greendao.DaoMaster.DevOpenHelper(context, DBNAME, null);
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
            openHelper = new greendao.DaoMaster.DevOpenHelper(context, DBNAME, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new greendao.DaoMaster.DevOpenHelper(context, DBNAME, null);
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
            long id = locationDao.insert(locationResult);
            LogUtils.i("insertLocation成功！");
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("insertLocation失败");
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
            LogUtils.i("insertLocationResult失败");
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

    /**
     * 插入单条数据
     *
     * @param drLocalData
     */
    public long insertDRLocalData(DRLocalData drLocalData) {
        greendao.DaoMaster daoMaster = new greendao.DaoMaster(getWritableDatabase());
        greendao.DaoSession daoSession = daoMaster.newSession();
        greendao.DRLocalDataDao drLocalDataDao = daoSession.getDRLocalDataDao();
        try {
            long id = drLocalDataDao.insert(drLocalData);
            LogUtils.i("insertDRLocalData成功！");
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("insertDRLocalData失败");
        }
        return ERROR_CODE;
    }

    /**
     * 查询列表
     */
    public List<DRLocalData> queryDRLocalData() {
        greendao.DaoMaster daoMaster = new greendao.DaoMaster(getReadableDatabase());
        greendao.DaoSession daoSession = daoMaster.newSession();
        greendao.DRLocalDataDao dRLocalDao = daoSession.getDRLocalDataDao();
        QueryBuilder<DRLocalData> qb = dRLocalDao.queryBuilder();
        List<DRLocalData> list = qb.list();
        return list;
    }

    /**
     * 清空表
     */
    public void deleteDRLocalAll() {
        greendao.DaoMaster daoMaster = new greendao.DaoMaster(getWritableDatabase());
        greendao.DaoSession daoSession = daoMaster.newSession();
        greendao.DRLocalDataDao dRLocalDao = daoSession.getDRLocalDataDao();
        dRLocalDao.deleteAll();
    }
}
