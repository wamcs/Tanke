package com.lptiyu.tanke.base.splash;

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
import com.lptiyu.tanke.activities.initialization.ui.GuideActivity;
import com.lptiyu.tanke.activities.initialization.ui.LoginActivity;
import com.lptiyu.tanke.mybase.MyBaseActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import rx.Observable;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/12
 *
 * @author ldx
 */
public abstract class BaseSplashActivity extends MyBaseActivity {

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

    private void init() {
        imageFile = createStorageFile();
        if (imageFile == null) {
            throw new IllegalStateException("SplashActivity : splash file create failed.");
        }
        splashView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    public void process() {
        if (isFirstInApp()) {
            startActivity(new Intent(this, GuideActivity.class));
            finish();
        } else {
            Glide.with(this).load(imageFile).error(R.drawable.bg_splash).into(splashView);
            smoothStartNext();
        }
    }

    protected void smoothStartNext() {
        final Intent intent = new Intent();
        if (isAccountsValid()) {
            intent.setClass(this, MainActivity.class);
        } else {
            intent.setClass(this, LoginActivity.class);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private File createStorageFile() {
        return imageFile = new File(getCacheDir(), "splash.jpg");
    }

    protected abstract boolean isFirstInApp();

    protected abstract boolean isAccountsValid();

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
