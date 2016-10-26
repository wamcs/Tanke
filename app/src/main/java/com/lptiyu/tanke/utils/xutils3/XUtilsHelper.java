package com.lptiyu.tanke.utils.xutils3;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lptiyu.tanke.utils.DirUtils;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

/**
 * Created by Jason on 2016/8/2.
 */
public class XUtilsHelper {
    private static XUtilsHelper instance;
    public Callback.Cancelable cancelable;
    private static Gson gson = new Gson();

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

    /**
     * 下载
     *
     * @param fileUrl
     * @param callback
     */
    public void downLoad(String fileUrl, String fileSavePath, final IDownloadCallback callback) {
        //xutils不支持非http协议
        RequestParams params;
        if (fileUrl.startsWith("http://"))
            params = new RequestParams(fileUrl);
        else
            params = new RequestParams("http://" + fileUrl);
        //设置断点续传
        params.setAutoResume(true);
        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/'));
        if (fileSavePath != null)
            params.setSaveFilePath(fileSavePath + "/" + fileName);
        else
            params.setSaveFilePath(DirUtils.getGameDirectory() + "/" + fileName);

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
                if (callback != null) {
                    callback.progress(total, current, isDownloading);
                }
            }

            @Override
            public void onSuccess(File file) {
                Log.i("jason", "xutils3.0下载的路径：" + file.getAbsolutePath());
                Log.i("jason", "文件名：" + file.getName());
                Log.i("jason", "父目录：" + file.getParentFile());

                if (callback != null) {
                    callback.successs(file);
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (callback != null) {
                    callback.onError(ex.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if (callback != null) {
                    callback.finished();
                }
            }
        });
    }

    /**
     * 下载回调接口
     */
    public interface IDownloadCallback {
        void successs(File file);

        void progress(long total, long current, boolean isDownloading);

        void finished();

        void onError(String errMsg);
    }

    //异步Get调用的方法
    public <T> void get(final RequestParams params, final XUtilsRequestCallBack<T> callBack, final Class<T> clazz) {
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("result", result);
                if (callBack != null) {
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            T t = new Gson().fromJson(result, clazz);
                            callBack.onSuccess(t);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            Log.i("result", "gson解析异常:" + e.getMessage());
                        }
                    } else {
                        callBack.onSuccess(null);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                failure(callBack, ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

        });
    }

    //异步Post调用的方法
    public <T> void post(final String url, Object req, final XUtilsRequestCallBack<T> callBack, final Class<T> clazz) {
        RequestParams requestParams = getRequestParams(req, url);
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("result", "onSuccess" + result);
                if (callBack != null) {
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            T t = new Gson().fromJson(result, clazz);
                            callBack.onSuccess(t);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            Log.i("result", "gson解析错误：" + e.getMessage());
                        }
                    } else {
                        callBack.onSuccess(null);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("result", "onError:" + ex.getMessage());
                failure(callBack, ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    //同步Get调用的方法
    public <T> T syncGet(RequestParams params, Class<T> clazz) {
        try {
            String result = null;
            try {
                result = x.http().getSync(params, String.class);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            return new Gson().fromJson(result, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //同步Post调用的方法
    public <T> T syncPost(String method, Object req, Class<T> clazz) {
        RequestParams requestParams = getRequestParams(req, method);
        try {
            String result = null;
            try {
                result = x.http().postSync(requestParams, String.class);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            return new Gson().fromJson(result, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //对参数进行封装格式，为了方便以后的维护，在这里可以统一处理头部信息以及一些上传下载的配置
    private RequestParams getRequestParams(Object req, String url) {
        //        RequestParams requestParams = new RequestParams(url);
        RequestParams requestParams = RequestParamsHelper.getBaseRequestParam(url);
        requestParams.setConnectTimeout(15000);
        if (req instanceof String) {
            requestParams.setBodyContent(String.valueOf(req));
        } else {
            requestParams.setBodyContent(gson.toJson(req));
        }
        return requestParams;
    }

    /**
     * 失败处理
     */
    public void failure(XUtilsRequestCallBack callBack, Throwable ex) {
        String s = ex.getMessage();
        if (ex instanceof HttpException) { // 网络错误
            HttpException exception = (HttpException) ex;
            if (exception.getCode() == 900 || exception.getCode() == 901) {

            } else if (exception.getCode() >= 500 && exception.getCode() < 600) {

            } else {

            }
        } else {

        }
        if (callBack != null) {
            callBack.onFailed(s);
        }
    }
}
