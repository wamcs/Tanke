package com.lptiyu.tanke.messagesystem.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.gson.Gson;
import com.lptiyu.tanke.database.DBHelper;
import com.lptiyu.tanke.database.Message;
import com.lptiyu.tanke.database.MessageDao;
import com.lptiyu.tanke.database.MessageList;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.messagesystem.adpater.PushAdapter;

import java.util.List;

import timber.log.Timber;

/**
 * author:wamcs
 * date:2016/6/4
 * email:kaili@hustunique.com
 */
public class PushHelper extends MessageHelper implements
        SwipeRefreshLayout.OnRefreshListener {

    private int loadMessageSumNumber;
    private int loadMessageNumber;
    private int tableItemNumber;
    private int type;
    private boolean isRefreshing = false;

    private PushAdapter adapter;
    private MessageDao messageDao;
    private List<Message> messageList;
    private MessageReceiver receiver;

    public PushHelper(AppCompatActivity activity, View view, int type) {
        super(activity, view, type);
        this.type = type;
        init();
        registerReceiver();
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

    private void registerReceiver(){
        receiver =new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Conf.PUSH_ACTION);
        filter.setPriority(10);
        context.registerReceiver(receiver,filter);
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

    @Override
    public void finish() {
        context.unregisterReceiver(receiver);
    }

    public class MessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Conf.PUSH_ACTION)) {
                String jsonString = intent.getExtras().getString("com.avos.avoscloud.Data");
                Timber.d("push data json is %s", jsonString);
                Message message = new Gson().fromJson(jsonString, Message.class);

                DBHelper.getInstance().getPushMessageDao().insert(message);

                MessageList list = new MessageList();
                list.setTime(message.getTime());
                list.setContent(message.getAlert());
                list.setIsRead(false);
                list.setType(message.getType());
                switch (message.getType()){
                    case Conf.OFFICIAL_MESSAGE:
                        list.setName("官方资讯");
                        break;
                    case Conf.SYSTEM_MESSAGE:
                        list.setName("系统消息");
                        break;
                }

                DBHelper.getInstance().getMessageListDao().insert(list);

                if ((message.getTime()-messageList.get(messageList.size()-1).getTime())>LIMIT_TIME){
                    Message timeMessage =new Message();
                    message.setTime(message.getTime());
                    message.setType(Conf.TIME_TYPE);
                    messageList.add(timeMessage);
                }
                messageList.add(message);
                adapter.notifyDataSetChanged();

            }

        }
    }



}
