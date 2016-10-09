package com.lptiyu.tanke.fragments.home;

import com.lptiyu.tanke.entity.response.HomeBannerAndHot;
import com.lptiyu.tanke.entity.response.HomeSortList;
import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

/**
 * Created by Jason on 2016/9/23.
 */

public class HomeContact {
    public interface IHomePresenter extends IBasePresenter {
        void firstLoadBannerAndHot();

        void loadSort();
    }

    public interface IHomeView extends IBaseView {
        void successFirstLoadBannerAndHot(HomeBannerAndHot homeBannerAndHot);

        void successLoadSort(HomeSortList homeSortList);
    }
}
