package com.lptiyu.tanke.activities.imagedistinguish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.enums.ResultCode;
import com.lptiyu.tanke.global.Conf;

import cn.easyar.engine.EasyAR;

public class ImageDistinguishActivity extends Activity {
    public static Context currContext;

    /*
    * Steps to create the key for this sample:
    *  1. login www.easyar.com
    *  2. create app with
    *      Name: HelloARVideo
    *      Package Name: cn.easyar.samples.helloarvideo
    *  3. find the created item in the list and show key
    *  4. set key string bellow
    */
    static String key =
            "3tDDVYPVBvQ3sFL6qZYE208oShLmwE701HnisoQOtM1CHBw3a78VlWRF6YFgCjwijLdUdPAzlUfU7PspcMsueAXRoPfgLktEhoeG7e719f320a1b6dfd44356f8c4de9d7d8ARkKDZE562C3BwYoNN3mvUrW5KuxSn4hEV1AW0WmlffE1pIfsJhN9MSnvrEJOrTNjQW2";

    static {
        System.loadLibrary("EasyAR");
        System.loadLibrary("HelloARVideoNative");
    }

    public Button btn_start_tracker;

    public static native void nativeInitGL();

    public static native void nativeResizeGL(int w, int h);

    public static native void nativeRender();

    private native boolean nativeInit(String[] strings, String[] strings1);

    public static native void nativeStartAr();

    public static native void nativeStopAr();

    private native boolean nativeInit();

    private native void nativeDestory();

    private native void nativeRotationChange(boolean portrait);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyC2Java.showContext = this;
        // currContext=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_distinguish);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams
                .FLAG_KEEP_SCREEN_ON);

        EasyAR.initialize(this, key);
        //        nativeInit();
        //        String imgPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cup.jpg";
        //        Log.i("jason", "图片路径：" + imgPath);
        String imgPath = getIntent().getStringExtra(Conf.IMG_DISTINGUISH_URL) + "";
        nativeInit(new String[]{imgPath}, new String[]{""});

        GLView glView = new GLView(this);
        glView.setRenderer(new Renderer());
        glView.setZOrderMediaOverlay(true);

        ((ViewGroup) findViewById(R.id.preview)).addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        nativeRotationChange(getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0);

        btn_start_tracker = (Button) findViewById(R.id.btn_startScan);
        btn_start_tracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageDistinguishActivity.nativeStartAr();
                btn_start_tracker.setClickable(false);
                btn_start_tracker.setEnabled(false);
                btn_start_tracker.setText("正在扫描...");
            }
        });

        MyC2Java.setOnSuccessDistinguishListener(new MyC2Java.ISuccessDistinguishListener() {
            @Override
            public void onSuccess() {
                AlertDialog.Builder builder = new AlertDialog.Builder(ImageDistinguishActivity.this);
                AlertDialog dialog = builder.setMessage("识别成功").setNegativeButton("确定", new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ImageDistinguishActivity.this.setResult(ResultCode.IMAGE_DISTINGUISH);
                        finish();
                    }
                }).create();
                dialog.show();
                ImageDistinguishActivity.nativeStopAr();
                btn_start_tracker.setText("扫描完毕");
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        nativeRotationChange(getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nativeDestory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EasyAR.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EasyAR.onPause();
    }
}
