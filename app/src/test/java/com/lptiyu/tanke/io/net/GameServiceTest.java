package com.lptiyu.tanke.io.net;

import com.lptiyu.tanke.BuildConfig;
import com.lptiyu.tanke.pojo.GameDetailsEntity;
import com.lptiyu.tanke.pojo.GameDisplayEntity;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import rx.functions.Action1;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/3
 *
 * @author ldx
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class GameServiceTest {


  @Test
  // success 2.12
  public void testGetGamePage() throws Exception {
    HttpService.getGameService().getGamePage(111, "1", "武汉", 1).subscribe(new Action1<Response<List<GameDisplayEntity>>>() {
      @Override
      public void call(Response<List<GameDisplayEntity>> listResponse) {
        System.out.println("listResponse = " + listResponse);
        Assert.assertEquals(listResponse.getStatus(), Response.RESPONSE_OK);

      }
    }, new Action1<Throwable>() {
      @Override
      public void call(Throwable throwable) {
        System.err.println("throwable = " + throwable);
        Assert.assertNull(throwable);
      }
    });

  }

  @Test// success 2.13
  public void testGetGameDetails() throws Exception {
    HttpService.getGameService().getGameDetails(4).subscribe(new Action1<Response<GameDetailsEntity>>() {
      @Override
      public void call(Response<GameDetailsEntity> gameDetailsEntityResponse) {
        System.out.println("gameDetailsEntityResponse = " + gameDetailsEntityResponse);
        Assert.assertEquals(gameDetailsEntityResponse.getStatus(), Response.RESPONSE_OK);
      }
    }, new Action1<Throwable>() {
      @Override
      public void call(Throwable throwable) {
        Assert.assertNull(throwable);
      }
    });

  }

  @Test // failed retry
  public void testRegisterQRCode() throws Exception {
    HttpService.getGameService().regenerateJudgementQRCode(UserServiceTest.UID, UserServiceTest.TOKEN, 4, "taskId://1//4//2//1460114291")
        .subscribe(new ResponseAction1(), new AssertNullAction1());
  }

  @Test
  @Ignore // success
  public void testVerifyQRCode() throws Exception {
    HttpService.getGameService().verifyQRCode(UserServiceTest.UID, UserServiceTest.TOKEN, "teamId://2//1//1//1460114291")
        .subscribe(new ResponseAction1(), new AssertNullAction1());

  }

  @Test // success
  public void testUploadGameRecords() throws Exception {
    HttpService.getGameService().uploadTeamGameRecords(1, "11", 1, 1, 1, 1, "23", "123.32", "123.1", GameService.TEAM_RECORD, GameService.RECORD_TYPE_GAME_FINISH)
        .subscribe(new ResponseAction1(), new AssertNullAction1());
  }

  @Test
  public void testDownloadGameZip() throws Exception {
    // 尚未测试

  }

  @Test // success
  public void testGetSupportedCities() throws Exception {
    HttpService.getGameService().getSupportedCities()
        .subscribe(new ResponseAction1(), new AssertNullAction1());
  }

  @Test // success
  public void testGetShareUrl() throws Exception {
    HttpService.getGameService().getShareUrl(1, "11", 1, 1)
        .subscribe(new ResponseAction1(), new AssertNullAction1());
  }
}