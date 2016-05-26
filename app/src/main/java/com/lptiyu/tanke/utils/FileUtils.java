package com.lptiyu.tanke.utils;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import timber.log.Timber;


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

  public static String readFileByChar(File file) {
    if (file == null || !file.exists()) {
      Timber.e("target file is illegal");
      return "";
    }
    StringBuilder jsonStringBuilder = new StringBuilder();
    try {
      FileInputStream fis = new FileInputStream(file);
      int tempChar;
      while ((tempChar = fis.read()) != -1) {
        // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
        // 但如果这两个字符分开显示时，会换两次行。
        // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
        if (((char) tempChar) != '\r') {
          jsonStringBuilder.append((char) tempChar);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (jsonStringBuilder.length() == 0) {
      return "";
    }
    return jsonStringBuilder.toString();
  }


  public static String readFileByChar(String filePath) {
    return readFileByChar(new File(filePath));
  }

  public static boolean isFileExist(String filePath) {
    File file = new File(filePath);
    return file.exists();
  }

}
