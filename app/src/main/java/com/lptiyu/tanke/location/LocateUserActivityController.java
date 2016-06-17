package com.lptiyu.tanke.location;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.permission.PermissionDispatcher;
import com.lptiyu.tanke.permission.TargetMethod;
import com.lptiyu.tanke.utils.thread;
import com.lptiyu.tanke.widget.CustomTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * @author: xiaoxiaoda
 * date: 15-12-1
 * email: daque@hustunique.com
 */

public class LocateUserActivityController extends ActivityController {

  @BindView(R.id.default_tool_bar_textview)
  CustomTextView mToolbarText;

  private AppCompatActivity mActivity;

  private int currentPage = LOCATION_PAGE_PROVINCE;
  private LocationProvinceController provinceList;
  private LocationScroll cityList;

  public static final int LOCATION_PAGE_PROVINCE = 1;
  public static final int LOCATION_PAGE_CITY = 2;

  public LocateUserActivityController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    mActivity = activity;
    mToolbarText.setText(getString(R.string.choose_area));

    provinceList = new LocationProvinceController(this, view);
    cityList = new LocationCityController(this, view);
  }

  public void startLocate() {
    if (provinceList != null) {
      provinceList.startLocate();
    }
  }

  public void moveToList(int page, String msg) {

    switch (page) {

      case LOCATION_PAGE_PROVINCE:
        provinceList.prepare(null);
        thread.mainThread(new Runnable() {
          @Override
          public void run() {
            provinceList.smoothIn();
            cityList.smoothOut();
          }
        });
        break;

      case LOCATION_PAGE_CITY:
        cityList.prepare(msg);
        thread.mainThread(new Runnable() {
          @Override
          public void run() {
            cityList.smoothIn();
            provinceList.smoothOut();
          }
        });
        break;
    }
    currentPage = page;
  }

  public boolean onBackPressed() {
    back();
    return true;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    ((LocationProvinceController) provinceList).onDestory();
  }

  public void setResult(int resultCode, Intent intent) {
    mActivity.setResult(resultCode, intent);
  }

  @OnClick(R.id.default_tool_bar_imageview)
  void back() {
    if (LOCATION_PAGE_CITY == currentPage) {
      moveToList(LOCATION_PAGE_PROVINCE, null);
    } else {
      finish();
    }
  }

}
