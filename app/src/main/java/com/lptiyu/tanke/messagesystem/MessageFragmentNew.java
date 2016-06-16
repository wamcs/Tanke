package com.lptiyu.tanke.messagesystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.FragmentController;
import com.lptiyu.tanke.base.ui.BaseFragment;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/16
 *
 * 一个可替代的消息界面
 *
 * @author ldx
 */
public class MessageFragmentNew extends BaseFragment {

  private MessageControllerNew controller;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view =  fromResLayout(inflater, container, R.layout.fragment_message_new);
    controller = new MessageControllerNew(this, getActivityController(), view);
    return view;
  }

  @Override
  public FragmentController getController() {
    return controller;
  }

}
