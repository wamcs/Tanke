package com.lptiyu.tanke;

import android.app.Activity;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.lptiyu.tanke.entity.BaseEntity;
import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.Task;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.LocationFileParser;
import com.lptiyu.tanke.utils.ThreadUtils;

import org.xutils.x;

import java.util.Stack;

import cn.sharesdk.framework.ShareSDK;
import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/5
 *
 * @author ldx
 */
public class RunApplication extends MultiDexApplication {

    public static Stack<Activity> activityStack;
    private static RunApplication singleton;
    public static GameRecord gameRecord;//维护一个正在玩的游戏数据
    public static int currentPointIndex;
    public static int currentTaskIndex;
    public static Task currentTask;
    public static Point currentPoint;
    public static long gameId;
    public static int type;
    public static boolean isPointOver;
    public static boolean isGameOver;
    public static BaseEntity entity;//点击的某个游戏实体
    public static int where;//从哪个入口进来的
    public static int recordId = -1;//查看通关奖励时，如果是从已完成进去的，则要传recordId，否则recordId=-1
    public static String SERVICE_IP;

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;

        MultiDex.install(this);

        Timber.plant(new Timber.DebugTree());
        AppData.init(this);

        x.Ext.init(this);
        x.Ext.setDebug(true);

        /*
        初始化程序崩溃记录器
         */
        CrashHandler.getInstance().init(this);

        try {
            ShareSDK.initSDK(this.getApplicationContext(), "1276c2d783264");
            DirUtils.init(this);
        } catch (Exception e) {
            Timber.e(e, e.getMessage());
        }

        ThreadUtils.background(new Runnable() {
            @Override
            public void run() {
                LocationFileParser.init(getApplicationContext(), Conf.DEFAULT_CITY_FILE_NAME);
            }
        });
    }


    // Returns the application instance
    public static RunApplication getInstance() {
        return singleton;
    }

    /**
     * add Activity 添加Activity到栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
        } catch (Exception e) {
        }
    }
}
