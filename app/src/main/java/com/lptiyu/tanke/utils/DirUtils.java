package com.lptiyu.tanke.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import timber.log.Timber;


/**
 * USER: xiaoxiaoda
 * DATE: 15-11-23
 * EMAIL: 2319821734@qq.com
 */
public class DirUtils {

    private static String SDCARD_ROOT_DIR;
    private static String DATA_ROOT_DIR;
    private static final String FILES = "/files";
    private static final String TEMP = "/temp";
    private static final String CRASH_lOG = "/CRASH_lOG";
    private static final String RES = "/res";
    private static final String RECORD = "/record";
    private static final String APK = "/apk";
    private static final String SCREEN_SHOT = "/screen_shot";
    private static final String DIRECTION_RUN = "/direction_run";
    private static final String GAME = "/game";
    private static final String VIDEO = "/video";
    private static final String AUDIO = "/audio";
    private static final String ANDROID_RESOURCE = "android.resource://";

    /**
     * 判断SDCard是否正常挂载
     *
     * @return
     */
    private static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 初始化SDCard文件目录和私有文件目录
     *
     * @param context
     * @throws Exception
     */
    public static void init(Context context) throws Exception {
        File file = context.getExternalFilesDir(null);
        if (file != null) {
            SDCARD_ROOT_DIR = file.getPath();
        } else {
            throw new Exception("GET SDCARD DIR ERROR");
        }
        DATA_ROOT_DIR = context.getFilesDir().toString();
    }

    public static File getResDirectory() {
        StringBuilder stringBuilder = new StringBuilder();
        if (hasSDCard())
            stringBuilder.append(SDCARD_ROOT_DIR).append(RES);
        else
            stringBuilder.append(DATA_ROOT_DIR).append(FILES).append(RES);
        File destDir = new File(stringBuilder.toString());
        if (!destDir.exists()) {
            if (destDir.mkdirs()) {
                Timber.d("=======create dir======== %s", destDir.getAbsolutePath());
            } else {
                Timber.d("=======create dir========failed");
            }
        }
        return destDir;
    }

    /**
     * 根据指定文件夹目录和类型创建文件夹
     *
     * @param rootDir
     * @param type
     * @return
     */
    public static File getDirectory(String rootDir, String type) {
        if (SDCARD_ROOT_DIR == null || DATA_ROOT_DIR == null) {
            Timber.e("you should invoke init() method before use DirUtils");
            return null;
        }
        File destDir = new File(rootDir + type);
        if (!destDir.exists()) {
            if (destDir.mkdirs()) {
                Timber.d("=======create dir======== %s", destDir.getAbsolutePath());
            } else {
                Timber.d("=======create dir========failed");
            }
        }
        return destDir;
    }

    /**
     * 获取SDCard文件目录下的temp目录
     *
     * @return
     */
    public static File getTempDirectory() {
        return getDirectory(SDCARD_ROOT_DIR, TEMP);
    }

    /**
     * 获取SDCard文件目录下的temp目录
     *
     * @return
     */
    public static File getCrashLogDirectory() {
        return getDirectory(SDCARD_ROOT_DIR, CRASH_lOG);
    }

    /**
     * 获取SDCard文件目录下的record目录
     *
     * @return
     */
    public static File getRecordDirectory() {
        return getDirectory(SDCARD_ROOT_DIR, RECORD);
    }

    public static File getVideoDirectory() {
        return getDirectory(SDCARD_ROOT_DIR, VIDEO);
    }

    public static File getAudioDirectory() {
        return getDirectory(SDCARD_ROOT_DIR, AUDIO);
    }

    /**
     * 获取SDCard文件目录下的game目录
     *
     * @return
     */
    public static File getGameDirectory() {
        return getDirectory(DATA_ROOT_DIR, GAME);
    }

    /**
     * 获取SDCard文件目录下的apk目录
     *
     * @return
     */
    public static File getAPKDirectory() {
        return getDirectory(SDCARD_ROOT_DIR, APK);
    }

    //获取屏幕截图的文件夹
    public static File getScreenShotDirectory() {
        return getDirectory(SDCARD_ROOT_DIR, SCREEN_SHOT);

    }

    //获取定向乐跑轨迹记录文件夹
    public static File getDirectionRunDirectory() {
        return getDirectory(SDCARD_ROOT_DIR, DIRECTION_RUN);
    }
}
