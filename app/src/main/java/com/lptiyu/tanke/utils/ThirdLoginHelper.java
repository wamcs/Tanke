package com.lptiyu.tanke.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.lptiyu.tanke.MainActivity;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.io.net.UserService;
import com.lptiyu.tanke.pojo.UserDetails;
import com.lptiyu.tanke.pojo.UserEntity;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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

    private WeakReference<Activity> activityWeakReference;

    private static UserDetails Muser = new UserDetails();

    public static UserDetails getUserDetail() {
        return Muser;
    }

    public ThirdLoginHelper(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
    }

    public void oauthLogin(String platformName) {
        Platform platform = ShareSDK.getPlatform(context, platformName);
        platform.setPlatformActionListener(this);
        platform.SSOSetting(false);
        platform.showUser(null);
    }

    private void getQzoneUserInformation(HashMap<String, Object> hashMap) {
        Muser.setNickname(hashMap.get("nickname").toString());
        Muser.setSex(hashMap.get("gender").toString());
        Muser.setAvatar(hashMap.get("figureurl_qq_1").toString());
    }

    private void getWeiboUserInformation(HashMap<String, Object> hashMap) {
        Muser.setNickname(hashMap.get("name").toString());
        switch (hashMap.get("gender").toString()) {
            case "m":
                Muser.setSex("男");
                break;
            case "f":
                Muser.setSex("女");
                break;
        }
        Muser.setAvatar(hashMap.get("avatar_hd").toString());

    }

    private void getWechatUserInformation(HashMap<String, Object> hashMap) {
        Accounts.setOpenId(String.valueOf(hashMap.get("openid")));
        Muser.setNickname(hashMap.get("nickname").toString());
        switch (String.valueOf(hashMap.get("sex"))) {
            case "1":
                Muser.setSex("男");
                break;
            case "2":
                Muser.setSex("女");
                break;

        }
        Muser.setAvatar(hashMap.get("headimgurl").toString());
    }

    public void login(String id, final int platformType, int ostype, String avatar_url, String nick_name) {
        //        final int plantType = platformType;
        HttpService.getUserService().loginThirdParty(id, platformType, ostype, avatar_url, nick_name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<UserEntity>>() {
                    @Override
                    public void call(Response<UserEntity> userEntityResponse) {
                        int status = userEntityResponse.getStatus();
                        if (status != 1) {
                            ToastUtil.TextToast(userEntityResponse.getInfo());
                            return;
                        }
                        UserEntity entity = userEntityResponse.getData();

                        Activity activity = activityWeakReference.get();
                        Intent intentToSignUP = new Intent();

                        //                        if (entity.getIsNewUserThirdParty() == 1) {
                        //                            intentToSignUP.setClass(activity, SignUpActivity.class);
                        //                            intentToSignUP.putExtra(Conf.SIGN_UP_CODE, Conf.REGISTER_CODE);
                        //                            intentToSignUP.putExtra(Conf.SIGN_UP_TYPE, plantType);
                        //                        } else {
                        //                            Accounts.setId(entity.getUid());
                        //                            Accounts.setToken(entity.getToken());
                        //                            intentToSignUP.setClass(activity, MainActivity.class);
                        //                        }
                        Accounts.setId(entity.getUid());
                        Accounts.setToken(entity.getToken());
                        Accounts.setPlatform(platformType);
                        Accounts.setNickName(entity.getNickname());
                        Accounts.setAvatar(entity.getAvatar());
                        intentToSignUP.setClass(activity, MainActivity.class);
                        activity.startActivity(intentToSignUP);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtil.TextToast("第三方登录失败");
                    }
                });
    }

    private final int ANDROID = 1;

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

        PlatformDb db = platform.getDb();
        String id = db.getUserId();//openID
        String nick_name = db.getUserName();//nick_name
        String avatar_url = db.getUserIcon();//avatar_url
        //platform.getDb().getToken();
        if (platform.getName().equals(QZONE)) {
            getQzoneUserInformation(hashMap);
            login(id, UserService.USER_TYPE_QQ, ANDROID, avatar_url, nick_name);
            //            return;
        }

        if (platform.getName().equals(WEIBO)) {
            getWeiboUserInformation(hashMap);
            login(id, UserService.USER_TYPE_WEIBO, ANDROID, avatar_url, nick_name);
            //            return;
        }
        if (platform.getName().equals(WECHAT)) {
            getWechatUserInformation(hashMap);
            login(id, UserService.USER_TYPE_WEIXIN, ANDROID, avatar_url, nick_name);
        }

        if (callback != null) {
            callback.onSuccess();
        }
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        if (callback != null) {
            callback.onError();
        }
    }

    @Override
    public void onCancel(Platform platform, int i) {
        if (callback != null) {
            callback.onCancle();
        }
    }

    private OnThirdLoginCallback callback;

    public void setThirdLoginCallback(OnThirdLoginCallback callback) {
        this.callback = callback;
    }

    public interface OnThirdLoginCallback {
        void onSuccess();

        void onError();

        void onCancle();
    }
}
