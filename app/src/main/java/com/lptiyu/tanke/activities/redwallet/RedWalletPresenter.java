package com.lptiyu.tanke.activities.redwallet;

import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.utils.xutils3.RequestParamsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsRequestCallBack;
import com.lptiyu.tanke.utils.xutils3.XUtilsUrls;

import org.xutils.http.RequestParams;

/**
 * Created by Jason on 2016/11/8.
 */

public class RedWalletPresenter implements RedWalletContact.IRedWalletPresenter {
    private RedWalletContact.IRedWalletView view;

    public RedWalletPresenter(RedWalletContact.IRedWalletView view) {
        this.view = view;
    }

    @Override
    public void loadRedWalletRecord() {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.LOOK_RED_WALLET_RECORD);
        params.addBodyParameter("uid", Accounts.getId() + "");
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<String>() {
            @Override
            protected void onSuccess(String s) {
                view.successLoadRedWalletRecord();
            }

            @Override
            protected void onFailed(String errorMsg) {
                view.failLoad(errorMsg);
            }
        }, String.class);
    }

    @Override
    public void requestRedWallet() {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.REQUEST_RED_WALLET);
        params.addBodyParameter("uid", Accounts.getId() + "");
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<String>() {
            @Override
            protected void onSuccess(String s) {
                view.successRequestRedWallet();
            }

            @Override
            protected void onFailed(String errorMsg) {
                view.failLoad(errorMsg);
            }
        }, String.class);
    }
}
