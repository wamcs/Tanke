package com.lptiyu.tanke.utils;

import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Jason on 2016/7/15.
 */
public class WebViewUtils {
    /**
     * 配置WebView
     *
     * @param webView
     */
    public static void setWebView(Context context, WebView webView) {
        // 打开网页时不调用系统浏览器， 而是在本WebView中显示
        webView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = webView.getSettings();
        //支持js
        webSettings.setJavaScriptEnabled(true);
        //打开页面时， 自适应屏幕
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        //支持自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        //是否显示网络图片
        webSettings.setBlockNetworkImage(false);
        //设置字体大小
        webSettings.setDefaultFontSize(Display.sp2px(context, 16f));
    }
}
