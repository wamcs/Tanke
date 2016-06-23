package com.lptiyu.tanke.utils;

import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.pojo.GameDisplayEntity;

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

  @Test
  public void testJson() throws Exception {
    String testJson = "{\"id\":\"17\",\"pic\":\"http:\\/\\/test.360guanggu.com\\/run\\/Public\\/Upload\\/pic\\/backimg\\/2016-06-07\\/thumb_575694a0d76e6.jpg\",\"title\":\"\\u534e\\u79d1\\u4e1c\\u6821\\u533a\\u5d1b\\u8d77\\u4e4b\\u8def\",\"area\":\"\\u534e\\u79d1\\u542f\\u660e\\u5b66\\u9662\",\"city\":\"1\",\"time_type\":\"0\",\"start_date\":\"\",\"end_date\":\"\",\"start_time\":\"\",\"end_time\":\"\",\"state\":4,\"type\":0,\"recommend\":1}";
    GameDisplayEntity entity = AppData.globalGson().fromJson(testJson, GameDisplayEntity.class);
    System.out.println(AppData.globalGson().toJson(entity));
  }

  @Test
  public void testExpUtils() {
      System.out.println(ExpUtils.calculateCurrentLevel(101));
  }
}