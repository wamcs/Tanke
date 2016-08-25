package com.lptiyu.tanke.gameplaying.parser;

import java.util.HashSet;

/**
 * author:wamcs
 * date:2016/7/7
 * email:kaili@hustunique.com
 */
public class Tag {

  private static HashSet<String> firstTags;
  private static HashSet<String> secondTags;
  private static HashSet<String> thirdTags;

  //为了方便Scanner检验
  public static final int FIRST_RANK = 0;
  public static final int SECOND_RANK = 2;
  public static final int THIRD_RANK = 3;
  public static final int NOT_EXIST = Integer.MIN_VALUE;

  public static final String ROOT_TAG = "tanke";
  public static final String PARAGRAPH_TAG = "p";
  public static final String VIDEO_TAG = "video";
  public static final String AUDIO_TAG = "audio";
  public static final String IMG_TAG = "img";
  public static final String BOLD_TAG = "b";
  public static final String LINE_FEED_TAG = "br";
  public static final String NORMAL_TAG = "n";

  static {
    firstTags = new HashSet<>();
    secondTags = new HashSet<>();
    thirdTags = new HashSet<>();
    firstTags.add(ROOT_TAG);
    secondTags.add(PARAGRAPH_TAG);
    secondTags.add(VIDEO_TAG);
    secondTags.add(AUDIO_TAG);
    secondTags.add(IMG_TAG);
    thirdTags.add(BOLD_TAG);
    thirdTags.add(LINE_FEED_TAG);
    thirdTags.add(NORMAL_TAG);
  }

  public static int checkTag(String tag){
    if (firstTags.contains(tag)){
      return FIRST_RANK;
    }

    if (secondTags.contains(tag)){
      return SECOND_RANK;
    }

    if (thirdTags.contains(tag)){
      return THIRD_RANK;
    }

    return NOT_EXIST;
  }

}
