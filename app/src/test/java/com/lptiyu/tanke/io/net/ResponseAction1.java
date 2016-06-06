package com.lptiyu.tanke.io.net;

import junit.framework.Assert;

import rx.functions.Action1;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/6
 *
 * @author ldx
 */
public class ResponseAction1 implements Action1<Response> {
  @Override
  public void call(Response response) {
    System.out.println("response = " + response);
    Assert.assertEquals(response.getStatus(), Response.RESPONSE_OK);
  }
}
