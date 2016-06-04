package com.lptiyu.tanke.messagesystem;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lptiyu.tanke.base.controller.ActivityController;

import butterknife.ButterKnife;

/**
 * author:wamcs
 * date:2016/6/4
 * email:kaili@hustunique.com
 */
public class MessageController extends ActivityController {


    public MessageController(AppCompatActivity activity, View view) {
        super(activity, view);
        ButterKnife.bind(this,view);
    }
}
