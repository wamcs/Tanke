package com.lptiyu.tanke.activities.userdirectionrunlist;

import com.lptiyu.tanke.entity.response.BaseResponse;
import com.lptiyu.tanke.entity.response.DRRecordListResponse;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.utils.xutils3.RequestParamsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsRequestCallBack;
import com.lptiyu.tanke.utils.xutils3.XUtilsUrls;

import org.xutils.http.RequestParams;

/**
 * Created by Jason on 2016/11/11.
 */

public class UserDirectionRunPresenter implements UserDirectionRunContact.IUserDirectionRunPresenter {
    private UserDirectionRunContact.IUserDirectionRunView view;

    public UserDirectionRunPresenter(UserDirectionRunContact.IUserDirectionRunView view) {
        this.view = view;
    }

    @Override
    public void loadDRList() {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.DR_RECORD_LIST);
        params.addBodyParameter("uid", Accounts.getId() + "");
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<DRRecordListResponse>() {
            @Override
            protected void onSuccess(DRRecordListResponse response) {
                if (response.status == BaseResponse.SUCCESS) {
                    view.successLoadDRList(response.data);
                } else {
                    if (response.info != null) {
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
        }, DRRecordListResponse.class);
    }
}
