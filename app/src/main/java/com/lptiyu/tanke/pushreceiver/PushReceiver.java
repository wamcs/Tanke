package com.lptiyu.tanke.pushreceiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.avos.avoscloud.AVOSCloud;
import com.google.gson.Gson;
import com.lptiyu.tanke.MainActivity;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.database.DBHelper;
import com.lptiyu.tanke.database.MessageNotification;
import com.lptiyu.tanke.database.MessageNotificationList;
import com.lptiyu.tanke.global.Conf;

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

        if (intent.getAction().equals(Conf.PUSH_ACTION)) {
            String jsonString = intent.getExtras().getString("com.avos.avoscloud.Data");
            Timber.d("push data json is %s", jsonString);
            MessageNotification messages = new Gson().fromJson(jsonString, MessageNotification.class);

            //            Intent resultIntent = playing Intent(this.context, MessageActivity.class);
            Intent resultIntent = new Intent(this.context, MainActivity.class);
            //            resultIntent.putExtra(Conf.MESSAGE_TYPE,messages.getType());
            PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0, resultIntent, PendingIntent
                    .FLAG_UPDATE_CURRENT);

            //add date into table Messages
            DBHelper.getInstance().getPushMessageDao().insert(messages);

            //add date into table MessageList
            updateMessageListDB(messages);

            NotificationCompat.Builder notification
                    = new NotificationCompat.Builder(this.context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText(messages.getTitle())
                    .setContentText(messages.getAlert())
                    .setContentIntent(pendingIntent)
                    .setTicker("有推送消息未读")
                    .setAutoCancel(true);

            NotificationManager mNotifyMgr =
                    (NotificationManager) this.context
                            .getSystemService(
                                    Context.NOTIFICATION_SERVICE);
            mNotifyMgr.notify(0, notification.build());


        }
    }

    private void updateMessageListDB(MessageNotification messages) {
        MessageNotificationList messageList = new MessageNotificationList();
        messageList.setTime(messages.getTime());
        messageList.setContent(messages.getTitle());
        messageList.setIsRead(false);
        messageList.setType(messages.getType());
        switch (messages.getType()) {
            case Conf.MESSAGE_LIST_TYPE_OFFICIAL:
                messageList.setUserId(Conf.MESSAGE_LIST_USERID_OFFICIAL);
                messageList.setName(context.getString(R.string.message_type_official));
                break;
            case Conf.MESSAGE_LIST_TYPE_SYSTEM:
                messageList.setUserId(Conf.MESSAGE_LIST_USERID_SYSTEM);
                messageList.setName("系统消息");
                break;
        }

        DBHelper.getInstance().getMessageListDao().insertOrReplace(messageList);
    }


}
