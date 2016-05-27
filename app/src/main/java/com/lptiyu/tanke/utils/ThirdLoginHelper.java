package com.lptiyu.tanke.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lptiyu.tanke.global.AppData;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * author:wamcs
 * date:2016/5/5
 * email:kaili@hustunique.com
 */
public class ThirdLoginHelper implements PlatformActionListener {

    public static final String QZONE = QZone.NAME;
    public static final String WECHAT = Wechat.NAME;
    public static final String WEIBO = SinaWeibo.NAME;

    private static Context context = AppData.getContext();

    public void oauthLogin(String platformName) {
        Platform platform = ShareSDK.getPlatform(context, platformName);
        platform.setPlatformActionListener(this);
        platform.SSOSetting(false);
        platform.showUser(null);
    }

    private void getQzoneUserInformation(HashMap<String, Object> hashMap) {
        Log.d("lk","qzone login "+hashMap.toString());
        // name:nickname
        // gender:gender("男","女")
        // location: province city
        // avatar: figureurl

    }

    private void getWeiboUserInformation(HashMap<String, Object> hashMap) {
        Log.d("lk","weibo login "+hashMap.toString());
        // name:name
        // gender:gender("m","f","n")
        // location: location
        // avatar: avatar_hd

    }

    private void getWechatUserInformation(HashMap<String, Object> hashMap) {
        Log.d("lk","weixin login "+hashMap.toString());
        // name:nickname
        // gender:sex("1(m)","2(f)")
        // location: province city
        // avatar: headimgurl


    }

    public void login(String id,String platformName,HashMap<String, Object> hashMap) {
        //TODO:send message to server,turn activity
    }


    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

        String id = platform.getDb().getUserId();//openID
        //platform.getDb().getToken();
        Log.d("lk","third login success");
        if (platform.getName().equals(QZONE))
            getQzoneUserInformation(hashMap);
        if (platform.getName().equals(WEIBO))
            getWeiboUserInformation(hashMap);
        if (platform.getName().equals(WECHAT))
            getWechatUserInformation(hashMap);
        login(id,platform.getName(),hashMap);
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {

    }

    @Override
    public void onCancel(Platform platform, int i) {

    }
}
