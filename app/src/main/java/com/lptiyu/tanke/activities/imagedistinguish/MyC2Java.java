package com.lptiyu.tanke.activities.imagedistinguish;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * Created by Lovepyj on 2016/7/24.
 */
public class MyC2Java {
    static int count = 0;
    public static Context showContext;

    public static void show() {
        postToMain(new Runnable() {
            @Override
            public void run() {
                if (MyC2Java.listener != null) {
                    listener.onSuccess();
                }
            }
        });
        Log.v("show", "show:" + count);
        count++;

    }

    public static void unshow() {
        //        postToMain(new Runnable() {
        //            @Override
        //            public void run() {
        //                Toast.makeText(showContext, "unshow:" + count, Toast.LENGTH_SHORT).show();
        //            }
        //        });
        //        count++;
        //        Log.v("unshow", "unshow:" + count);
    }

    public static void postToMain(Runnable r) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(r);
    }

    public static void setOnSuccessDistinguishListener(ISuccessDistinguishListener listener) {
        MyC2Java.listener = listener;
    }

    private static ISuccessDistinguishListener listener;

    public interface ISuccessDistinguishListener {
        void onSuccess();
    }
}
