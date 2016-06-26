package com.lptiyu.tanke.messagesystem;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.messagesystem.helper.MessageHelper;
import com.lptiyu.tanke.messagesystem.helper.PushHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * author:wamcs
 * date:2016/6/4
 * email:kaili@hustunique.com
 */
public class MessageController extends ActivityController {

  @BindView(R.id.message_bottom_bar)
  LinearLayout bottomBar;

  private MessageHelper helper;

  public MessageController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    init(activity, view);
  }

  private void init(AppCompatActivity activity, View view) {
    int type = getIntent().getIntExtra(Conf.MESSAGE_TYPE, Integer.MIN_VALUE);
    Timber.d("message type: %d", type);
    if (type == Integer.MIN_VALUE) {
      throw new IllegalStateException("not has this type");
    }
    switch (type) {
      case Conf.MESSAGE_LIST_TYPE_OFFICIAL:
        helper = new PushHelper(activity, view, Conf.MESSAGE_LIST_TYPE_OFFICIAL);
        bottomBar.setVisibility(View.GONE);
        break;
      case Conf.MESSAGE_LIST_TYPE_SYSTEM:
        helper = new PushHelper(activity, view, Conf.MESSAGE_LIST_TYPE_SYSTEM);
        bottomBar.setVisibility(View.GONE);
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
