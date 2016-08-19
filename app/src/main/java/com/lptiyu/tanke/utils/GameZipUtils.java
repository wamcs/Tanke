package com.lptiyu.tanke.utils;

import android.util.Log;

import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.Task;
import com.lptiyu.tanke.entity.ThemeLine;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

/**
 * Created by Jason on 2016/8/2.
 */
public class GameZipUtils {

    public String zippedFilePath;//游戏压缩包的绝对路径

    public String zippedFileName;//游戏压缩包名称
    public String parsedFileName;//游戏解压包名称
    public String fileRootPath;//游戏解压包的父目录
    public String parsedFilePath;//游戏解压包的绝对路径
    public long gameId;//游戏id
    public long lineId;//游戏线路id
    public long version;//当前游戏版本号


    public ThemeLine mThemeLine;//线路信息
    public List<Point> mPoints;//所有章节点集合
    public ArrayList<Task> mTasks;//章节点下的任务集合

    public GameZipUtils() {
    }

    //    public GameZipUtils(String zippedFilePath) {
    //        setGameIdLineIdVersion(zippedFilePath);
    //        parseZipFile(zippedFilePath);
    //        transformParsedFileToEntity();
    //    }

    public void setGameIdLineIdVersion(String zippedFilePath) {
        this.zippedFilePath = zippedFilePath;
        this.zippedFileName = zippedFilePath.substring(zippedFilePath.lastIndexOf('/') + 1);
        this.fileRootPath = zippedFilePath.substring(0, zippedFilePath.lastIndexOf('/'));
        this.parsedFileName = zippedFilePath.substring(zippedFilePath.lastIndexOf('/') + 1,
                zippedFilePath.lastIndexOf('.'));
        String[] split = parsedFileName.split("_");
        this.gameId = Long.parseLong(split[0]);
        this.lineId = Long.parseLong(split[1]);
        this.version = Long.parseLong(split[2]);
    }

    /**
     * 判断该游戏解压包是否存在
     */
    public String isParsedFileExist(long gameId) {
        //获取游戏包父目录
        File gameZipRootFile = DirUtils.getTempDirectory();
        //筛选出解压文件
        //        String[] list = gameZipRootFile.list(new GameParsedFileFilter());
        String[] list = gameZipRootFile.list();
        for (String filePath : list) {
//            Log.i("jason", "筛选出的游戏文件夹：" + filePath);
            if (filePath.startsWith(gameId + "_") && !filePath.endsWith(".zip")) {
                //                this.parsedFilePath = gameZipRootFile.getAbsolutePath() + "/" + filePath;
                //                setGameIdLineIdVersion(parsedFilePath + ".zip");
                //                transformParsedFileToEntity();
                return gameZipRootFile.getAbsolutePath() + "/" + filePath;
            }
        }
        return null;
    }

    /**
     * 判断该游戏压缩包是否存在
     */
    public String isZippedFileExist(long gameId) {
        //获取游戏包父目录
        File gameZipRootFile = DirUtils.getTempDirectory();
        //筛选出压缩文件，也可以通过filePath.endsWith(".zip")来筛选
        String[] list = gameZipRootFile.list(new GameZippedFileFilter());
        for (String filePath : list) {
            if (filePath.startsWith(gameId + "")) {
                this.zippedFilePath = gameZipRootFile.getAbsolutePath() + "/" + filePath;
                return zippedFilePath;
            }
        }
        return null;
    }

    private class GameParsedFileFilter implements FilenameFilter {

        /**
         * 文件名格式：37_31_12
         */
        String pattern = "[0-9]+_[0-9]+_[0-9]";
        Pattern gameParsedPattern = Pattern.compile(pattern);

        /**
         * 文件过滤器，返回true的文件则合格
         *
         * @param dir      文件的当前目录
         * @param filename 文件名称
         * @return
         */
        @Override
        public boolean accept(File dir, String filename) {
            Matcher matcher = gameParsedPattern.matcher(filename);
            return matcher.matches();
        }

    }

    private class GameZippedFileFilter implements FilenameFilter {

        /**
         * 文件名格式：37_31_12
         */
        Pattern gameZipPattern = Pattern.compile("[0-9]+_[0-9]+_[0-9].zip$");

        /**
         * 文件过滤器，返回true的文件则合格
         *
         * @param dir      文件的当前目录
         * @param filename 文件名称
         * @return
         */
        @Override
        public boolean accept(File dir, String filename) {
            Matcher matcher = gameZipPattern.matcher(filename);
            return matcher.matches();
        }

    }


    /**
     * 判断游戏是否更新
     *
     * @param gameId
     * @return
     */
    public boolean isGameUpdated(long gameId, String parsedFileName) {
        String parsedFilePath = isParsedFileExist(gameId);
        if (parsedFilePath == null) {
            Log.i("jason", "gameId=" + gameId + "的游戏包不存在");
            return false;
        } else {
            String fileName = parsedFilePath.substring(parsedFilePath.lastIndexOf('/') + 1);
            if (fileName.equals(parsedFileName)) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 解压游戏包，并返回解压后游戏文件夹的绝对路径
     *
     * @return
     */
    public String parseZipFile(String zippedFilePath) {
        boolean isFileExist = FileUtils.isFileExist(zippedFilePath);
        if (isFileExist) {
            String parsedFilePath = FileUtils.unzipFile(zippedFilePath);
            return parsedFilePath;
        } else {
            return null;
        }
    }

    public boolean transformParsedFileToEntity(String parsedFilePath) {
        //        if (isParsedFileExist(gameId)) {
        //            return false;
        //            //            throw playing NullPointerException("游戏文件夹不存在");
        //        }
        File file = new File(parsedFilePath);
        File[] listFiles = file.listFiles();
        if (listFiles == null || listFiles.length <= 0) {
            return false;
            //            throw playing NullPointerException("游戏文件夹下不存在任何文件");
        }
        //解析theme_line.json文件
        String themeLineJsonFilePath = parsedFilePath + "/theme_line.json";
        mThemeLine = checkAndParseTJsonFile(themeLineJsonFilePath, ThemeLine.class);
        if (mThemeLine == null) {
            Log.i("jason", "parse theme line json file error");
            return false;
        }

        //解析points.json文件
        String[] pointDirs = file.list(new PointDirFilter("point", ".json"));
        int pointCountInFile = pointDirs.length;
        if (Integer.parseInt(mThemeLine.point_count) != pointCountInFile) {
            Log.i("jason", "point count in the dir is not match with json information");
            return false;
        }
        if (mPoints == null) {
            mPoints = new ArrayList<>();
        }
        for (int i = 0; i < pointCountInFile; i++) {
            String absolutePointDir = parsedFilePath + "/" + pointDirs[i];
            Point point = checkAndParsePointDir(absolutePointDir);
            if (point == null) {
                return false;
            }
            mPoints.add(point);
        }

        //解析tasks.json文件
        String[] taskDirs = file.list(new PointDirFilter("task", ".json"));
        int taskCountInFile = taskDirs.length;
        if (mTasks == null) {
            mTasks = new ArrayList<>();
        }
        for (int i = 0; i < taskCountInFile; i++) {
            String absoluteTaskDir = parsedFilePath + "/" + taskDirs[i];
            Task task = checkAndParseTaskDir(absoluteTaskDir);
            if (task == null) {
                return false;
            }
            mTasks.add(task);
        }
        return true;
    }

    //文件筛选
    private class PointDirFilter implements FilenameFilter {
        private String firstType;
        private String endType;

        public PointDirFilter(String firstType, String endType) {
            this.firstType = firstType;
            this.endType = endType;
        }

        @Override
        public boolean accept(File dir, String filename) {
            File file = new File(dir + "/" + filename);
            if (filename.startsWith(firstType) && filename.endsWith(endType) && !file.isDirectory())
                return true;
            else
                return false;
        }
    }

    /**
     * 解析point.json
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
        String pointJsonFilePath = pointRootDir;
        Point point = checkAndParseTJsonFile(pointJsonFilePath, Point.class);
        if (point == null) {
            Timber.e("point dir parse error");
            return null;
        }
        return point;
    }

    /**
     * 解析task.json
     *
     * @param taskRootDir
     * @return
     */
    private Task checkAndParseTaskDir(String taskRootDir) {
        File file = new File(taskRootDir);
        if (!file.exists()) {
            Timber.e("%s point dir is not exist or can not be open", taskRootDir);
            return null;
        }
        String taskJsonFilePath = taskRootDir;
        Task task = checkAndParseTJsonFile(taskJsonFilePath, Task.class);
        if (task == null) {
            Timber.e("point dir parse error");
            return null;
        }
        return task;
    }


    private <T> T checkAndParseTJsonFile(String jsonFilePath, Class<T> clazz) {
        File file = new File(jsonFilePath);
        if (!file.exists()) {
            Timber.e("%s is not exist or can not be open", jsonFilePath);
            return null;
        }
        return FileUtils.parseJsonFile(file, clazz);
    }
}
