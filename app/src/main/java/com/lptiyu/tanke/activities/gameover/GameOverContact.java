package com.lptiyu.tanke.activities.gameover;

import com.lptiyu.tanke.entity.response.GameOverReward;
import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

/**
 * Created by Jason on 2016/11/2.
 */

public class GameOverContact {
    interface IGameOverView extends IBaseView {
        void successLoadGameOverReward(GameOverReward gameOverReward);
    }

    interface IGameOverPresenter extends IBasePresenter {
        void loadGameOverReward(long gameId);
    }
}
