package com.lptiyu.tanke.utils;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-19
 *         email: wonderfulifeel@gmail.com
 */
public class ExpUtils {

  private static final int MIN_LEVEL = 0;
  private static final int MAX_LEVEL = 100;

  private static final int BASE_EXP = 50;

  public static int calculateCurrentLevel(int exp) {
    if (exp <= 0) {
      return MIN_LEVEL;
    }
    if (exp < 50) {
      return 1;
    }
    int level = 0;
    while (calculateExpByLevel(level) < exp) {
      level++;
    }
    return level - 1;
  }

  public static int calculateExpByLevel(int level) {
    if (level <= 0) {
      return 0;
    }
    if (level == 1) {
      return 1;
    }
    int temp = level * (level - 1) / 2;
    return temp * BASE_EXP;
  }

}
