package com.lptiyu.tanke.messagesystem.adpater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.database.MessageNotification;
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

    private List<MessageNotification> messagesList;

    private static final int VIEW_TYPE_TIME = 1;
    private static final int VIEW_TYPE_MESSAGE = 2;
    private static final int VIEW_TYPE_LOADING_MORE = 3;

    private long currentTime;
    private Context context;

    public PushAdapter(Context context, List<MessageNotification> messagesList, MessageViewHolderClickListener
            listener) {
        super(listener);
        this.messagesList = messagesList;
        this.context = context;
        currentTime = TimeUtils.getCurrentDate();
    }
<<<<<<< HEAD

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_TIME:
                return new TimeViewHolder(Inflater.inflate(R.layout.layout_message_time, parent, false));
            case VIEW_TYPE_MESSAGE:
                return new MessageViewHolder(Inflater.inflate(R.layout.layout_message_item, parent, false));
            default:
                return new MessageViewHolder(Inflater.inflate(R.layout.layout_message_item, parent, false));
=======
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    Message message = messageList.get(position);
    switch (holder.getItemViewType()) {
      case VIEW_TYPE_TIME:
        TimeViewHolder holder1 = (TimeViewHolder) holder;
        long time = message.getTime();
        if (time >= currentTime) {
          holder1.mTime.setText(TimeUtils.parseFrontPartTime(time));
        } else {
          holder1.mTime.setText(TimeUtils.parseCompleteTime(time));
>>>>>>> e58b86f8f9aebb904cd708fe0ff1a580a3032167
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageNotification messages = messagesList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_TIME:
                TimeViewHolder holder1 = (TimeViewHolder) holder;
                long time = messages.getTime();
                if (time >= currentTime) {
                    holder1.mTime.setText(TimeUtils.parsePartTime(time));
                } else {
                    holder1.mTime.setText(TimeUtils.parseCompleteTime(time));
                }
                break;
            case VIEW_TYPE_MESSAGE:
                MessageViewHolder holder2 = (MessageViewHolder) holder;
                Glide.with(context).load(messages.getImage()).error(R.mipmap.need_to_remove_4_so_big).into(holder2
                        .mImage);
                holder2.mTitle.setText(messages.getTitle());
                holder2.mTime.setText(TimeUtils.parseCompleteTime(messages.getTime()));
                holder2.mContent.setText(Html.fromHtml(Html.fromHtml(messages.getAlert()).toString()));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (messagesList == null) {
            return 0;
        }
        return messagesList.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (messagesList.get(position).getType() == Conf.TIME_TYPE) {
            return VIEW_TYPE_TIME;
        }
        return VIEW_TYPE_MESSAGE;
    }


}
