package com.lptiyu.tanke.activities.bannerdetail;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.widget.GradientProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BannerDetailActivity extends MyBaseActivity {

    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.progress_bar)
    GradientProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_detail);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        String content = getIntent().getStringExtra(Conf.CONTENT);

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
        mWebView.loadUrl(Html.fromHtml(content).toString());
        //        mWebView.loadData(content, "text/html", "utf-8");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebView.stopLoading();
        mWebView.removeAllViews();
        mWebView.destroy();
        mWebView = null;
    }
}
