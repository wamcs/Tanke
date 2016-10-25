package com.lptiyu.tanke.gameplaying.assist;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.widget.BaseSpotScrollView;
import com.lptiyu.tanke.widget.RunningSpotScrollView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-1
 *         email: wonderfulifeel@gmail.com
 */
public class ConsoleHelper {

  @BindView(R.id.running_scroll_view)
  RunningSpotScrollView runningSpotScrollView;

  List<Point> mPoints;
  Context context;
  AppCompatActivity mActivity;

  public ConsoleHelper(AppCompatActivity activity, View view, List<Point> points) {
    ButterKnife.bind(this, view);
    mActivity = activity;
    context = view.getContext();
    mPoints = points;
    if (runningSpotScrollView == null) {
      throw new IllegalStateException("Running ScrollView can not be null");
    }
    init();
  }

  private void init() {
    runningSpotScrollView.initAllSpots(mPoints.size());
  }

  public void onReachAttackPoint(int index) {
    runningSpotScrollView.setSpots(index, BaseSpotScrollView.STATE.STATE_DONE);
  }

  public void updateNextPoint(int index) {
    runningSpotScrollView.setSpots(index, BaseSpotScrollView.STATE.STATE_DOING);
  }

  public void setOnSpotClickListener(BaseSpotScrollView.OnSpotItemClickListener listener) {
    if (runningSpotScrollView != null) {
      runningSpotScrollView.setSpotClickable(true);
      runningSpotScrollView.setOnSpotItemClickListener(listener);
    }
  }

}
