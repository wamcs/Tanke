package com.lptiyu.tanke.messagesystem.adpater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.database.Message;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.utils.Inflater;
import com.lptiyu.tanke.utils.TimeUtils;

import java.util.List;


/**
 * author:wamcs
 * date:2016/6/4
 * email:kaili@hustunique.com
 */
public class PushAdapter extends MessageBaseAdapter {

  private List<Message> messageList;

  private static final int VIEW_TYPE_TIME = 1;
  private static final int VIEW_TYPE_MESSAGE = 2;
  private long currentTime;
  private Context context;

  public PushAdapter(Context context, List<Message> messageList, MessageViewHolderClickListener listener) {
    super(listener);
    this.messageList = messageList;
    this.context = context;
    currentTime = TimeUtils.getCurrentDate();
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case VIEW_TYPE_TIME:
        return new TimeViewHolder(Inflater.inflate(R.layout.layout_message_time, parent, false));
      case VIEW_TYPE_MESSAGE:
        return new MessageViewHolder(Inflater.inflate(R.layout.layout_message_item_right, parent, false));
      default:
        return new MessageViewHolder(Inflater.inflate(R.layout.layout_message_item_right, parent, false));
    }
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    Message message = messageList.get(position);
    switch (holder.getItemViewType()) {
      case VIEW_TYPE_TIME:
        TimeViewHolder holder1 = (TimeViewHolder) holder;
        long time = message.getTime();
        if (time >= currentTime) {
          holder1.mTime.setText(TimeUtils.parsePartTime(time));
        } else {
          holder1.mTime.setText(TimeUtils.parseCompleteTime(time));
        }
        break;
      case VIEW_TYPE_MESSAGE:
        MessageViewHolder holder2 = (MessageViewHolder) holder;
        Glide.with(context).load(message.getImage()).error(R.mipmap.need_to_remove_4_so_big).into(holder2.mImage);
        holder2.mTitle.setText(message.getTitle());
        holder2.mContent.setText(Html.fromHtml(Html.fromHtml(message.getAlert()).toString()));

        break;
    }
  }

  @Override
  public int getItemCount() {
    if (messageList == null) {
      return 0;
    }
    return messageList.size();
  }


  @Override
  public int getItemViewType(int position) {
    if (messageList.get(position).getType() == Conf.TIME_TYPE) {
      return VIEW_TYPE_TIME;
    }
    return VIEW_TYPE_MESSAGE;
  }


}
