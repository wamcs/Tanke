package com.lptiyu.tanke.io.net;

import com.lptiyu.tanke.BuildConfig;
import com.lptiyu.tanke.bean.User;
import com.lptiyu.tanke.global.AppData;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/24
 *
 * @author ldx
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class UserServiceTest {

  UserService userService;

  @Before
  public void setUp() throws Exception {
    userService = HttpService.getUserService();
  }

  @Test
  public void testRegister() throws Exception {
    userService.register("13006180386", "123qwe", "132465", UserService.USER_TYPE_NORMAL)
        .subscribe(new Action1<Response<User>>() {
          @Override
          public void call(Response<User> userResponse) {
            Assert.assertNotNull(userResponse);
            System.out.println("userResponse = " + userResponse);
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            Assert.assertNull(throwable);
            Assert.assertFalse(true);
          }
        });
  }

  @Test
  public void testLogin() throws Exception {
    userService.login("13006180386", "123qwe", UserService.USER_TYPE_NORMAL)
        .subscribe(new Action1<Response<User>>() {
          @Override
          public void call(Response<User> userResponse) {
            Assert.assertNotNull(userResponse);
            System.out.println(userResponse.toString());
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            Assert.assertNull(throwable);
            Assert.assertFalse(true);
          }
        });
  }

  @Test
  public void testLoginThirdParty() throws Exception {

  }

  @Test
  public void testForgetPassword() throws Exception {

  }

  @Test
  public void testGetVerifyCode() throws Exception {

  }

  @Test
  public void testResetPassword() throws Exception {

  }

  @Test
  public void testGetUserDetail() throws Exception {

  }

  @Test
  public void testUploadUserAvatar() throws Exception {

  }

  @Test
  public void testResetUserDetails() throws Exception {

  }

  @Test
  public void testUserProtocol() throws Exception {

  }
}