package com.lptiyu.tanke.utils.xutils3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.utils.FileUtils;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.ProgressDialogHelper;

import java.io.File;

/**
 * Created by Jason on 2016/8/26.
 */
public class APKDownloader {

    private ProgressDialog progressDialog;
    private String apkUrl;
    private Context context;
    private Handler handler;
    private int targetVersionCode;
    private final double ONE_MILLION_BYTE = 1024 * 1024.0;

    public APKDownloader(Context context, String apkUrl, int targetVersionCode) {
        this.context = context;
        this.targetVersionCode = targetVersionCode;
        this.apkUrl = apkUrl;
        handler = new Handler();
        init();
    }

    private void init() {
        File localAPKFile = FileUtils.isLocalAPKFileExist(targetVersionCode);
        if (localAPKFile != null) {
            update(localAPKFile);
        } else {
            initProgressDialog();
            chooseToDownload();
        }
    }

    private void initProgressDialog() {
        progressDialog = ProgressDialogHelper.getHorizontalProgressDialog(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressNumberFormat("%1d MB/%2d MB");
        progressDialog.setTitle("应用更新");
        progressDialog.setMessage("正在下载，请稍后...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
    }

    private void chooseToDownload() {
        if (NetworkUtil.isWlanAvailable()) {
            startDownloadByXUtils();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final double size = FileUtils.getFileSizeByUrl(apkUrl);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("jason", "当前要下载的升级包大小：" + size);
                            showIsContinueDownloadDialog(size);
                        }
                    });
                }
            }).start();
        }
    }

    private void showIsContinueDownloadDialog(double size) {
        new AlertDialog.Builder(context).setMessage("升级包大小" + new java.text.DecimalFormat("#0.0")
                .format(size / 1024 / 1024) + "MB,您当前不是wifi状态，是否继续下载？")
                .setPositiveButton("我是土豪，继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startDownloadByXUtils();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RunApplication.getInstance().AppExit();
            }
        }).show();
    }

    public void startDownloadByXUtils() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        XUtilsHelper.getInstance().downLoad(apkUrl, new XUtilsHelper.IDownloadCallback() {
            @Override
            public void successs(File file) {
                progressDialog.setMessage("下载完成");
                update(file);
            }

            @Override
            public void progress(long total, long current, boolean isDownloading) {
                //游戏进度
                int max = (int) (total / ONE_MILLION_BYTE);
                if (progressDialog.getMax() != max) {
                    progressDialog.setMax(max);
                }
                progressDialog.setProgress((int) (current / ONE_MILLION_BYTE));
            }

            @Override
            public void finished() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onError(String errMsg) {
                PopupWindowUtils.getInstance().showNetExceptionPopupwindow(context, new PopupWindowUtils
                        .OnNetExceptionListener() {
                    @Override
                    public void onClick(View view) {
                        startDownloadByXUtils();
                    }
                });
            }
        });
    }

    private void update(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
        RunApplication.getInstance().AppExit();
    }
}
