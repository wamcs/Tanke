package com.lptiyu.tanke.io.net;

import com.lptiyu.tanke.pojo.City;
import com.lptiyu.tanke.pojo.GameDetails;
import com.lptiyu.tanke.pojo.GameDisplayEntity;
import com.lptiyu.tanke.pojo.GameStatus;

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

  @GET("Home/Index")
  Observable<Response<List<GameDisplayEntity>>> getGamePage(
      @Query("city") String location,
      @Query("page") int page);

  @GET("Home/Details")
  Observable<Response<GameDetails>> getGameDetails(
      @Query("game_id") int gameId);


  @Streaming
  @GET
  Observable<ResponseBody> downloadGameZip(@Url String url);

  @GET("System/City?page=1")
  Observable<Response<List<City>>> getSupportedCities();


  @GET("Home/Getnow")
  Observable<Response<GameStatus>> getCurrentGameStatus(
      @Query("uid") int uid,
      @Query("token") String token,
      @Query("game_id") String gameId);

  //TODO startGame的接口不知道干什么用的
  //TODO 游戏日志需要调整

}
