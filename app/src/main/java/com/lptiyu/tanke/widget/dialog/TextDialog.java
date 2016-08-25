package com.lptiyu.tanke.widget.dialog;

import android.content.Context;
import android.view.View;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.Inflater;
import com.lptiyu.tanke.widget.CustomTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author : xiaoxiaoda
 *         date: 16-7-3
 *         email: wonderfulifeel@gmail.com
 */
public class TextDialog extends BaseDialog {

  @BindView(R.id.text)
  public CustomTextView textView;
  @BindView(R.id.cancel_button)
  public CustomTextView cancelButton;
  @BindView(R.id.ensure_button)
  public CustomTextView ensureButton;

  private OnTextDialogButtonClickListener mListener;

  public TextDialog(Context context) {
    super(context);
    this.setCustomView(R.layout.layout_dialog_text, context)
        .withTitle(null)
        .setCanceledOnTouchOutside(true);
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

  public void show(String msg) {
    textView.setText(msg);
    super.show();
  }

  public void setmListener(OnTextDialogButtonClickListener mListener) {
    this.mListener = mListener;
  }

  @OnClick(R.id.cancel_button)
  void onCancel() {
    if (mListener != null) {
      mListener.onNegtiveClicked();
    }
  }

  @OnClick(R.id.ensure_button)
  void onEnsure() {
    if (mListener != null) {
      mListener.onPositiveClicked();
    }
  }

  public interface OnTextDialogButtonClickListener {

    void onPositiveClicked();

    void onNegtiveClicked();
  }
}
