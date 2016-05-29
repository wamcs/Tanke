package com.lptiyu.tanke.io.net;

import com.lptiyu.tanke.BuildConfig;
import com.lptiyu.tanke.pojo.UserEntity;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.functions.Action1;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/24
 *
 * @author ldx
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class UserServiceTest {

  public static final int UID = 1;

  public static final String TOKEN = "1929822";

  UserService userService;

  @Before
  public void setUp() throws Exception {
    userService = HttpService.getUserService();
  }

  @Test
  public void testRegister() throws Exception {
    userService.register("13006180386", "123qwe", "132465", UserService.USER_TYPE_NORMAL)
        .subscribe(new Action1<Response<UserEntity>>() {
          @Override
          public void call(Response<UserEntity> userResponse) {
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
    userService.login("13006180386", "123qwe")
        .subscribe(new Action1<Response<UserEntity>>() {
          @Override
          public void call(Response<UserEntity> userResponse) {
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
    File file = new File("src/test/res/need_to_remove.png");
    userService.uploadUserAvatar(UID, TOKEN,RequestBody.create(MediaType.parse("application/octet-stream"), file))
        .subscribe(new Action1<Response<String>>() {
          @Override
          public void call(Response<String> stringResponse) {
            Assert.assertNotNull(stringResponse);
            System.out.println("stringResponse = " + stringResponse.getInfo());
            Assert.assertEquals(stringResponse.getStatus(), Response.RESPONSE_OK);
            Assert.assertNotNull(stringResponse.getData());
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            Assert.assertNull(throwable);
          }
        });
  }

  @Test
  public void testResetUserDetails() throws Exception {

  }

  @Test
  public void testUserProtocol() throws Exception {

  }
}