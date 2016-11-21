package com.lptiyu.tanke.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author: xiaoxiaoda
 * date: 16-1-20
 * email: daque@hustunique.com
 */
public class GenderChooseDialog extends BaseDialog {

    @BindView(R.id.layout_dialog_check_box_male)
    RadioButton mMale;
    @BindView(R.id.layout_dialog_check_box_female)
    RadioButton mFemale;

    private OnGenderChoosedListener mListener;

    public GenderChooseDialog(Context context) {
        super(context);
        this.withTitle(context.getString(R.string.gender))
                .withMessage(null)
                .isCancelableOnTouchOutside(false)
                .setCustomView(R.layout.layout_dialog_choose_gender, context);
    }

    @Override
    public BaseDialog setCustomView(int resId, Context context) {
        View v = Inflater.inflate(resId, null, false);
        return setCustomView(v, context);
    }

    @Override
    public BaseDialog setCustomView(View view, Context context) {
        ButterKnife.bind(this, view);
        return super.setCustomView(view, context);
    }

    public BaseDialog setOnGenderChoosedListener(OnGenderChoosedListener listener) {
        mListener = listener;
        return this;
    }

    public void show(String gender) {
        if (gender.equals("男"))
            mMale.setChecked(true);
        else
            mFemale.setChecked(true);
        super.show();
    }


    @OnClick(R.id.layout_dialog_check_box_male)
    void onMaleClicked() {
        if (null != mListener) {
            mListener.onGenderChoosed("男");
        }
        dismiss();
    }

    @OnClick(R.id.layout_dialog_check_box_female)
    void onFemaleClicked() {
        if (null != mListener) {
            mListener.onGenderChoosed("女");
        }
        dismiss();
    }

    public interface OnGenderChoosedListener {
        void onGenderChoosed(String gender);
    }

}
