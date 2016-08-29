package com.lptiyu.tanke.activities.guessriddle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lptiyu.tanke.R;
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
import com.lptiyu.tanke.utils.PopupWindowUtils;
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
    //    @BindView(R.id.tv_please_wait)
    //    TextView tvIsScanning;
    @BindView(R.id.rl_submit_record)
    RelativeLayout rlSubmitRecord;

    private TextView popup_tv_btn;
    private ImageView popup_img_result;
    private TextView popup_tv_result;
    private PopupWindow popupWindow;
    private View popupView;
    private boolean isOK;
    private AnimationDrawable anim;
    private RiddlePresenter presenter;
    private long gameId;
    //    private long gameType;
    private Point point;
    private boolean isPointOver;

    private final String FAIL = "什么都没有发现";
    private final String NET_EXCEPTION = "网络错误";
    private final String SUCESS = "找到新线索";
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_riddle);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        task = intent.getParcelableExtra(Conf.CURRENT_TASK);
        gameId = getIntent().getLongExtra(Conf.GAME_ID, 0);
        //        gameType = getIntent().getLongExtra(Conf.GAME_TYPE, 0);
        point = getIntent().getParcelableExtra(Conf.POINT);
        isPointOver = getIntent().getBooleanExtra(Conf.IS_POINT_OVER, false);

        presenter = new RiddlePresenter(this);

        Log.i("jason", "当前task：" + task);

        initPopupwindow();
        initAnim();

        if (AppData.isFirstInGuessRiddleActivity()) {
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    PopupWindowUtils.getInstance().showTaskGuide(GuessRiddleActivity.this,
                            "这是猜谜任务，提交你的答案，正确即可通关");
                }
            }, 500);
        }

    }

    private void initPopupwindow() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_scan_result, null);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //        popupWindow.setAnimationStyle(R.style.Popup_Animation);

        popup_tv_btn = (TextView) popupView.findViewById(R.id.tv_continue_scan);
        popup_img_result = (ImageView) popupView.findViewById(R.id.img_result);
        popup_tv_result = (TextView) popupView.findViewById(R.id.tv_result_tip);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (isOK) {
                    Intent intent = new Intent();
                    intent.putExtra(Conf.UPLOAD_RECORD_RESPONSE, resultRecord);
                    GuessRiddleActivity.this.setResult(ResultCode.GUESS_RIDDLE, intent);
                    finish();
                } else {
                    hidePopup();
                }
            }
        });

        popup_tv_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
//                if (isOK) {
//                    Intent intent = new Intent();
//                    intent.putExtra(Conf.UPLOAD_RECORD_RESPONSE, resultRecord);
//                    GuessRiddleActivity.this.setResult(ResultCode.GUESS_RIDDLE, intent);
//                    finish();
//                } else {
//                    hidePopup();
//                }
            }
        });
    }

    private void showPopup() {
        if (popupView != null && popupWindow != null) {
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        }
    }

    private void hidePopup() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private void initAnim() {
        imgAnim.setBackgroundResource(R.drawable.anim_upload_record);
        anim = (AnimationDrawable) imgAnim.getBackground();
    }

    private void startAnim() {
        if (anim != null) {
            anim.start();
        }
        rlSubmitRecord.setVisibility(View.VISIBLE);
    }

    private void stopAnim() {
        if (anim != null) {
            anim.stop();
        }
        rlSubmitRecord.setVisibility(View.GONE);
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
                startAnim();
                if (answer.equals(task.pwd)) {
                    upLoadGameRecord();
                } else {
                    showFailResult();
                }
                break;
        }
    }

    private void upLoadGameRecord() {
        UpLoadGameRecord record = new UpLoadGameRecord();
        record.uid = Accounts.getId() + "";
        //        record.type = gameType + "";
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
        resultRecord = response;
        showSuccessResult();
        if (response.game_statu == PlayStatus.GAME_OVER) {
            popup_tv_result.setText("游戏完成");
        } else {
            popup_tv_result.setText("找到新线索");
        }
        //震动提示
        VibratorHelper.startVibrator(this);

    }

    @Override
    public void failUploadRecord() {
        showFailResult();
    }

    @Override
    public void netException() {
        showNetException();
    }

    /**
     * 展示成功信息
     */
    private void showSuccessResult() {
        isOK = true;
        stopAnim();
        popup_tv_btn.setText("查看");
        popup_img_result.setImageResource(R.drawable.task_result_right);
        popup_tv_result.setText(SUCESS);
        showPopup();
    }

    /**
     * 展示失败信息
     */
    private void showFailResult() {
        isOK = false;
        stopAnim();
        popup_tv_btn.setText("关闭");
        popup_img_result.setImageResource(R.drawable.task_result_wrong);
        popup_tv_result.setText(FAIL);
        showPopup();
    }

    /**
     * 展示网络异常信息
     */
    private void showNetException() {
        isOK = false;
        stopAnim();
        popup_tv_btn.setText("关闭");
        popup_img_result.setImageResource(R.drawable.task_result_wrong);
        popup_tv_result.setText(NET_EXCEPTION);
        showPopup();
    }
}
