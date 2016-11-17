package com.lptiyu.tanke.mybase;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by Jason on 2016/9/23.
 */

public class MyBaseFragment extends Fragment implements IBaseView {

    @Override
    public void failLoad(String errMsg) {
        if (!TextUtils.isEmpty(errMsg)) {
            Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "请求失败", Toast.LENGTH_SHORT).show();
        }
    }
}
