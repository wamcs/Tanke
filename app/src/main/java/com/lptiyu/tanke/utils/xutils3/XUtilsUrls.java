package com.lptiyu.tanke.utils.xutils3;

import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.net.HttpService;

/**
 * Created by Jason on 2016/8/11.
 */
public class XUtilsUrls {
    // 从SharedPreference中未获取到ip时返回的默认值
    public static String DEFAULT_IP = "http://121.43.96.124/";
    public static String TEST_PART = "/lepao/api.php/";
    public static String FORMAL_PART = "/lepao2/api.php/";
    //服务器类型，1-测试服务器，2-正式服务器
    public static int SERVICE_TYPE = 2;//正式打包时需要改为2
    public static String FORMAL_GET_IP_BY_DOMAIN = "http://api.lptiyu.com/lepao2/api.php/System/getIp";
    public static String TEST_GET_IP_BY_DOMAIN = "http://test.lptiyu.com/lepao/api.php/System/getIp";
    public static String SERVICE_IP = DEFAULT_IP + TEST_PART;//给一个初始默认值

    private XUtilsUrls() {
    }

    public static void setServiceIP(String ip) {
        if (SERVICE_TYPE == 1) {
            SERVICE_IP = ip + TEST_PART;
        } else {
            SERVICE_IP = ip + FORMAL_PART;
        }
        XUtilsUrls.init();
        HttpService.init();
    }

    private static void init() {
        BIND_PHONE = SERVICE_IP + "login/bind_phone";
        FEED_BACK = SERVICE_IP + "system/feedback";
        HOME_SIGN_IN = SERVICE_IP + "My/getSignupPoints";
        HOME_BANNER_RECOMMEND = SERVICE_IP + "Home/getBannerAndRecommend";
        HOME_SORT_TAB = SERVICE_IP + "Game/getGameCategory";
        HOME_GAME_LIST_NEW = SERVICE_IP + "Game/getGameList";
        ENTER_GAME = SERVICE_IP + "system/join_game";
        LEAVE_GAME = SERVICE_IP + "system/leave_game";
        GET_RECORD = SERVICE_IP + "system/GetRankslog";
        UPLOAD_RECORD = SERVICE_IP + "system/Rankslog";
        STOP_RUN = SERVICE_IP + "Run/stopRun";
        START_RUN = SERVICE_IP + "Run/startRun";
        RUN_LINE = SERVICE_IP + "Run/getRunLine";
        LOG_RUN = SERVICE_IP + "Run/logRun";
        GAME_DETAIL = SERVICE_IP + "home/details";
        MESSAGE = SERVICE_IP + "System/News";
        GAME_OVER_REWARD = SERVICE_IP + "Game/getTotalPrize";
        GET_SCORE_AFTER_SHARE = SERVICE_IP + "Game/getSharePoints";
        UPLOAD_DR_FILE = SERVICE_IP + "Run/uploadRunRecord";
        DR_RECORD_LIST = SERVICE_IP + "Run/recordList";
        DR_RECORD_DETAIL = SERVICE_IP + "Run/recordDetail";

        if (SERVICE_TYPE == 1) {
            REQUEST_RED_WALLET = "http://test.lptiyu.com/lepao/index" +
                    ".php/Cash/Cash/uid/" + Accounts.getId() + "/token/" + Accounts.getToken();
            LOOK_RED_WALLET_RECORD = "http://test.lptiyu.com/lepao/index" +
                    ".php/Cash/Record/uid/" + Accounts.getId() + "/token" + Accounts.getToken();
        } else {
            REQUEST_RED_WALLET = "http://api.lptiyu.com/lepao2/index.php/Cash/Cash/uid/" + Accounts.getId() +
                    "/token/" + Accounts.getToken() +
                    "/ECCBFF1967E920782C54DC8DFCE35BE4";
            LOOK_RED_WALLET_RECORD = "http://api.lptiyu.com/lepao2/index" +
                    ".php/Cash/record/uid/" + Accounts.getId() + "/token/" + Accounts.getToken();
        }
    }

    public static String BIND_PHONE;

    public static String FEED_BACK;
    public static String HOME_SIGN_IN;
    public static String HOME_BANNER_RECOMMEND;
    public static String HOME_SORT_TAB;
    public static String HOME_GAME_LIST_NEW;
    public static String ENTER_GAME;
    public static String LEAVE_GAME;
    public static String GET_RECORD;
    public static String UPLOAD_RECORD;
    public static String STOP_RUN;
    public static String START_RUN;
    public static String RUN_LINE;
    public static String LOG_RUN;
    public static String GAME_DETAIL;
    public static String MESSAGE;
    public static String GAME_OVER_REWARD;
    public static String GET_SCORE_AFTER_SHARE;
    public static String UPLOAD_DR_FILE;
    public static String DR_RECORD_LIST;
    public static String DR_RECORD_DETAIL;
    public static String REQUEST_RED_WALLET;
    public static String LOOK_RED_WALLET_RECORD;
}
