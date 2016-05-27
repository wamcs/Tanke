package com.lptiyu.tanke.widget.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lptiyu.tanke.R;


import net.simonvt.numberpicker.NumberPicker;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author: xiaoxiaoda
 * date: 16-1-20
 * email: daque@hustunique.com
 */
public class NumberPickerDialog extends AlertDialog {

  @BindView(R.id.layout_dialog_number_picker_picker)
  NumberPicker mNumberPicker;
  @BindView(R.id.layout_dialog_number_picker_cancel)
  TextView mCancel;
  @BindView(R.id.layout_dialog_number_picker_ensure)
  TextView mEnsure;

  private OnNumberPickedListener mListener;

  public NumberPickerDialog(Context context) {
    super(context);
    View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_image_choose, null);
    ButterKnife.bind(this, view);
    setView(view);
  }


  public AlertDialog withMinMaxValue(int min, int max) {
    mNumberPicker.setMinValue(min);
    mNumberPicker.setMaxValue(max);
    return this;
  }

  public void setOnNumberPickedListener(OnNumberPickedListener listener) {
    mListener = listener;
  }

  public interface OnNumberPickedListener {
    void onNumberPicked(int number);
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
