package com.lptiyu.tanke.activities.drrecorddetail;

import com.lptiyu.tanke.entity.response.DRRecordDetail;
import com.lptiyu.tanke.entity.response.RunLine;
import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

import java.io.File;

/**
 * Created by Jason on 2016/11/11.
 */

public class DRRecordDetailContact {
    interface IDRRecordDetailView extends IBaseView {
        void successLoadDRRecordDetail(DRRecordDetail detail);

        void successDownloadFile(File file);

        void successGetRunLine(RunLine runLine);
    }

    interface IDRRecordDetailPresenter extends IBasePresenter {
        void loadDRRecordDetail(String record_id);

        void downloadFile(String fileUrl);

        void getRunLine(long gameId);
    }
}
