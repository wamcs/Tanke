package com.lptiyu.tanke.activities.drrecorddetail;

import com.lptiyu.tanke.entity.response.BaseResponse;
import com.lptiyu.tanke.entity.response.DRRecordDetailResponse;
import com.lptiyu.tanke.entity.response.RunLineResponse;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.xutils3.RequestParamsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsRequestCallBack;
import com.lptiyu.tanke.utils.xutils3.XUtilsUrls;

import org.xutils.http.RequestParams;

import java.io.File;

/**
 * Created by Jason on 2016/11/11.
 */

public class DRRecordDetailPresenter implements DRRecordDetailContact.IDRRecordDetailPresenter {
    private DRRecordDetailContact.IDRRecordDetailView view;

    public DRRecordDetailPresenter(DRRecordDetailContact.IDRRecordDetailView view) {
        this.view = view;
    }

    @Override
    public void loadDRRecordDetail(String record_id) {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.DR_RECORD_DETAIL);
        params.addBodyParameter("uid", Accounts.getId() + "");
        params.addBodyParameter("record_id", record_id + "");
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<DRRecordDetailResponse>() {
            @Override
            protected void onSuccess(DRRecordDetailResponse response) {
                if (response.status == BaseResponse.SUCCESS) {
                    view.successLoadDRRecordDetail(response.data);
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
        }, DRRecordDetailResponse.class);
    }

    @Override
    public void downloadFile(String fileUrl) {
        XUtilsHelper.getInstance().downLoad(fileUrl, DirUtils.getDirectionRunDirectory().getAbsolutePath(), new
                XUtilsHelper.IDownloadCallback() {
                    @Override
                    public void successs(File file) {
                        view.successDownloadFile(file);
                    }

                    @Override
                    public void progress(long total, long current, boolean isDownloading) {

                    }

                    @Override
                    public void finished() {

                    }

                    @Override
                    public void onError(String errMsg) {
                        view.failLoad(errMsg);
                    }
                });
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
}
