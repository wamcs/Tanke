package com.lptiyu.tanke.activities.locationtask;

import com.lptiyu.tanke.pojo.UpLoadGameRecord;
import com.lptiyu.tanke.pojo.UploadGameRecordResponse;

/**
 * Created by Jason on 2016/8/9.
 */
public class LocationTaskContact {
    interface ILocationTaskView {
        void successUploadRecord(UploadGameRecordResponse response);

        void failUploadRecord();

        void netException();
    }

    interface ILocationTaskPresenter {
        void uploadRecord(UpLoadGameRecord upLoadGameRecord);
    }
}
