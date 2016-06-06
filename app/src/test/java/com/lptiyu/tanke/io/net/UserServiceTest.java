package com.lptiyu.tanke.io.net;

import com.lptiyu.tanke.BuildConfig;
import com.lptiyu.tanke.pojo.UserDetails;
import com.lptiyu.tanke.pojo.UserEntity;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
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
  @Ignore //已经测试成功
  public void testRegister() throws Exception {
    userService.register("13006180386", "123qwe", "132465", UserService.USER_TYPE_NORMAL)
        .subscribe(new Action1<Response<UserEntity>>() {
          @Override
          public void call(Response<UserEntity> userResponse) {
            System.out.println("userResponse = " + userResponse);
            Assert.assertNotNull(userResponse);
            Assert.assertEquals(userResponse.getStatus(), Response.RESPONSE_OK);
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            Assert.assertNull(throwable);
            Assert.assertFalse(true);
          }
        });
  }

  @Test //成功
  public void testLogin() throws Exception {
    userService.login("13006180386", "123qwe")
        .subscribe(new Action1<Response<UserEntity>>() {
          @Override
          public void call(Response<UserEntity> userResponse) {
            Assert.assertNotNull(userResponse);
            System.out.println(userResponse.toString());
            Assert.assertEquals(userResponse.getStatus(), Response.RESPONSE_OK);
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            Assert.assertNull(throwable);
          }
        });
  }

  @Test // 成功
  public void testLoginThirdParty() throws Exception {
    HttpService.getUserService().loginThirdParty("193840382029484", UserService.USER_TYPE_QQ)
        .subscribe(new Action1<Response<UserEntity>>() {
          @Override
          public void call(Response<UserEntity> userEntityResponse) {
            System.out.println(userEntityResponse.toString());
            Assert.assertEquals(userEntityResponse.getStatus(), Response.RESPONSE_OK);
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            Assert.assertNull(throwable);
          }
        });
  }

  @Test
  //应该ok了吧，显示验证码或手机号错误
  public void testForgetPassword() throws Exception {
    HttpService.getUserService().forgetPassword("13006180386", "123qwe", "123000")
        .subscribe(new Action1<Response<Void>>() {
          @Override
          public void call(Response<Void> voidResponse) {
            System.out.println("voidResponse = " + voidResponse);
            Assert.assertEquals(voidResponse.getStatus(), Response.RESPONSE_OK);
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            Assert.assertNull(throwable);
          }
        });

  }

  @Test
  public void testGetVerifyCode() throws Exception {
    HttpService.getUserService().getVerifyCodeForgetPassword("13006180386")
        .subscribe(new Action1<Response<Void>>() {
          @Override
          public void call(Response<Void> voidResponse) {
            System.out.println("voidResponse = " + voidResponse);
            Assert.assertEquals(voidResponse.getStatus(), Response.RESPONSE_OK);

          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            Assert.assertNull(throwable);
          }
        });

  }

  @Test //测试成功
  public void testResetPassword() throws Exception {
    HttpService.getUserService().resetPassword(1L, "1123", "123qwe", "123qwe")
        .subscribe(new ResponseAction1(), new AssertNullAction1());
  }

  @Test
  //测试成功
  public void testGetUserDetail() throws Exception {
    HttpService.getUserService().getUserDetail(1, "haha")
        .subscribe(new Action1<Response<UserDetails>>() {
          @Override
          public void call(Response<UserDetails> userDetailsResponse) {
            System.out.println("userDetailsResponse = " + userDetailsResponse);
            Assert.assertEquals(userDetailsResponse.getStatus(), Response.RESPONSE_OK);
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            Assert.assertNull(throwable);
          }
        });

  }

  @Test
  public void testUploadUserAvatar() throws Exception {
    File file = new File("src/test/res/need_to_remove.png");
    userService.uploadUserAvatar(UID, TOKEN, RequestBody.create(MediaType.parse("application/octet-stream"), file))
        .subscribe(new Action1<Response<String>>() {
          @Override
          public void call(Response<String> stringResponse) {
            System.out.println("stringResponse = " + stringResponse);
            Assert.assertEquals(stringResponse.getStatus(), Response.RESPONSE_OK);
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            Assert.assertNull(throwable);
          }
        });
  }

  @Test
  //成功
  public void testResetUserDetails() throws Exception {
    userService.resetUserDetails(UID, TOKEN, UserService.USER_DETAIL_BIRTHDAY, "1994-12-12")
        .subscribe(new ResponseAction1(), new AssertNullAction1());
  }

  @Test
  //成功
  public void testUserProtocol() throws Exception {
    userService.userProtocol().subscribe(new Action1<Response<String>>() {
      @Override
      public void call(Response<String> stringResponse) {
        System.out.println("stringResponse = " + stringResponse);
        Assert.assertEquals(stringResponse.getStatus(), Response.RESPONSE_OK);
      }
    }, new Action1<Throwable>() {
      @Override
      public void call(Throwable throwable) {
        Assert.assertNull(throwable);
      }
    });

  }

  @Test
  public void testGamePlaying() throws Exception{
    userService.gamePlaying(UID, TOKEN)
        .subscribe(new ResponseAction1(), new AssertNullAction1());
  }
  
  @Test
  public void testGameFinished() throws Exception{
    userService.gameFinished(UID, TOKEN)
        .subscribe(new ResponseAction1(), new AssertNullAction1());
  }

  @Test //成功
  public void testGetRewards() throws Exception{
    userService.getRewards(UID, TOKEN)
        .subscribe(new ResponseAction1(), new AssertNullAction1());
  }

  @Test
  public void testManagerTask() throws Exception{
    userService.getManagerTask(UID, TOKEN)
        .subscribe(new ResponseAction1(), new AssertNullAction1());
  }

  @Test
  public void testRegisterInstallation() throws Exception{
    userService.registerInstallation(UID, TOKEN, "i12303i30ejd021o0d0d0023o4p230;234")
        .subscribe(new ResponseAction1(), new AssertNullAction1());
  }

  @Test
  public void testAboutUrl() throws Exception{
    userService.about()
        .subscribe(new ResponseAction1(), new AssertNullAction1());
  }


}