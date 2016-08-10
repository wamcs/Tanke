package com.lptiyu.tanke.messagesystem;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.messagesystem.helper.MessageHelper;
import com.lptiyu.tanke.messagesystem.helper.PushHelper;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * author:wamcs
 * date:2016/6/4
 * email:kaili@hustunique.com
 */
public class MessageController extends ActivityController {

  private MessageHelper helper;

  public MessageController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    init(activity, view);
  }

  private void init(AppCompatActivity activity, View view) {
    int type = getIntent().getIntExtra(Conf.MESSAGE_TYPE, Integer.MIN_VALUE);
    if (type == Integer.MIN_VALUE) {
      throw new IllegalStateException("not has this type");
    }
    switch (type) {
      case Conf.MESSAGE_LIST_TYPE_OFFICIAL:
        helper = new PushHelper(activity, view, Conf.MESSAGE_LIST_TYPE_OFFICIAL);
        break;
      case Conf.MESSAGE_LIST_TYPE_SYSTEM:
        helper = new PushHelper(activity, view, Conf.MESSAGE_LIST_TYPE_SYSTEM);
        break;
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    helper.finish();
  }

  @OnClick(R.id.default_tool_bar_imageview)
  void back() {
    finish();
  }

}
