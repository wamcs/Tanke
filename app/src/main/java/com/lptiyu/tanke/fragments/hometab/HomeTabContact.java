package com.lptiyu.tanke.fragments.hometab;

import com.lptiyu.tanke.entity.response.HomeGameList;
import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

import java.util.List;

/**
 * Created by Jason on 2016/9/23.
 */

public class HomeTabContact {
    public interface IHomeTabPresenter extends IBasePresenter {
        void firstLoadGameList(int cid);
    }

    public interface IHomeTabView extends IBaseView {
        void successFirstLoadGameList(List<HomeGameList> list);
    }
}
