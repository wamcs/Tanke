package com.lptiyu.tanke.fragments.hometab;

import com.lptiyu.tanke.entity.response.HomeTabEntity;
import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

import java.util.List;

/**
 * Created by Jason on 2016/9/23.
 */

public class HomeTabContact {
    public interface IHomeTabPresenter extends IBasePresenter {
        void firstLoadGameList(int cid);

        void loadMoreGame(int cid);

        void reloadGameList(int cid);
    }

    public interface IHomeTabView extends IBaseView {
        void successFirstLoadGameList(List<HomeTabEntity> list);

        void successLoadMoreGame(List<HomeTabEntity> list);

        void successReloadGame(List<HomeTabEntity> list);

        void failLoadMoreGame(String errMsg);

    }
}
