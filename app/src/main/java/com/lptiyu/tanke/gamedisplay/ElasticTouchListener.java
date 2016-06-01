package com.lptiyu.tanke.gamedisplay;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/1
 *
 * @author ldx
 */
public class ElasticTouchListener implements RecyclerView.OnItemTouchListener {

  // 首部Holder，有setPercent的功能
  private ElasticHeaderViewHolder hh;

  // 手指滑动，和显示的比例，手指移动2单位，View移动1单位。一个比较重要的参数
  private static final float THRESHOLD = 2;

  private float _downY; // 全局变量，暂时保存上一次之前状态

  private OnRefreshListener onRefreshListener; // 当拉动距离超过阈值后，松手则发出信号。需要合理忽略

  @Override
  public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
    if (hh == null) {
      hh = (ElasticHeaderViewHolder) rv.findViewHolderForAdapterPosition(0);
    }
    if (hh == null) {
      return false;
    }

    switch (e.getAction()) {
      case MotionEvent.ACTION_DOWN:
        _downY = e.getY();
        break;
      case MotionEvent.ACTION_MOVE:
      case MotionEvent.ACTION_UP:
        if (e.getY() - _downY > 0 && !ViewCompat.canScrollVertically(rv, -1)) {
          return true;
        }
        break;
      default:

    }
    return false;
  }

  @Override
  public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    float offset = e.getY() - _downY;
    switch (e.getAction()) {
      case MotionEvent.ACTION_MOVE:
        if (offset >= 0) {
          hh.setPercent(offset / hh.itemView.getHeight());
        }
        break;
      case MotionEvent.ACTION_UP:
        if (offset >= hh.itemView.getHeight()) {
          if (onRefreshListener != null) {
            onRefreshListener.onRefresh();
          }
        }

        if (offset >= 0) {
          hh.smoothBack();
        }
        break;
      default:
    }

  }

  @Override
  public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
  }


  public void setOnRefreshListener(OnRefreshListener listener) {
    this.onRefreshListener = listener;
  }

  public interface OnRefreshListener {
    public void onRefresh();
  }

}
