package com.lptiyu.tanke.io.net;

import com.lptiyu.tanke.bean.GameDetails;
import com.lptiyu.tanke.bean.GameEntry;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/18
 *
 * @author ldx
 */
public interface GameService {

  @GET("Home/Index")
  Observable<Response<List<GameEntry>>> getGamePage(
      @Query("city") String location,
      @Query("page") int page);

  @GET("Home/Details")
  Observable<Response<GameDetails>> getGameDetails(
      @Query("game_id") int gameId);


}
