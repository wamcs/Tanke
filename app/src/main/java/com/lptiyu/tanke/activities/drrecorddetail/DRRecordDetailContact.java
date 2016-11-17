package com.lptiyu.tanke.activities.drrecorddetail;

import com.lptiyu.tanke.entity.response.DRRecordDetail;
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
    }

    interface IDRRecordDetailPresenter extends IBasePresenter {
        void loadDRRecordDetail(String record_id);

        void downloadFile(String fileUrl);
    }
}
