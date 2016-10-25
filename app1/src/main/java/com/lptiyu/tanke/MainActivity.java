package com.lptiyu.tanke;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.widget.ImageView;

import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.ToastUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends BaseActivity {

    MainActivityController mController;

    /**
     * Init value is a non-zero value, and then will be set to value.
     */
    public int mCurrentIndex = 2;

    @BindView(R.id.page_1)
    ImageView tab1;

    @BindView(R.id.page_2)
    ImageView tab2;

    @BindView(R.id.page_3)
    ImageView tab3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mController = new MainActivityController(this, getWindow().getDecorView());
        init();
        uploadInstallationId(Accounts.getId(), Accounts.getToken());
    }

    @Override
    public ActivityController getController() {
        return mController;
    }

    private void init() {
        selectTab(0);
    }

    @IntDef({0, 1, 2})
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @interface page {
    }


    public void selectTab(@page int index) {
        if (mCurrentIndex == index) {
            return;
        }

        //                selectTab(index == 0, tab1, R.drawable.home_page_selected, R.drawable.home_page_normal);
        //                selectTab(index == 1, tab2, R.drawable.message_selected, R.drawable.message_normal);
        //                selectTab(index == 2, tab3, R.drawable.mine_selected, R.drawable.mine_normal);

        selectTab(index == 0, tab1, R.drawable.tab_home_selected, R.drawable.tab_home_normal);
        selectTab(index == 1, tab2, R.drawable.tab_message_selected, R.drawable.tab_message_normal);
        selectTab(index == 2, tab3, R.drawable.tab_mine_selected, R.drawable.tab_mine_normal);
        mCurrentIndex = index;
    }

    private void selectTab(boolean select, ImageView view, int resSelected, int resUnselected) {
        if (select) {
            view.setImageResource(resSelected);
        } else {
            view.setImageResource(resUnselected);
        }
    }

    private void uploadInstallationId(long id, String token) {
        HttpService.getUserService().registerInstallation(id, token, Accounts.getInstallationId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<Void>>() {
                    @Override
                    public void call(Response<Void> voidResponse) {
                        int status = voidResponse.getStatus();
                        if (status != 1) {
                            ToastUtil.TextToast(voidResponse.getInfo());
                        }
                        Timber.d("绑定installationId成功");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (!NetworkUtil.checkIsNetworkConnected()) {
                            ToastUtil.TextToast(R.string.no_network);
                            return;
                        }
                        Timber.e(throwable, "loading error...");
                    }
                });
    }

}


