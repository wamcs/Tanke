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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.gameover.GameOverActivity;
import com.lptiyu.tanke.activities.guessriddle.GuessRiddleActivity;
import com.lptiyu.tanke.activities.imagedistinguish.ImageDistinguishActivity;
import com.lptiyu.tanke.activities.locationtask.LocationTaskActivity;
import com.lptiyu.tanke.adapter.LVForPointTaskAdapter;
import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.Task;
import com.lptiyu.tanke.entity.UploadGameRecord;
import com.lptiyu.tanke.entity.eventbus.GamePointTaskStateChanged;
import com.lptiyu.tanke.entity.response.UpLoadGameRecordResult;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.enums.RequestCode;
import com.lptiyu.tanke.enums.TaskType;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseFragment;
import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.LogUtils;
import com.lptiyu.tanke.utils.StringUtils;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.xutils3.XUtilsHelper;
import com.lptiyu.zxinglib.android.CaptureActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lptiyu.tanke.RunApplication.currentTaskIndex;
import static com.lptiyu.tanke.RunApplication.gameId;

/**
 * A simple {@link Fragment} subclass.
 */
public class PointTaskFragment extends MyBaseFragment implements PointTaskContact.IPointTaskView {

    @BindView(R.id.img_getKey)
    ImageView imgGetKey;
    @BindView(R.id.rl_getKey)
    RelativeLayout rlGetKey;
    @BindView(R.id.lv)
    ListView lv;
    @BindView(R.id.tv_look_game_over_reward)
    TextView tvLookGameOverReward;
    private RoundedImageView img;
    private TextView tv_title;
    private LVForPointTaskAdapter adapter;
    private boolean isPointOver = false;
    private ProgressDialog distinguishImageDialog;
    private Point currentPoint;
    private Task currentTask;
    private String[] imgUrls;
    private PointTaskPresenter presenter;
    private int index;
    private boolean isGameOver;

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
        Bundle bundle = getArguments();
        if (bundle != null) {
            index = bundle.getInt(Conf.INDEX);
            GameRecord gameRecord = RunApplication.gameRecord;
            if (gameRecord != null && gameRecord.game_detail != null && gameRecord.game_detail.point_list != null)
                currentPoint = gameRecord.game_detail.point_list.get(index);
        }
        presenter = new PointTaskPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pointtask, container, false);
        ButterKnife.bind(this, view);
        View header = inflater.inflate(R.layout.header_listview_pointtask, null);
        tv_title = (TextView) header.findViewById(R.id.tv_title);
        img = (RoundedImageView) header.findViewById(R.id.img);
        lv.addHeaderView(header);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindData();
    }

    private void bindData() {
        if (currentPoint == null) {
            Toast.makeText(getActivity(), "currentPoint为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Glide.with(getActivity()).load(currentPoint.point_img).error(R.drawable.default_pic).into(img);
        tv_title.setText(currentPoint.point_title);
        adapter = new LVForPointTaskAdapter(getActivity(), currentPoint.task_list);
        lv.setAdapter(adapter);
        if (currentPoint.status == PointTaskStatus.FINISHED) {
            imgGetKey.setVisibility(View.GONE);
            if (index == Integer.parseInt(RunApplication.gameRecord.game_point_num) - 1) {
                tvLookGameOverReward.setVisibility(View.VISIBLE);
            }
        } else {
            imgGetKey.setVisibility(View.VISIBLE);
            for (int i = 0; i < currentPoint.task_list.size(); i++) {
                if (TextUtils.isEmpty(currentPoint.task_list.get(i).exp)) {//目前通过经验值是否为空来判断当前任务是否完成
                    RunApplication.currentTaskIndex = i;
                    currentTask = currentPoint.task_list.get(currentTaskIndex);
                    RunApplication.currentTask = currentTask;
                    if (Integer.parseInt(currentTask.type) == TaskType.FINISH) {//当前任务是结束任务，表明游戏结束
                        isGameOver = true;
                        //游戏结束，上传游戏记录
                        imgGetKey.setVisibility(View.GONE);
                        UploadGameRecord record = new UploadGameRecord();
                        record.uid = Accounts.getId() + "";
                        record.point_id = currentPoint.id + "";
                        record.game_id = gameId + "";
                        record.point_statu = PointTaskStatus.FINISHED + "";
                        record.task_id = currentTask.id + "";
                        presenter.uploadGameOverRecord(record);
                        return;
                    }
                    break;
                }
            }
            lv.setSelection(currentTaskIndex);
            setImgGetKeyType();
        }
    }

    private void setImgGetKeyType() {
        switch (Integer.parseInt(currentTask.type)) {
            case TaskType.DISTINGUISH:
                imgGetKey.setImageResource(R.drawable.paizhao);
                break;
            case TaskType.FINISH:
                imgGetKey.setVisibility(View.GONE);
                break;
            case TaskType.LOCATE:
                imgGetKey.setImageResource(R.drawable.dingwei);
                break;
            case TaskType.RIDDLE:
                imgGetKey.setImageResource(R.drawable.dati);
                break;
            case TaskType.SCAN_CODE:
                imgGetKey.setImageResource(R.drawable.saoma);
                break;
            case TaskType.UPLOAD_PHOTO:
                break;
        }
    }

    private void skipToAnswerTask() {
        LogUtils.i("点击的任务:" + currentTask + "");
        //判断此任务做完之后当前章节点是否要结束了
        isPointOver();
        RunApplication.isPointOver = isPointOver;
        Intent intent = new Intent();
        intent.putExtra(Conf.INDEX, index);
        //根据当前任务的类型决定如何操作
        switch (Integer.parseInt(currentTask.type)) {
            case TaskType.DISTINGUISH:
                Log.i("jason", "图像识别任务");
                distinguishImageDialog = ProgressDialog.show(getActivity(), null, "正在启动摄像头...");
                checkDistinguishPicIsExist();
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
                intent.setClass(getActivity(), CaptureActivity.class);
                intent.putExtra("isFirstInLocation", AppData.isFirstInCaptureActivity());
                startActivityForResult(intent, RequestCode.CAMERA_PERMISSION_REQUEST_CODE);
                break;
            case TaskType.UPLOAD_PHOTO:
                Log.i("jason", "图像上传任务");
                ToastUtil.TextToast("该类型任务尚未开通");
                break;
        }
    }

    private void checkDistinguishPicIsExist() {
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
            times = 0;
            downLoadPic();
        } else {
            boolean hasPic = false;
            for (int i = 0; i < imgUrls.length; i++) {
                for (File file : files) {
                    if (file.getName().equals(StringUtils.getFileNameFromURL(imgUrls[i]))) {
                        imgArr[i] = file.getAbsolutePath();
                        hasPic = true;
                    }
                }
            }
            if (!hasPic) {
                times = 0;
                downLoadPic();
            } else {
                skipToImageDistinguish();
            }
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
        XUtilsHelper.getInstance().downLoad(imgUrls[times], DirUtils.getGameDirectory().getAbsolutePath(), new
                XUtilsHelper.IDownloadCallback() {
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
        if (currentPoint == null) {
            Toast.makeText(getActivity(), "currentPoint为空", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < currentPoint.task_list.size(); i++) {
            if (TextUtils.isEmpty(currentPoint.task_list.get(i).exp)) {
                if (i != currentPoint.task_list.size() - 1 && Integer.parseInt(currentPoint.task_list.get(i + 1)
                        .type) != TaskType.FINISH) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isGameOver) {
            //章节点结束，通知PointTaskV2Activity销毁界面
            setActivityResult();
        }
    }

    @Override
    public void successUploadGameOverRecord(final UpLoadGameRecordResult result) {
        currentTask.ftime = result.task_finish_time;
        currentTask.exp = result.get_exp;
        if (result.game_statu == PlayStatus.GAME_OVER) {
            imgGetKey.setVisibility(View.GONE);
            tvLookGameOverReward.setVisibility(View.VISIBLE);
        }
    }

    private void setActivityResult() {
        //章节点结束，通知PointTaskV2Activity销毁界面
        EventBus.getDefault().post(new GamePointTaskStateChanged());
    }

    @Override
    public void failUploadRecord(String errorMsg) {
        if (errorMsg != null) {
            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "游戏记录上传失败", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick({R.id.img_getKey, R.id.rl_getKey, R.id.tv_look_game_over_reward})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_getKey:
                skipToAnswerTask();
                break;
            case R.id.rl_getKey:
                getActivity().finish();
                break;
            case R.id.tv_look_game_over_reward:
                startActivity(new Intent(getActivity(), GameOverActivity.class));
                break;
        }
    }
}
