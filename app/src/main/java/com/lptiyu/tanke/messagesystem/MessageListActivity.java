package com.lptiyu.tanke.messagesystem;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;

/**
 * author:wamcs
 * date:2016/6/9
 * email:kaili@hustunique.com
 */
public class MessageListActivity extends BaseActivity {

    ActivityController activityController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        activityController = new MessageListController(this,getWindow().getDecorView());
    }


    @Override
    public ActivityController getController() {
        return activityController;
    }
}
