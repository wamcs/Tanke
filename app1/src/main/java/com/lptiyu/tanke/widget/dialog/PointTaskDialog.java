package com.lptiyu.tanke.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.Inflater;
import com.lptiyu.tanke.widget.CustomTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/8/3.
 */
public class PointTaskDialog extends BasePointTaskDialog {

    @BindView(R.id.ctv_taskName)
    CustomTextView ctvTaskName;
    @BindView(R.id.img_close)
    ImageView imgClose;
    @BindView(R.id.img_indicate)
    ImageView imgIndicate;
    @BindView(R.id.ctv_getAnswer)
    CustomTextView ctvGetAnswer;
    @BindView(R.id.lv)
    ListView lv;

    public PointTaskDialog(Context context) {
        super(context);
        this.withTitle(context.getString(R.string.birthday)).withMessage(null)
                .isCancelableOnTouchOutside(true)
                .setCustomView(R.layout.dialog_point_task, context);
        initView();
    }

    private void initView() {

    }

    @Override
    public BasePointTaskDialog setCustomView(View view, Context context) {
        ButterKnife.bind(this, view);
        return super.setCustomView(view, context);
    }

    @Override
    public BasePointTaskDialog setCustomView(int resId, Context context) {
        View v = Inflater.inflate(resId, null, false);
        return setCustomView(v, context);
    }
}
