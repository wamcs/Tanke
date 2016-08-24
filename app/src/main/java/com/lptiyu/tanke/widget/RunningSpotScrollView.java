package com.lptiyu.tanke.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author xiaoxiaoda
 *         date: 16-1-22
 *         email: daque@hustunique.com
 */
public class RunningSpotScrollView extends BaseSpotScrollView {

  public RunningSpotScrollView(Context context) {
    this(context, null);
  }

  public RunningSpotScrollView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RunningSpotScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setSpotClickable(true);
  }

  public void initAllSpots(int count) {
    if (count <= 0) {
      return;
    }
    for (int i = 0; i < count; i++) {
      if (0 == i) {
        addSpot(i, count, STATE.STATE_DOING);
      } else {
        addSpot(i, count, STATE.STATE_TO_DO);
      }
    }
    mCurrentCircle = spotList.get(0);
  }

  public void setSpots(int index, STATE state) {
    if (mCurrentCircle == null || spotList.size() == 0 || index < 0 || index >=  spotList.size()) {
      throw new IllegalArgumentException("Index not legal");
    }
    spotList.get(index).setmState(state);
  }

  private void scrollToSpot(View v) {
    if (null != v) {
      smoothScrollTo((int) v.getX(), 0);
    }
  }

  private void scrollToSpot(int index) {
    if (null != spotList && index < spotList.size()) {
      scrollToSpot(spotList.get(index));
    }
  }

}
