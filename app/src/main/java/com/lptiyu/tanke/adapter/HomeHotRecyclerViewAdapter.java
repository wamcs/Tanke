package com.lptiyu.tanke.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.entity.response.Recommend;
import com.lptiyu.tanke.widget.CircularImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/9/23.
 */

public class HomeHotRecyclerViewAdapter extends BaseRecyclerViewAdapter<Recommend> {
    public HomeHotRecyclerViewAdapter(Context mContext, List<Recommend> list) {
        super(mContext, list);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_home_hot, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onClick(myViewHolder.getAdapterPosition());
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (clickListener != null) {
                    clickListener.onLongClick(myViewHolder.getAdapterPosition());
                }
                return true;
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        Recommend recommend = list.get(position);
        myViewHolder.tvGameName.setText(recommend.title + "");
        Glide.with(context).load(recommend.pic).error(R.drawable.default_pic).into(myViewHolder.cImg);
        if (recommend.address_short == null || TextUtils.isEmpty(recommend.address_short)) {
            myViewHolder.tvTag.setVisibility(View.GONE);
        } else {
            myViewHolder.tvTag.setVisibility(View.VISIBLE);
            myViewHolder.tvTag.setText(recommend.address_short + "");
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cImg)
        CircularImageView cImg;
        @BindView(R.id.tv_game_name)
        TextView tvGameName;
        @BindView(R.id.tv_tag)
        TextView tvTag;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
