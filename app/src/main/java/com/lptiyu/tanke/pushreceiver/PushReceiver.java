package com.lptiyu.tanke.pushreceiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.messagesystem.MessageActivity;

/**
 * author:wamcs
 * date:2016/6/2
 * email:kaili@hustunique.com
 */
public class PushReceiver extends BroadcastReceiver {

    private Context context = AppData.getContext();
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("com.tanke.PUSH_ACTION")){
            String json = intent.getExtras().getString("com.avos.avoscloud.Data");
//            PushMessage message = new Gson().fromJson()
            Intent resultIntent = new Intent(this.context, MessageActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this.context,0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //TODO:添加message到数据库，包括PushMessage和MessageList，MessageList更新数据，PushMessage插入
//            NotificationCompat.Builder notification
//                    = new NotificationCompat.Builder(this.context)
//                    .setSmallIcon(R.mipmap.ic_launcher)

        }
    }
}
