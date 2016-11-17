package com.lptiyu.tanke.activities.gameover;

import com.lptiyu.tanke.entity.response.BaseResponse;
import com.lptiyu.tanke.entity.response.GameOverRewardResponse;
import com.lptiyu.tanke.entity.response.GetScoreAfterShareResponse;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.utils.xutils3.RequestParamsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsRequestCallBack;
import com.lptiyu.tanke.utils.xutils3.XUtilsUrls;

import org.xutils.http.RequestParams;

/**
 * Created by Jason on 2016/11/2.
 */

public class GameOverPresenter implements GameOverContact.IGameOverPresenter {
    private GameOverContact.IGameOverView view;

    public GameOverPresenter(GameOverContact.IGameOverView view) {
        this.view = view;
    }

    @Override
    public void loadGameOverReward(long gameId, long recordId) {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.GAME_OVER_REWARD);
        params.addBodyParameter("uid", Accounts.getId() + "");
        params.addBodyParameter("game_id", gameId + "");
        params.addBodyParameter("record_id", recordId + "");
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<GameOverRewardResponse>() {
            @Override
            protected void onSuccess(GameOverRewardResponse response) {
                if (response.status == BaseResponse.SUCCESS) {
                    view.successLoadGameOverReward(response.data);
                } else {
                    view.failLoad(response.info);
                }
            }

            @Override
            protected void onFailed(String errorMsg) {
                view.failLoad(errorMsg);
            }
        }, GameOverRewardResponse.class);
    }

    @Override
    public void getScoreAfterShare() {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.GET_SCORE_AFTER_SHARE);
        params.addBodyParameter("uid", Accounts.getId() + "");
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<GetScoreAfterShareResponse>() {
            @Override
            protected void onSuccess(GetScoreAfterShareResponse response) {
                if (response.status == BaseResponse.SUCCESS) {
                    view.successGetScore(response.data);
                } else {
                    view.failLoad(response.info);
                }
            }

            @Override
            protected void onFailed(String errorMsg) {
                view.failLoad(errorMsg);
            }
        }, GetScoreAfterShareResponse.class);
    }
}
