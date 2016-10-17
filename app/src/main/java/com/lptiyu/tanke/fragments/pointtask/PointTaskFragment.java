package com.lptiyu.tanke.fragments.pointtask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.guessriddle.GuessRiddleActivity;
import com.lptiyu.tanke.activities.imagedistinguish.ImageDistinguishActivity;
import com.lptiyu.tanke.activities.locationtask.LocationTaskActivity;
import com.lptiyu.tanke.adapter.LVForPointTaskV2Adapter;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.Task;
import com.lptiyu.tanke.entity.UpLoadGameRecord;
import com.lptiyu.tanke.entity.eventbus.NotifyGamePlayingV2RefreshData;
import com.lptiyu.tanke.entity.eventbus.NotifyPointTaskV2RefreshData;
import com.lptiyu.tanke.entity.response.UpLoadGameRecordResult;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.enums.RequestCode;
import com.lptiyu.tanke.enums.TaskType;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseFragment;
import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.LogUtils;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.xutils3.XUtilsHelper;
import com.lptiyu.zxinglib.android.CaptureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lptiyu.tanke.RunApplication.currentTaskIndex;
import static com.lptiyu.tanke.RunApplication.gameId;

/**
 * A simple {@link Fragment} subclass.
 */
public class PointTaskFragment extends MyBaseFragment implements PointTaskContact.IPointTaskView {

    @BindView(R.id.img_getKey)
    ImageView imgGetKey;
    @BindView(R.id.lv)
    ListView lv;
    private ImageView img;
    private TextView tv_title;
    private LVForPointTaskV2Adapter adapter;
    private boolean isPointOver = false;
    private ProgressDialog distinguishImageDialog;
    private Point currentPoint;
    private Task currentTask;
    private String[] imgUrls;
    private PointTaskPresenter presenter;

    public static PointTaskFragment create(int index) {
        PointTaskFragment fragment = new PointTaskFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Conf.INDEX, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            currentPoint = RunApplication.gameRecord.game_detail.point_list.get(bundle.getInt(Conf.INDEX));
        }
        presenter = new PointTaskPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pointtask, container, false);
        ButterKnife.bind(this, view);
        View header = inflater.inflate(R.layout.header_listview_pointtask, null);
        tv_title = (TextView) header.findViewById(R.id.tv_title);
        img = (ImageView) header.findViewById(R.id.img);
        lv.addHeaderView(header);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Glide.with(getActivity()).load(currentPoint.point_img).error(R.drawable.default_pic).into(img);
        tv_title.setText(currentPoint.point_title);
        adapter = new LVForPointTaskV2Adapter(getActivity(), currentPoint.task_list);
        lv.setAdapter(adapter);
        if (currentPoint.status == PointTaskStatus.FINISHED) {
            imgGetKey.setVisibility(View.GONE);
        } else {
            imgGetKey.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < currentPoint.task_list.size(); i++) {
            if (TextUtils.isEmpty(currentPoint.task_list.get(i).exp)) {
                RunApplication.currentTaskIndex = i;
                currentTask = currentPoint.task_list.get(currentTaskIndex);
                if (Integer.parseInt(currentTask.type) == TaskType.FINISH) {//当前任务是结束任务，表明游戏结束
                    //游戏结束，上传游戏记录
                    imgGetKey.setVisibility(View.GONE);
                    UpLoadGameRecord record = new UpLoadGameRecord();
                    record.uid = Accounts.getId() + "";
                    record.point_id = currentPoint.id + "";
                    record.game_id = gameId + "";
                    record.point_statu = PointTaskStatus.FINISHED + "";
                    record.task_id = currentTask.id + "";
                    presenter.uploadRecord(record);
                    return;
                }
                break;
            }
        }
        lv.setSelection(currentTaskIndex);

        imgGetKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipToAnswerTask();
            }
        });
    }

    private void skipToAnswerTask() {
        LogUtils.i("点击的任务:" + currentTask + "");
        //跳转
        final Intent intent = new Intent();
        isPointOver();
        intent.putExtra(Conf.IS_POINT_OVER, isPointOver);
        //根据当前任务的类型决定如何操作
        switch (Integer.parseInt(currentTask.type)) {
            case TaskType.DISTINGUISH:
                Log.i("jason", "图像识别任务");
                distinguishImageDialog = ProgressDialog.show(getActivity(), null, "正在启动摄像头...");
                String pwd = currentTask.pwd;
                imgUrls = pwd.split(",");
                if (imgUrls == null || imgUrls.length <= 0) {
                    Toast.makeText(getActivity(), "未发现目标图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                imgArr = new String[imgUrls.length];
                //查询本地是否有图片,如果有就直接取出来用，没有就下载之后再用
                File[] files = DirUtils.getGameDirectory().listFiles();
                if (files == null) {
                    downLoadPic();
                } else {
                    boolean hasPic = false;
                    for (int i = 0; i < imgUrls.length; i++) {
                        for (File file : files) {
                            if (file.getName().equals(imgUrls[i])) {
                                imgArr[i] = file.getAbsolutePath();
                                hasPic = true;
                            }
                        }
                    }
                    if (!hasPic) {
                        downLoadPic();
                    } else {
                        skipToImageDistinguish();
                    }
                }
                break;
            case TaskType.FINISH:
                Log.i("jason", "finish任务");
                break;
            case TaskType.LOCATE:
                Log.i("jason", "定位任务");
                initGPS();
                break;
            case TaskType.RIDDLE:
                Log.i("jason", "猜谜任务");
                intent.setClass(getActivity(), GuessRiddleActivity.class);
                startActivity(intent);
                break;
            case TaskType.SCAN_CODE:
                Log.i("jason", "扫码任务");
                intent.putExtra("isFirstInLocation", AppData.isFirstInCaptureActivity());
                intent.setClass(getActivity(), CaptureActivity.class);
                startActivityForResult(intent, RequestCode.CAMERA_PERMISSION_REQUEST_CODE);
                /*
            if (resultCode == RESULT_OK) {//扫码识别返回,RESULT_OK是Activity里面的一个静态常量
            Bundle b = intent.getExtras();
            //扫描到的结果
            String str = b.getString(CaptureActivity.QR_CODE_DATA);
            if (str == null || str.length() == 0) {
                ToastUtil.TextToast(getString(R.string.scan_error));
                return;
            }
            //与答案匹配
            if (str.equals(currentTask.pwd)) {
                //                refreshData(resultResponse);
                finishedCurrentTast();
            } else {
                Toast.makeText(this, "二维码不正确", Toast.LENGTH_SHORT).show();
            }
        }
                * */
                break;
            case TaskType.UPLOAD_PHOTO:
                Log.i("jason", "图像上传任务");
                ToastUtil.TextToast("该类型任务尚未开通");
                break;
        }
    }

    private int times = 0;
    private String[] imgArr = null;

    //递归下载图片
    private void downLoadPic() {
        if (times >= imgUrls.length) {
            skipToImageDistinguish();
            return;
        }
        XUtilsHelper.getInstance().downLoad(imgUrls[times], new XUtilsHelper.IDownloadCallback() {
            @Override
            public void successs(File file) {
                imgArr[times] = file.getAbsolutePath();
                times++;
                downLoadPic();
            }

            @Override
            public void progress(long total, long current, boolean isDownloading) {

            }

            @Override
            public void finished() {

            }

            @Override
            public void onError(String errMsg) {
                LogUtils.i(errMsg);
            }
        });
    }

    private void skipToImageDistinguish() {
        Intent intent = new Intent(getActivity(), ImageDistinguishActivity.class);
        intent.putExtra(Conf.IS_POINT_OVER, isPointOver);
        intent.putExtra(Conf.IMG_DISTINGUISH_ARRAY, imgArr);
        getActivity().startActivity(intent);
    }

    private void isPointOver() {
        for (int i = 0; i < currentPoint.task_list.size(); i++) {
            if (TextUtils.isEmpty(currentPoint.task_list.get(i).exp)) {
                if (i != currentPoint.task_list.size() - 1) {
                    isPointOver = false;
                } else {
                    isPointOver = true;
                }
            } else {
                isPointOver = true;
            }
        }
    }

    private void initGPS() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("为了定位更加精确，请打开GPS");
            dialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                        }
                    });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        } else {
            Intent intent = new Intent();
            intent.putExtra(Conf.IS_POINT_OVER, isPointOver);
            intent.setClass(getActivity(), LocationTaskActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (distinguishImageDialog != null) {
            distinguishImageDialog.dismiss();
        }
    }

    /*无论在哪个线程发送都在主线程接收*/
    @Subscribe
    public void onEventMainThread(UpLoadGameRecordResult result) {
        currentTask.ftime = result.task_finish_time;
        currentTask.exp = result.get_exp;
        adapter.notifyDataSetChanged();
        if (currentTaskIndex < currentPoint.task_list.size() - 1) {
            lv.setSelection(++currentTaskIndex);
            if (Integer.parseInt(currentPoint.task_list.get(currentTaskIndex).type) == TaskType.FINISH) {
                //当前章节点完成
                EventBus.getDefault().post(new NotifyPointTaskV2RefreshData());
                imgGetKey.setVisibility(View.GONE);
            }
        } else {
            //当前章节点完成
            EventBus.getDefault().post(new NotifyPointTaskV2RefreshData());
            imgGetKey.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void successUploadRecord(UpLoadGameRecordResult result) {
        currentTask.ftime = result.task_finish_time;
        currentTask.exp = result.get_exp;
        //章节点结束，通知PointTaskV2Activity刷新数据
        EventBus.getDefault().post(new NotifyPointTaskV2RefreshData());
        EventBus.getDefault().post(new NotifyGamePlayingV2RefreshData());
    }

    @Override
    public void failUploadRecord(String errorMsg) {

    }
}
