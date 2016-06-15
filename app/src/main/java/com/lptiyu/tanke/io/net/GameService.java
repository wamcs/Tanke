package com.lptiyu.tanke.io.net;

import android.support.annotation.IntDef;

import com.lptiyu.tanke.pojo.City;
import com.lptiyu.tanke.pojo.GameDetailsEntity;
import com.lptiyu.tanke.pojo.GameDisplayEntity;
import com.lptiyu.tanke.pojo.RECOMMENDED_TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
      @Query("uid") long uid,
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
  @GET("Home/Code?type=1")
  Observable<Response<Void>> regenerateTeamQRCode(
      @Query("uid") long uid,
      @Query("token") String token,
      @Query("ranks_id") int ranksId,
      @Query("content") String content
  );

  @GET("Home/Code?type=2")
  Observable<Response<Void>> regenerateJudgementQRCode(
      @Query("uid") long uid,
      @Query("token") String token,
      @Query("task_id") int task_id,
      @Query("content") String content
  );

  /**
   * 2.18 扫码
   * 加入团队，或者扫码通关
   */
  @GET("Home/Subcode")
  Observable<Response<Void>> verifyQRCode(
      @Query("uid") long uid,
      @Query("token") String token,
      @Query("content") String content
  );

  int RECORD_TYPE_GAME_START = 1;
  int RECORD_TYPE_SPOT_REACHED = 2;
  int RECORD_TYPE_TASK_START = 3;
  int RECORD_TYPE_TASK_FINISH = 4;
  int RECORD_TYPE_GAME_FINISH = 5;

  @IntDef({RECORD_TYPE_GAME_START,
      RECORD_TYPE_SPOT_REACHED,
      RECORD_TYPE_TASK_START,
      RECORD_TYPE_TASK_FINISH,
      RECORD_TYPE_GAME_FINISH})
  @Retention(RetentionPolicy.SOURCE)
  public @interface RecordType {
  }

  int TEAM_RECORD = 1;
  int USER_RECORD = 2;

  @IntDef({TEAM_RECORD,
      USER_RECORD})
  @Retention(RetentionPolicy.SOURCE)
  public @interface TeamOrUserRecord{
  }

  /**
   * 2.23 提交用户记录
   */
  @GET("System/Rankslog?type=2")
  Observable<Response<Void>> uploadPersonalGameRecords(
      @Query("uid") long uid,
      @Query("token") String token,
      @Query("game_id") int gameId,
      @Query("point_id") int pointId,
      @Query("distance") String distanceFromRecords,
      @Query("x") String x,
      @Query("y") String y,
      @Query("state") @RecordType int state
  );

  /**
   * 2.23 提交用户记录
   */
  @GET("System/Rankslog?type=1")
  Observable<Response<Void>> uploadTeamGameRecords(
      @Query("uid") long uid,
      @Query("token") String token,
      @Query("game_id") long gameId,
      @Query("ranks_id") long rankId,
      @Query("point_id") long pointId,
      @Query("task_id") long taskId,
      @Query("distance") String distanceFromRecords,
      @Query("x") String x,
      @Query("y") String y,
      @Query("type") @TeamOrUserRecord int type,
      @Query("state") @RecordType int state
      );

  @Streaming
  @GET
  Observable<retrofit2.Response<ResponseBody>> downloadGameZip(@Url String url);

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
  Observable<Response<String>> getShareUrl(
      @Query("uid") long uid,
      @Query("token") String token,
      @Query("game_id") long gameId,
      @Query("ranks_id") long teamId
  );

}
