package com.lptiyu.tanke.userCenter.ui;

import android.content.Intent;
import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.activities.initialization.ui.UserProtocolActivity;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.widget.CustomTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends MyBaseActivity {
    @BindView(R.id.default_tool_bar_textview)
    CustomTextView mToolbarTitle;
    @BindView(R.id.activity_about_us_version)
    CustomTextView mVersionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mToolbarTitle.setText(getString(R.string.about_us_activity));
        mVersionName.setText(AppData.getVersionName());
    }

    @OnClick(R.id.default_tool_bar_imageview)
    void back() {
        finish();
    }

    @OnClick(R.id.activity_about_us_user_protocol)
    void userProtocol() {
        Intent intent = new Intent(AboutUsActivity.this, UserProtocolActivity.class);
        startActivity(intent);
    }
}
