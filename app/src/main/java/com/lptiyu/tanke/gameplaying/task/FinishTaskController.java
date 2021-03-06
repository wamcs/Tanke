package com.lptiyu.tanke.gameplaying.task;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-30
 *         email: wonderfulifeel@gmail.com
 */
public class FinishTaskController extends MultiplyTaskController {

  private View answerView;

  public FinishTaskController(Fragment fragment, ActivityController controller, View view) {
    super(fragment, controller, view);
  }

  @Override
  public void initTaskView() {
    if (answerView == null) {
      answerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_finish_task, null);
      RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
      mAnswerArea.addView(answerView, layoutParams);
      mAnswerArea.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mActivityController.onBackPressed();
        }
      });
      mWebView.loadUrl(mTask.getContent());
    }
  }
}
