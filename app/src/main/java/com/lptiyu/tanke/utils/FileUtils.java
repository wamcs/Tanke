package com.lptiyu.tanke.utils;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/3/11
 *
 * @author ldx
 */
public class FileUtils {

  static final int BUFFER = 2048;

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

  //TODO : UTF-8 only, do not support GBK
  public static String unzipFile(String fileName, String filePath) {
    String dirPath = null;
    try {
      ZipFile zipFile = new ZipFile(fileName);
      Enumeration emu = zipFile.entries();
      while (emu.hasMoreElements()) {
        ZipEntry entry = (ZipEntry) emu.nextElement();
        if (entry.isDirectory()) {
          new File(filePath + entry.getName()).mkdirs();
          continue;
        }
        BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
        File file = new File(filePath + entry.getName());
        File parent = file.getParentFile();
        if (parent != null && (!parent.exists())) {
          parent.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER);

        int count;
        byte data[] = new byte[BUFFER];
        while ((count = bis.read(data, 0, BUFFER)) != -1) {
          bos.write(data, 0, count);
        }
        bos.flush();
        bos.close();
        bis.close();
      }
      zipFile.close();
      String path = zipFile.getName();
      dirPath = path.substring(0, path.length() - 4);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return dirPath;
  }

}
