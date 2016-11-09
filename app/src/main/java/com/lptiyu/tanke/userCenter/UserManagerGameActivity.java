package com.lptiyu.tanke.userCenter;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;

public class UserManagerGameActivity extends BaseActivity {
    private ActivityController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_judge_game);
        mController = new UserManagerGameController(this, getWindow().getDecorView());
    }

    @Override
    public ActivityController getController() {
        return mController;
    }
}
