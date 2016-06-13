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

  public static final long UID = 1L;

  public static final String TOKEN = "2D9A6633414967B5DF2BF397BD3AD695";

  UserService userService;

  @Before
  public void setUp() throws Exception {
    userService = HttpService.getUserService();
  }

  @Test
  @Ignore //success 2.1
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

  @Test //success 2.3
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

  @Test //success 2.4
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
  //success 2.5
  public void testForgetPassword() throws Exception {
    HttpService.getUserService().forgetPassword("13006180386", "123qwe", "712442")
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

  @Test //success 2.6
  public void testGetVerifyCode() throws Exception {
    HttpService.getUserService().getVerifyCode(2, "18062145623")
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

  @Test //success 2.7
  public void testResetPassword() throws Exception {
    HttpService.getUserService().resetPassword(UID, TOKEN, "123qwe", "123qwe")
        .subscribe(new ResponseAction1(), new AssertNullAction1());
  }

  @Test
  //success 2.8
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

  @Test // failed 2.9
  public void testUploadUserAvatar() throws Exception {
    File file = new File("src/test/res/need_to_remove.png");
    userService.uploadUserAvatar(1
        , TOKEN
        , RequestBody.create(
            MediaType.parse("application/octet-stream")
            , file))
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
  // 2.11 体重、生日不能更改= =,其他的都通过了
  public void testResetUserDetails() throws Exception {
    userService.resetUserDetails(UID, TOKEN, UserService.USER_DETAIL_HEIGHT, "65")
        .subscribe(new ResponseAction1(), new AssertNullAction1());
  }

  @Test
  //success 2.2
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

  @Test //success
  public void testGamePlaying() throws Exception {
    userService.gamePlaying(UID, TOKEN)
        .subscribe(new ResponseAction1(), new AssertNullAction1());
  }

  @Test //success
  public void testGameFinished() throws Exception {
    userService.gameFinished(UID, TOKEN)
        .subscribe(new ResponseAction1(), new AssertNullAction1());
  }

  @Test //成功
  public void testGetRewards() throws Exception {
    userService.getRewards(UID, TOKEN)
        .subscribe(new ResponseAction1(), new AssertNullAction1());
  }

  @Test //success
  public void testManagerTask() throws Exception {
    userService.getManagerTask(UID, TOKEN)
        .subscribe(new ResponseAction1(), new AssertNullAction1());
  }

  @Test //success
  public void testRegisterInstallation() throws Exception {
    userService.registerInstallation(UID, TOKEN, "i12303234")
        .subscribe(new ResponseAction1(), new AssertNullAction1());
  }

}