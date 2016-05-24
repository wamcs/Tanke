package com.lptiyu.tanke.gameplaying.assist.zip;

import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.FileUtils;

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
   * @param lineId
   */
  public boolean checkAndParseGameZip(int gameId, int lineId) {
    if (mGameZipScanner.isZipFileExist(lineId) == GameZipScanner.ZIP_FILE_NOT_FOUND) {
      Timber.e("zip file not found which gameId : %d, lineId : %d", gameId, lineId);
      return false;
    }
    String unzippedDir = mGameZipScanner.isZipFileUnzipped(lineId);
    if (unzippedDir == null) {
      unzippedDir = FileUtils.unzipFile(mGameZipScanner.getGameZipFileAbsolutePath(lineId), DirUtils.getTempDirectory().getAbsolutePath() + "/");
    }
    return mGameZipParser.parseGameZip(unzippedDir);
  }

}
