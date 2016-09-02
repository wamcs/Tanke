package com.lptiyu.tanke;

import android.app.Activity;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.SDKInitializer;
import com.lptiyu.tanke.entity.ThemeLine;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.location.LocationFileParser;
import com.lptiyu.tanke.messagesystem.MessageActivity;
import com.lptiyu.tanke.pojo.GameDisplayEntity;
import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.thread;

import org.xutils.x;

import java.util.List;
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

    private static Stack<Activity> activityStack;
    private static RunApplication singleton;
    private BMapManager manager;

    public static List<GameDisplayEntity> gameList; //维护一份列表数据的索引，避免回收
    private static ThemeLine themeLine;//维护一个正在玩的游戏数据

    private static long lastLoginUserId = 0;

    public static boolean isPlayingStatusChanged = false;

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
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

        try {
            //      initBMapManager(this);
            ShareSDK.initSDK(this.getApplicationContext(), "1276c2d783264");
            AVOSCloud.initialize(AppData.getContext(), "Wqseclbr8wx2kFAS7YseVc5n-gzGzoHsz", "1z4GofW1zaArBjcj53u3oBm1");
            PushService.setDefaultPushCallback(this, MessageActivity.class);
            SDKInitializer.initialize(this);
            DirUtils.init(this);
            if (Accounts.getInstallationId().isEmpty()) {
                String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                Accounts.setInstallationId(installationId);
                Timber.d("this device installation is %s", installationId);
            }

        } catch (Exception e) {
            // To test it automatically.
            Timber.e(e, e.getMessage());
        }

        thread.background(new Runnable() {
            @Override
            public void run() {
                LocationFileParser.init(getApplicationContext(), Conf.DEFAULT_CITY_FILE_NAME);
            }
        });
    }

    //  public void initBMapManager(Context context) {
    //    if (manager == null) {
    //      manager = playing BMapManager();
    //    }
    //    manager.init();
    //  }


    // Returns the application instance
    public static RunApplication getInstance() {
        return singleton;
    }

    public static ThemeLine getPlayingThemeLine() {
        return themeLine;
    }

    public static void setgetPlayingThemeLine(ThemeLine playLine) {
        themeLine = playLine;
    }

    //获取上一次的登录账户
    public static long getLastLoginUserId() {
        return lastLoginUserId;
    }

    public static void setLastLoginUserId(long id) {
        lastLoginUserId = id;
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


    //主要是改游戏状态和下载包链接
    public void setGameDataByGameId(long game_id, int game_statu, String game_zip_url) {
        if (gameList == null)
            return;

        for (int i = 0; i < gameList.size(); i++) {
            GameDisplayEntity tmp = gameList.get(i);
            if (game_id == tmp.getId()) {
                tmp.setPlayStatu(game_statu);
                tmp.setGameZipUrl(game_zip_url);
            }
        }
    }

}
