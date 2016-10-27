package com.lptiyu.tanke.activities.directionrun;

import com.lptiyu.tanke.entity.response.BaseResponse;
import com.lptiyu.tanke.entity.response.RunLineResponse;
import com.lptiyu.tanke.entity.response.RunSignUpResponse;
import com.lptiyu.tanke.entity.response.StartRunResponse;
import com.lptiyu.tanke.entity.response.StopRunResponse;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.utils.xutils3.RequestParamsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsRequestCallBack;
import com.lptiyu.tanke.utils.xutils3.XUtilsUrls;

import org.xutils.http.RequestParams;

/**
 * Created by Jason on 2016/10/14.
 */

public class DirectionRunPresenter implements DirectionRunContact.IDirectionRunPresenter {
    private DirectionRunContact.IDirectionRunView view;

    public DirectionRunPresenter(DirectionRunContact.IDirectionRunView view) {
        this.view = view;
    }

    @Override
    public void startRun(long gameId, long pointId) {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.START_RUN);
        params.addBodyParameter("uid", Accounts.getId() + "");
        params.addBodyParameter("game_id", gameId + "");
        params.addBodyParameter("point_id", pointId + "");
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<StartRunResponse>() {
            @Override
            protected void onSuccess(StartRunResponse response) {
                if (response.status == BaseResponse.SUCCESS) {
                    view.successStartRun(response.data);
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
        }, StartRunResponse.class);
    }

    @Override
    public void getRunLine(long gameId) {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.RUN_LINE);
        params.addBodyParameter("uid", Accounts.getId() + "");
        params.addBodyParameter("game_id", gameId + "");
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<RunLineResponse>() {
            @Override
            protected void onSuccess(RunLineResponse response) {
                if (response.status == BaseResponse.SUCCESS) {
                    view.successGetRunLine(response.data);
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
        }, RunLineResponse.class);
    }

    @Override
    public void runSignUp(long gameId, long pointId, long recordId, double distance) {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.LOG_RUN);
        params.addBodyParameter("uid", Accounts.getId() + "");
        params.addBodyParameter("game_id", gameId + "");
        params.addBodyParameter("point_id", pointId + "");
        params.addBodyParameter("record_id", recordId + "");
        params.addBodyParameter("distance", distance + "");
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<RunSignUpResponse>() {
            @Override
            protected void onSuccess(RunSignUpResponse response) {
                if (response.status == BaseResponse.SUCCESS) {
                    view.successRunSignUp(response.data);
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
        }, RunSignUpResponse.class);
    }

    @Override
    public void stopRun(long gameId, long recordId, double distance) {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.STOP_RUN);
        params.addBodyParameter("uid", Accounts.getId() + "");
        params.addBodyParameter("game_id", gameId + "");
        params.addBodyParameter("record_id", recordId + "");
        params.addBodyParameter("distance", distance + "");
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<StopRunResponse>() {
            @Override
            protected void onSuccess(StopRunResponse response) {
                if (response.status == BaseResponse.SUCCESS) {
                    view.successStopRun(response.data);
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
        }, StopRunResponse.class);
    }
}
