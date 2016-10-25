package com.lptiyu.tanke.messagesystem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.widget.CustomTextView;
import com.lptiyu.tanke.widget.GradientProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-27
 *         email: wonderfulifeel@gmail.com
 */
public class SystemWebController extends ActivityController {

    @BindView(R.id.system_web_tool_bar_textview)
    CustomTextView mToolbarTitle;
    @BindView(R.id.web_view_progress_bar)
    GradientProgressBar progressBar;
    @BindView(R.id.web_view)
    WebView mWebView;

    public SystemWebController(AppCompatActivity activity, View view) {
        super(activity, view);
        ButterKnife.bind(this, view);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        String title = intent.getStringExtra(Conf.MESSAGE_TITLE);
        String url = intent.getStringExtra(Conf.MESSAGE_URL);
        mToolbarTitle.setText(title);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            // Set progress bar during loading
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress / 100.0f);
            }
        });
        mWebView.loadUrl(Html.fromHtml(url).toString());
    }

    @Override
    public void onDestroy() {
        mWebView.stopLoading();
        mWebView.removeAllViews();
        mWebView.destroy();
        mWebView = null;
        super.onDestroy();
    }

    @OnClick(R.id.system_web_tool_bar_imageview)
    void back() {
        finish();
    }

}
