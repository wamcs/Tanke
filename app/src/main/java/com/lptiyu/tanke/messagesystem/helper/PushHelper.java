package com.lptiyu.tanke.messagesystem.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lptiyu.tanke.database.DBHelper;
import com.lptiyu.tanke.database.Message;
import com.lptiyu.tanke.database.MessageDao;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.messagesystem.adpater.PushAdapter;

import java.util.List;

/**
 * author:wamcs
 * date:2016/6/4
 * email:kaili@hustunique.com
 */
public class PushHelper extends MessageHelper implements
        SwipeRefreshLayout.OnRefreshListener {

    private MessageDao messageDao;
    private int loadMessageSumNumber;
    private int loadMessageNumber;
    private int tableItemNumber;
    private List<Message> messageList;
    private int type;
    private PushAdapter adapter;
    private boolean isRefreshing = false;

    public PushHelper(AppCompatActivity activity, View view, int type) {
        super(activity, view, type);
        this.type = type;
        init();
    }

    private void init() {
        switch (type){
            case Conf.SYSTEM_MESSAGE:
                mTitleText.setText("系统消息");
                break;
            case Conf.OFFICIAL_MESSAGE:
                mTitleText.setText("官方资讯");
                break;
        }

        messageDao = DBHelper.getInstance().getPushMessageDao();
        tableItemNumber = (int) messageDao.queryBuilder()
                .where(MessageDao.Properties.Type.eq(type))
                .count();
        //获取分页数
        if ((tableItemNumber % LOAD_MESSAGE_NUMBER) == 0) {
            loadMessageSumNumber = (tableItemNumber / LOAD_MESSAGE_NUMBER) - 1;
        } else {
            loadMessageSumNumber = tableItemNumber / LOAD_MESSAGE_NUMBER;
        }
        loadMessageNumber = loadMessageSumNumber;
        loadMessage();
        adapter =new PushAdapter(context,messageList);
        mRecyclerView.setAdapter(adapter);

    }

    private void loadMessage() {

        //获取list
        messageList = decorateMessageList(messageDao.queryBuilder()
                .where(MessageDao.Properties.Type.eq(type))
                .offset(loadMessageNumber * LOAD_MESSAGE_NUMBER)
                .limit((loadMessageSumNumber-loadMessageNumber)*LOAD_MESSAGE_NUMBER)
                .list());
        if (loadMessageNumber > 0) {
            loadMessageNumber = loadMessageNumber - 1;
        }
    }

    @Override
    public void onRefresh() {
        if (isRefreshing) {
            return;
        }
        isRefreshing = true;
        mSwipeRefreshLayout.setRefreshing(true);
        loadMessage();
        adapter.notifyDataSetChanged();
        isRefreshing = false;
        mSwipeRefreshLayout.setRefreshing(false);
    }
}