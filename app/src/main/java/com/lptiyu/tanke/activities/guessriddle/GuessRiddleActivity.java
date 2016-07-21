package com.lptiyu.tanke.activities.guessriddle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.enums.ResultCode;
import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.lptiyu.tanke.widget.CustomTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuessRiddleActivity extends AppCompatActivity {

    @BindView(R.id.ctv_showAnswer)
    CustomTextView ctvShowAnswer;
    @BindView(R.id.et_writeAnswer)
    EditText etWriteAnswer;
    @BindView(R.id.btn_submitAnswer)
    Button btnSubmitAnswer;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_riddle);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        task = intent.getParcelableExtra(Task.class.getName());

        Log.i("jason", "当前task：" + task);
    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn_submitAnswer:
                String answer = etWriteAnswer.getText() + "";
                ctvShowAnswer.setText("你输入的答案：" + answer);
                if (answer.equals("")) {
                    Toast.makeText(this, "先写点什么吧", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (answer.equals(task.getPwd())) {
                    Toast.makeText(this, "答案正确", Toast.LENGTH_SHORT).show();
                    setResult(ResultCode.GUESS_RIDDLE);
                    finish();
                } else {
                    Toast.makeText(this, "答案错误,请重新输入答案", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
