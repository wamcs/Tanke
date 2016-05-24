package com.lptiyu.tanke.gameplaying.assist.zip;


import java.io.File;

import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-24
 *         email: wonderfulifeel@gmail.com
 */
class GameZipParser {

  public GameZipParser() {

  }

  /**
   * the zip file is exist and it has been unzipped
   * just parse it
   * @param unzippedDir
   * @return
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
    //TODO ： start to parse info from dir
    String[] files = file.list();
    for (String s : files) {
      Timber.e(s);
    }
    return true;
  }

}
