package com.lptiyu.tanke.pushreceiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lptiyu.tanke.database.Message;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;
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

        if (intent.getAction().equals(Conf.PUSH_ACTION)){
            String json = intent.getExtras().getString("com.avos.avoscloud.Data");
//            Message message = new Gson().fromJson()
            Intent resultIntent = new Intent(this.context, MessageActivity.class);
            //TODO:intent加type
            PendingIntent pendingIntent = PendingIntent.getActivity(this.context,0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //TODO:添加message到数据库，包括PushMessage和MessageList，MessageList更新数据，PushMessage插入
//            NotificationCompat.Builder notification
//                    = new NotificationCompat.Builder(this.context)
//                    .setSmallIcon(R.mipmap.ic_launcher)

            //给pushHelper发广播用于处理在message界面信息更新,需要考虑下怎么写
//            Intent messageIntent = new Intent();
//            messageIntent.setAction(Conf.PUSH_ACTION);
////            intent.putExtra(Conf.PUSH_MESSAGE,message);
//            context.sendBroadcast(messageIntent);
        }
    }
}
