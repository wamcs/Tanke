package com.lptiyu.tanke.gameplaying.task;

import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.utils.ToastUtil;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-30
 *         email: wonderfulifeel@gmail.com
 */
public class RiddleTaskController extends MultiplyTaskController implements
    View.OnClickListener {

  private View answerView;
  private EditText editText;
  private ImageView mEnsureButton;

  public RiddleTaskController(Fragment fragment, ActivityController controller, View view) {
    super(fragment, controller, view);
  }

  @Override
  public void initTaskView() {
    if (answerView == null) {
      answerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_riddle_task, null);
      RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
      mAnswerArea.addView(answerView, layoutParams);
      editText = ((EditText) answerView.findViewById(R.id.riddle_task_edittext));
      mEnsureButton = ((ImageView) answerView.findViewById(R.id.riddle_task_ensure));
      mEnsureButton.setOnClickListener(this);
      mWebView.loadUrl(mTask.getContent());
    }
  }

  @Override
  public void onClick(View v) {
    finishTask();
    mActivityController.openNextTaskIfExist();
//    switch (v.getId()) {
//      case R.id.riddle_task_ensure:
//        Editable editable = editText.getText();
//        if (editable == null || editable.length() == 0) {
//          ToastUtil.TextToast(getString(R.string.error_input));
//          return;
//        }
//        String userResult = editable.toString();
//        if (mTask.getPwd().equals(userResult)) {
//          ToastUtil.TextToast(getString(R.string.right_answer));
//          finishTask();
//          mActivityController.openNextTaskIfExist();
//        } else {
//          ToastUtil.TextToast(getString(R.string.error_answer));
//        }
//        break;
//    }
  }

}
