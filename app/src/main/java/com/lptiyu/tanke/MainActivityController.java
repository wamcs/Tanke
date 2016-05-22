package com.lptiyu.tanke;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lptiyu.tanke.base.controller.ActivityController;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/21
 *
 * @author ldx
 */
public class MainActivityController extends ActivityController {

  public MainActivityController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
  }


  @OnClick(R.id.page_1)
  public void page_1() {

  }

  @OnClick(R.id.page_2)
  public void page_2() {

  }

  @OnClick(R.id.page_3)
  public void page_3() {

  }


}
