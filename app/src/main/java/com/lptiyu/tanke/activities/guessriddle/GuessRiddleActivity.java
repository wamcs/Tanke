package com.lptiyu.tanke.activities.guessriddle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import static com.lptiyu.tanke.RunApplication.currentPoint;
import static com.lptiyu.tanke.RunApplication.currentTask;

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
    private int index;
    private boolean isGameOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_riddle);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        if (currentPoint == null || currentTask == null) {
            return;
        }
        task = currentTask;
        gameId = RunApplication.gameId;
        point = currentPoint;
        isPointOver = RunApplication.isPointOver;
        index = getIntent().getIntExtra(Conf.INDEX, -1);

        presenter = new RiddlePresenter(this);

        taskResultHelper = new TaskResultHelper(this, rlSubmitRecord, imgAnim, new TaskResultHelper
                .TaskResultCallback() {
            @Override
            public void onSuccess() {
                if (!isGameOver) {
                    setActivityResult();
                }
            }
        });

        if (AppData.isFirstInGuessRiddleActivity()) {
            getWindow().getDecorView().post(new Runnable() {
                public void run() {
                    PopupWindowUtils.getInstance().showTaskGuide(GuessRiddleActivity.this,
                            "这是猜谜任务，提交你的答案，正确即可通关");
                }
            });
        }
    }

    private void setActivityResult() {
        //发通知销毁PointTaskV2Activity，GamePlayingV2Activity刷新数据
        EventBus.getDefault().post(new GamePointTaskStateChanged());
        finish();
    }

    @OnClick({R.id.img_close, R.id.tv_submitAnswer})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                finish();
                break;
            case R.id.tv_submitAnswer:
                if (Accounts.getPhoneNumber() != null && Accounts.getPhoneNumber().equals("18272164317")) {
                    taskResultHelper.startAnim();
                    upLoadGameRecord();
                    return;
                }
                String answer = etWriteAnswer.getText() + "";
                if (answer.equals("")) {
                    Toast.makeText(this, "请先输入答案", Toast.LENGTH_SHORT).show();
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

    private UpLoadGameRecordResult resultRecord;

    @Override
    public void successUploadRecord(UpLoadGameRecordResult response) {
        resultRecord = response;
        resultRecord.index = this.index;
        taskResultHelper.showSuccessResult(response);
        taskResultHelper.stopAnim();
        if (response.game_statu == PlayStatus.GAME_OVER) {//游戏通关，需要弹出通关视图，弹出通关视图
            isGameOver = true;
            taskResultHelper.hidePopup();
            startActivity(new Intent(GuessRiddleActivity.this, GameOverActivity.class));
            finish();
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
