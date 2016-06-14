package com.lptiyu.tanke.messagesystem;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.avos.avoscloud.PushService;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.messagesystem.helper.MessageHelper;
import com.lptiyu.tanke.messagesystem.helper.PushHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * author:wamcs
 * date:2016/6/4
 * email:kaili@hustunique.com
 */
public class MessageController extends ActivityController {

    @BindView(R.id.message_bottom_bar)
    LinearLayout bottomBar;

    private MessageHelper helper;

    public MessageController(AppCompatActivity activity, View view) {
        super(activity, view);
        ButterKnife.bind(this,view);
        init(activity, view);
    }

    private void init(AppCompatActivity activity, View view){
        int type = getIntent().getIntExtra(Conf.MESSAGE_TYPE,-1);
        Timber.d("message type: %d",type);
        if (type == -1) {
            throw new IllegalStateException("not has this type");
        }

        switch (type){
            case Conf.OFFICIAL_MESSAGE:
                helper = new PushHelper(activity,view,Conf.OFFICIAL_MESSAGE);
                bottomBar.setVisibility(View.GONE);
                break;
            case Conf.SYSTEM_MESSAGE:
                helper = new PushHelper(activity,view,Conf.SYSTEM_MESSAGE);
                bottomBar.setVisibility(View.GONE);
                break;
        }

    }

    @OnClick(R.id.default_tool_bar_imageview)
    void back(){
        finish();
    }

}
