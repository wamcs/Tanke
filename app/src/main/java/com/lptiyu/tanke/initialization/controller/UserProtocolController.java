package com.lptiyu.tanke.initialization.controller;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * author:wamcs
 * date:2016/5/19
 * email:kaili@hustunique.com
 */
public class UserProtocolController extends ActivityController {

  @BindView(R.id.protocol_web_view)
  WebView mWebView;


  public UserProtocolController(AppCompatActivity activity, View view) {
    super(activity, view);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    ButterKnife.bind(this, view);
    init();
  }

  private void init() {
    HttpService.getUserService().userProtocol().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Response<String>>() {
          @Override
          public void call(Response<String> stringResponse) {
            if (stringResponse.getStatus() != Response.RESPONSE_OK) {
              ToastUtil.TextToast(stringResponse.getInfo());
              return;
            }
            mWebView.loadUrl(stringResponse.getData());
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            ToastUtil.TextToast("获取用户协议失败");
          }
        });
  }

  @Override
  protected boolean isToolbarEnable() {
    return false;
  }

  @OnClick(R.id.protocol_last_button)
  void back() {
    finish();
  }

  @Override
  public void onResume() {
    super.onResume();
    mWebView.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    mWebView.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mWebView.destroy();

  }

  @Override
  public boolean onBackPressed() {
    return false;
  }
}
