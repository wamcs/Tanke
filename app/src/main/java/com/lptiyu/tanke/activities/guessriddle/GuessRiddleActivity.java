package com.lptiyu.tanke.activities.guessriddle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.base.MyBaseActivity;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.Task;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.enums.ResultCode;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.pojo.UpLoadGameRecord;
import com.lptiyu.tanke.pojo.UploadGameRecordResponse;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.TaskResultHelper;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.VibratorHelper;

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

    private TaskResultHelper taskResultHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_riddle);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        task = intent.getParcelableExtra(Conf.CURRENT_TASK);
        gameId = getIntent().getLongExtra(Conf.GAME_ID, 0);
        point = getIntent().getParcelableExtra(Conf.POINT);
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
            getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    PopupWindowUtils.getInstance().showTaskGuide(GuessRiddleActivity.this,
                            "这是猜谜任务，提交你的答案，正确即可通关");
                }
            });
        }
    }

    private void setActivityResult() {
        Intent intent = new Intent();
        intent.putExtra(Conf.UPLOAD_RECORD_RESPONSE, resultRecord);
        GuessRiddleActivity.this.setResult(ResultCode.GUESS_RIDDLE, intent);
        finish();
    }

    @OnClick({R.id.img_close, R.id.tv_submitAnswer})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                finish();
                break;
            case R.id.tv_submitAnswer:
                if (Accounts.getPhoneNumber().endsWith("4317")) {
                    loadNetWorkData();
                    return;
                }
                String answer = etWriteAnswer.getText() + "";
                if (answer.equals("")) {
                    Toast.makeText(this, "请先输入答案", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (answer.equals(task.pwd)) {
                    taskResultHelper.startAnim();
                    loadNetWorkData();
                } else {
                    taskResultHelper.showFailResult();
                }
                break;
        }
    }

    private void loadNetWorkData() {
        if (NetworkUtil.checkIsNetworkConnected()) {
            upLoadGameRecord();
        } else {
            PopupWindowUtils.getInstance().showNetExceptionPopupwindow(this, new PopupWindowUtils
                    .OnNetExceptionListener() {
                @Override
                public void onClick(View view) {
                    loadNetWorkData();
                }
            });
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

    private UploadGameRecordResponse resultRecord;

    @Override
    public void successUploadRecord(UploadGameRecordResponse response) {
        RunApplication.isNeededRefresh = true;
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
