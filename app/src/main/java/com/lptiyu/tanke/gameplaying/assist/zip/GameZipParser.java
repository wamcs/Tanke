package com.lptiyu.tanke.gameplaying.assist.zip;


import com.lptiyu.tanke.gameplaying.assist.zip.filter.GameUnzippedPointDirFilter;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.lptiyu.tanke.gameplaying.pojo.ThemeLine;
import com.lptiyu.tanke.utils.FileUtils;
import com.lptiyu.zxinglib.core.common.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-24
 *         email: wonderfulifeel@gmail.com
 */
public class GameZipParser {

    private ThemeLine mThemeLine;
    private List<Point> mPoints;

    private static final String THEME_LINE_JSON_FILE_NAME = "theme_line.json";
    private static final String POINT_JSON_FILE_NAME = "point.json";
    private static final String TASK_JSON_FILE_NAME = "mission.json";
    private static final String LOCAL_FILE_PREFIX = "file://";

    public GameZipParser() {
    }

    /**
     * the zip file is exist and it has been unzipped
     * just parse it
     * <p>
     * 判断游戏压缩包是否存在，如果存在则解压该文件，返回是否解压成功
     *
     * @param unzippedDir the zip file was unzipped directory
     * @return true if parse unzipped file success
     */
    public boolean parseGameZip(String unzippedDir) {
        if (unzippedDir == null) {
            throw new IllegalArgumentException("Invalid arguments, the unzip dir is null");
        }

        File file = new File(unzippedDir);

        if (!file.exists()) {
            Timber.e("the unzip dir : %s is not exist", unzippedDir);
            return false;
        }

        String themeLineJsonFilePath = unzippedDir + "/" + THEME_LINE_JSON_FILE_NAME;
        mThemeLine = checkAndParseTJsonFile(themeLineJsonFilePath, ThemeLine.class);
        if (mThemeLine == null) {
            Timber.e("parse theme line json file error");
            return false;
        }

        String[] pointDirs = file.list(new GameUnzippedPointDirFilter());
        int pointCountInFile = pointDirs.length;
        if (mThemeLine.getPointCount() != pointCountInFile) {
            Timber.e("point count in the dir is not match with json information");
            return false;
        }

        if (mPoints == null) {
            mPoints = new ArrayList<>();
        }

        for (int i = 0; i < pointCountInFile; i++) {
            String absolutePointDir = unzippedDir + "/" + i;
            Point point = checkAndParsePointDir(absolutePointDir);
            if (point == null) {
                return false;
            }
            mPoints.add(point);
        }
        return true;
    }


    /**
     * Check the point dir is exist
     * parse the file in the dir
     * <p>
     * 1. parse the point.json file
     * 2. parse the other message
     * <p>
     * 解析point.json文件
     *
     * @param pointRootDir
     * @return
     */
    private Point checkAndParsePointDir(String pointRootDir) {
        File file = new File(pointRootDir);
        if (!file.exists()) {
            Timber.e("%s point dir is not exist or can not be open", pointRootDir);
            return null;
        }
        String pointJsonFilePath = pointRootDir + "/" + POINT_JSON_FILE_NAME;
        Point point = checkAndParseTJsonFile(pointJsonFilePath, Point.class);
        if (point == null) {
            Timber.e("point dir parse error");
            return null;
        }

        Map<String, Task> taskMap = new HashMap<>();
        List<String> taskIds = point.getTaskId();
        if (taskIds == null) {
            Timber.e("task ids is null");
            return null;
        }
        for (String taskName : taskIds) {
            String taskDirPath = pointRootDir + "/" + taskName;
            Task task = checkAndParsePointMission(taskDirPath);
            if (task == null) {
                Timber.e("task %s parse error", taskName);
                return null;
            }
            taskMap.put(taskName, task);
            //this method only be used when point only has one task
            point.setIntroImage(LOCAL_FILE_PREFIX + taskDirPath + "/" + point.getPointIndex() + ".jpg");
        }
        point.setTaskMap(taskMap);
        return point;
    }

    /**
     * This method is to parse task info
     * from the task dir
     *
     * @param taskDirPath task dir path
     * @return diff type of task if parse success, null for error
     */
    private Task checkAndParsePointMission(String taskDirPath) {
        File file = new File(taskDirPath);
        if (!file.exists()) {
            Timber.e("%s, task dir is not exist or can not be open", taskDirPath);
            return null;
        }

        String taskJsonFilePath = taskDirPath + "/" + TASK_JSON_FILE_NAME;
        Task task = checkAndParseTJsonFile(taskJsonFilePath, Task.class);
        if (task == null) {
            Timber.e("parse task json file error");
            return null;
        }

        if (!completeMissionInfo(task, taskDirPath)) {
            // task info files are damaged or not exist
            return null;
        }
        return task;
    }

    /**
     * Complete task info about the content and pwd
     *
     * @param task        task to be completed
     * @param taskDirPath task content file's root directory
     * @return true if complete success, false for error
     */
    private boolean completeMissionInfo(Task task, String taskDirPath) {
        String taskContentFilePath = taskDirPath + "/" + task.getContent();
        if (!FileUtils.isFileExist(taskContentFilePath)) {
            Timber.e("task : %s,  message file is damaged", taskContentFilePath);
            return false;
        }
        task.setContent(LOCAL_FILE_PREFIX + taskContentFilePath);

        String taskPwdFilePath = taskDirPath + "/" + task.getPwd();
        if (!FileUtils.isFileExist(taskPwdFilePath)) {
            Timber.e("task : %s,  password file is damaged", taskPwdFilePath);
            return false;
        }
        String pwd = FileUtils.readFileByLine(taskPwdFilePath);
        if (pwd.length() == 0) {
            Timber.e("read file : %s error", taskPwdFilePath);
            return false;
        }
        task.setPwd(StringUtils.replaceBlank(pwd));
        return true;
    }


    /**
     * Check and parse the target json file
     * return the class instance
     *
     * @param jsonFilePath target json file to be parsed
     * @param clazz        convert json file to clazz
     * @param <T>          type of clazz
     * @return T for success, null for error
     */
    private <T> T checkAndParseTJsonFile(String jsonFilePath, Class<T> clazz) {
        File file = new File(jsonFilePath);
        if (!file.exists()) {
            Timber.e("%s is not exist or can not be open", jsonFilePath);
            return null;
        }
        return FileUtils.parseJsonFile(file, clazz);
    }

    public ThemeLine getmThemeLine() {
        return mThemeLine;
    }

    public List<Point> getmPoints() {
        return mPoints;
    }
}
