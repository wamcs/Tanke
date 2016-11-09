package com.lptiyu.tanke.activities.messagedetail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.MobileDisplayHelper;
import com.lptiyu.tanke.widget.CustomTextView;
import com.lptiyu.tanke.widget.GradientProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SystemWebActivity extends MyBaseActivity {
    @BindView(R.id.web_view_progress_bar)
    GradientProgressBar progressBar;
    @BindView(R.id.web_view)
    WebView mWebView;
    @BindView(R.id.default_tool_bar_textview)
    CustomTextView defaultToolBarTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_web);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        String title = intent.getStringExtra(Conf.MESSAGE_TITLE);
        String url = intent.getStringExtra(Conf.MESSAGE_URL);
        Point point = MobileDisplayHelper.getMobileWidthHeight(this);
        int width = point.x;
        defaultToolBarTextview.setMaxWidth(width * 7 / 10);//最大宽度为当前手机手机屏幕宽度的70%
        defaultToolBarTextview.setMaxLines(1);
        defaultToolBarTextview.setEllipsize(TextUtils.TruncateAt.END);
        defaultToolBarTextview.setText(title);

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

    @OnClick(R.id.default_tool_bar_imageview)
    public void onClick() {
        finish();
    }
}
