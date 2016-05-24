package com.lptiyu.tanke.gameplaying.assist.zip;

import com.lptiyu.tanke.utils.DirUtils;

import java.io.File;

import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-24
 *         email: wonderfulifeel@gmail.com
 */
public class GameZipScaner {

  public GameZipScaner() {

  }

  public void scanGameZipFiles() {
    File gameDir = DirUtils.getGameDirectory();
    if (gameDir == null) {
      Timber.e("Game dir create failed");
      return;
    }

  }
}
