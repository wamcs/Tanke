package com.lptiyu.tanke.activities.feedback;

import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.net.Response;
import com.lptiyu.tanke.utils.xutils3.RequestParamsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsRequestCallBack;
import com.lptiyu.tanke.utils.xutils3.XUtilsUrls;
import com.lptiyu.tanke.entity.response.FeedbackResponse;

import org.xutils.http.RequestParams;

/**
 * Created by Jason on 2016/8/9.
 */
public class FeedbackPresenter implements FeedbackContact.IFeedbackPresenter {
    private FeedbackContact.IFeedbackView view;

    public FeedbackPresenter(FeedbackContact.IFeedbackView view) {
        this.view = view;
    }

    @Override
    public void submitFeedback(String contact, String content) {
        RequestParams param = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.FEED_BACK);
        param.addBodyParameter("uid", Accounts.getId() + "");
        param.addBodyParameter("type", "1");//1代表android端，2代表ios端
        param.addBodyParameter("phone", contact);
        param.addBodyParameter("content", content);
        XUtilsHelper.getInstance().get(param, new XUtilsRequestCallBack<FeedbackResponse>() {
            @Override
            protected void onSuccess(FeedbackResponse feedbackResponse) {
                if (feedbackResponse.status == Response.RESPONSE_OK) {
                    view.successSubmit();
                } else {
                    view.failSubmit();
                }
            }

            @Override
            protected void onFailed(String errorMsg) {
                view.netException();
            }
        }, FeedbackResponse.class);
    }
}
