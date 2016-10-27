package com.lptiyu.tanke.activities.imagedistinguish;

import com.lptiyu.tanke.entity.UploadGameRecord;
import com.lptiyu.tanke.entity.UploadGameRecordResponse;
import com.lptiyu.tanke.entity.response.BaseResponse;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.utils.xutils3.RequestParamsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsRequestCallBack;
import com.lptiyu.tanke.utils.xutils3.XUtilsUrls;

import org.xutils.http.RequestParams;

/**
 * Created by Jason on 2016/8/9.
 */
public class ImagedistinguishPresenter implements ImagedistinguishContact.ImagedistinguishPresenter {
    private ImagedistinguishContact.ImagedistinguishView view;

    public ImagedistinguishPresenter(ImagedistinguishContact.ImagedistinguishView view) {
        this.view = view;
    }

    @Override
    public void uploadRecord(UploadGameRecord record) {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.UPLOAD_RECORD);
        params.addBodyParameter("uid", Accounts.getId() + "");
        params.addBodyParameter("game_id", record.game_id);
        params.addBodyParameter("point_id", record.point_id);
        params.addBodyParameter("task_id", record.task_id);
        params.addBodyParameter("point_statu", record.point_statu);
        params.addBodyParameter("ranks_id", record.ranks_id);
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<UploadGameRecordResponse>() {
            @Override
            protected void onSuccess(UploadGameRecordResponse response) {
                if (response.status == BaseResponse.SUCCESS) {
                    view.successUploadRecord(response.data);
                } else {
                    if (response.info != null)
                        view.failUploadRecord(response.info);
                    else
                        view.failLoad();
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
        }, UploadGameRecordResponse.class);
    }
}
