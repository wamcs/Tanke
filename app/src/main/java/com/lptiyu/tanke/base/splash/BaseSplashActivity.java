package com.lptiyu.tanke.base.splash;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lptiyu.tanke.MainActivity;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.initialization.ui.GuideActivity;
import com.lptiyu.tanke.initialization.ui.LoginActivity;
import com.lptiyu.tanke.utils.anim.SimpleAnimatorListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/12
 *
 * @author ldx
 */
public abstract class BaseSplashActivity extends BaseActivity {

  private File imageFile;

  private ImageView splashView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    splashView = new ImageView(this);
    setContentView(splashView);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    init();
    process();
  }

  protected void configSplashView(ImageView splashView) {
    splashView.setScaleType(ImageView.ScaleType.CENTER_CROP);
  }

  private void init() {
    imageFile = createStorageFile();
    if (imageFile == null) {
      throw new IllegalStateException("SplashActivity : splash file create failed.");
    }
    configSplashView(splashView);
  }

  public void process() {
    if (isFirstInApp()) {
      startActivity(new Intent(this, GuideActivity.class));
      finish();
    } else {
//      if (isImageValid()) {
//        Glide.with(this).load(imageFile).into(splashView);
//        smoothStartNext();
//      } else {
        startNext();
//      }
    }

//    fetchSplashUrl()
//        .subscribeOn(Schedulers.io())
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(new SaveAction(getBaseContext(), imageFile), new Action1<Throwable>() {
//          @Override
//          public void call(Throwable throwable) {
//            if (imageFile.delete()) {
//              Timber.e("Delete file failed.");
//            }
//          }
//        });
  }

  protected void startNext() {
    Intent intent = new Intent();
    if (isAccountsValid()) {
      intent.setClass(this, MainActivity.class);
    } else {
      intent.setClass(this, LoginActivity.class);
    }
    startActivity(intent);
    finish();
  }

  protected void smoothStartNext() {
    final Intent intent = new Intent();
    if (isAccountsValid()) {
      intent.setClass(this, MainActivity.class);
    } else {
      intent.setClass(this, LoginActivity.class);
    }
    splashView
        .animate()
        .scaleX(1.2f)
        .scaleY(1.2f)
        .setDuration(3000)
        .setListener(new SimpleAnimatorListener() {
          @Override
          public void onAnimationEnd(Animator animation) {
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
          }
        });
  }

  protected boolean isImageValid() {
    return imageFile.exists() && imageFile.isFile();
  }


  private File createStorageFile() {
    return imageFile = new File(getCacheDir(), "splash.jpg");
  }

  protected abstract Observable<String> fetchSplashUrl();

  protected abstract boolean isFirstInApp();

  protected abstract boolean isAccountsValid();

  @Override
  public ActivityController getController() {
    return null;
  }


  protected static class SimpleSaveTarget extends SimpleTarget<Bitmap> {

    private File saveFile;

    public SimpleSaveTarget(File file) {
      this.saveFile = file;
    }

    @Override
    public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
      Observable.just(resource)
          .subscribeOn(Schedulers.io())
          .subscribe(new Action1<Bitmap>() {
            @Override
            public void call(Bitmap bitmap) {
              File file = saveFile;
              OutputStream os = null;
              try {
                os = new FileOutputStream(file);
              } catch (FileNotFoundException e) {
                throw Exceptions.propagate(e);
              }
              resource.compress(Bitmap.CompressFormat.JPEG, 100, os);
            }
          });
    }
  }

  protected static class SaveAction implements Action1<String> {
    private Context context;
    private File imageFile;

    public SaveAction(Context context, File imageFile) {
      this.context = context;
      this.imageFile = imageFile;
    }

    @Override
    public void call(String s) {
      Glide.with(context)
          .load(s)
          .asBitmap()
          .into(new SimpleSaveTarget(imageFile));
    }
  }

}
