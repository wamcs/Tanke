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
    FileUtils.unzipFile("src/test/res/步道最新切图.zip", "src/test/res//");
  }
}