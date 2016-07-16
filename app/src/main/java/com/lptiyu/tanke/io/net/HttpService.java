package com.lptiyu.tanke.io.net;

import com.lptiyu.tanke.global.AppData;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
    public static final String BASE_URL = "http://api.lptiyu.com/lepao/api.php/";

    private static final int DEFAULT_TIMEOUT = 10;

    private static GameService gameService;

    private static UserService userService;

    private static TeamService teamService;

    /**
     * OKHttpClient网络客户端配置
     */
    static {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                System.out.println("message = " + message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClientBuilder.addInterceptor(interceptor);
        httpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                HttpUrl originUrl = originalRequest.url();
                HttpUrl newUrl = originUrl.newBuilder()
                        .addQueryParameter("ostype", "1")
                        .addQueryParameter("version", String.valueOf(AppData.getVersionCode()))
                        .build();
                Request processed = originalRequest.newBuilder()
                        .url(newUrl).build();
                return chain.proceed(processed);
            }
        });
        //设置10秒超时
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        //设置缓存目录
        httpClientBuilder.cache(new Cache(AppData.cacheDir("network"), 1024 * 1024 * 100));

        Retrofit retrofit = new Retrofit.Builder()
                //设置OKHttpClient为网络客户端
                .client(httpClientBuilder.build())
                //配置转化库，默认是Gson
                .addConverterFactory(GsonConverterFactory.create(AppData.globalGson()))
                //配置回调库，采用RxJava
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                //配置服务器路径
                .baseUrl(BASE_URL)
                .build();

        gameService = retrofit.create(GameService.class);
        userService = retrofit.create(UserService.class);
        teamService = retrofit.create(TeamService.class);
    }

    private HttpService() {
    }

    public static GameService getGameService() {
        return gameService;
    }

    public static UserService getUserService() {
        return userService;
    }

    public static TeamService getTeamService() {
        return teamService;
    }
}
