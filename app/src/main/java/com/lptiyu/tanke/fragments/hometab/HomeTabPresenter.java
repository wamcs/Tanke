package com.lptiyu.tanke.fragments.hometab;

import com.lptiyu.tanke.entity.response.HomeGameListResponse;
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

public class HomeTabPresenter implements HomeTabContact.IHomeTabPresenter {
    private HomeTabContact.IHomeTabView view;
    private int page = 1;

    public HomeTabPresenter(HomeTabContact.IHomeTabView view) {
        this.view = view;
    }

    @Override
    public void firstLoadGameList(int cid) {
        page = 1;
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.HOME_GAME_LIST_NEW);
        params.addBodyParameter("uid", Accounts.getId() + "");
        params.addBodyParameter("city", Accounts.getCityCode());
        params.addBodyParameter("lat", Accounts.getLatitude() + "");
        params.addBodyParameter("lng", Accounts.getLongitude() + "");
        params.addBodyParameter("cid", cid + "");
        params.addBodyParameter("page", page + "");
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<HomeGameListResponse>() {
            @Override
            protected void onSuccess(HomeGameListResponse response) {
                if (response.status == Response.RESPONSE_OK) {
                    view.successFirstLoadGameList(response.data);
                } else {
                    view.failLoad(response.info);
                }
            }

            @Override
            protected void onFailed(String errorMsg) {
                if (errorMsg != null)
                    view.failLoad(errorMsg);
                else
                    view.netException();
            }
        }, HomeGameListResponse.class);
    }

    @Override
    public void loadMoreGame(int cid) {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.HOME_GAME_LIST_NEW);
        params.addBodyParameter("uid", Accounts.getId() + "");
        params.addBodyParameter("city", Accounts.getCityCode());
        params.addBodyParameter("lat", Accounts.getLatitude() + "");
        params.addBodyParameter("lng", Accounts.getLongitude() + "");
        params.addBodyParameter("cid", cid + "");
        params.addBodyParameter("page", ++page + "");
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<HomeGameListResponse>() {
            @Override
            protected void onSuccess(HomeGameListResponse response) {
                if (response.status == Response.RESPONSE_OK) {
                    view.successLoadMoreGame(response.data);
                } else {
                    view.failLoadMoreGame(response.info);
                }
            }

            @Override
            protected void onFailed(String errorMsg) {
                if (errorMsg != null)
                    view.failLoad(errorMsg);
                else
                    view.netException();
            }
        }, HomeGameListResponse.class);
    }

    @Override
    public void reloadGameList(int cid) {
        page = 1;
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.HOME_GAME_LIST_NEW);
        params.addBodyParameter("uid", Accounts.getId() + "");
        params.addBodyParameter("city", Accounts.getCityCode());
        params.addBodyParameter("lat", Accounts.getLatitude() + "");
        params.addBodyParameter("lng", Accounts.getLongitude() + "");
        params.addBodyParameter("cid", cid + "");
        params.addBodyParameter("page", page + "");
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<HomeGameListResponse>() {
            @Override
            protected void onSuccess(HomeGameListResponse response) {
                if (response.status == Response.RESPONSE_OK) {
                    view.successReloadGame(response.data);
                } else {
                    view.failLoad(response.info);
                }
            }

            @Override
            protected void onFailed(String errorMsg) {
                if (errorMsg != null)
                    view.failLoad(errorMsg);
                else
                    view.netException();
            }
        }, HomeGameListResponse.class);
    }
}
