package com.lptiyu.tanke.messagesystem;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.messagesystem.MessageController;

/**
 * author:wamcs
 * date:2016/6/4
 * email:kaili@hustunique.com
 */
public class MessageActivity extends BaseActivity {

    private ActivityController mActivityController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mActivityController = new MessageController(this,getWindow().getDecorView());
    }

    @Override
    public ActivityController getController() {
        return mActivityController;
    }
}
