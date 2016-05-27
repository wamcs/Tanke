package com.lptiyu.tanke.widget.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.global.Conf;


import net.simonvt.numberpicker.NumberPicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author: xiaoxiaoda
 * date: 16-1-20
 * email: daque@hustunique.com
 */
public class DatePickerDialog extends AlertDialog {

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

  private OnDateChoosedListener mListener;

  public DatePickerDialog(Context context) {
    super(context);
    View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_image_choose, null);
    ButterKnife.bind(this, view);
    init();
    setView(view);
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


  public void setOnDateChoosedListener(OnDateChoosedListener listener) {
    mListener = listener;
  }

  @OnClick(R.id.layout_dialog_date_picker_cancel)
  void clickCancel() {
    dismiss();
  }

  @OnClick(R.id.layout_dialog_date_picker_ensure)
  void clickEnsure() {
    String date = mYearPicker.getValue() + "." + mMonthPicker.getValue() + "." + mDayPicker.getValue();
    mListener.onDateChoosed(date);
    dismiss();
  }

  public interface OnDateChoosedListener {
     void onDateChoosed(String date);
  }

}
