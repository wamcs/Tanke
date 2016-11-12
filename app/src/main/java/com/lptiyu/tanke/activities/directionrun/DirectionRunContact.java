package com.lptiyu.tanke.activities.directionrun;

import com.lptiyu.tanke.entity.response.RunLine;
import com.lptiyu.tanke.entity.response.RunSignUp;
import com.lptiyu.tanke.entity.response.StartRun;
import com.lptiyu.tanke.entity.response.StopRun;
import com.lptiyu.tanke.entity.response.UploadDRFile;
import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

import java.io.File;

/**
 * Created by Jason on 2016/10/14.
 */

public class DirectionRunContact {
    interface IDirectionRunView extends IBaseView {
        void successStartRun(StartRun startRun);

        void successGetRunLine(RunLine runLine);

        void successRunSignUp(RunSignUp runSignUp);

        void successStopRun(StopRun stopRun);

        void successUploadFile(UploadDRFile fileUrl);
    }

    interface IDirectionRunPresenter extends IBasePresenter {
        void startRun(long gameId, long pointId);

        void getRunLine(long gameId);

        void runSignUp(long gameId, long pointId, long recordId, double distance);

        void stopRun(long gameId, long recordId, double distance);

        void uploadDRFile(long recordId, String timestamp, File file);
    }
}
