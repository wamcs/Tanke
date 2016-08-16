package com.lptiyu.tanke.activities.pointtask;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.base.MyBaseActivity;
import com.lptiyu.tanke.activities.guessriddle.GuessRiddleActivity;
import com.lptiyu.tanke.activities.imagedistinguish.ImageDistinguishActivity;
import com.lptiyu.tanke.activities.locationtask.LocationTaskActivity;
import com.lptiyu.tanke.adapter.LVForPointTaskAdapter;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.PointRecord;
import com.lptiyu.tanke.entity.Task;
import com.lptiyu.tanke.entity.TaskRecord;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.enums.RequestCode;
import com.lptiyu.tanke.enums.ResultCode;
import com.lptiyu.tanke.enums.TaskType;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.pojo.UpLoadGameRecord;
import com.lptiyu.tanke.pojo.UploadGameRecordResponse;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.widget.CustomTextView;
import com.lptiyu.zxinglib.android.CaptureActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PointTaskActivity extends MyBaseActivity implements PointTaskContact.IPointTaskView {

    @BindView(R.id.ctv_taskName)
    CustomTextView ctvTaskName;
    //    @BindView(R.id.et_input_answer)
    //    EditText etInputAnswer;
    //    @BindView(R.id.ctv_getAnswer)
    //    CustomTextView ctvGetAnswer;
    @BindView(R.id.lv)
    ListView lv;
    @BindView(R.id.img_getKey)
    ImageView imgGetKey;

    private Point point;
    private LVForPointTaskAdapter adapter;
    private PointTaskPresenter presenter;
    private long gameId;
    //    private long gameType;
    private ArrayList<TaskRecord> list_task_record;
    private ArrayList<Task> list_task;
    private int selectPosition;
    private PointRecord point_record;
    private String unZippedDir;
    private Task currentTask;
    private boolean isPointOver;
    private boolean isFinishedPoint;
    private boolean isLastAvaliableTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_task);
        ButterKnife.bind(this);

        presenter = new PointTaskPresenter(this);

        //        initStyle();

        initData();
    }

    //    private void initStyle() {
    //        Window window = getWindow();
    //        WindowManager.LayoutParams params = window.getAttributes();
    //        params.height = (int) (Display.height() * 0.8f);
    //        params.width = (int) (Display.width() * 0.9f);
    //        window.setAttributes(params);
    //        window.setGravity(Gravity.CENTER);
    //    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Toast.makeText(this, "数据传递错误", Toast.LENGTH_SHORT).show();
            return;
        }
        point = bundle.getParcelable(Conf.POINT);
        point_record = bundle.getParcelable(Conf.POINT_RECORD);
        gameId = bundle.getLong(Conf.GAME_ID, -1);
        //        gameType = bundle.getLong(Conf.GAME_TYPE, -1);
        unZippedDir = bundle.getString(Conf.UNZIPPED_DIR);
        isFinishedPoint = bundle.getBoolean(Conf.IS_FINISHED_POINT);
        if (isFinishedPoint) {
            imgGetKey.setVisibility(View.GONE);
        }

        if (point == null) {
            Toast.makeText(this, "该章节点数据异常", Toast.LENGTH_SHORT).show();
            return;
        }
        if (point.list_task == null || point.list_task.size() <= 0) {
            Toast.makeText(this, "该章节点无任务", Toast.LENGTH_SHORT).show();
            return;
        }
        list_task = point.list_task;

        if (point_record == null) {
            list_task_record = null;
        } else {
            list_task_record = point_record.task;
        }

        ctvTaskName.setText(point.point_title + "");

        //如果当前章节点只有一个任务并且是FINISH类型的任务，则表示该章节点结束（这种情况一般在最后一个章节点出现）
        if (Integer.parseInt(list_task.get(0).type) == TaskType.FINISH) {
            imgGetKey.setVisibility(View.GONE);
            selectPosition = 0;
            list_task.get(0).state = PointTaskStatus.FINISHED;
            uploadPointOverRecord();
            return;
        }
        //根据任务记录决定当前任务的状态
        checkTaskState();

        setAdapter();
    }

    /**
     * 章节点结束时上传记录
     */
    private void uploadPointOverRecord() {
        isPointOver = true;
        UpLoadGameRecord record = new UpLoadGameRecord();
        record.uid = Accounts.getId() + "";
        //        record.type = gameType + "";
        record.point_id = point.id + "";
        record.game_id = gameId + "";
        record.point_statu = PointTaskStatus.FINISHED + "";
        record.task_id = list_task.get(selectPosition).id + "";
        presenter.uploadGameOverRecord(record);
    }

    @Override
    public void successUploadGameOverRecord(UploadGameRecordResponse response) {
        //根据任务记录决定当前任务的状态
        //        checkTaskState();
        setAdapter();
    }

    private void setAdapter() {
        adapter = new LVForPointTaskAdapter(this, list_task, list_task_record);
        lv.setAdapter(adapter);
        lv.setSelection(selectPosition);
        currentTask = list_task.get(selectPosition);
        Log.i("jason", "selectPosition=" + selectPosition);
        if (Integer.parseInt(currentTask.type) == TaskType.FINISH) {
            imgGetKey.setVisibility(View.GONE);
        }
    }

    private int maxFinishedIndex;

    /**
     * 根据游戏记录设置任务的状态
     */
    private void checkTaskState() {
        //如果当前章节点只有一个任务并且是FINISH类型的任务，则表示该章节点结束（这种情况一般在最后一个章节点出现）
        if (Integer.parseInt(list_task.get(0).type) == TaskType.FINISH) {
            isPointOver = true;
            for (Task task : list_task) {
                task.state = PointTaskStatus.FINISHED;
            }
            imgGetKey.setVisibility(View.GONE);
            return;
        } else if (point.state == PointTaskStatus.FINISHED) {//章节点已结束，所有任务已完成
            for (Task task : list_task) {
                task.state = PointTaskStatus.FINISHED;
            }
            isPointOver = true;
            selectPosition = 0;
            imgGetKey.setVisibility(View.GONE);
        } else if (point.state == PointTaskStatus.UNSTARTED) {//章节点未开启，所有任务未开启
            for (Task task : list_task) {
                task.state = PointTaskStatus.UNSTARTED;
            }
            selectPosition = 0;
        } else {//章节点已开启
            if (list_task_record != null && list_task_record.size() > 0) {
                //根据游戏记录设置任务状态
                for (int i = 0; i < list_task_record.size(); i++) {
                    TaskRecord taskRecord = list_task_record.get(i);
                    for (int j = 0; j < list_task.size(); j++) {
                        Task task = list_task.get(j);
                        if (Long.parseLong(task.id) == taskRecord.id) {
                            task.state = PointTaskStatus.FINISHED;
                            maxFinishedIndex = j;
                            break;
                        }
                    }
                }
                //将最后一个已完成后面的那个任务的状态设置为进行中
                if (maxFinishedIndex != list_task.size() - 1) {
                    selectPosition = maxFinishedIndex + 1;
                    list_task.get(maxFinishedIndex + 1).state = PointTaskStatus.PLAYING;
                }
            } else {//章节点虽然开启，但是还没有完成过任务
                for (Task task : list_task) {
                    task.state = PointTaskStatus.UNSTARTED;
                }
                selectPosition = 0;
            }
        }

        if (selectPosition == list_task.size() - 1) {
            isPointOver = true;
        } else {
            if (Integer.parseInt(list_task.get(selectPosition + 1).type) == TaskType.FINISH) {
                isPointOver = true;
            } else {
                isPointOver = false;
            }
        }
    }

    /**
     * 任务完成后回调
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {//扫码识别返回
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
        if (resultCode == ResultCode.IMAGE_DISTINGUISH) {//图像识别返回
            UploadGameRecordResponse resultResponse = intent.getParcelableExtra(Conf.UPLOAD_RECORD_RESPONSE);
            refreshData(resultResponse);
        }
        if (resultCode == ResultCode.GUESS_RIDDLE) {//猜谜返回
            UploadGameRecordResponse resultResponse = intent.getParcelableExtra(Conf.UPLOAD_RECORD_RESPONSE);
            refreshData(resultResponse);
        }
        if (resultCode == ResultCode.LOCATION_TASK) {//定位返回
            UploadGameRecordResponse resultResponse = intent.getParcelableExtra(Conf.UPLOAD_RECORD_RESPONSE);
            refreshData(resultResponse);
        }

    }

    /**
     * 任务完成后要执行的
     */
    private void finishedCurrentTast() {
        //        uploadGameRecordDialog = ProgressDialog.show(this, null, "正在上传游戏记录...", true, true);
        //上传游戏记录
        upLoadGameRecord();
    }

    /**
     * 先将游戏记录上传到服务器，获取服务器返回的joint_time和start_time,再将游戏记录存储到本地数据库（游戏记录依然以本地数据库的为准）
     */
    private void upLoadGameRecord() {
        UpLoadGameRecord record = new UpLoadGameRecord();
        record.uid = Accounts.getId() + "";
        //        record.type = gameType + "";
        record.point_id = point.id + "";
        record.game_id = gameId + "";
        //        if (selectPosition == list_task.size() - 1) {
        //            isPointOver = true;
        //        } else {
        //            if (Integer.parseInt(list_task.get(selectPosition + 1).type) == TaskType.FINISH) {
        //                isPointOver = true;
        //            } else {
        //                isPointOver = false;
        //            }
        //        }
        if (selectPosition == list_task.size() - 1) {
            isPointOver = true;
        } else {
            if (selectPosition + 1 == list_task.size() - 1) {
                isPointOver = true;
            } else {
                isPointOver = false;
            }
        }

        if (isPointOver) {
            record.point_statu = PointTaskStatus.FINISHED + "";
        } else {
            record.point_statu = PointTaskStatus.PLAYING + "";
        }
        record.task_id = currentTask.id + "";
        presenter.uploadRecord(record);
    }

    @Override
    public void successUploadRecord(UploadGameRecordResponse response) {
        Log.i("jason", "成功回调");
        //将返回的游戏记录添加到list_task_record中
        TaskRecord taskRecord = new TaskRecord();
        taskRecord.ftime = response.task_finish_time;
        taskRecord.exp = response.get_exp;
        taskRecord.id = Long.parseLong(currentTask.id);
        if (list_task_record == null) {
            list_task_record = new ArrayList<>();
        }
        list_task_record.add(taskRecord);

        if (selectPosition == list_task.size() - 1) {
            list_task.get(selectPosition).state = PointTaskStatus.FINISHED;
            imgGetKey.setVisibility(View.GONE);
            isPointOver = true;
        } else {
            if (Integer.parseInt(list_task.get(selectPosition + 1).type) == TaskType.FINISH) {
                list_task.get(selectPosition).state = PointTaskStatus.FINISHED;
                list_task.get(selectPosition + 1).state = PointTaskStatus.FINISHED;
            } else {
                list_task.get(selectPosition).state = PointTaskStatus.FINISHED;
                list_task.get(selectPosition + 1).state = PointTaskStatus.PLAYING;
            }
            if (selectPosition + 1 == list_task.size() - 1) {
                isPointOver = true;
            } else {
                isPointOver = false;
            }
            selectPosition += 1;
        }
        setAdapter();
    }


    @Override
    public void failUploadRecord() {
        Toast.makeText(this, "提交失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void netException() {
        Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show();
    }

    private void refreshData(UploadGameRecordResponse response) {
        //将返回的游戏记录添加到list_task_record中
        TaskRecord taskRecord = new TaskRecord();
        taskRecord.ftime = response.task_finish_time;
        taskRecord.exp = response.get_exp;
        taskRecord.id = Long.parseLong(currentTask.id);
        if (list_task_record == null) {
            list_task_record = new ArrayList<>();
        }
        list_task_record.add(taskRecord);

        if (selectPosition == list_task.size() - 1) {
            list_task.get(selectPosition).state = PointTaskStatus.FINISHED;
            imgGetKey.setVisibility(View.GONE);
            isPointOver = true;
        } else {
            if (Integer.parseInt(list_task.get(selectPosition + 1).type) == TaskType.FINISH) {
                list_task.get(selectPosition).state = PointTaskStatus.FINISHED;
                list_task.get(selectPosition + 1).state = PointTaskStatus.FINISHED;
            } else {
                list_task.get(selectPosition).state = PointTaskStatus.FINISHED;
                list_task.get(selectPosition + 1).state = PointTaskStatus.PLAYING;
            }
            if (selectPosition + 1 == list_task.size() - 1) {
                isPointOver = true;
            } else {
                isPointOver = false;
            }
            selectPosition += 1;
        }
        setAdapter();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isPointOver) {
            setResult(ResultCode.POINT_OVER);
        }
        finish();
    }

    @OnClick({R.id.img_close, R.id.img_getKey})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                if (isPointOver) {
                    setResult(ResultCode.POINT_OVER);
                }
                finish();
                break;
            case R.id.img_getKey:
                //跳转
                Intent intent = new Intent();
                intent.putExtra(Conf.GAME_ID, gameId);
                //                intent.putExtra(Conf.GAME_TYPE, gameType);
                intent.putExtra(Conf.POINT, point);
                intent.putExtra(Conf.CURRENT_TASK, currentTask);
                if (selectPosition == list_task.size() - 1) {
                    isPointOver = true;
                } else {
                    if (selectPosition + 1 == list_task.size() - 1) {
                        isPointOver = true;
                    } else {
                        isPointOver = false;
                    }
                }
                intent.putExtra(Conf.IS_POINT_OVER, isPointOver);
                Log.i("jason", "isPointOver=" + isPointOver);
                //根据当前任务的类型决定如何操作
                switch (Integer.parseInt(currentTask.type)) {
                    case TaskType.DISTINGUISH:
                        //TODO 等待交互
                        Log.i("jason", "图像识别任务");
                        intent.setClass(PointTaskActivity.this, ImageDistinguishActivity.class);
                        Log.i("jason", "要识别的图片路径：" + currentTask.pwd);
                        intent.putExtra(Conf.IMG_DISTINGUISH_URL, currentTask.pwd);
                        intent.putExtra(Conf.PARENT_DIR, unZippedDir);
                        startActivityForResult(intent, RequestCode.IMAGE_DISTINGUISH);
                        Log.i("jason", "跳转完毕");
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
                        intent.setClass(PointTaskActivity.this, GuessRiddleActivity.class);
                        startActivityForResult(intent, RequestCode.GUESS_RIDDLE);
                        break;
                    case TaskType.SCAN_CODE:
                        Log.i("jason", "扫码任务");
                        intent.putExtra("uid", Accounts.getId());
                        intent.putExtra("task_id", currentTask.id);
                        intent.putExtra("point_id", point.id);
                        intent.setClass(PointTaskActivity.this, CaptureActivity.class);
                        startActivityForResult(intent, RequestCode.CAMERA_PERMISSION_REQUEST_CODE);
                        break;
                    case TaskType.UPLOAD_PHOTO:
                        Log.i("jason", "图像上传任务");
                        ToastUtil.TextToast("该类型任务尚未开通");
                        break;
                }
                break;
        }
    }

    private void initGPS() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("为了定位更加精确，请打开GPS");
            dialog.setPositiveButton("确定",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                        }
                    });
            dialog.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        } else {
            Intent intent = new Intent();
            intent.putExtra(Conf.GAME_ID, gameId);
            intent.putExtra(Conf.POINT, point);
            intent.putExtra(Conf.CURRENT_TASK, currentTask);
            intent.setClass(PointTaskActivity.this, LocationTaskActivity.class);
            startActivityForResult(intent, RequestCode.LOCATION_TASK);
        }
    }
}
