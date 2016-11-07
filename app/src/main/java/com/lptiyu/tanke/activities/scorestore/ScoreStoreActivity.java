package com.lptiyu.tanke.activities.scorestore;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.widget.CustomTextView;
import com.lptiyu.tanke.widget.GradientProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScoreStoreActivity extends MyBaseActivity {

    @BindView(R.id.web_view_progress_bar)
    GradientProgressBar progressBar;
    @BindView(R.id.web_view)
    WebView mWebView;
    @BindView(R.id.default_tool_bar_textview)
    CustomTextView defaultToolBarTextview;

    private static final String ScoreUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_store);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        defaultToolBarTextview.setText("积分商城");

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
        mWebView.loadUrl(Html.fromHtml(ScoreUrl).toString());
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
