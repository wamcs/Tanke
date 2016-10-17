package com.lptiyu.tanke.activities.gamedetail;

import com.lptiyu.tanke.entity.EnterGameResponse;
import com.lptiyu.tanke.entity.response.BaseResponse;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.utils.xutils3.RequestParamsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsRequestCallBack;
import com.lptiyu.tanke.utils.xutils3.XUtilsUrls;

import org.xutils.http.RequestParams;

/**
 * Created by Jason on 2016/10/10.
 */

public class GameDetailPresenter implements GameDetailContact.IGameDetailPresenter {
    private GameDetailContact.IGameDetailView view;

    public GameDetailPresenter(GameDetailContact.IGameDetailView view) {
        this.view = view;
    }

    @Override
    public void enterGame(long gameId, int type) {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.ENTER_GAME);
        params.addBodyParameter("uid", Accounts.getId() + "");
        params.addBodyParameter("game_id", gameId + "");
        params.addBodyParameter("type", type + "");
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<EnterGameResponse>() {
            @Override
            protected void onSuccess(EnterGameResponse response) {
                if (response.status == BaseResponse.SUCCESS) {
                    view.successEnterGame();
                } else {
                    if (response.info != null) {
                        view.failLoad(response.info);
                    } else {
                        view.failLoad();
                    }
                }
            }

            @Override
            protected void onFailed(String errorMsg) {
                if (errorMsg != null)
                    view.failLoad(errorMsg);
                else
                    view.netException();
            }
        }, EnterGameResponse.class);
    }

    @Override
    public void leaveGame(long gameId) {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.LEAVE_GAME);
        params.addBodyParameter("uid", Accounts.getId() + "");
        params.addBodyParameter("game_id", gameId + "");
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<BaseResponse>() {
            @Override
            protected void onSuccess(BaseResponse response) {
                if (response != null) {
                    if (response.status == BaseResponse.SUCCESS) {
                        view.successLeaveGame();
                    } else {
                        view.failLeaveGame(response.info);
                    }
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
        }, BaseResponse.class);
    }
}
