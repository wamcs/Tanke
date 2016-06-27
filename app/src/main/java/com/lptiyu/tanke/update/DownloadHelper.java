package com.lptiyu.tanke.update;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-27
 *         email: wonderfulifeel@gmail.com
 */
public class DownloadHelper implements
    DownloadProgressInterceptor.DownloadProgressListener {

  private ProgressDialog mDownloadDialog;

  private DownloadApi downloadApi;
  private WeakReference<Context> weakReference;

  private static final double ONE_MILLION_BYTE = 1024 * 1024.0;

  public DownloadHelper(Context context) {
    weakReference = new WeakReference<>(context);
    initDownloadDialog();
    initRetrofit();
  }

  public void startDownload(String url) {
    if (!mDownloadDialog.isShowing()) {
      mDownloadDialog.show();
    }
    downloadApi.downloadApk(url).map(new Func1<Response<ResponseBody>, File>() {
      @Override
      public File call(Response<ResponseBody> response) {

        Timber.e(AppData.globalGson().toJson(response.headers()));

        File file = new File(DirUtils.getTempDirectory(), "update.apk");
        try {
          if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Create file failed.");
          }
          BufferedSink sink = Okio.buffer(Okio.sink(file));
          sink.writeAll(response.body().source());
          sink.close();
          return file;
        } catch (IOException e) {
          if (file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
          }
          throw Exceptions.propagate(e);
        }
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<File>() {
          @Override
          public void call(File file) {
            update(file);
            mDownloadDialog.dismiss();
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            ToastUtil.TextToast("下载出错");
          }
        });
  }

  @Override
  public void update(long bytesRead, long contentLength, boolean done) {
    int max = (int) (contentLength / ONE_MILLION_BYTE);
    if (mDownloadDialog.getMax() != max) {
      mDownloadDialog.setMax(max);
    }
    if (done) {
      mDownloadDialog.setProgress((int) (contentLength / ONE_MILLION_BYTE));
    } else {
      mDownloadDialog.setProgress((int) (bytesRead / ONE_MILLION_BYTE));
    }
  }

  private void update(File file) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
    weakReference.get().startActivity(intent);
  }

  private void initRetrofit() {
    OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
      @Override
      public void log(String message) {
        System.out.println("message = " + message);
      }
    });
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    httpClientBuilder.addNetworkInterceptor(new DownloadProgressInterceptor(this));
    Retrofit retrofit = new Retrofit.Builder()
        .client(httpClientBuilder.build())
        .addConverterFactory(GsonConverterFactory.create(AppData.globalGson()))
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .baseUrl("http://115.156.188.230/d2.eoemarket.com/")
        .build();
    downloadApi = retrofit.create(DownloadApi.class);
  }

  private void initDownloadDialog() {
    if (mDownloadDialog == null) {
      mDownloadDialog = new ProgressDialog(weakReference.get());
      mDownloadDialog.setProgressNumberFormat("%1d MB/%2d MB");
      mDownloadDialog.setTitle("应用更新");
      mDownloadDialog.setMessage("正在下载，请稍后...");
      mDownloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      mDownloadDialog.setCancelable(false);
    }
  }

  public interface DownloadApi {
    @Streaming
    @GET
    Observable<Response<ResponseBody>> downloadApk(@Url String url);
  }

}
