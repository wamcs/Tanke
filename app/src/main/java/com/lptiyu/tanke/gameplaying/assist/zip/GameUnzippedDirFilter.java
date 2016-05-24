package com.lptiyu.tanke.gameplaying.assist.zip;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-24
 *         email: wonderfulifeel@gmail.com
 */
public class GameUnzippedDirFilter implements FilenameFilter {

  static Pattern gameUpzippedPattern = Pattern.compile("[0-9]{10}_[0-9]{10}_[0-9]{10}");

  @Override
  public boolean accept(File dir, String filename) {
    Matcher matcher = gameUpzippedPattern.matcher(filename);
    return matcher.matches();
  }
}
