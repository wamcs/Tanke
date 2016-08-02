package com.lptiyu.tanke.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.Inflater;

import net.simonvt.numberpicker.NumberPicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author: xiaoxiaoda
 * date: 16-1-20
 * email: daque@hustunique.com
 */
public class NumberPickerDialog extends BaseDialog {

  @BindView(R.id.layout_dialog_number_picker_picker)
  NumberPicker mNumberPicker;
  @BindView(R.id.layout_dialog_number_picker_cancel)
  TextView mCancel;
  @BindView(R.id.layout_dialog_number_picker_ensure)
  TextView mEnsure;

  private OnNumberPickedListener mListener;

  public NumberPickerDialog(Context context) {
    super(context);
    this.setCustomView(R.layout.layout_dialog_number_picker, context)
        .withMessage(null)
        .isCancelableOnTouchOutside(true);
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

  public BaseDialog withMinMaxValue(int min, int max) {
    mNumberPicker.setMinValue(min);
    mNumberPicker.setMaxValue(max);
    return this;
  }

  public void setOnNumberPickedListener(OnNumberPickedListener listener) {
    mListener = listener;
  }

  public interface OnNumberPickedListener {
    public void onNumberPicked(int number);
  }

  @OnClick(R.id.layout_dialog_number_picker_cancel)
  void clickCancel() {
    dismiss();
  }

  @OnClick(R.id.layout_dialog_number_picker_ensure)
  void clickEnsure() {
    mListener.onNumberPicked(mNumberPicker.getValue());
    dismiss();
  }
}
