package com.lptiyu.tanke.global;


import com.lptiyu.tanke.utils.TimeUtils;

/**
 * author:wamcs
 * date:2016/5/25
 * email:kaili@hustunique.com
 */

public final class Conf {

    public static final String IMG_DISTINGUISH_URL = "img_distinguish_url";

    public static final String IS_FIRST_IN_APP = "is_first_in_app";
    /**
     * Sign up conf
     */
    public static final String SIGN_UP_CODE = "sign_up_code";
    public static final String SIGN_UP_TYPE = "sign_up_type";
    public static final int REGISTER_CODE = 1;
    public static final int RESET_PASSWORD_CODE = 2;

    /**
     * Request Code For Start LocateUserActivity
     */
    public static final String CITY_STRUCT = "city_struct";
    public static final String CITY_NAME = "city_name";
    public static final String CITY_CODE = "city_code";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final int REQUEST_CODE_START_USER_LOCATE = 3;
    public static final int RESULT_CODE_START_USER_LOCATE = 4;

    /**
     * City List File
     */
    public static final String HOT_CITY = "HotCity";
    public static final String DEFAULT_CITY_FILE_NAME = "city.json";

    /**
     * User Avatar conf
     */
    public static final int RESULT_CODE_CANCEL = 5;
    public static final int REQUEST_CODE_TAKE_PHOTO = 6;
    public static final int REQUEST_CODE_GALLERY = 7;
    public static final int REQUEST_CODE_CUT_PHOTO = 8;
    public static final String TEMP_AVATAR_NAME = "temp_user_avatar.png";
    public static final int USER_AVATAR_SIZE = 320;

    /**
     * user information
     */

    public static final int MAX_HEIGHT = 210;
    public static final int MIN_HEIGHT = 150;
    public static final int MAX_WEIGHT = 100;
    public static final int MIN_WEIGHT = 40;

    public static final int CURRENT_YEAR = TimeUtils.getCurrentYear();
    public static final int MAX_YEAR = CURRENT_YEAR + 10;
    public static final int MAX_BIIRTHDAY_YEAR = CURRENT_YEAR - 5;
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
    public static final String PROTOCOL_STATE = "protocol_state";


    /**
     * game playing activity
     */
    public static final float LOCATION_DISTANCE_THRESHOLD_BOTTOM = 1.0f;
    public static final float LOCATION_DISTANCE_THRESHOLD_TOP = 20.0f;
    public static final double POINT_RADIUS = 50.0;
    public static final double LOCATE_TASK_POINT_RADIUS = 10.0;

    public static final String CLICKED_POINT = "clicked_point";
    public static final String TEAM_ID = "team_id";
    public static final String GAME_ID = "game_id";
    public static final String LINE_ID = "line_id";
    public static final String GAME_TYPE = "game_type";
    public static final String GAME_DETAIL = "game_detail";
    public static final int REQUEST_CODE_TASK_ACTIVITY = 9;
    public static final int RESULT_CODE_TASK_ACTIVITY = 10;

    public static final String GAME_ACTIVITY_FINISH_TYPE = "game_activity_finish_type";
    public static final String TIMING_TASK = "timing_task";
    public static final String IS_POINT_TASK_ALL_FINISHED_INDEX = "is_point_task_all_finished_index";
    public static final String IS_POINT_TASK_ALL_FINISHED = "is_point_task_all_finished";

    /**
     * game data activity
     */
    public static final String GAME_POINTS = "game_points";
    public static final String GAME_RECORDS = "game_records";
    public static final String GAME_DISPLAY_ENTITY = "game_display_entity";
    public static final String GAME_PLAYING_ENTITY = "game_playing_entity";
    public static final String GAME_FINISHED_ENTITY = "game_finished_entity";
    public static final String LIST_POINTS = "list_points";

    /**
     * Message
     */

    public static final int TIME_TYPE = 11;
    public static final int MESSAGE_LIST_TYPE_OFFICIAL = -1;
    public static final int MESSAGE_LIST_TYPE_SYSTEM = -2;
    public static final int MESSAGE_LIST_TYPE_USER = -3;
    public static final long MESSAGE_LIST_USERID_OFFICIAL = -1L;
    public static final long MESSAGE_LIST_USERID_SYSTEM = -2L;
    public static final String MESSAGE_URL = "message_url";
    public static final String MESSAGE_TITLE = "message_title";
    public static final String PUSH_ACTION = "com.tanke.PUSH_ACTION";
    public static final String MESSAGE_ACTION = "com.tanke.MESSAGE_ACTION";
    public static final String PUSH_MESSAGE = "push_message";
    public static final String USER_MESSAGE = "user_message";
    public static final String MESSAGE_TYPE = "message_type";

    /**
     * UserCenter
     */

    public static final String USER_DETAIL = "user_detail";
    public static final String DATA_TO_INFO_MODIFY = "data_to_user_info_modify";

    public static final String USER_LOCATION = "user_location";
    public static final String USER_INFO_TYPE = "user_info_type";
    public static final String USER_INFO = "user_info";
    public static final int REQUEST_CODE_NICKNAME = 14;
    public static final int REQUEST_CODE_LOCATION = 17;

    public static final String DEFAULT_CITY_ASSETS = "city.json";

    /**
     * ShareDialog
     */
    public static final String SHARE_TITLE = "share_title";
    public static final String SHARE_TEXT = "share_text";
    public static final String SHARE_IMG_URL = "share_img_url";
    public static final String SHARE_URL = "share_url";

    /**
     * ClueDisplay
     */
    public static final String CLUE_DISPLAY_TASK_TYPE = "clue_task_type";
    public static final String CLUE_DISPLAY_TASK_PWD = "clue_task_pwd";
    public static final int REQUEST_CODE_BEGIN_DECODE = 11;
    public static final int RESULT_CODE_INPUT_PASSWORD = 12;
    public static final int RESULT_CODE_SCAN_CODE = 13;
    public static final int RESULT_CODE_RECOGNISE_IMAGE = 14;
    public static final int RESULT_CODE_UPLOAD_IMAGE = 15;
    public static final int RESULT_CODE_CHECK_POSITION = 16;


    /**
     * This method is to make up the entity name of baidu hawk eye service
     *
     * @param gameId
     * @param teamId
     * @return
     */
    public static String makeUpTrackEntityName(long gameId, long teamId) {
        return String.format("%d_%d".toLowerCase(), gameId, teamId);
    }

    public static final long TEMP_GAME_ID = -1;
    public static final long TEMP_LINE_ID = -1;
    public static final long TEMP_TEAM_ID = -1;

    public static final String JOIN_TIME = "join_time";
    public static final String START_TIME = "start_time";

}
