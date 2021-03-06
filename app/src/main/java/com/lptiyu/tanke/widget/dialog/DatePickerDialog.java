package com.lptiyu.tanke.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.utils.Inflater;

import net.simonvt.numberpicker.NumberPicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * date: 16-1-20
 * email: daque@hustunique.com
 */
public class DatePickerDialog extends BaseDialog {

  @BindView(R.id.layout_dialog_date_picker_year)
  NumberPicker mYearPicker;
  @BindView(R.id.layout_dialog_date_picker_month)
  NumberPicker mMonthPicker;
  @BindView(R.id.layout_dialog_date_picker_day)
  NumberPicker mDayPicker;

  @BindView(R.id.layout_dialog_date_picker_cancel)
  TextView mCancel;
  @BindView(R.id.layout_dialog_date_picker_ensure)
  TextView mEnsure;

  private int DEFAULT_YEAR = 1990;
  private int DEFAULT_MONTH = 1;
  private int DEFAULT_DAY = 1;


  private OnDateChoosedListener mListener;

  public DatePickerDialog(Context context) {
    super(context);
    this.withTitle(context.getString(R.string.birthday)).withMessage(null)
        .isCancelableOnTouchOutside(true)
        .setCustomView(R.layout.layout_dialog_date_picker, context);
    init();
  }

  private void init() {
    mYearPicker.setMaxValue(Conf.MAX_BIIRTHDAY_YEAR);
    mYearPicker.setMinValue(Conf.MIN_BIRTHDAY_YEAR);
    mMonthPicker.setMaxValue(Conf.MAX_MONTH);
    mMonthPicker.setMinValue(Conf.MIN_MONTH);
    mDayPicker.setMinValue(Conf.MIN_DAY);
    initDayPicker();

    mYearPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
      @Override
      public void onScrollStateChange(NumberPicker view, int scrollState) {
        initDayPicker();
      }
    });

    mMonthPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
      @Override
      public void onScrollStateChange(NumberPicker view, int scrollState) {
        if (SCROLL_STATE_IDLE == scrollState) {
          initDayPicker();
        }
      }
    });
  }

  private void initDayPicker() {
    int month = mMonthPicker.getValue();
    if (Conf.FEB_MONTH == month) {
      int year = mYearPicker.getValue();
      if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
        mDayPicker.setMaxValue(Conf.FEB_BIG_MONTH_MAX_DAY);
      } else {
        mDayPicker.setMaxValue(Conf.FEB_SMALL_MONTH_MAX_DAY);
      }
    } else {
      if (month == 3 || month == 6 || month == 9 || month == 11) {
        mDayPicker.setMaxValue(Conf.NORMAL_SMALL_MONTH_MAX_DAY);
      } else {
        mDayPicker.setMaxValue(Conf.NORMAL_BIG_MONTH_MAX_DAY);
      }
    }
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

  public void scrollToDefaultValue() {
    mYearPicker.setValue(DEFAULT_YEAR);
    mMonthPicker.setValue(DEFAULT_MONTH);
    mDayPicker.setValue(DEFAULT_DAY);
  }

  public void setOnDateChoosedListener(OnDateChoosedListener listener) {
    mListener = listener;
  }

  @OnClick(R.id.layout_dialog_date_picker_cancel)
  void clickCancel() {
    dismiss();
  }

  @OnClick(R.id.layout_dialog_date_picker_ensure)
  void clickEnsure() {
    if (mListener != null) {
      String date = String.format(getContext().getString(R.string.date_picker_date_formatter), mYearPicker.getValue(), mMonthPicker.getValue(), mDayPicker.getValue());
      mListener.onDateChoosed(date);
    }
    dismiss();
  }

  public interface OnDateChoosedListener {
    void onDateChoosed(String date);
  }
}
