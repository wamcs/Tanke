package com.lptiyu.tanke.io.net;

import junit.framework.Assert;

import rx.functions.Action1;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/6
 *
 * @author ldx
 */
public class AssertNullAction1 implements Action1<Throwable> {

  @Override
  public void call(Throwable throwable) {
    Assert.assertNull(throwable);
  }
}
