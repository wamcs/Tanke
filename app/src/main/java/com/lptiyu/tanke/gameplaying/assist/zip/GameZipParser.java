package com.lptiyu.tanke.gameplaying.assist.zip;


import com.google.gson.Gson;
import com.lptiyu.tanke.gameplaying.assist.zip.filter.GameUnzippedPointDirFilter;
import com.lptiyu.tanke.gameplaying.pojo.Mission;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.gameplaying.pojo.ThemeLine;
import com.lptiyu.tanke.utils.FileUtils;

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

  private Gson mGson;
  private ThemeLine mThemeLine;
  private List<Point> mPoints;

  private static final String THEME_LINE_JSON_FILE_NAME = "theme_line.json";
  private static final String POINT_JSON_FILE_NAME = "point.json";
  private static final String MISSION_JSON_FILE_NAME = "mission.json";

  public GameZipParser() {
    mGson = new Gson();
  }

  /**
   * the zip file is exist and it has been unzipped
   * just parse it
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

    for (String pointDir : pointDirs) {
      String absolutePointDir = unzippedDir + "/" + pointDir;
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

    Map<String, Mission> missionMap = new HashMap<>();
    for (String missionName : point.getTaskId()) {
      String missionDirPath = pointRootDir + "/" + missionName;
      Mission mission = checkAndParsePointMission(missionDirPath);
      if (mission == null) {
        Timber.e("mission %s parse error", missionName);
        return null;
      }
      missionMap.put(missionName, mission);
    }
    point.setMissionMap(missionMap);
    return point;
  }

  /**
   * This method is to parse mission info
   * from the mission dir
   *
   * @param missionDirPath mission dir path
   * @return diff type of mission if parse success, null for error
   */
  private Mission checkAndParsePointMission(String missionDirPath) {
    File file = new File(missionDirPath);
    if (!file.exists()) {
      Timber.e("%s, mission dir is not exist or can not be open", missionDirPath);
      return null;
    }

    String missionJsonFilePath = missionDirPath + "/" + MISSION_JSON_FILE_NAME;
    Mission mission = checkAndParseTJsonFile(missionJsonFilePath, Mission.class);
    if (mission == null) {
      Timber.e("parse mission json file error");
      return null;
    }

    if (!completeMissionInfo(mission, missionDirPath)) {
      // mission info files are damaged or not exist
      return null;
    }
    return mission;
  }

  /**
   * Complete mission info about the content and pwd
   *
   * @param mission        mission to be completed
   * @param missionDirPath mission content file's root directory
   * @return true if complete success, false for error
   */
  private boolean completeMissionInfo(Mission mission, String missionDirPath) {
    String missionContentFilePath = missionDirPath + "/" + mission.getContent();
    if (!FileUtils.isFileExist(missionContentFilePath)) {
      Timber.e("mission : %s,  message file is damaged", missionContentFilePath);
      return false;
    }
    mission.setContent(missionContentFilePath);

    String missionPwdFilePath = missionDirPath + "/" + mission.getPwd();
    if (!FileUtils.isFileExist(missionPwdFilePath)) {
      Timber.e("mission : %s,  password file is damaged", missionPwdFilePath);
      return false;
    }
    String pwd = FileUtils.readFileByChar(missionPwdFilePath);
    if (pwd.length() == 0) {
      Timber.e("read file : %s error", missionPwdFilePath);
      return false;
    }
    mission.setPwd(pwd);
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

    String fileContent = FileUtils.readFileByChar(file);
    if (fileContent.length() == 0) {
      Timber.e("read json file : %s error", jsonFilePath);
      return null;
    }
    return mGson.fromJson(fileContent, clazz);
  }

  public ThemeLine getmThemeLine() {
    return mThemeLine;
  }

  public List<Point> getmPoints() {
    return mPoints;
  }
}
