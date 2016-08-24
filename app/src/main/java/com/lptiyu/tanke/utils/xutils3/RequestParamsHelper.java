package com.lptiyu.tanke.utils.xutils3;

import android.content.Context;
import android.support.annotation.NonNull;

import org.xutils.http.RequestParams;

public class RequestParamsHelper {
    /**
     * 获取配置好的RequestParams对象
     *
     * @param context
     * @param url
     * @return
     */
    public static RequestParams getCacheRequestParams(Context context, String url) {
        RequestParams params = getBaseRequestParam(url);
        long maxMemory = Runtime.getRuntime().maxMemory();
        int cacheSize = (int) (maxMemory / 8);
        String cacheDir = SDCardHelper.getSDCardPrivateCacheDir(context);
        params.setCacheSize(cacheSize);
        params.setCacheDirName(cacheDir);
        params.setCacheMaxAge(30000);
        return params;
    }


    /**
     * 获取配置好的RequestParams对象
     *
     * @param url
     * @return
     */
    @NonNull
    public static RequestParams getBaseRequestParam(String url) {
        RequestParams params = new RequestParams(url);
        params.addHeader("Connection", "keep-alive");
        params.setConnectTimeout(7000);
        return params;
    }
}
