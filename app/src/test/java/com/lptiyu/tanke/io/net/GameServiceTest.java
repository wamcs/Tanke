package com.lptiyu.tanke.io.net;

import com.lptiyu.tanke.BuildConfig;
import com.lptiyu.tanke.pojo.GameDetailsEntity;
import com.lptiyu.tanke.pojo.GameDisplayEntity;

import junit.framework.Assert;

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
  public void testGetGamePage() throws Exception {
    HttpService.getGameService().getGamePage(111,"1", "武汉", 1).subscribe(new Action1<Response<List<GameDisplayEntity>>>() {
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

  @Test
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
        System.err.println("throwable = " + throwable);
        Assert.assertNull(throwable);
      }
    });

  }

  @Test
  public void testRegisterCode() throws Exception {

  }

  @Test
  public void testUploadCode() throws Exception {

  }

  @Test
  public void testUploadGameRecords() throws Exception {

  }

  @Test
  public void testDownloadGameZip() throws Exception {

  }

  @Test
  public void testGetSupportedCities() throws Exception {

  }

  @Test
  public void testShare() throws Exception {

  }
}