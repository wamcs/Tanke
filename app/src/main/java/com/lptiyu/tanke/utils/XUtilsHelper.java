package com.lptiyu.tanke.utils;

import android.util.Log;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

/**
 * Created by Jason on 2016/8/2.
 */
public class XUtilsHelper {
    private static XUtilsHelper instance;
    private Callback.Cancelable cancelable;

    private XUtilsHelper() {
    }

    public static XUtilsHelper getInstance() {
        if (instance == null)
            instance = new XUtilsHelper();
        return instance;
    }

    public void cancelDownloadTask() {
        if (cancelable != null) {
            cancelable.cancel();
        }
    }

    public void downLoadGameZip(String fileUrl, final IDownloadGameZipFileListener listener) {
        //xutils不支持非http协议
        RequestParams params;
        if (fileUrl.startsWith("http://"))
            params = new RequestParams(fileUrl);
        else
            params = new RequestParams("http://" + fileUrl);
        //设置断点续传
        params.setAutoResume(true);
        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/'));
        params.setSaveFilePath(DirUtils.getTempDirectory() + "/" + fileName);
        params.setAutoRename(false);
        params.setConnectTimeout(15000);
        //进度条
        cancelable = x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                //进度条
                if (listener != null) {
                    listener.progress(total, current, isDownloading);
                }
            }

            @Override
            public void onSuccess(File file) {
                Log.i("jason", "xutils3.0下载的游戏包路径：" + file.getAbsolutePath());
                Log.i("jason", "文件名：" + file.getName());
                Log.i("jason", "父目录：" + file.getParentFile());

                if (listener != null) {
                    listener.successs(file);
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if (listener != null) {
                    listener.finished();
                }
            }
        });
    }

    public interface IDownloadGameZipFileListener {
        void successs(File file);

        void progress(long total, long current, boolean isDownloading);

        void finished();
    }
}
