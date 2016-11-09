package com.lptiyu.tanke.activities.redwallet;

import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

/**
 * Created by Jason on 2016/11/8.
 */

public class RedWalletContact {
    interface IRedWalletView extends IBaseView {
        void successLoadRedWalletRecord();

        void successRequestRedWallet();
    }

    interface IRedWalletPresenter extends IBasePresenter {
        void loadRedWalletRecord();

        void requestRedWallet();
    }
}
