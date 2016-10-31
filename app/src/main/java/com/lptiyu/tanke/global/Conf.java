package com.lptiyu.tanke.global;


import com.lptiyu.tanke.utils.TimeUtils;

/**
 * author:wamcs
 * date:2016/5/25
 * email:kaili@hustunique.com
 */

public final class Conf {

    public static final String IMG_DISTINGUISH_ARRAY = "img_distinguish_url";

    public static final String IS_FIRST_IN_APP = "is_first_in_app";
    public static final String IS_FIRST_IN_IMAGE_DISTINGUISH_ACTIVITY = "is_first_in_image_distinguish";
    public static final String IS_FIRST_IN_GUESS_RIDDLE_ACTIVITY = "is_first_in_guess_riddle";
    public static final String IS_FIRST_IN_LOCATION_ACTIVITY = "is_first_in_location";
    public static final String IS_FIRST_IN_CAPTURE_ACTIVITY = "is_first_in_capture";
    public static final String IS_FIRST_IN_GAME_pLAYING2_ACTIVITY = "is_first_in_game_playing2";
    public static final String IS_FIRST_IN_POINT_TASK_ACTIVITY = "is_first_in_point_task";
    public static final String IS_FIRST_IN_SETTING_ACTIVITY = "is_first_in_setting";
    public static final String LATLNGS = "latlngs";
    /**
     * Sign up conf
     */
    public static final String SIGN_UP_CODE = "sign_up_code";
    public static final String SIGN_UP_TYPE = "sign_up_type";
    public static final int REGISTER_CODE = 1;
    public static final int RESET_PASSWORD_CODE = 2;
    public static final int BIND_TEL = 22;

    /**
     * Request Code For Start LocateUserActivity
     */
    public static final String CITY_STRUCT = "city_struct";
    public static final String CITY_NAME = "city_name";
    public static final String CITY_CODE = "city_code";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longtitude";
    public static final int RESULT_CODE_START_USER_LOCATE = 4;

    /**
     * City List File
     */
    public static final String HOT_CITY = "HotCity";
    public static final String DEFAULT_CITY_FILE_NAME = "city.json";

    /**
     * User Avatar conf
     */
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


    public static final String GAME_ID = "game_id";
    public static final String GAME_TYPE = "game_type";
    public static final String GAME_DETAIL = "game_detail";

    /**
     * game data activity
     */
    public static final String GAME_DISPLAY_ENTITY = "game_display_entity";
    public static final String GAME_PLAYING_ENTITY = "game_playing_entity";
    public static final String GAME_FINISHED_ENTITY = "game_finished_entity";

    /**
     * Message
     */

    public static final int TIME_TYPE = 11;
    public static final int MESSAGE_LIST_TYPE_OFFICIAL = -1;
    public static final int MESSAGE_LIST_TYPE_SYSTEM = -2;
    public static final long MESSAGE_LIST_USERID_OFFICIAL = -1L;
    public static final long MESSAGE_LIST_USERID_SYSTEM = -2L;
    public static final String MESSAGE_URL = "message_url";
    public static final String MESSAGE_TITLE = "message_title";
    public static final String PUSH_ACTION = "com.tanke.PUSH_ACTION";
    public static final String MESSAGE_TYPE = "message_type";

    /**
     * UserCenter
     */

    public static final String USER_DETAIL = "user_detail";
    public static final String DATA_TO_INFO_MODIFY = "data_to_user_info_modify";

    public static final String USER_INFO = "user_info";
    public static final int REQUEST_CODE_NICKNAME = 14;

    /**
     * ShareDialog
     */
    public static final String SHARE_TITLE = "share_title";
    public static final String SHARE_TEXT = "share_text";
    public static final String SHARE_IMG_URL = "share_img_url";
    public static final String SHARE_URL = "share_url";

    public static final long TEMP_GAME_ID = -1;

    public static final String POINT = "point";
    public static final String CLICK_INDEX = "click_index";
    public static final String INDEX = "index";
    public static final String POINT_LIST = "point_list";

    public static final String UNZIPPED_DIR = "unZippedDir";

    public static final String FROM_WHERE = "from_where";
    public static final String NORMAL_VIEW_HOLDER = "normal_view_holder";
    public static final String ELASTIC_HEADER_VIEW_HOLDER = "elastic_header_view_holder";
    public static final String GAME_PLAYing_V2_ACTIVITY = "game_playing_v2_activity";

    public static final String CURRENT_TASK = "current_task";
    public static final String IS_POINT_OVER = "is_point_over";
    public static final String UPLOAD_RECORD_RESPONSE = "upload_record_response";
    public static final String CONTENT = "content";
    public static final String BANNER_TITLE = "banner_title";
    public static final String PARENT_DIR = "parent_dir";
    public static final String TASK_IMG = "task_img";

    public static final String APP_NAME = "appName";
    public static final String PACKAGE_NAME = "packageName";
    public static final String VERSION_NAME = "versionName";
    public static final String VERSION_CODE = "versionCode";

    public static final String SORT_INDEX = "sort_index";

    public static final String HOME_HOT = "home_hot";
    public static final String HOME_TAB = "home_tab";
    public static final String RECOMMEND = "home_hot_entity";
    public static final String HOME_TAB_ENTITY = "home_tab_entity";

}
