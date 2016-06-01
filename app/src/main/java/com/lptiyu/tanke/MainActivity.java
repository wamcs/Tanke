package com.lptiyu.tanke;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.view.View;

import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

  MainActivityController mController;

  /**
   * Init value is a non-zero value, and then will be set to value.
   */
  public int mCurrentIndex = 2;

  @BindView(R.id.page_1)
  View tab1;

  @BindView(R.id.page_2)
  View tab2;

  @BindView(R.id.page_3)
  View tab3;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mController = new MainActivityController(this, getWindow().getDecorView());
    ButterKnife.bind(this);
    init();
  }

  @Override
  public ActivityController getController() {
    return mController;
  }

  private void init() {
    selectTab(0);
  }

  @IntDef({0, 1, 2})
  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.SOURCE)
  @interface page {
  }


  public void selectTab(@page int index) {
    if (mCurrentIndex == index) {
      return;
    }

    selectTab(index == 0, tab1);
    selectTab(index == 1, tab2);
    selectTab(index == 2, tab3);
  }

  private void selectTab(boolean select, View View) {
    // TODO Need to do
  }

}
