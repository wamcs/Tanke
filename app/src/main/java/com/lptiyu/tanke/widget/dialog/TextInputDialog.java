package com.lptiyu.tanke.widget.dialog;

import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.Inflater;
import com.lptiyu.tanke.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author : xiaoxiaoda
 *         date: 16-7-3
 *         email: wonderfulifeel@gmail.com
 */
public class TextInputDialog extends BaseDialog {

  private String oldMsg = "";
  private OnTextInputListener listener;

  @BindView(R.id.input_edit_text)
  EditText editText;

  public TextInputDialog(Context context) {
    super(context);
    this.setCustomView(R.layout.layout_dialog_input_text, context)
        .withMessage(null)
        .setCancelable(true);
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
    oldMsg = msg;
    editText.setText(oldMsg);
    super.show();
  }

  public void setListener(OnTextInputListener listener) {
    this.listener = listener;
  }

  @OnClick(R.id.cancel_button)
  public void onCancel() {
    dismiss();
  }

  @OnClick(R.id.ensure_button)
  public void onEnsure() {
    Editable editable = editText.getText();
    if (editable == null || editable.length() == 0) {
      ToastUtil.TextToast("您的输入不规范");
      return;
    }
    String modifyText = editable.toString();
    if (oldMsg.equals(modifyText)) {
      dismiss();
      return;
    }
    if (listener != null) {
      listener.onTextInput(modifyText);
      dismiss();
    }
  }

  public interface OnTextInputListener {
    void onTextInput(String msg);
  }

}
