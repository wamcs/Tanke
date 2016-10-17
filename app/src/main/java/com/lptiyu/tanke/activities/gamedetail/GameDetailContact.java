package com.lptiyu.tanke.activities.gamedetail;

import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

/**
 * Created by Jason on 2016/10/10.
 */

public class GameDetailContact {
    interface IGameDetailView extends IBaseView {
        void successEnterGame();

        void successLeaveGame();

        void failLeaveGame(String errMsg);
    }

    interface IGameDetailPresenter extends IBasePresenter {
        void enterGame(long gameId, int type);

        void leaveGame(long gameId);

    }
}
