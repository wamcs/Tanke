package com.lptiyu.tanke.activities.pointtaskv2;

import com.lptiyu.tanke.entity.UploadGameRecord;
import com.lptiyu.tanke.entity.response.UpLoadGameRecordResult;
import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

/**
 * Created by Jason on 2016/8/4.
 */
public class PointTaskV2Contact {
    interface IPointTaskV2View extends IBaseView {
        void successUploadRecord(UpLoadGameRecordResult response);


    }

    interface IPointTaskV2Presenter extends IBasePresenter {
        void uploadRecord(UploadGameRecord upLoadGameRecord);

    }
}
