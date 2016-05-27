package com.lptiyu.tanke.gameplaying.assist.zip.filter;

import java.io.File;
import java.io.FilenameFilter;


/**
 * @author : xiaoxiaoda
 *         date: 16-5-25
 *         email: wonderfulifeel@gmail.com
 */

/**
 * this class is to filter the point directory
 */
public class GameUnzippedPointDirFilter implements FilenameFilter {

  @Override
  public boolean accept(File dir, String filename) {
    File file = new File(dir + "/" + filename);
    return file.isDirectory();
  }
}
