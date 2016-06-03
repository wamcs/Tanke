package com.lptiyu.tanke.io.net;

import com.lptiyu.tanke.pojo.City;
import com.lptiyu.tanke.pojo.GameDetailsEntity;
import com.lptiyu.tanke.pojo.GameDisplayEntity;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/18
 *
 * @author ldx
 */
public interface GameService {

  /**
   * 2.12 首页接口
   */
  @GET("Home/Index")
  Observable<Response<List<GameDisplayEntity>>> getGamePage(
      @Query("uid") int uid,
      @Query("token") String token,
      @Query("city") String location, // 城市
      @Query("page") long page);

  /**
   * 2.13 游戏详情
   */
  @GET("Home/Details")
  Observable<Response<GameDetailsEntity>> getGameDetails(
      @Query("game_id") long gameId);

  /**
   * 2.17 把生成的二维码信息上传后台
   */
  @GET("Home/Code")
  Observable<Response<Void>> registerCode(
      @Query("uid") long uid,
      @Query("token") String token,
      @Query("content") String content
  );

  /**
   * 2.18 扫码
   * 加入团队，或者扫码通关
   */
  @GET("Home/Subcode")
  Observable<Response<Void>> uploadCode(
      @Query("uid") long uid,
      @Query("token") String token,
      @Query("content") String content
  );

  /**
   * 2.23 提交用户记录
   */
  @GET("System/Rankslog")
  Observable<Response<Void>> uploadGameRecords(
      @Query("uid") long uid,
      @Query("token") String token
      //TODO 接口字段以确定，具体命名由服务端确定
  );

  @Streaming
  @GET
  Observable<ResponseBody> downloadGameZip(@Url String url);

  /**
   * 2.28 获取开通城市
   */
  @GET("System/City?page=1")
  Observable<Response<List<City>>> getSupportedCities();


  /**
   * 2.29 游戏结束后获取分享接口
   *
   * @param uid    用户id
   * @param token  判断用户合法的随机数
   * @param gameId 游戏Id
   * @param teamId 团队Id
   * @return Response, String 分享的链接
   */
  @GET("Home/Share")
  Observable<Response<String>> share(
      @Query("uid") long uid,
      @Query("token") String token,
      @Query("game_id") long gameId,
      @Query("tanks_id") long teamId
  );

}
