package com.lptiyu.tanke.messagesystem.adpater;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.database.DBHelper;
import com.lptiyu.tanke.database.MessageList;
import com.lptiyu.tanke.database.MessageListDao;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.messagesystem.MessageActivity;
import com.lptiyu.tanke.utils.Inflater;
import com.lptiyu.tanke.utils.TimeUtils;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author:wamcs
 * date:2016/6/13
 * email:kaili@hustunique.com
 */
public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    class MessageListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.message_list_item_picture)
        SimpleDraweeView mPicture;
        @BindView(R.id.message_list_item_tag)
        CircularImageView mTag;
        @BindView(R.id.message_list_item_name)
        TextView mName;
        @BindView(R.id.message_list_item_content)
        TextView mContent;
        @BindView(R.id.message_list_item_time)
        TextView mTime;
        @BindView(R.id.message_list_item_layout)
        LinearLayout mLayout;

        public MessageListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    private List<MessageList> messageList;
    private Context context;
    private MessageListDao dao;

    public MessageListAdapter(Context context) {
        dao = DBHelper.getInstance().getMessageListDao();
        messageList = dao.loadAll();
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageListViewHolder(Inflater.inflate(R.layout.item_message_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MessageListViewHolder holder1 = (MessageListViewHolder) holder;
        final MessageList item = messageList.get(position);
        holder1.mName.setText(item.getName());
        holder1.mContent.setText(item.getContent());
        if (item.getTime() > TimeUtils.getCurrentDate()){
            holder1.mTime.setText(TimeUtils.parsePartTime(item.getTime()));
        }else {
            holder1.mTime.setText(TimeUtils.parseCompleteTime(item.getTime()));
        }
        if (item.getIsRead()){
            holder1.mTag.setVisibility(View.GONE);
        }
        switch (item.getType()){
            case Conf.OFFICIAL_MESSAGE:
                holder1.mLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder1.mTag.setVisibility(View.GONE);
                        item.setIsRead(true);
                        dao.update(item);
                        jumpToMessage(Conf.OFFICIAL_MESSAGE);

                    }
                });
                holder1.mPicture.setBackgroundResource(R.mipmap.ic_launcher);
                break;
            case Conf.SYSTEM_MESSAGE:
                holder1.mLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder1.mTag.setVisibility(View.GONE);
                        item.setIsRead(true);
                        dao.update(item);
                        jumpToMessage(Conf.SYSTEM_MESSAGE);
                    }
                });
                holder1.mPicture.setBackgroundResource(R.mipmap.ic_launcher);
                break;
        }
    }

    private void jumpToMessage(int type){
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(Conf.MESSAGE_TYPE,type);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
