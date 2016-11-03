package com.lptiyu.tanke.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.entity.response.MessageEntity;
import com.lptiyu.tanke.utils.DateFormatterUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/10/31.
 */

public class MessageListAdapter extends BaseRecyclerViewAdapter<MessageEntity> {
    public MessageListAdapter(Context mContext, List<MessageEntity> mDataList) {
        super(mContext, mDataList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_message_list, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onClick(viewHolder.getAdapterPosition());
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageEntity messageEntity = list.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.messageItemTime.setText(DateFormatterUtils.parseTimeStamp(messageEntity.create_time));
        viewHolder.messageItemTimeText.setText(DateFormatterUtils.parseTimeStamp(messageEntity.create_time));
        viewHolder.messageItemTitle.setText(messageEntity.title);
        Glide.with(context).load(messageEntity.pic).placeholder(R.drawable.default_pic).error(R.drawable.default_pic)
                .into(viewHolder.messageItemImageView);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message_item_time_text)
        TextView messageItemTimeText;
        @BindView(R.id.message_item_title)
        TextView messageItemTitle;
        @BindView(R.id.message_item_time)
        TextView messageItemTime;
        @BindView(R.id.message_item_image_view)
        ImageView messageItemImageView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
