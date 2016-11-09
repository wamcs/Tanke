package com.lptiyu.tanke.utils;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * author:wamcs
 * date:2016/5/5
 * email:kaili@hustunique.com
 */
public class ShareHelper {

    public static final int SHARE_WECHAT_FRIENDS = 1;
    public static final int SHARE_WECHAT_CIRCLE = 2;
    public static final int SHARE_QQ = 3;
    public static final int SHARE_WEIBO = 4;

    private static final String DEFAULT_TITLE = "我是分享标题";
    private static final String DEFAUTL_TEXT = "我是分享文本";

    public static void share(int platform, String title, String text, String imagePath, String shareUrl) {
        switch (platform) {
            case SHARE_QQ:
                shareQQ(title, text, imagePath, shareUrl);
                break;
            case SHARE_WEIBO:
                shareWeibo(title, text, imagePath, shareUrl);
                break;
            case SHARE_WECHAT_FRIENDS:
                shareWechatFriends(title, text, imagePath, shareUrl);
                break;
            case SHARE_WECHAT_CIRCLE:
                shareWechatCircle(title, text, imagePath, shareUrl);
                break;
            default:
                break;
        }
    }


    private static void shareWeibo(String title, String text, String imagePath, String shareUrl) {
        Platform.ShareParams shareParams = new Platform.ShareParams();
        shareParams.setText(text);
        shareParams.setImageUrl(imagePath);
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.setPlatformActionListener(new Listener());
        weibo.share(shareParams);
    }

    /**
     * @param title     title of the content which will be shared
     * @param text      text of the content which will be shared
     * @param imagePath this imageUrl will be shared
     * @param shareUrl  after user clicking,what the url point to will be opened
     */
    private static void shareQQ(String title, String text, String imagePath, String shareUrl) {

        Platform.ShareParams shareParams = new Platform.ShareParams();
        shareParams.setTitle(title);
        shareParams.setText(text);
        shareParams.setImageUrl(imagePath);
        if (null != shareUrl) {
            shareParams.setTitleUrl(shareUrl);
        }
        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        qq.setPlatformActionListener(new Listener());
        qq.share(shareParams);

    }


    private static void shareWechatFriends(String title, String text, String imagePath, String shareUrl) {
        Platform.ShareParams shareParams = new Platform.ShareParams();
        if (null != shareUrl) {
            shareParams.setShareType(Platform.SHARE_WEBPAGE);
            shareParams.setUrl(shareUrl);
        } else {
            shareParams.setShareType(Platform.SHARE_IMAGE);
        }
        shareParams.setTitle(title);
        shareParams.setText(text);
        shareParams.setImageUrl(imagePath);
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        wechat.setPlatformActionListener(new Listener());
        wechat.share(shareParams);
    }

    private static void shareWechatCircle(String title, String text, String imagePath, String shareUrl) {
        Platform.ShareParams shareParams = new Platform.ShareParams();
        if (null != shareUrl) {
            shareParams.setShareType(Platform.SHARE_WEBPAGE);
            shareParams.setUrl(shareUrl);
        } else {
            shareParams.setShareType(Platform.SHARE_IMAGE);
        }
        shareParams.setTitle(title);
        shareParams.setText(text);
        shareParams.setImageUrl(imagePath);
        Platform wechat = ShareSDK.getPlatform(WechatMoments.NAME);
        wechat.setPlatformActionListener(new Listener());
        wechat.share(shareParams);
    }

    public static void shareImage(int platform, String imagePath, PlatformActionListener listener) {
        switch (platform) {
            case SHARE_QQ:
                shareLocalImage(QQ.NAME, imagePath, listener);
                break;
            case SHARE_WEIBO:
                shareLocalImage(SinaWeibo.NAME, imagePath, listener);
                break;
            case SHARE_WECHAT_FRIENDS:
                shareLocalImage(Wechat.NAME, imagePath, listener);
                break;
            case SHARE_WECHAT_CIRCLE:
                shareLocalImage(WechatMoments.NAME, imagePath, listener);
                break;
            default:
                break;
        }
    }

    private static void shareLocalImage(String platform, String imagepath, PlatformActionListener listener) {
        Platform.ShareParams shareParams = new Platform.ShareParams();
        shareParams.setImagePath(imagepath);
        Platform plat = ShareSDK.getPlatform(platform);
        plat.setPlatformActionListener(listener);
        plat.share(shareParams);
    }

    private static class Listener implements PlatformActionListener {

        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            //            if (callback != null) {
            //                callback.onComplete(platform, i, hashMap);
            //            }
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            //            if (callback != null) {
            //                callback.onError(platform, i, throwable);
            //            }
        }

        @Override
        public void onCancel(Platform platform, int i) {
            //            if (callback != null) {
            //                callback.onCancel(platform, i);
            //            }
        }
    }

    //    public interface OnShareCallback {
    //        void onComplete(Platform platform, int i, HashMap<String, Object> hashMap);
    //
    //        void onError(Platform platform, int i, Throwable throwable);
    //
    //        void onCancel(Platform platform, int i);
    //    }
}
