package com.lptiyu.tanke.fragments.messagesystem;

import android.text.TextUtils;

import com.lptiyu.tanke.entity.response.BaseResponse;
import com.lptiyu.tanke.entity.response.MessageResponse;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.utils.xutils3.RequestParamsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsRequestCallBack;
import com.lptiyu.tanke.utils.xutils3.XUtilsUrls;

import org.xutils.http.RequestParams;

/**
 * Created by Jason on 2016/10/31.
 */

public class MessagePresenter implements MessageContact.IMessagePresenter {
    private MessageContact.IMessageView view;

    public MessagePresenter(MessageContact.IMessageView view) {
        this.view = view;
    }

    @Override
    public void loadMessage(int page) {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.MESSAGE);
        params.addBodyParameter("uid", Accounts.getId() + "");
        params.addBodyParameter("page", page + "");
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<MessageResponse>() {
            @Override
            protected void onSuccess(MessageResponse response) {
                if (response.status == BaseResponse.SUCCESS) {
                    view.successLoadMessage(response.data);
                } else {
                    if (!TextUtils.isEmpty(response.info)) {
                        view.failLoad(response.info);
                    } else {
                        view.failLoad("");
                    }
                }
            }

            @Override
            protected void onFailed(String errorMsg) {
                view.failLoad(errorMsg);
            }
        }, MessageResponse.class);
    }
}
