package com.lptiyu.tanke.activities.pointtask;

import com.lptiyu.tanke.entity.UpLoadGameRecord;
import com.lptiyu.tanke.entity.UploadGameRecordResponse;
import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

/**
 * Created by Jason on 2016/8/4.
 */
public class PointTaskContact {
    interface IPointTaskView extends IBaseView {
        void successUploadRecord(UploadGameRecordResponse response);

        void successUploadGameOverRecord(UploadGameRecordResponse response);

        void failUploadRecord();

        void netException();
    }

    interface IPointTaskPresenter extends IBasePresenter {
        void uploadRecord(UpLoadGameRecord upLoadGameRecord);

        void uploadGameOverRecord(UpLoadGameRecord upLoadGameRecord);

    }
}
