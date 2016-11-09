package com.lptiyu.tanke.activities.redwallet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.lookredwalletrecord.LookRedWalletRecordActivity;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.LogUtils;
import com.lptiyu.tanke.utils.xutils3.XUtilsUrls;
import com.lptiyu.tanke.widget.CustomTextView;
import com.lptiyu.tanke.widget.GradientProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RedWalletActivity extends MyBaseActivity {

    @BindView(R.id.default_tool_bar_textview)
    CustomTextView defaultToolBarTextview;
    @BindView(R.id.web_view_progress_bar)
    GradientProgressBar progressBar;
    @BindView(R.id.web_view)
    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_wallet);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        defaultToolBarTextview.setText("红包提现");
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
        String url = XUtilsUrls.REQUEST_RED_WALLET + "?uid=" + Accounts.getId() + "&token=" + Accounts.getToken();
        LogUtils.i("红包提现url:" + url);
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

    @OnClick({R.id.default_tool_bar_imageview, R.id.default_tool_bar_imageview_look_record})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.default_tool_bar_imageview_look_record:
                //查看提现记录
                startActivity(new Intent(RedWalletActivity.this, LookRedWalletRecordActivity.class));
                break;
            case R.id.default_tool_bar_imageview:
                finish();
                break;
        }
    }
}
