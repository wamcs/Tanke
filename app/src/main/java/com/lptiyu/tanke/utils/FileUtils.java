package com.lptiyu.tanke.utils;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/3/11
 *
 * @author ldx
 */
public class FileUtils {

  public static String tail(File file) throws IOException {
    return tail(file, 1);
  }

  public static String tail(File file, int lines) throws IOException {
    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
    int line = 0;
    StringBuilder builder = new StringBuilder();
    long length = file.length();
    length--;
    randomAccessFile.seek(length);
    char x = (char) randomAccessFile.read();
    if (x == '\n') {
      length--;
    }
    for (long seek = length; seek >= 0; --seek) {
      randomAccessFile.seek(seek);
      char c = (char) randomAccessFile.read();
      builder.append(c);
      if (c == '\n') {
        line++;
        if (line == lines) {
          break;
        }
      }
    }
    return builder.reverse().toString();
  }
}
