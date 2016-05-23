package com.lptiyu.tanke.io.net;

import com.lptiyu.tanke.global.AppData;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/18
 *
 * @author ldx
 */
public final class HttpService {
  public static final String BASE_URL = "https://api.douban.com/v2/movie/";

  private static final int DEFAULT_TIMEOUT = 5;

  private static GameService gameService;

  static {
    OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

    httpClientBuilder.addInterceptor(interceptor);
    httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    httpClientBuilder.cache(new Cache(AppData.cacheDir("network"), 1024 * 1024 * 100));

    Retrofit retrofit = new Retrofit.Builder()
        .client(httpClientBuilder.build())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .baseUrl(BASE_URL)
        .build();

    gameService = retrofit.create(GameService.class);
  }

  private HttpService() {
  }

  public static GameService getGameService() {
    return gameService;
  }

}
