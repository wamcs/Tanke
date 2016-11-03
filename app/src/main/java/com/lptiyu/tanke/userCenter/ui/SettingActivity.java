package com.lptiyu.tanke.userCenter.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.feedback.FeedBackActivity;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.activities.initialization.ui.LoginActivity;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.DataCleanManager;
import com.lptiyu.tanke.utils.ShaPreferManager;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.widget.CustomTextView;
import com.lptiyu.tanke.widget.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lptiyu.tanke.global.AppData.getContext;

/**
 * author:wamcs
 * date:2016/6/9
 * email:kaili@hustunique.com
 */
public class SettingActivity extends MyBaseActivity {
    @BindView(R.id.default_tool_bar_textview)
    CustomTextView mToolbarText;
    @BindView(R.id.setting_activity_mobile_vibrate)
    SwitchButton mVibrate;
    @BindView(R.id.setting_activity_logout)
    CustomTextView mLogout;
    @BindView(R.id.setting_activity_feedback)
    CustomTextView mFeedback;
    @BindView(R.id.clear_cache)
    CustomTextView mClearCache;
    @BindView(R.id.ctv_cache_size)
    CustomTextView mCacheSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        mToolbarText.setText(getString(R.string.setting));
        init();
    }

    private void init() {
        if (AppData.isFirstInSettingActivity()) {
            ShaPreferManager.setMobileVibrate(true);
        }
        mVibrate.setChecked(ShaPreferManager.getMobileVibrate());
    }

    @OnClick(R.id.setting_activity_feedback)
    void onFeedback() {
        startActivity(new Intent(SettingActivity.this, FeedBackActivity.class));
    }

    @OnClick(R.id.setting_activity_logout)
    void onLogoutClicked() {
        Accounts.logOut();
        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.clear_cache)
    void clearCache() {
        try {
            String cacheSize = DataCleanManager.getTotalCacheSize(this);
            new AlertDialog.Builder(this).setMessage("缓存共计" + cacheSize + "，确认清除吗？").setCancelable(true)
                    .setPositiveButton
                            ("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DataCleanManager.clearAllCache(getContext());
                                    ToastUtil.TextToast("清理完毕");
                                }
                            }).setNegativeButton("取消", null).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.default_tool_bar_imageview)
    void back() {
        finish();
    }

    @OnClick(R.id.setting_activity_mobile_vibrate)
    void mobileVirate() {
        if (mVibrate.isChecked()) {
            ShaPreferManager.setMobileVibrate(true);
        } else {
            ShaPreferManager.setMobileVibrate(false);
        }
    }

    @OnClick(R.id.setting_activity_about_us)
    void startAboutUs() {
        startActivity(new Intent(SettingActivity.this, AboutUsActivity.class));
    }
}
