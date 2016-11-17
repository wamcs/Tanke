package com.lptiyu.tanke.activities.modifylocation;

import android.support.annotation.Nullable;

/**
 * @author: xiaoxiaoda
 * date: 15-12-2
 * email: daque@hustunique.com
 */

/**
 * Province list and City list scroll interface
 */
public interface LocationScroll {

  //start animation in
  void smoothIn();

  //start animation out
  void smoothOut();

  //before animate, load data ect
  void prepare(@Nullable String msg);
}
