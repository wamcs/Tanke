package com.lptiyu.tanke.pushreceiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.avos.avoscloud.AVOSCloud;
import com.google.gson.Gson;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.database.DBHelper;
import com.lptiyu.tanke.database.Message;
import com.lptiyu.tanke.database.MessageList;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.messagesystem.MessageActivity;

import timber.log.Timber;

/**
 * author:wamcs
 * date:2016/6/2
 * email:kaili@hustunique.com
 */
public class PushReceiver extends BroadcastReceiver {

    private Context context = AVOSCloud.applicationContext;
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("lk","has receive");
        if (intent.getAction().equals(Conf.PUSH_ACTION)){
            String jsonString = intent.getExtras().getString("com.avos.avoscloud.Data");
            Timber.d("push data json is %s",jsonString);
            Message message = new Gson().fromJson(jsonString,Message.class);

            Intent resultIntent = new Intent(this.context, MessageActivity.class);
            resultIntent.putExtra(Conf.MESSAGE_TYPE,message.getType());
            PendingIntent pendingIntent = PendingIntent.getActivity(this.context,0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //add date into table Message
            DBHelper.getInstance().getPushMessageDao().insert(message);

            //add date into table MessageList
            updateMessageListDB(message);

            NotificationCompat.Builder notification
                    = new NotificationCompat.Builder(this.context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText(message.getTitle())
                    .setContentText(message.getAlert())
                    .setContentIntent(pendingIntent)
                    .setTicker("有推送消息未读")
                    .setAutoCancel(true);

            NotificationManager mNotifyMgr =
                    (NotificationManager) this.context
                            .getSystemService(
                                    Context.NOTIFICATION_SERVICE);
            mNotifyMgr.notify(0,notification.build());

            //给pushHelper发广播用于处理在message界面信息更新,需要考虑下怎么写
//            Intent messageIntent = new Intent();
//            messageIntent.setAction(Conf.PUSH_ACTION);
////            intent.putExtra(Conf.PUSH_MESSAGE,message);
//            context.sendBroadcast(messageIntent);
        }
    }

    private void updateMessageListDB(Message message){
        MessageList messageList = new MessageList();
        messageList.setTime(message.getTime());
        messageList.setContent(message.getAlert());
        messageList.setIsRead(false);
        messageList.setType(message.getType());
        switch (message.getType()){
            case Conf.OFFICIAL_MESSAGE:
                messageList.setName("官方资讯");
                break;
            case Conf.SYSTEM_MESSAGE:
                messageList.setName("系统消息");
                break;
        }

       DBHelper.getInstance().getMessageListDao().insertOrReplace(messageList);
    }
}
