package com.lptiyu.tanke.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : xiaoxiaoda
 *         date: 16-3-8
 *         email: daque@hustunique.com
 */
public class LoadingDialog extends BaseDialog {

  @BindView(R.id.layout_dialog_loading_text)
  TextView mText;

  public LoadingDialog(Context context) {
    super(context);
    withTitle(null)
        .isCancelableOnTouchOutside(false)
        .withDialogColor(getContext().getResources().getColor(R.color.default_font_color))
        .setCustomView(R.layout.layout_dialog_loading, context);
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

  public void setDialogText(String text) {
    if (null != mText) {
      mText.setText(text);
    }
  }


}
