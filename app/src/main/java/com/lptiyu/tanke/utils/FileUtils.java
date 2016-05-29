package com.lptiyu.tanke.utils;


import com.file.zip.ZipEntry;
import com.file.zip.ZipFile;
import com.google.gson.stream.JsonReader;
import com.lptiyu.tanke.global.AppData;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Enumeration;


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
      ZipFile zipFile = new ZipFile(fileName, "GBK");
      Enumeration emu = zipFile.getEntries();
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
      dirPath = fileName.substring(0, fileName.length() - 4);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return dirPath;
  }

  public static String readFileByLine(String filePath) {
    return readFileByLine(new File(filePath));
  }

  public static String readFileByLine(File file) {
    StringBuilder resultBuilder = new StringBuilder("");
    String encoding = "GBK";
    if (file.isFile() && file.exists()) {
      try {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file), encoding);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        while ((line = br.readLine()) != null) {
          resultBuilder.append(line);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return resultBuilder.toString();
  }

  public static <T> T parseJsonFile(String filePath, Class<T> clazz) {
    File file = new File(filePath);
    return parseJsonFile(file, clazz);
  }

  public static <T> T parseJsonFile(File file, Class<T> clazz) {
    T result = null;
    try {
      InputStream is = new FileInputStream(file);
      InputStreamReader isr = new InputStreamReader(is);
      result = AppData.globalGson().fromJson(new JsonReader(isr), clazz);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  public static boolean isFileExist(String filePath) {
    File file = new File(filePath);
    return file.exists();
  }

}
