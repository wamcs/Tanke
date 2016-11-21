package com.lptiyu.tanke.mybase;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.NetworkUtil;

/**
 * Created by Jason on 2016/9/23.
 */

public class MyBaseFragment extends Fragment implements IBaseView {

    @Override
    public void failLoad(String errMsg) {
        if (!NetworkUtil.checkIsNetworkConnected()) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
        } else {
            if (TextUtils.isEmpty(errMsg)) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.fail_load), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
