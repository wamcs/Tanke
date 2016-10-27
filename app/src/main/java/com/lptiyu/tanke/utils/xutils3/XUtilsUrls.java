package com.lptiyu.tanke.utils.xutils3;

/**
 * Created by Jason on 2016/8/11.
 */
public interface XUtilsUrls {
    // 正式URL
    //    String SERVER_URL_OFFICIAL = "http://api.lptiyu.com/lepao/api.php/";

    // 测试URL
    String SERVER_URL_TEST = "http://test.lptiyu.com/lepao/api.php/";

    String GET_BANNER = SERVER_URL_TEST + "System/get_banner";

    String BIND_PHONE = SERVER_URL_TEST + "login/bind_phone";

    String FEED_BACK = SERVER_URL_TEST + "system/feedback";

    String HOME_SIGN_IN = SERVER_URL_TEST + "My/getSignupPoints";

    String HOME_BANNER_RECOMMEND = SERVER_URL_TEST + "Home/getBannerAndRecommend";

    String HOME_SORT_TAB = SERVER_URL_TEST + "Game/getGameCategory";

    String HOME_GAME_LIST_NEW = SERVER_URL_TEST + "Game/getGameList";

    String ENTER_GAME = SERVER_URL_TEST + "system/join_game";

    String LEAVE_GAME = SERVER_URL_TEST + "system/leave_game";

    String GET_RECORD = SERVER_URL_TEST + "system/GetRankslog";

    String UPLOAD_RECORD = SERVER_URL_TEST + "system/Rankslog";

    String STOP_RUN = SERVER_URL_TEST + "Run/stopRun";

    String START_RUN = SERVER_URL_TEST + "Run/startRun";

    String RUN_LINE = SERVER_URL_TEST + "Run/getRunLine";

    String LOG_RUN = SERVER_URL_TEST + "Run/logRun";

    String GAME_DETAIL = SERVER_URL_TEST + "home/details";
}
