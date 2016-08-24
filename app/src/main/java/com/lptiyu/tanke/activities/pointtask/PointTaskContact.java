package com.lptiyu.tanke.activities.pointtask;

import com.lptiyu.tanke.pojo.UpLoadGameRecord;
import com.lptiyu.tanke.pojo.UploadGameRecordResponse;

/**
 * Created by Jason on 2016/8/4.
 */
public class PointTaskContact {
    interface IPointTaskView {
        void successUploadRecord(UploadGameRecordResponse response);

        void successUploadGameOverRecord(UploadGameRecordResponse response);

        void failUploadRecord();

        void netException();
    }

    interface IPointTaskPresenter {
        void uploadRecord(UpLoadGameRecord upLoadGameRecord);

        void uploadGameOverRecord(UpLoadGameRecord upLoadGameRecord);

    }
}
