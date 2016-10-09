package com.lptiyu.tanke.activities.guessriddle;

import com.lptiyu.tanke.entity.UpLoadGameRecord;
import com.lptiyu.tanke.entity.UploadGameRecordResponse;
import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

/**
 * Created by Jason on 2016/8/9.
 */
public class RiddleContact {
    interface IRiddleView extends IBaseView {
        void successUploadRecord(UploadGameRecordResponse response);

        void failUploadRecord(String errorMsg);

        void netException();
    }

    interface IRiddlePresenter extends IBasePresenter {
        void uploadRecord(UpLoadGameRecord upLoadGameRecord);
    }
}
