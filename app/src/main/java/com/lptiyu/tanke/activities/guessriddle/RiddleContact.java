package com.lptiyu.tanke.activities.guessriddle;

import com.lptiyu.tanke.pojo.UpLoadGameRecord;
import com.lptiyu.tanke.pojo.UploadGameRecordResponse;

/**
 * Created by Jason on 2016/8/9.
 */
public class RiddleContact {
    interface IRiddleView {
        void successUploadRecord(UploadGameRecordResponse response);

        void failUploadRecord(String errorMsg);

        void netException();
    }

    interface IRiddlePresenter {
        void uploadRecord(UpLoadGameRecord upLoadGameRecord);
    }
}
