package com.lptiyu.tanke.activities.pointtask;

import android.util.Log;

import com.lptiyu.tanke.entity.UploadGameRecord;
import com.lptiyu.tanke.entity.UploadGameRecordResponse;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.net.HttpService;
import com.lptiyu.tanke.net.Response;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Jason on 2016/8/4.
 */
public class PointTaskPresenter implements PointTaskContact.IPointTaskPresenter {
    private PointTaskContact.IPointTaskView view;

    public PointTaskPresenter(PointTaskContact.IPointTaskView view) {
        this.view = view;
    }

    @Override
    public void uploadRecord(UploadGameRecord record) {
        HttpService.getGameService()
                .upLoadGameRecord(Accounts.getId(), Long.parseLong(record.game_id), Long.parseLong(record.point_id),
                        Long.parseLong(record.task_id), Long.parseLong(record.point_statu))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<UploadGameRecordResponse>>() {
                    @Override
                    public void call(Response<UploadGameRecordResponse> response) {
                        Log.i("jason", "PointTaskActivity上传游戏记录结果:" + response);
                        if (response.getStatus() == Response.RESPONSE_OK) {
                            Log.i("jason", "response.getStatus() = Response.RESPONSE_OK");
                            view.successUploadRecord(response.getData());
                        } else {
                            Log.i("jason", "游戏记录上传失败：" + response.getInfo());
                            view.failUploadRecord();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i("jason", "upload records error:" + throwable.getMessage());
                        view.netException();
                    }
                });

        //        RequestParams params = new RequestParams("http://192.168.1.5/trunk/lepao/api.php/system/Rankslog");
        //        params.addBodyParameter("uid", Accounts.getId() + "");
        //        params.addBodyParameter("game_id", record.game_id);
        //        params.addBodyParameter("point_id", record.point_id);
        //        params.addBodyParameter("task_id", record.task_id);
        //        params.addBodyParameter("type", record.type);
        //        params.addBodyParameter("point_statu", record.point_statu);
        //        x.http().get(params, new Callback.CommonCallback<String>() {
        //            @Override
        //            public void onSuccess(String result) {
        //                Log.i("jason", "xUtils上传游戏记录：" + result);
        //                try {
        //                    JSONObject jsonObject = new JSONObject(result);
        //                    if (jsonObject.optInt("status") == Response.RESPONSE_OK) {
        //                        JSONObject data = jsonObject.optJSONObject("data");
        //                        UploadGameRecordResponse response = new Gson().fromJson(data.toString(),
        //                                UploadGameRecordResponse.class);
        //                        view.successUploadRecord(response);
        //                    }
        //                } catch (JSONException e) {
        //                    e.printStackTrace();
        //                }
        //            }
        //
        //            @Override
        //            public void onError(Throwable ex, boolean isOnCallback) {
        //                Log.i("jason", "onError:" + ex.getMessage());
        //                view.failSubmit();
        //            }
        //
        //            @Override
        //            public void onCancelled(CancelledException cex) {
        //                Log.i("jason", "onCancelled:" + cex.getMessage());
        //            }
        //
        //            @Override
        //            public void onFinishedDownload() {
        //                Log.i("jason", "onFinishedDownload()");
        //            }
        //        });

    }

    @Override
    public void uploadGameOverRecord(UploadGameRecord record) {
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
                            view.successUploadGameOverRecord(response.getData());
                        } else {
                            Log.i("jason", "游戏记录上传失败：" + response.getInfo());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i("jason", "upload records error:" + throwable.getMessage());
                    }
                });
    }
}
