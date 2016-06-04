package com.lptiyu.tanke.messagesystem.adpater;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.messagesystem.OnRecyclerItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author:wamcs
 * date:2016/6/4
 * email:kaili@hustunique.com
 */
public class MessageBaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private OnRecyclerItemClickListener listener;

        @BindView(R.id.message_item_image_view)
        ImageView mAvatar;
        @BindView(R.id.message_item_text_view)
        TextView mMessage;

        public MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            mMessage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != listener){
                listener.onItemClick();
            }
        }

        public MessageViewHolder setListener(OnRecyclerItemClickListener listener){
            this.listener = listener;
            return this;
        }

    }

    class TimeViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.message_item_time_text)
        TextView mTime;

        public TimeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
