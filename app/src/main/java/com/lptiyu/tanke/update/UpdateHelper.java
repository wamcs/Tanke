package com.lptiyu.tanke.update;

import android.content.Context;

import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.net.HttpService;
import com.lptiyu.tanke.net.Response;
import com.lptiyu.tanke.utils.xutils3.APKDownloader;
import com.lptiyu.tanke.widget.dialog.TextDialog;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-27
 *         email: wonderfulifeel@gmail.com
 */
public class UpdateHelper {

    private TextDialog mNotifyUpdateDialog;

    private String apkUrl;
    //    private WeakReference<Context> weakReference;
    private int versionCode;
    private String versionName;
    private Context context;

    public UpdateHelper(Context context) {
        this.context = context;
        //        weakReference = new WeakReference<>(context);
        initDialog();
    }

    public void checkForUpdate() {
        HttpService.getUserService().getAppVersion()
                .map(new Func1<Response<VersionEntity>, VersionEntity>() {
                    @Override
                    public VersionEntity call(Response<VersionEntity> versionEntityResponse) {
                        if (versionEntityResponse == null || versionEntityResponse.getStatus() == 0) {
                            return null;
                        }
                        VersionEntity entity = versionEntityResponse.getData();
                        if (entity == null) {
                            return null;
                        }
                        return entity;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<VersionEntity>() {
                    @Override
                    public void call(VersionEntity versionEntity) {
                        versionCode = versionEntity.getVersionCode();
                        versionName = versionEntity.getVersionName();
                        int minVersion = versionEntity.getMin_version();
                        apkUrl = versionEntity.getUrl();
                        if (versionCode > AppData.getVersionCode()) {
                            if (AppData.getVersionCode() >= minVersion)
                                showChooseUpdateDialog(AppData.getVersionName(), versionName);
                            else
                                showMustUpdateDialog();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.e(throwable, "version code.....error");
                    }
                });
    }

    private boolean isMust = false;

    private void showChooseUpdateDialog(String oldVersionName, String newVersionName) {
        isMust = false;
        mNotifyUpdateDialog.show(String.format("检测到新版本：%s", newVersionName));
    }

    private void showMustUpdateDialog() {
        isMust = true;
        mNotifyUpdateDialog.show("您当前版本过低，升级到新版本才能使用");
    }

    private void initDialog() {
        if (mNotifyUpdateDialog == null) {
            mNotifyUpdateDialog = new TextDialog(context);
            mNotifyUpdateDialog.withTitle("版本更新").isCancelable(false).withMessage(null)
                    .setCancelable(false);
            mNotifyUpdateDialog.setmListener(new TextDialog.OnTextDialogButtonClickListener() {
                @Override
                public void onPositiveClicked() {
                    new APKDownloader(context, apkUrl, versionCode);
                    mNotifyUpdateDialog.dismiss();
                }

                @Override
                public void onNegtiveClicked() {
                    if (isMust) {
                        RunApplication.getInstance().AppExit();
                    } else {
                        mNotifyUpdateDialog.dismiss();
                    }
                }
            });
        }
    }

}
