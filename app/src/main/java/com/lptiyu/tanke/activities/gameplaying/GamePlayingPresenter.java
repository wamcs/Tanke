package com.lptiyu.tanke.activities.gameplaying;

import com.lptiyu.tanke.entity.GameRecordResponse;
import com.lptiyu.tanke.entity.response.BaseResponse;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.utils.xutils3.RequestParamsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsRequestCallBack;
import com.lptiyu.tanke.utils.xutils3.XUtilsUrls;

import org.xutils.http.RequestParams;

/**
 * Created by Jason on 2016/7/11.
 */
public class GamePlayingPresenter implements GamePlayingContract.IGamePlaying2Presenter {
    private GamePlayingContract.IGamePlaying2View view;

    public GamePlayingPresenter(GamePlayingContract.IGamePlaying2View view) {
        this.view = view;
    }

    /**
     * 从服务器请求游戏记录
     */
    @Override
    public void downLoadGameRecord(long gameId, long teamId) {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.GET_RECORD);
        params.addBodyParameter("uid", Accounts.getId() + "");
        params.addBodyParameter("game_id", gameId + "");
        params.addBodyParameter("team_id", teamId + "");//个人游戏传0，团队游戏传1
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<GameRecordResponse>() {
            @Override
            protected void onSuccess(GameRecordResponse response) {
                if (response.status == BaseResponse.SUCCESS) {
                    view.successDownLoadRecord(response.data);
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
                if (errorMsg != null) {
                    view.failLoad(errorMsg);
                } else {
                    view.netException();
                }
            }
        }, GameRecordResponse.class);
    }

    @Override
    public void reloadGameRecord(long gameId, long teamId) {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.GET_RECORD);
        params.addBodyParameter("uid", Accounts.getId() + "");
        params.addBodyParameter("game_id", gameId + "");
        params.addBodyParameter("team_id", teamId + "");//个人游戏传0，团队游戏传1
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<GameRecordResponse>() {
            @Override
            protected void onSuccess(GameRecordResponse response) {
                if (response.status == BaseResponse.SUCCESS) {
                    view.successReloadRecord(response.data);
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
                if (errorMsg != null) {
                    view.failLoad(errorMsg);
                } else {
                    view.netException();
                }
            }
        }, GameRecordResponse.class);
    }

}
