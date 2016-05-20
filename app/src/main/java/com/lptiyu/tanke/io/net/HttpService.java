package com.lptiyu.tanke.io.net;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/18
 *
 * @author ldx
 */
public class HttpService {
  public static final String BASE_URL = "https://api.douban.com/v2/movie/";

  private static final int DEFAULT_TIMEOUT = 5;

  private Retrofit retrofit;

  private GameService gameService;

  //构造方法私有
  private HttpService() {
    //手动创建一个OkHttpClient并设置超时时间
    OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
    httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

    retrofit = new Retrofit.Builder()
        .client(httpClientBuilder.build())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .baseUrl(BASE_URL)
        .build();

    gameService = retrofit.create(GameService.class);
  }

  private static class SingletonHolder {
    private static final HttpService INSTANCE = new HttpService();
  }

  //获取单例
  public static HttpService getInstance() {
    return SingletonHolder.INSTANCE;
  }

  public GameService getGameService() {
    return gameService;
  }

}
