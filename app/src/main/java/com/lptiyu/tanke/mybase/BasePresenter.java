package com.lptiyu.tanke.mybase;

import com.lptiyu.tanke.utils.xutils3.RequestParamsHelper;

import org.xutils.http.RequestParams;

/**
 * Created by Jason on 2016/10/14.
 */

public class BasePresenter {
    public RequestParams params;

    public BasePresenter(String url) {
        params = RequestParamsHelper.getBaseRequestParam(url);
    }
}
