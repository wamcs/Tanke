package com.lptiyu.tanke.gameplaying.task;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.utils.Inflater;
import com.lptiyu.tanke.utils.ToastUtil;


/**
 * @author : xiaoxiaoda
 *         date: 16-5-30
 *         email: wonderfulifeel@gmail.com
 */
public class TimingTaskController extends MultiplyTaskController implements
    TimingCounter.OnCounterFinishedListener {

  private View answerView;

  private TimingCounter counter;

  private static final long DEFAULT_TIMING_MILLIS = 10000L;
  private static final long DEFAULT_TIMING_INTERVAL = 1000L;

  public TimingTaskController(Fragment fragment, ActivityController controller, View view) {
    super(fragment, controller, view);
  }

  @Override
  public void initTaskView() {
    if (answerView == null) {
      answerView = Inflater.inflate(R.layout.layout_timing_task, null, false);
      RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
      mAnswerArea.addView(answerView, layoutParams);
      mAnswerArea.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//          ToastUtil.TextToast("定时器将于10s后启动");
//          counter.startCounting();

          finishTask();
          mActivityController.openNextTaskIfExist();

        }
      });
      counter = new TimingCounter(getContext(), DEFAULT_TIMING_MILLIS, DEFAULT_TIMING_INTERVAL);
      counter.setListener(this);
      mWebView.loadUrl(mTask.getContent());
    }
  }

  @Override
  public void onCounterFinished() {
    getFragment().getActivity().finish();
  }

}