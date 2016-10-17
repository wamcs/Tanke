package com.lptiyu.tanke.activities.directionrun;

import com.lptiyu.tanke.entity.response.RunLine;
import com.lptiyu.tanke.entity.response.RunSignUp;
import com.lptiyu.tanke.entity.response.StartRun;
import com.lptiyu.tanke.entity.response.StopRun;
import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

/**
 * Created by Jason on 2016/10/14.
 */

public class DirectionRunContact {
    interface IDirectionRunView extends IBaseView {
        void successStartRun(StartRun startRun);

        void successGetRunLine(RunLine runLine);

        void successRunSignUp(RunSignUp runSignUp);

        void successStopRun(StopRun stopRun);
    }

    interface IDirectionRunPresenter extends IBasePresenter {
        void startRun(long gameId, long pointId);

        void getRunLine(long gameId);

        void runSignUp(long gameId, long pointId, long recordId, long distance);

        void stopRun(long gameId, long recordId, long distance);
    }
}
