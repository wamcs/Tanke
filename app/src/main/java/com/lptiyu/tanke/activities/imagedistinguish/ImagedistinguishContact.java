package com.lptiyu.tanke.activities.imagedistinguish;

import com.lptiyu.tanke.pojo.UpLoadGameRecord;
import com.lptiyu.tanke.pojo.UploadGameRecordResponse;

/**
 * Created by Jason on 2016/8/9.
 */
public class ImagedistinguishContact {
    interface ImagedistinguishView {
        void successUploadRecord(UploadGameRecordResponse response);

        void failUploadRecord();

        void netException();
    }

    interface ImagedistinguishPresenter {
        void uploadRecord(UpLoadGameRecord upLoadGameRecord);
    }
}
