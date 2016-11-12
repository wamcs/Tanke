package com.lptiyu.tanke.activities.guessriddle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.gameover.GameOverActivity;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.Task;
import com.lptiyu.tanke.entity.UploadGameRecord;
import com.lptiyu.tanke.entity.eventbus.GamePointTaskStateChanged;
import com.lptiyu.tanke.entity.response.UpLoadGameRecordResult;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.TaskResultHelper;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.VibratorHelper;
import com.lptiyu.tanke.widget.CustomTextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lptiyu.tanke.RunApplication.currentPoint;
import static com.lptiyu.tanke.RunApplication.currentTask;

public class GuessRiddleActivity extends MyBaseActivity implements RiddleContact.IRiddleView {

    @BindView(R.id.et_writeAnswer)
    EditText etWriteAnswer;
    @BindView(R.id.tv_submitAnswer)
    TextView tvSubmitAnswer;
    @BindView(R.id.default_tool_bar_textview)
    CustomTextView ctvTitle;

    private RiddlePresenter presenter;
    private long gameId;
    private Point point;
    private boolean isPointOver;
    private TaskResultHelper taskResultHelper;
    private Task task;
    //    private int index;
    private boolean isGameOver;
    private Thread thread;
    private final long duration = 2000;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_riddle);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        ctvTitle.setText("提交答案");
        if (currentPoint == null || currentTask == null) {
            return;
        }
        task = currentTask;
        gameId = RunApplication.gameId;
        point = currentPoint;
        isPointOver = RunApplication.isPointOver;
        //        index = getIntent().getIntExtra(Conf.INDEX, -1);
        etWriteAnswer.setHint(String.format(getString(R.string.answer_text_count), task.pwd.length()));

        presenter = new RiddlePresenter(this);

        taskResultHelper = new TaskResultHelper(this, new TaskResultHelper
                .TaskResultCallback() {
            @Override
            public void onSuccess() {
                if (!isGameOver) {
                    setActivityResult();
                }
            }
        });
        initToast();
    }

    private void setActivityResult() {
        //发通知销毁PointTaskV2Activity，GamePlayingV2Activity刷新数据
        EventBus.getDefault().post(new GamePointTaskStateChanged());
        finish();
    }

    private void initTimeTask() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(duration);
                    thread = null;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initToast();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void initToast() {
        toast = Toast.makeText(this, "请先输入答案", Toast.LENGTH_SHORT);
    }

    @OnClick({R.id.tv_submitAnswer, R.id.default_tool_bar_imageview})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.default_tool_bar_imageview:
                finish();
                break;
            case R.id.tv_submitAnswer:
                String answer = etWriteAnswer.getText() + "";
                if (answer.equals("")) {
                    //控制Toast的显示
                    if (toast != null) {
                        toast.show();
                        toast = null;
                        if (thread == null) {
                            initTimeTask();
                        }
                    }
                    tvSubmitAnswer.setEnabled(true);
                    return;
                }
                if (answer.equals(task.pwd)) {
                    tvSubmitAnswer.setEnabled(false);
                    upload();
                } else {
                    taskResultHelper.showFailResult();
                }
                break;
        }
    }

    public void upload() {
        if (NetworkUtil.checkIsNetworkConnected()) {
            taskResultHelper.startSubmitting();
            upLoadGameRecord();
        } else {
            PopupWindowUtils.getInstance().showNetExceptionPopupwindow(this, new PopupWindowUtils.OnRetryCallback() {
                @Override
                public void onRetry() {
                    tvSubmitAnswer.setEnabled(true);
                    upload();
                }
            });
        }
    }

    private void upLoadGameRecord() {
        UploadGameRecord record = new UploadGameRecord();
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

    @Override
    public void successUploadRecord(UpLoadGameRecordResult response) {
        taskResultHelper.showSuccessResult(response);
        taskResultHelper.stopSubmitting();
        if (response.game_statu == PlayStatus.GAME_OVER) {//游戏通关，需要弹出通关视图，弹出通关视图
            isGameOver = true;
            taskResultHelper.dismiss();
            startActivity(new Intent(GuessRiddleActivity.this, GameOverActivity.class));
            finish();
        }
        //震动提示
        VibratorHelper.startVibrator(this);
    }

    @Override
    public void failLoad() {
        super.failLoad();
        taskResultHelper.showNetException();
        taskResultHelper.stopSubmitting();
        tvSubmitAnswer.setEnabled(true);
    }

    @Override
    public void failLoad(String errMsg) {
        super.failLoad(errMsg);
        taskResultHelper.showNetException();
        taskResultHelper.stopSubmitting();
        tvSubmitAnswer.setEnabled(true);
    }

    @Override
    public void failUploadRecord(String errorMsg) {
        ToastUtil.TextToast(errorMsg);
        taskResultHelper.showFailResult();
        taskResultHelper.stopSubmitting();
        tvSubmitAnswer.setEnabled(true);
    }

    @Override
    public void netException() {
        taskResultHelper.showNetException();
        taskResultHelper.stopSubmitting();
        tvSubmitAnswer.setEnabled(true);
    }
}
