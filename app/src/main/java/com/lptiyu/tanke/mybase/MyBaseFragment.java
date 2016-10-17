package com.lptiyu.tanke.mybase;

import android.support.v4.app.Fragment;

import com.lptiyu.tanke.utils.ToastUtil;

/**
 * Created by Jason on 2016/9/23.
 */

public class MyBaseFragment extends Fragment implements IBaseView {
    @Override
    public void failLoad() {
        //        ToastUtil.TextToast("暂无数据");
    }

    @Override
    public void failLoad(String errMsg) {
        ToastUtil.TextToast(errMsg);
    }

    @Override
    public void netException() {
        ToastUtil.TextToast("网络异常");
    }
}
