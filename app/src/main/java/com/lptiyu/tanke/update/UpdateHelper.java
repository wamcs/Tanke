package com.lptiyu.tanke.update;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.widget.dialog.TextDialog;

import java.lang.ref.WeakReference;

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
    private DownloadHelper downloadHelper;
    private WeakReference<Context> weakReference;

    public UpdateHelper(Context context) {
        weakReference = new WeakReference<>(context);
        initDialog();
        downloadHelper = new DownloadHelper(context);
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
                        int versionCode = versionEntity.getVersionCode();
                        String versionName = versionEntity.getVersionName();
                        int minVersion = versionEntity.getMin_version();
                        apkUrl = versionEntity.getUrl();
                        Context context = weakReference.get();
                        PackageManager pm = context.getPackageManager();//context为当前Activity上下文
                        try {
                            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
                            Log.i("jason", "当签名版本号：" + pi.versionCode + ",当前版本名称：" + pi.versionName);
                            if (versionCode > pi.versionCode && pi.versionCode > minVersion) {
                                showChooseUpdateDialog(pi.versionName, versionName);
                            }
                            if (versionCode > pi.versionCode && pi.versionCode < minVersion) {
                                showMustUpdateDialog();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
        //        mNotifyUpdateDialog.show(String.format("当前版本号：%s\n新版本号：%s", oldVersionName, newVersionName));
        isMust = false;
        mNotifyUpdateDialog.show(String.format("检测到新版本：%s", newVersionName));
    }

    private void showMustUpdateDialog() {
        isMust = true;
        mNotifyUpdateDialog.show("您当前版本过低，升级到新版本才能使用");
    }

    private void initDialog() {
        if (mNotifyUpdateDialog == null) {
            mNotifyUpdateDialog = new TextDialog(weakReference.get());
            mNotifyUpdateDialog.withTitle("版本更新").isCancelable(false).withMessage(null)
                    .setCancelable(false);
            mNotifyUpdateDialog.setmListener(new TextDialog.OnTextDialogButtonClickListener() {
                @Override
                public void onPositiveClicked() {
                    downloadHelper.startDownload(apkUrl);
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
