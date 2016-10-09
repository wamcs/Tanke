package com.lptiyu.tanke.fragments.home;

import com.lptiyu.tanke.entity.response.HomeBannerAndHotResponse;
import com.lptiyu.tanke.entity.response.HomeSortResponse;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.net.Response;
import com.lptiyu.tanke.utils.xutils3.RequestParamsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsRequestCallBack;
import com.lptiyu.tanke.utils.xutils3.XUtilsUrls;

import org.xutils.http.RequestParams;

/**
 * Created by Jason on 2016/9/23.
 */

public class HomePresenter implements HomeContact.IHomePresenter {
    private HomeContact.IHomeView view;
    private int page = 1;

    public HomePresenter(HomeContact.IHomeView view) {
        this.view = view;
    }

    @Override
    public void firstLoadBannerAndHot() {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.HOME_BANNER_RECOMMEND);
        params.addBodyParameter("uid", Accounts.getId() + "");
        params.addBodyParameter("city", Accounts.getCityCode());
        params.addBodyParameter("page", page + "");
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<HomeBannerAndHotResponse>() {
            @Override
            protected void onSuccess(HomeBannerAndHotResponse response) {
                if (response.status == Response.RESPONSE_OK) {
                    view.successFirstLoadBannerAndHot(response.data);
                } else {
                    view.failLoad();
                }
            }

            @Override
            protected void onFailed(String errorMsg) {
                if (errorMsg != null)
                    view.failLoad(errorMsg);
                else
                    view.netException();
            }
        }, HomeBannerAndHotResponse.class);
    }

    @Override
    public void loadSort() {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.HOME_SORT_TAB);
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<HomeSortResponse>() {
            @Override
            protected void onSuccess(HomeSortResponse response) {
                if (response.status == Response.RESPONSE_OK) {
                    view.successLoadSort(response.data);
                } else {
                    view.failLoad();
                }
            }

            @Override
            protected void onFailed(String errorMsg) {
                if (errorMsg != null)
                    view.failLoad(errorMsg);
                else
                    view.netException();
            }
        }, HomeSortResponse.class);
    }
}
