package com.lptiyu.tanke.activities.imagedistinguish;

import com.lptiyu.tanke.entity.UpLoadGameRecord;
import com.lptiyu.tanke.entity.response.UpLoadGameRecordResult;
import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

/**
 * Created by Jason on 2016/8/9.
 */
public class ImagedistinguishContact {
    interface ImagedistinguishView extends IBaseView {
        void successUploadRecord(UpLoadGameRecordResult response);

        void failUploadRecord(String errorMsg);

        void netException();
    }

    interface ImagedistinguishPresenter extends IBasePresenter {
        void uploadRecord(UpLoadGameRecord upLoadGameRecord);
    }
}
