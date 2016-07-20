package com.lptiyu.tanke.messagesystem.adpater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.lptiyu.tanke.database.DBHelper;
import com.lptiyu.tanke.database.MessageNotificationList;
import com.lptiyu.tanke.database.MessageNotificationListDao;
import com.lptiyu.tanke.messagesystem.MessageListViewHolder;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * author:wamcs
 * date:2016/6/13
 * email:kaili@hustunique.com
 */
public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MessageNotificationList> mMessageList;
    private Context context;
    private MessageNotificationListDao dao;

    public MessageListAdapter(Context context) {
        dao = DBHelper.getInstance().getMessageListDao();
        mMessageList = dao.loadAll();
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageListViewHolder(parent, dao);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MessageListViewHolder holder1 = (MessageListViewHolder) holder;
        final MessageNotificationList item = mMessageList.get(position);
        holder1.bind(item);
    }

    public void updateMessageData(MessageNotificationList messageList) {
        if (messageList == null) {
            return;
        }
        int type = messageList.getType();
        QueryBuilder<MessageNotificationList> builder = dao.queryBuilder();
        builder.where(MessageNotificationListDao.Properties.Type.eq(type));
        List<MessageNotificationList> result = builder.list();
        if (result == null || result.size() == 0) {
            dao.insertOrReplace(messageList);
        } else {
            if (messageList.getTime() > result.get(0).getTime()) {
                dao.update(messageList);
            }
        }
        mMessageList = dao.loadAll();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mMessageList == null) {
            return 0;
        }
        return mMessageList.size();
    }

}
