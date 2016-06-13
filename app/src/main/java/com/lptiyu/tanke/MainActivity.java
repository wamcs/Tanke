package com.lptiyu.tanke;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.widget.ImageView;

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
  ImageView tab1;

  @BindView(R.id.page_2)
  ImageView tab2;

  @BindView(R.id.page_3)
  ImageView tab3;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    mController = new MainActivityController(this, getWindow().getDecorView());
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

    selectTab(index == 0, tab1, R.mipmap.icon_main_page_selected, R.mipmap.icon_main_page_unselected);
    selectTab(index == 1, tab2, R.mipmap.icon_message_selected, R.mipmap.icon_message_unselected);
    selectTab(index == 2, tab3, R.mipmap.icon_me_selected, R.mipmap.icon_me_unselected);
    mCurrentIndex = index;
  }

  private void selectTab(boolean select, ImageView view, int resSelected, int resUnselected) {
    if (select) {
      view.setImageResource(resSelected);
    } else {
      view.setImageResource(resUnselected);
    }
  }

}
