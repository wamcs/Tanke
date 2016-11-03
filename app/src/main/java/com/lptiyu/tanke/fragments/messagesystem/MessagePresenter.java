package com.lptiyu.tanke.fragments.messagesystem;

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
            protected void onSuccess(MessageResponse messageResponse) {
                if (messageResponse.status == BaseResponse.SUCCESS) {
                    view.successLoadMessage(messageResponse.data);
                } else {
                    view.failLoad(messageResponse.info);
                }
            }

            @Override
            protected void onFailed(String errorMsg) {
                if (errorMsg != null) {
                    view.failLoad(errorMsg);
                } else {
                    view.netException();
                }
            }
        }, MessageResponse.class);
    }
}
