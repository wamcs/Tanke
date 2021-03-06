package com.lptiyu.tanke.activities.locationtask;

import android.util.Log;

import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.UpLoadGameRecord;
import com.lptiyu.tanke.pojo.UploadGameRecordResponse;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Jason on 2016/8/9.
 */
public class LocationTaskPresenter implements LocationTaskContact.ILocationTaskPresenter {
    private LocationTaskContact.ILocationTaskView view;

    public LocationTaskPresenter(LocationTaskContact.ILocationTaskView view) {
        this.view = view;
    }

    @Override
    public void uploadRecord(final UpLoadGameRecord record) {
        HttpService.getGameService()
                .upLoadGameRecord(Accounts.getId(), Long.parseLong(record.game_id), Long.parseLong(record.point_id),
                        Long.parseLong(record.task_id), Long.parseLong(record.point_statu))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<UploadGameRecordResponse>>() {
                    @Override
                    public void call(Response<UploadGameRecordResponse> response) {
                        Log.i("jason", "上传游戏记录结果:" + response);
                        if (response.getStatus() == Response.RESPONSE_OK) {
                            view.successUploadRecord(response.getData());
                        } else {
                            if (response.getInfo() != null) {
                                Log.i("jason", "游戏记录上传失败：" + response.getInfo());
                                view.failUploadRecord(response.getInfo());
                            } else {
                                view.netException();
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i("jason", "upload records error:" + throwable.getMessage());
                        view.netException();
                    }
                });
    }
}
