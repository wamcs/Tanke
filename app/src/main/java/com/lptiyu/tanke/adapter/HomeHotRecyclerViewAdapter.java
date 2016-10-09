package com.lptiyu.tanke.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.interfaces.OnRecyclerViewItemClickListener;
import com.lptiyu.tanke.entity.response.Recommend;
import com.lptiyu.tanke.widget.CircularImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/9/23.
 */

public class HomeHotRecyclerViewAdapter extends RecyclerView.Adapter<HomeHotRecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private List<Recommend> mDataList;
    private OnRecyclerViewItemClickListener listener;

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    public HomeHotRecyclerViewAdapter(Context mContext, List<Recommend> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_home_hot, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(myViewHolder.getAdapterPosition());
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onLongClick(myViewHolder.getAdapterPosition());
                }
                return true;
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Recommend recommend = mDataList.get(position);
        holder.tvGameName.setText(recommend.title + "");
        Glide.with(mContext).load(recommend.pic).error(R.drawable.default_pic).into(holder.cImg);
        if (recommend.address_short == null || TextUtils.isEmpty(recommend.address_short)) {
            holder.tvTag.setVisibility(View.GONE);
        } else {
            holder.tvTag.setVisibility(View.VISIBLE);
            holder.tvTag.setTag(recommend.address_short);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
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
