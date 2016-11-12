package com.lptiyu.tanke.activities.userdirectionrunlist;

import com.lptiyu.tanke.entity.response.DRRecordEntity;
import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

import java.util.List;

/**
 * Created by Jason on 2016/11/11.
 */

public class UserDirectionRunContact {
    interface IUserDirectionRunView extends IBaseView {
        void successLoadDRList(List<DRRecordEntity> list);
    }

    interface IUserDirectionRunPresenter extends IBasePresenter {
        void loadDRList();
    }
}
