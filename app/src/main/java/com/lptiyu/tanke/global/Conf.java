package com.lptiyu.tanke.global;


import com.lptiyu.tanke.utils.TimeUtils;

/**
 * author:wamcs
 * date:2016/5/25
 * email:kaili@hustunique.com
 */

public final class Conf {

    /**
     * Sign up conf
     */
    public static final String SIGN_UP_CODE = "sign_up_code";
    public static final String SIGN_UP_TYPE = "sign_up_type";
    public static final int REGISTER_CODE = 1;
    public static final int RESET_PASSWORD_CODE = 2;

    /**
     * Third login conf
     */
    public static final String USER_NAME = "user_name";
    public static final String USER_AVATAR = "user_avatar";
    public static final String USER_GENDER = "user_gender";

    /**
     * User Avatar conf
     */
    public static final int RESULT_CODE_CANCEL = 0;
    public static final int REQUEST_CODE_TAKE_PHOTO = 1;
    public static final int REQUEST_CODE_GALLERY = 2;
    public static final int REQUEST_CODE_CUT_PHOTO = 3;
    public static final String TEMP_AVATAR_NAME = "temp_user_avatar.png";
    public static final int USER_AVATAR_SIZE = 320;

    /**
     * user information*/

    public static final int MAX_HEIGHT = 210;
    public static final int MIN_HEIGHT = 150;
    public static final int MAX_WEIGHT = 100;
    public static final int MIN_WEIGHT = 40;

    public static final int CURRENT_YEAR = TimeUtils.getCurrentYear();
    public static final int MAX_YEAR = CURRENT_YEAR + 10;
    public static final int MAX_BIIRTHDAY_YEAR  = CURRENT_YEAR - 5;
    public static final int MIN_BIRTHDAY_YEAR = 1950;
    public static final int MAX_MONTH = 12;
    public static final int MIN_MONTH = 1;
    public static final int FEB_MONTH = 2;
    public static final int NORMAL_BIG_MONTH_MAX_DAY = 31;
    public static final int NORMAL_SMALL_MONTH_MAX_DAY = 30;
    public static final int FEB_BIG_MONTH_MAX_DAY = 29;
    public static final int FEB_SMALL_MONTH_MAX_DAY = 28;
    public static final int MIN_DAY = 1;
    public static final int MAX_HOUR = 23;
    public static final int MIN_HOUR = 0;
    public static final int MAX_MINUTE = 59;
    public static final int MIN_MINUTE = 0;

    /**
     * Protocol Area
     */
    public static final String PROTOCOL_URL = "protocol_url";
    public static final int PROTOCOL_CODE = 1;
    public static final String PROTOCOL_STATE = "protocol_state";


  /**
   * game playing activity
   */
  public static final float LOCATION_DISTANCE_THRESHOLD_BOTTOM = 0.5f;
  public static final float LOCATION_DISTANCE_THRESHOLD_TOP = 10.0f;
  public static final double POINT_RADIUS = 10.0;

  public static final String CLICKED_POINT = "clicked_point";
  public static final String TEAM_ID = "team_id";
  public static final String GAME_ID = "game_id";
  public static final String MULTIPLY_FRAGMENT_BUNDLE_INDEX = "multiply_fragment_bundle_index";
  public static final int REQUEST_CODE_TASK_ACTIVITY = 6;
  public static final int RESULT_CODE_TASK_ACTIVITY = 7;
  public static final String IS_POINT_TASK_ALL_FINISHED = "is_point_task_all_finished";


}
