package com.lptiyu.tanke.utils.xutils3;

/**
 * Created by Jason on 2016/8/11.
 */
public interface XUtilsUrls {
    // 正式URL
    //    String SERVER_URL = "http://api.lptiyu.com/lepao/api.php/";
    // 测试URL
    String SERVER_URL = "http://test.lptiyu.com/lepao/api.php/";

    String GET_BANNER = SERVER_URL + "System/get_banner";
    String BIND_PHONE = SERVER_URL + "login/bind_phone";
    String FEED_BACK = SERVER_URL + "system/feedback";
    String HOME_SIGN_IN = SERVER_URL + "My/getSignupPoints";
    String HOME_BANNER_RECOMMEND = SERVER_URL + "Home/getBannerAndRecommend";
    String HOME_SORT_TAB = SERVER_URL + "Game/getGameCategory";
    String HOME_GAME_LIST_NEW = SERVER_URL + "Game/getGameList";
    String ENTER_GAME = SERVER_URL + "system/join_game";
    String LEAVE_GAME = SERVER_URL + "system/leave_game";
    String GET_RECORD = SERVER_URL + "system/GetRankslog";
    String UPLOAD_RECORD = SERVER_URL + "system/Rankslog";
    String STOP_RUN = SERVER_URL + "Run/stopRun";
    String START_RUN = SERVER_URL + "Run/startRun";
    String RUN_LINE = SERVER_URL + "Run/getRunLine";
    String LOG_RUN = SERVER_URL + "Run/logRun";
    String GAME_DETAIL = SERVER_URL + "home/details";
    String MESSAGE = SERVER_URL + "System/News";
    String GAME_OVER_REWARD = SERVER_URL + "Game/getTotalPrize";
    String GET_SCORE_AFTER_SHARE = SERVER_URL + "Game/getSharePoints";

    String REQUEST_RED_WALLET = "http://test.lptiyu.com/lepao/index" +
            ".php/Cash/Cash/uid/1/token/ECCBFF1967E920782C54DC8DFCE35BE4";
    String LOOK_RED_WALLET_RECORD = "http://test.lptiyu.com/lepao/index" +
            ".php/Cash/Record/uid/1/token/ECCBFF1967E920782C54DC8DFCE35BE4";
}
