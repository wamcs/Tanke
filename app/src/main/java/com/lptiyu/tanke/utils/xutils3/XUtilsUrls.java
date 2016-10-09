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

    //    String HOME_GAME_LIST = SERVER_URL_TEST + "home/Index";

    String HOME_SIGN_IN = SERVER_URL_TEST + "My/getSignupPoints";

    String HOME_BANNER_RECOMMEND = SERVER_URL_TEST + "Home/getBannerAndRecommend";

    String HOME_SORT_TAB = SERVER_URL_TEST + "Game/getGameCategory";

    String HOME_GAME_LIST_NEW = SERVER_URL_TEST + "Game/getGameList";
}
