package com.lptiyu.tanke.utils.xutils3;

/**
 * Xutils返回回调
 */
public abstract class XUtilsRequestCallBack<T> {
    protected abstract void onSuccess(T t);

    protected abstract void onFailed(String errorMsg);

    public void onStart() {
    }

    public void onLoading(long total, long current, boolean isUploading) {
    }
}
