package com.lptiyu.tanke.gameplaying.assist.zip;

import com.lptiyu.tanke.gameplaying.assist.zip.filter.GameUnzippedDirFilter;
import com.lptiyu.tanke.gameplaying.assist.zip.filter.GameZipFileFilter;
import com.lptiyu.tanke.utils.DirUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-24
 *         email: wonderfulifeel@gmail.com
 */
public class GameZipScanner {

    private Map<Long, String> gameIdZipFileMap;
    private Map<Long, Long> gameIdTimeStampMap;
    private Map<Long, String> gameIdUnzippedDirMap;

    private static final String ZIP_DIVIDER = "_";
    //获取游戏包的存储目录：SDCard/Android/data/包名/files/temp
    private static final String DEFAULT_GAME_ROOT_DIR = DirUtils.getTempDirectory().getAbsolutePath() + "/";
    public static final long ZIP_FILE_NOT_FOUND_TIMESTAMP = -1L;

    public GameZipScanner() {
        gameIdZipFileMap = new HashMap<>();
        gameIdTimeStampMap = new HashMap<>();
        gameIdUnzippedDirMap = new HashMap<>();
        scanGameZipFiles();
        scanGameUnzippedDir();
    }

    public void reload() {
        gameIdZipFileMap.clear();
        gameIdTimeStampMap.clear();
        gameIdUnzippedDirMap.clear();
        scanGameZipFiles();
        scanGameUnzippedDir();
    }

    /**
     * if the zip file is exist, return the timestamp
     * if not exist, return -1
     *
     * @param gameId
     * @return
     */
    public long isZipFileExist(long gameId) {
        return getGameZipFileTimeStamp(gameId);
    }

    /**
     * if the zip file is unzipped, return the absolute path
     * else return null
     * <p/>
     * 根据游戏ID返回解压后的游戏文件夹的绝对路径
     *
     * @param gameId
     * @return
     */
    public String isZipFileUnzipped(long gameId) {
        String result = gameIdUnzippedDirMap.get(gameId);
        if (result == null) {
            return null;
        }
        return DirUtils.getTempDirectory() + "/" + result;
    }

    /**
     * This method will return the unix timestamp of game zip
     *
     * @param gameId the target game's id
     * @return if the zip of gameId is exist, return the timestamp
     * if not exist, return {ZIP_FILE_NOT_FOUND_TIMESTAMP : -1}
     * 根据游戏ID返回该游戏的时间戳
     */
    public long getGameZipFileTimeStamp(long gameId) {
        if (gameIdTimeStampMap.get(gameId) == null) {
            return ZIP_FILE_NOT_FOUND_TIMESTAMP;
        }
        return gameIdTimeStampMap.get(gameId);
    }

    //返回游戏压缩包的绝对路径
    public String getGameZipFileAbsolutePath(long gameId) {
        return DEFAULT_GAME_ROOT_DIR + gameIdZipFileMap.get(gameId);
    }

    /**
     * 根据筛选条件筛选出满足条件的文件
     *
     * @param filter
     * @return 返回满足条件的文件的文件路径数组
     */
    private String[] scanFilesWithFilter(FilenameFilter filter) {
        File gameDir = DirUtils.getTempDirectory();
        if (gameDir == null) {
            Timber.e("Game dir could not found");
            return null;
        }
        return gameDir.list(filter);
    }

    /**
     * 遍历压缩文件
     */
    private void scanGameZipFiles() {
        //筛选出游戏压缩包文件并获取该文件下的文件列表
        String[] files = scanFilesWithFilter(new GameZipFileFilter());
        if (files == null) {
            return;
        }
        for (String zipFile : files) {
            //通过"_"分隔符来切割文件名
            String[] gameLineTimeStamp = zipFile.split(ZIP_DIVIDER);
            //存储文件路径
            gameIdZipFileMap.put(Long.valueOf(gameLineTimeStamp[0]), zipFile);
            //存储时间戳
            gameIdTimeStampMap.put(Long.valueOf(gameLineTimeStamp[0]), Long.valueOf(gameLineTimeStamp[2].substring(0,
                    10)));
        }
    }

    /**
     * 遍历未压缩的文件
     */
    private void scanGameUnzippedDir() {
        //筛选出游戏压缩包解压后的文件夹并获取该文件夹下的文件列表
        String[] files = scanFilesWithFilter(new GameUnzippedDirFilter());
        if (files == null) {
            return;
        }
        for (String zipFile : files) {
            String[] gameLineTimeStamp = zipFile.split(ZIP_DIVIDER);
            //存储游戏包文件下的每一个文件的路径
            gameIdUnzippedDirMap.put(Long.valueOf(gameLineTimeStamp[0]), zipFile);
        }
    }
}
