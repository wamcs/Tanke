package com.lptiyu.tanke.gameplaying.assist.zip;

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

  private Map<Integer, String> lineIdZipFileMap;
  private Map<Integer, Integer> lineIdTimeStampMap;
  private Map<Integer, String> lineIdUnzippedDirMap;

  private static final String ZIP_DIVIDER = "_";
  private static final String DEFAULE_GAME_ROOT_DIR = DirUtils.getTempDirectory().getAbsolutePath() + "/";
  public static final int ZIP_FILE_NOT_FOUND = -1;

  public GameZipScanner() {
    lineIdZipFileMap = new HashMap<>();
    lineIdTimeStampMap = new HashMap<>();
    lineIdUnzippedDirMap = new HashMap<>();
    scanGameZipFiles();
    scanGameUnzippedDir();
  }

  /**
   * if the zip file is exist, return the timestamp
   * if not exist, return -1
   *
   * @param lineId
   * @return
   */
  public int isZipFileExist(int lineId) {
    return getGameZipFileTimeStamp(lineId);
  }

  /**
   * if the zip file is unzipped, return the absolute path
   * else return null
   *
   * @param lineId
   * @return
   */
  public String isZipFileUnzipped(int lineId) {
    return lineIdUnzippedDirMap.get(lineId);
  }

  public int getGameZipFileTimeStamp(int lineId) {
    if (lineIdTimeStampMap.get(lineId) == null) {
      return ZIP_FILE_NOT_FOUND;
    }
    return lineIdTimeStampMap.get(lineId);
  }

  public String getGameZipFileAbsolutePath(int lineId) {
    return DEFAULE_GAME_ROOT_DIR + lineIdZipFileMap.get(lineId);
  }

  private String[] scanFilesWithFilter(FilenameFilter filter) {
    File gameDir = DirUtils.getTempDirectory();
    if (gameDir == null) {
      Timber.e("Game dir could not found");
      return null;
    }
    return gameDir.list(filter);
  }

  private void scanGameZipFiles() {
    String[] files = scanFilesWithFilter(new GameZipFileFilter());
    if (files == null) {
      return;
    }
    for (String zipFile : files) {
      String[] gameLineTimeStamp = zipFile.split(ZIP_DIVIDER);
      lineIdZipFileMap.put(Integer.valueOf(gameLineTimeStamp[1]), zipFile);
      lineIdTimeStampMap.put(Integer.valueOf(gameLineTimeStamp[1]), Integer.valueOf(gameLineTimeStamp[2].substring(0, 10)));
    }
  }

  private void scanGameUnzippedDir() {
    String[] files = scanFilesWithFilter(new GameUnzippedDirFilter());
    if (files == null) {
      return;
    }
    for (String zipFile : files) {
      String[] gameLineTimeStamp = zipFile.split(ZIP_DIVIDER);
      lineIdUnzippedDirMap.put(Integer.valueOf(gameLineTimeStamp[1]), zipFile);
    }
  }
}
