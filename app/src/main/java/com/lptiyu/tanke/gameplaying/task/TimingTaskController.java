package com.lptiyu.tanke.gameplaying.task;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseFragment;
import com.lptiyu.tanke.gameplaying.pojo.GAME_ACTIVITY_FINISH_TYPE;
import com.lptiyu.tanke.utils.Inflater;
import com.lptiyu.tanke.utils.ToastUtil;

import timber.log.Timber;


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
          counter.startCounting();
//          finishTask();
//          mActivityController.openNextTaskIfExist();
        }
      });
      counter = new TimingCounter(getContext(), DEFAULT_TIMING_MILLIS, DEFAULT_TIMING_INTERVAL);
      counter.setListener(this);
      mWebView.loadUrl(mTask.getContent());
    }
  }

  @Override
  public void onCounterFinished() {
    //finish game activity and notify the GamePlayingActivity to start timing task flow
    Fragment fragment = getFragment();
    if (fragment == null) {
      return;
    }
    if (!(fragment instanceof BaseFragment)) {
      Timber.d("Fragment : %s is not instance of BaseFragment", fragment.toString());
      return;
    }
    BaseFragment baseFragment = ((BaseFragment) fragment);
    ActivityController activityController = baseFragment.getActivityController();
    if (activityController == null || (!(activityController instanceof GameTaskController))) {
      Timber.d("ActivityController : %s is not instance of GameTaskController", activityController.toString());
      return;
    }
    ((GameTaskController) activityController).finishGameTaskActivityByType(GAME_ACTIVITY_FINISH_TYPE.TIMING_TASK, mTask);
  }

}
