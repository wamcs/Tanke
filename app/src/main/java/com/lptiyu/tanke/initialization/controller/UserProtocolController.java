package com.lptiyu.tanke.initialization.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.global.Conf;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * author:wamcs
 * date:2016/5/19
 * email:kaili@hustunique.com
 */
public class UserProtocolController extends ActivityController {

    @BindView(R.id.protocol_web_view)
    WebView mWebView;
    @BindView(R.id.tool_bar)
    Toolbar toolbar;

    public UserProtocolController(AppCompatActivity activity, View view) {
        super(activity, view);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this,view);
        init();
    }

    private void init(){
        toolbar.setVisibility(View.GONE);
        String urlString = getIntent().getStringExtra(Conf.PROTOCOL_URL);
        mWebView.loadUrl(urlString);
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
    }

    @OnClick(R.id.protocol_last_button)
    void back() {
        mWebView.onPause();
        mWebView.destroy();
        finish();
    }

    @OnClick(R.id.protocol_next_button)
    void read(){
        Intent intent =new Intent();
        intent.putExtra(Conf.PROTOCOL_STATE,true);
        getActivity().setResult(Conf.PROTOCOL_CODE,intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mWebView.onPause();
        mWebView.destroy();
        finish();
    }
}
