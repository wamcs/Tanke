package com.lptiyu.tanke.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.DisplayUtils;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/1
 * <p>
 * 给RecyclerView的每一项画分割线
 *
 * @author ldx
 */
public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable shadowDrawable;
    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float shadowHeight = DisplayUtils.dp2px(10);

    private Context context;

    public RecyclerViewItemDecoration(Context context) {
        super();
        if (context == null) {
            throw new IllegalArgumentException("Context is null");
        }
        this.context = context;
        init();
    }

    private void init() {
        shadowDrawable = context.getResources().getDrawable(R.drawable.shadow);
        linePaint.setColor(getColor(R.color.grey06));
    }

    private int getColor(int resId) {
        return context.getResources().getColor(resId);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        float rl = parent.getPaddingLeft(); // raw left
        float rr = parent.getWidth() - parent.getPaddingRight(); // raw right

        final int childCount = parent.getChildCount();
        View child;
        RecyclerView.LayoutParams params;
        float top;
        float bottom;
        for (int i = 0; i < childCount; i++) {
            child = parent.getChildAt(i);
            params = (RecyclerView.LayoutParams) child.getLayoutParams();

            top = child.getBottom() + params.bottomMargin;
            //            if (params.getViewLayoutPosition() == 0) {
            //                bottom = top + shadowHeight;
            //                shadowDrawable.setBounds((int) rl, (int) top, (int) rr, (int) bottom);
            //                shadowDrawable.draw(c);
            //            } else {
            //                bottom = top + DisplayUtils.dp2px(0.5f);
            //                c.drawLine(rl + DisplayUtils.dp2px(21f), top, rr - DisplayUtils.dp2px(15), bottom,
            // linePaint);
            //            }
            bottom = top + DisplayUtils.dp2px(0.5f);
            c.drawLine(rl + DisplayUtils.dp2px(21f), top, rr - DisplayUtils.dp2px(15), bottom, linePaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            //      outRect.set(0, 0, 0, (int) shadowHeight); // May be increase, for there is a shadow cast.
            outRect.set(0, 0, 0, 0);
        } else {
            outRect.set(0, 0, 0, DisplayUtils.dp2px(1)); // A line.
        }
    }
}
