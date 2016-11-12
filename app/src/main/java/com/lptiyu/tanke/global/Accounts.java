package com.lptiyu.tanke.global;


import com.lptiyu.tanke.utils.ShaPrefer;
import com.lptiyu.tanke.utils.xutils3.XUtilsUrls;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 15/12/5
 *
 * @author ldx
 */
public class Accounts {

    public static void setPhoneNumber(String number) {
        ShaPrefer.put("user_phone", number);
    }

    public static void setHaveDRData(boolean isHaveDRData) {
        ShaPrefer.put("have_dr_data", isHaveDRData);
    }

    public static boolean isHaveDRData() {
        return ShaPrefer.getBoolean("have_dr_data", false);
    }

    public static void setCrashLog(boolean isLogExist) {
        ShaPrefer.put("is_crash_log_exist", isLogExist);
    }

    public static String getPhoneNumber() {
        return ShaPrefer.getString("user_phone", "");
    }

    public static long getId() {
        return ShaPrefer.getLong("user_id", 0L);
    }

    public static void setId(long s) {
        ShaPrefer.put("user_id", s);
    }

    public static String getIP() {
        return ShaPrefer.getString("ip", XUtilsUrls.DEFAULT_IP);
    }

    public static void setIP(String s) {
        ShaPrefer.put("ip", s);
    }

    public static String getInstallationId() {
        return ShaPrefer.getString("installationId", "");
    }

    public static void setInstallationId(String installationId) {
        ShaPrefer.put("installationId", installationId);
    }

    public static String getToken() {
        return ShaPrefer.getString("user_token", "");
    }

    public static void setToken(String s) {
        ShaPrefer.put("user_token", s);
    }

    public static String getNickName() {
        return ShaPrefer.getString("user_nickname", "");
    }

    public static void setNickName(String s) {
        ShaPrefer.put("user_nickname", s);
    }

    public static String getAvatar() {
        return ShaPrefer.getString("user_avatar_url", "");
    }

    public static void setAvatar(String s) {
        ShaPrefer.put("user_avatar_url", s);
    }

    public static String getOpenId() {
        return ShaPrefer.getString("user_openId", "");
    }

    public static void setOpenId(String openId) {
        ShaPrefer.put("user_openId", openId);
    }

    public static int getPlatform() {
        return ShaPrefer.getInt("platform", -1);
    }

    public static void setPlatform(int platform) {
        ShaPrefer.put("platform", platform);
    }

    public static boolean isLogin() {
        long key = getId();
        return (key != 0L);
    }

    public static void logOut() {
        setId(0L);
    }

    public static void setSignUp(boolean isSignUp) {
        ShaPrefer.put("is_sign_up", isSignUp);
    }

    public static boolean isSignUp() {
        return ShaPrefer.getBoolean("is_sign_up", false);
    }

    public static void setShareScoreGot(boolean isShareScoreGot) {
        ShaPrefer.put("is_share_score_got", isShareScoreGot);
    }

    public static boolean isShareScoreGot() {
        return ShaPrefer.getBoolean("is_share_score_got", false);
    }

    public static void setDayIndex(int index) {
        ShaPrefer.put("day_index", index);
    }

    public static int getDayIndex() {
        return ShaPrefer.getInt("day_index", -1);
    }

    public static void setCityCode(String cityCode) {
        ShaPrefer.put("city_code", cityCode);
    }

    public static String getCityCode() {
        return ShaPrefer.getString("city_code", "027");//027是高德地图的武汉编码(我去，这里居然不返回默认值)
    }

    public static void setCity(String city) {
        ShaPrefer.put("city_name", city);
    }

    public static String getCity() {
        return ShaPrefer.getString("city_name", "武汉");
    }

    public static void setLatitude(float latitude) {
        ShaPrefer.put("latitude", latitude);
    }

    public static float getLatitude() {
        return ShaPrefer.getFloat("latitude", 30.516670f);
    }

    public static void setLongitude(float longitude) {
        ShaPrefer.put("longtitude", longitude);
    }

    public static float getLongitude() {
        return ShaPrefer.getFloat("longtitude", 114.316670f);
    }

    /*
    武汉的经纬度
    * 114.31667,30.51667
    * */
}
