package com.lptiyu.tanke.utils;

import org.junit.Test;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-29
 *         email: wonderfulifeel@gmail.com
 */
public class FileUtilsTest {

  @Test
  public void testUnzipFile() throws Exception {
    FileUtils.unzipFile("./1000000001_2000000001_1464075128.zip", "./");
  }
}