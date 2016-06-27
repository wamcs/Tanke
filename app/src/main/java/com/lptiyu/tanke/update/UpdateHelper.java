package com.lptiyu.tanke.update;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.utils.ToastUtil;

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

  private AlertDialog mNotifyUpdateDialog;

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
            VersionEntity entity1 = new VersionEntity();
            entity1.setUrl("app0/135/135199/apk/1482208.apk?channel_id=426");
            entity1.setVersionCode(2);
            entity1.setVersionName("2.0.1");
            return entity1;
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Action1<VersionEntity>() {
          @Override
          public void call(VersionEntity versionEntity) {
            int versionCode = versionEntity.getVersionCode();
            String versionName = versionEntity.getVersionName();
            apkUrl = versionEntity.getUrl();
            Context context = weakReference.get();
            PackageManager pm = context.getPackageManager();//context为当前Activity上下文
            try {
              PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
              if (versionCode > pi.versionCode) {
                showAlertDialog(pi.versionName, versionName);
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

  private void showAlertDialog(String oldVersionName, String newVersionName) {
    mNotifyUpdateDialog.setMessage(String.format("当前版本号：%s\n新版本号：%s", oldVersionName, newVersionName));
    mNotifyUpdateDialog.show();
  }

  private void initDialog() {
    if (mNotifyUpdateDialog == null) {
      mNotifyUpdateDialog = new AlertDialog.Builder(weakReference.get())
          .setTitle("版本更新")
          .setCancelable(false)
          .setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              downloadHelper.startDownload(apkUrl);
              dialog.dismiss();
            }
          })
          .setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              RunApplication.getInstance().AppExit();
            }
          })
          .create();
    }
  }

}
