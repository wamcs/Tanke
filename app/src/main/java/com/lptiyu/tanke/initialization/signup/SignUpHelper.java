package com.lptiyu.tanke.initialization.signup;


import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.RegularExpressionHelper;
import com.lptiyu.tanke.utils.Strings;
import com.lptiyu.tanke.utils.TimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author:wamcs
 * date:2016/5/25
 * email:kaili@hustunique.com
 */
public class SignUpHelper {

    protected AppCompatActivity context;


    @BindView(R.id.sign_up_title)
    protected TextView signUpTitle;
    @BindView(R.id.sign_up_phone_edit_text)
    protected EditText signUpPhoneEditText;
    @BindView(R.id.sign_up_code_edit_text)
    protected EditText signUpCodeEditText;
    @BindView(R.id.sign_up_get_code_button)
    protected TextView signUpGetCodeButton;
    @BindView(R.id.sign_up_password_edit_text)
    protected EditText signUpPasswordEditText;
    @BindView(R.id.sign_up_next_button)
    protected Button signUpNextButton;

    protected final long COUNT_DOWN_TIME = 60000L;
    private SpannableString spannableString;

    SignUpHelper(AppCompatActivity activity, View view) {
        context = activity;
        ButterKnife.bind(this, view);
    }


    public boolean getCode() {
        String phone = signUpPhoneEditText.getText().toString().trim();
        if (phone.length() != 11 || !Strings.checkPhone(phone)) {
            spannableString = new SpannableString(context.getResources().getString(R.string.error_user_phone));
            spannableString.setSpan(new ForegroundColorSpan(Color.RED),
                    0, spannableString.length(),
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            signUpPhoneEditText.setText(spannableString);
            return false;
        }

        return true;
    }


    public boolean next() {
        Editable phone = signUpPhoneEditText.getText();
        if (!RegularExpressionHelper.isPhoneNumber(phone + "")) {

            spannableString = new SpannableString(context.getResources().getString(R.string.error_user_phone));
            spannableString.setSpan(new ForegroundColorSpan(Color.RED),
                    0, spannableString.length(),
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            signUpPhoneEditText.setText(spannableString);
            return false;
        }

        Editable password = signUpPasswordEditText.getText();
        if (password.length() < 6) {
            spannableString = new SpannableString(context.getResources().getString(R.string.error_user_password));
            spannableString.setSpan(new ForegroundColorSpan(Color.RED),
                    0, spannableString.length(),
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            signUpPasswordEditText.setText(spannableString);
            return false;
        }

        return true;
    }


    class TimeCounter extends CountDownTimer {

        public TimeCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            signUpGetCodeButton.setText(TimeUtils.getFriendlyTime(millisUntilFinished));
        }

        @Override
        public void onFinish() {
            signUpGetCodeButton.setText(R.string.get_verify_code);
            signUpGetCodeButton.setEnabled(true);
            signUpGetCodeButton.setClickable(true);
        }
    }


}
