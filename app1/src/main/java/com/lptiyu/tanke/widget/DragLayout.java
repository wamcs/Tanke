package com.lptiyu.tanke.widget;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by jacob-wj on 2015/4/14.
 */
public class DragLayout extends RelativeLayout {
    private View mDragView;
    private ViewDragHelper mViewDragHelper;

    //给出3个位置等级
    public static int LEFT_SNAPPING_POINT = 0;
    public static int MIDDLE_SNAPPING_POINT = 0;
    public static int RIGHT_SNAPPING_POINT = 0;

    private float lastViewX;
    private Handler handler = new Handler();

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mViewDragHelper = ViewDragHelper.create(this, 1f, new DragLayoutCallBack());
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    public void setChildView(View view) {
        this.mDragView = view;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if(mDragView == null)
            return;

        LEFT_SNAPPING_POINT = 0;
        MIDDLE_SNAPPING_POINT = getMeasuredWidth() / 2 - mDragView.getMeasuredWidth() / 2;
        RIGHT_SNAPPING_POINT = getMeasuredWidth() - mDragView.getMeasuredWidth();
        lastViewX = mDragView.getX();
    }

    private class DragLayoutCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mDragView;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }


        /**
         * 子控件水平移动的范围
         *
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return Integer.MAX_VALUE;
        }

        /**
         * 触碰到边缘时回调
         *
         * @param edgeFlags
         * @param pointerId
         */
        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            Log.i("jason", "ponitId:" + pointerId);
            mViewDragHelper.captureChildView(mDragView, pointerId);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            Log.i("jason", "xvel: " + xvel + ", yvel: " + yvel);
            snapToPoint(releasedChild.getX());
        }
    }

    private void snapToPoint(float currentViewX) {
        Log.i("jason", "paddingLeft:" + currentViewX);
        if (currentViewX - lastViewX < -100) {//向左滑动
            snapLeft();
        } else if (currentViewX - lastViewX > -100 && currentViewX - lastViewX < 100) {
            snapBack();
            invalidate();
            return;
        } else if (currentViewX - lastViewX > 100) {//向右滑动
            snapRight();
        } else {
            //不做任何处理
            return;
        }
        invalidate();
        lastViewX = currentViewX;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    mDragView.setVisibility(GONE);
                    listener.onDrag();
                }
            }
        }, 500);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mViewDragHelper.cancel();
                return false;
        }
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    private void snapLeft() {
        mViewDragHelper.settleCapturedViewAt(LEFT_SNAPPING_POINT - mDragView.getMeasuredWidth(), 0);
    }

    private void snapBack() {
        mViewDragHelper.settleCapturedViewAt(LEFT_SNAPPING_POINT, 0);
    }

    private void snapRight() {
        mViewDragHelper.settleCapturedViewAt(RIGHT_SNAPPING_POINT + mDragView.getMeasuredWidth(), 0);
    }

    private OnDragOverListener listener;

    public void setOnDragOverListener(OnDragOverListener listener) {
        this.listener = listener;
    }

    public interface OnDragOverListener {
        void onDrag();
    }
}
