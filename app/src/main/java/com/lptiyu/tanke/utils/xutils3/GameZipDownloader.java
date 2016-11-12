package com.lptiyu.tanke.utils.xutils3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.FileUtils;
import com.lptiyu.tanke.utils.GameZipUtils;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.ProgressDialogHelper;

import java.io.File;

/**
 * Created by Jason on 2016/8/20.
 */
public class GameZipDownloader {

    private ProgressDialog progressDialog;
    private String tempGameZipUrl;
    private long gameId;
    private FinishDownloadCallback callback;
    private String fileName;
    private Context context;
    private Handler handler;

    public GameZipDownloader(Context context, String tempGameZipUrl, final long gameId, FinishDownloadCallback
            callback) {
        this.context = context;
        this.tempGameZipUrl = tempGameZipUrl;
        this.gameId = gameId;
        this.callback = callback;
        this.fileName = tempGameZipUrl.substring(tempGameZipUrl.lastIndexOf('/') + 1, tempGameZipUrl.lastIndexOf('.'));
        handler = new Handler();

        initProgressDialog();
        load();
    }

    private void load() {
        GameZipUtils gameZipUtils = new GameZipUtils();
        //如果游戏包不存在或者游戏包有更新，则都需要下载最新的游戏包
        if (gameZipUtils.isParsedFileExistAndUpdate(fileName) == null) {
            String parsedFileExist = gameZipUtils.isParsedFileExist(gameId);
            if (parsedFileExist != null)
                FileUtils.deleteDirectory(parsedFileExist);
            //游戏包不存在，需要下载游戏包
            chooseToDownload();
        } else {
            if (callback != null) {
                callback.onFinishedDownload();
            }
        }
    }

    private void initProgressDialog() {
        progressDialog = ProgressDialogHelper.getSpinnerProgressDialog(context);
    }

    private void chooseToDownload() {
        if (NetworkUtil.isWlanAvailable()) {
            download();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final double size = FileUtils.getFileSizeByUrl(tempGameZipUrl);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("jason", "当前要下载的游戏包大小：" + size);
                            if (size >= 1024 * 1024) {
                                showIsContinueDownloadDialog(size);
                            } else {
                                download();
                            }
                        }
                    });
                }
            }).start();
        }
    }

    public void download() {
        progressDialog.show();
        XUtilsHelper.getInstance().downLoad(tempGameZipUrl, DirUtils.getGameDirectory().getAbsolutePath(), new
                XUtilsHelper.IDownloadCallback() {
            @Override
            public void successs(File file) {
                String zippedFilePath = file.getAbsolutePath();
                GameZipUtils gameZipUtils = new GameZipUtils();
                //解压文件
                gameZipUtils.parseZipFile(zippedFilePath);
                String parsedFilePath = gameZipUtils.isParsedFileExist(gameId);
                if (parsedFilePath != null) {
                    file.delete();
                    if (callback != null) {
                        callback.onFinishedDownload();
                    }
                } else {
                    Toast.makeText(context, "游戏包解压失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void progress(long total, long current, boolean isDownloading) {
                //游戏进度
                if (progressDialog != null) {
                    progressDialog.setMessage("加载中" + current * 100 / total + "%");
                }
            }

            @Override
            public void finished() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onError(String errMsg) {
                PopupWindowUtils.getInstance().showFailLoadPopupwindow(context);
            }
        });
    }

    private void showIsContinueDownloadDialog(double size) {
        new AlertDialog.Builder(context).setMessage("游戏包大小" + new java.text.DecimalFormat("#0.0")
                .format(size / 1024 / 1024) + "MB,您当前不是wifi状态，是否继续下载？")
                .setPositiveButton("我是土豪，继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        download();
                    }
                }).setNegativeButton("取消", null).show();
    }

    public interface FinishDownloadCallback {
        void onFinishedDownload();
    }
}
