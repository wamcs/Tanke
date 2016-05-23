package com.lptiyu.tanke.io.net;

import com.lptiyu.tanke.bean.GameEntry;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/18
 *
 * @author ldx
 */
public interface GameService {

  @GET("all")
  Observable<Response<List<GameEntry>>> getGamePage(String location);

  @GET("location")
  Observable<Response<List<String>>> getSupportLocations();
}
