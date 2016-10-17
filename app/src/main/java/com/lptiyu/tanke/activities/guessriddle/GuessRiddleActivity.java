package com.lptiyu.tanke.activities.guessriddle;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.Task;
import com.lptiyu.tanke.entity.UpLoadGameRecord;
import com.lptiyu.tanke.entity.response.UpLoadGameRecordResult;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.TaskResultHelper;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.VibratorHelper;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GuessRiddleActivity extends MyBaseActivity implements RiddleContact.IRiddleView {

    @BindView(R.id.et_writeAnswer)
    EditText etWriteAnswer;
    @BindView(R.id.tv_submitAnswer)
    TextView tvSubmitAnswer;
    private Task task;
    @BindView(R.id.img_anim)
    ImageView imgAnim;
    @BindView(R.id.rl_submit_record)
    RelativeLayout rlSubmitRecord;

    private RiddlePresenter presenter;
    private long gameId;
    private Point point;
    private boolean isPointOver;

    private Handler mHandler = new Handler();
    private TaskResultHelper taskResultHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_riddle);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        task = RunApplication.gameRecord.game_detail.point_list.get(RunApplication.currentPointIndex).task_list.get
                (RunApplication.currentTaskIndex);
        gameId = RunApplication.gameId;
        point = RunApplication.gameRecord.game_detail.point_list.get(RunApplication.currentPointIndex);
        isPointOver = getIntent().getBooleanExtra(Conf.IS_POINT_OVER, false);
        Log.i("jason", "当前task：" + task);

        presenter = new RiddlePresenter(this);

        taskResultHelper = new TaskResultHelper(this, rlSubmitRecord, imgAnim, new TaskResultHelper
                .TaskResultCallback() {
            @Override
            public void onSuccess() {
                setActivityResult();
            }
        });

        if (AppData.isFirstInGuessRiddleActivity()) {
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    PopupWindowUtils.getInstance().showTaskGuide(GuessRiddleActivity.this,
                            "这是猜谜任务，提交你的答案，正确即可通关");
                }
            }, 500);
        }
    }

    private void setActivityResult() {
        EventBus.getDefault().post(resultRecord);//通知PointTaskFragment刷新数据
        finish();
    }

    @OnClick({R.id.img_close, R.id.tv_submitAnswer})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                finish();
                break;
            case R.id.tv_submitAnswer:
                String answer = etWriteAnswer.getText() + "";
                if (answer.equals("")) {
                    Toast.makeText(this, "请先输入答案", Toast.LENGTH_SHORT).show();
                    return;
                }
                //TODO 给自己埋下彩蛋
                if (Accounts.getPhoneNumber() != null && Accounts.getPhoneNumber().endsWith("4317") || Accounts
                        .getPhoneNumber().endsWith("1965")) {
                    taskResultHelper.startAnim();
                    upLoadGameRecord();
                    return;
                }
                if (answer.equals(task.pwd)) {
                    taskResultHelper.startAnim();
                    upLoadGameRecord();
                } else {
                    taskResultHelper.showFailResult();
                }
                break;
        }
    }

    private void upLoadGameRecord() {
        UpLoadGameRecord record = new UpLoadGameRecord();
        record.uid = Accounts.getId() + "";
        record.point_id = point.id + "";
        record.game_id = gameId + "";
        if (isPointOver)
            record.point_statu = PointTaskStatus.FINISHED + "";
        else
            record.point_statu = PointTaskStatus.PLAYING + "";
        record.task_id = task.id + "";
        presenter.uploadRecord(record);
    }

    private UpLoadGameRecordResult resultRecord;

    @Override
    public void successUploadRecord(UpLoadGameRecordResult response) {
        resultRecord = response;
        taskResultHelper.showSuccessResult();
        taskResultHelper.stopAnim();
        if (response.game_statu == PlayStatus.GAME_OVER) {
            taskResultHelper.popup_tv_result.setText("游戏完成");
        } else {
            taskResultHelper.popup_tv_result.setText("找到新线索");
        }
        //震动提示
        VibratorHelper.startVibrator(this);

    }

    @Override
    public void failUploadRecord(String errorMsg) {
        ToastUtil.TextToast(errorMsg);
        taskResultHelper.showFailResult();
        taskResultHelper.stopAnim();
    }

    @Override
    public void netException() {
        taskResultHelper.showNetException();
        taskResultHelper.stopAnim();
    }
}
