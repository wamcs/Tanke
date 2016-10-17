package com.lptiyu.tanke.fragments.pointtask;

import com.lptiyu.tanke.entity.UpLoadGameRecord;
import com.lptiyu.tanke.entity.response.UpLoadGameRecordResult;
import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

/**
 * Created by Jason on 2016/10/13.
 */

public class PointTaskContact {
    interface IPointTaskView extends IBaseView {
        void successUploadRecord(UpLoadGameRecordResult result);

        void failUploadRecord(String errorMsg);
    }

    interface IPointTaskPresenter extends IBasePresenter {
        void uploadRecord(UpLoadGameRecord record);
    }
}
