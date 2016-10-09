package com.lptiyu.tanke.mybase;

/**
 * Created by Jason on 2016/9/23.
 */

public interface IBaseView {
    void failLoad();

    void failLoad(String errMsg);

    void netException();
}
