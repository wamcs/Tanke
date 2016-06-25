package com.lptiyu.tanke.messagesystem.adpater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.lptiyu.tanke.database.DBHelper;
import com.lptiyu.tanke.database.MessageList;
import com.lptiyu.tanke.database.MessageListDao;
import com.lptiyu.tanke.messagesystem.MessageListViewHolder;

import java.util.List;

/**
 * author:wamcs
 * date:2016/6/13
 * email:kaili@hustunique.com
 */
public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private List<MessageList> mMessageList;
  private Context context;
  private MessageListDao dao;

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
    final MessageList item = mMessageList.get(position);
    holder1.bind(item);
  }

  public void updateMessageData(List<MessageList> messageList) {
    if (messageList == null || messageList.size() == 0) {
      return;
    }
    dao.insertOrReplaceInTx(messageList);
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
