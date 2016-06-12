package com.lptiyu.tanke.gameplaying.assist;

import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.gameplaying.GamePlayingController;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.lptiyu.tanke.gameplaying.records.RecordsUtils;
import com.lptiyu.tanke.gameplaying.records.RunningRecord;
import com.lptiyu.tanke.utils.TimeUtils;
import com.lptiyu.tanke.utils.VibrateUtils;
import com.lptiyu.tanke.widget.TickView;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-6
 *         email: wonderfulifeel@gmail.com
 */
public class TimingTaskHelper implements
    TickView.OnTickFinishListener {

  private TickView mTickView;
  private Task timingTask;

  boolean isTimingTask = false;
  boolean isTimingSuccess = true;

  private AlertDialog mAlertDialog;

  private WeakReference<GamePlayingController> contextWeakReference;

  public TimingTaskHelper(GamePlayingController controller, TickView tickView) {
    contextWeakReference = new WeakReference<>(controller);
    mTickView = tickView;
    mTickView.setmListener(this);
  }

  /**
   * This method is to start timing task
   * when receive timing signal from GameTaskActivity
   */
  public void startTimingTaskFlow(Task task, long startTime) {
    isTimingTask = true;
    isTimingSuccess = true;
    timingTask = task;
    long limitTime = startTime + Integer.valueOf(timingTask.getPwd()) * TimeUtils.ONE_MINUTE_TIME - System.currentTimeMillis();
    showTickView(limitTime);
  }

  public void checkTimingTask() {
    if (isTimingTask) {
      if (isTimingSuccess) {
        mTickView.stopTick();
        dismissTickView();
        //TODO : user arrive the point at time
        // notify user
        showAlertDialog("您在指定时间内完成");
        isTimingSuccess = false;
      }
      if (contextWeakReference.get() != null) {
        GamePlayingController controller = contextWeakReference.get();
        Point lastPoint = controller.getLastPoint();
        if (lastPoint != null) {
          List<String> taskIds = lastPoint.getTaskId();
          if (taskIds != null && taskIds.size() != 0) {
            long pointId = lastPoint.getId();
            long taskId = Long.valueOf(taskIds.get(taskIds.size() - 1));
            RecordsUtils.dispatchTypeRecord(34.123123, 114.321321, pointId, taskId, RunningRecord.RECORD_TYPE.TASK_FINISH);
            RecordsUtils.dispatchTypeRecord(34.123123, 114.321321, pointId, 0, RunningRecord.RECORD_TYPE.POINT_FINISH);
          }
        }
      }
    }
    isTimingTask = false;
  }

  private void showTickView(final long limitTime) {
    if (contextWeakReference.get() == null) {
      return;
    }
    int translationY = (int) (mTickView.getHeight() + contextWeakReference.get().getResources().getDimension(R.dimen.tick_view_margin_top));
    mTickView.setTranslationY(-translationY);
    mTickView.setVisibility(View.VISIBLE);
    mTickView.animate().setInterpolator(new BounceInterpolator()).translationY(0).setDuration(800).setListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {
        mTickView.startTick(limitTime);
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });

  }

  private void dismissTickView() {
    if (contextWeakReference.get() == null) {
      return;
    }
    int translationY = (int) (mTickView.getHeight() + contextWeakReference.get().getResources().getDimension(R.dimen.tick_view_margin_top));
    mTickView.animate().setInterpolator(new AccelerateInterpolator()).translationY(-translationY).setDuration(500).setListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {
        mTickView.setVisibility(View.GONE);
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });
  }

  @Override
  public void onTickFinish() {
    showAlertDialog("计时任务结束");
    isTimingSuccess = false;
    dismissTickView();
  }

  void showAlertDialog(String message) {
    if (contextWeakReference.get() == null) {
      return;
    }
    Context context = contextWeakReference.get().getContext();
    if (context == null) {
      return;
    }
    if (mAlertDialog == null) {
      mAlertDialog = new AlertDialog.Builder(context)
          .setPositiveButton(context.getString(R.string.ensure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              VibrateUtils.cancel();
            }
          })
          .setMessage(message)
          .create();
    }
    mAlertDialog.show();
  }

  public boolean isTimingTask() {
    return isTimingTask;
  }
}
