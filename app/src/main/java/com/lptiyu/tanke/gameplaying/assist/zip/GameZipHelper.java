package com.lptiyu.tanke.gameplaying.assist.zip;

import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.gameplaying.pojo.ThemeLine;
import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.FileUtils;

import java.util.List;

import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-24
 *         email: wonderfulifeel@gmail.com
 */
public class GameZipHelper {

  private GameZipScanner mGameZipScanner;
  private GameZipParser mGameZipParser;

  public GameZipHelper() {
    mGameZipScanner = new GameZipScanner();
    mGameZipParser = new GameZipParser();
  }

  /**
   * check、unzip、parse the zip file
   *
   * @param gameId
   */
  public boolean checkAndParseGameZip(long gameId) {
    if (mGameZipScanner.isZipFileExist(gameId) == GameZipScanner.ZIP_FILE_NOT_FOUND) {
      Timber.e("zip file not found which gameId : %d", gameId);
      return false;
    }
    String unzippedDir = mGameZipScanner.isZipFileUnzipped(gameId);
    if (unzippedDir == null) {
      unzippedDir = FileUtils.unzipFile(mGameZipScanner.getGameZipFileAbsolutePath(gameId), DirUtils.getTempDirectory().getAbsolutePath() + "/");
    }
    return mGameZipParser.parseGameZip(unzippedDir);
  }

  public ThemeLine getmThemeLine() {
    return mGameZipParser.getmThemeLine();
  }

  public List<Point> getmPoints() {
    return mGameZipParser.getmPoints();
  }

}
