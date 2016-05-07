package com.lptiyu.tanke.base;

import android.app.Application;

import com.lptiyu.tanke.utils.AppData;

import cn.sharesdk.framework.ShareSDK;

/**
 * author:wamcs
 * date:2016/5/5
 * email:kaili@hustunique.com
 */
public class TankeApplication extends Application {



    @Override
    public void onCreate() {
        super.onCreate();

        AppData.init(this);
        ShareSDK.initSDK(this, "1276c2d783264");
    }
}
