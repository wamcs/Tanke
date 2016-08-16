package com.lptiyu.tanke.global;


import com.lptiyu.tanke.utils.ShaPrefer;

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

    public static boolean isLogin() {
        long key = getId();
        return (key != 0L);
    }

    public static void logOut() {
        setId(0L);
    }
}
