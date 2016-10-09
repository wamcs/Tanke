package com.lptiyu.tanke.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.entity.response.HomeGameList;
import com.lptiyu.tanke.enums.SortIndex;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/9/23.
 */

public class HomeDisplayAdapter extends BaseRecyclerViewAdapter<HomeGameList> {
    private int sortIndex;

    public HomeDisplayAdapter(Context context, List<HomeGameList> list, int sortIndex) {
        this.mDataList = list;
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        this.sortIndex = sortIndex;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_home_display, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomeGameList gameEntity = mDataList.get(position);

        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.title.setText(gameEntity.title + "");
        viewHolder.tvPlayerCount.setText(gameEntity.player_num + "人在玩");
        Glide.with(mContext).load(gameEntity.pic).error(R.drawable.default_pic).into(viewHolder.imageView);
        viewHolder.ratingBar.setRating(gameEntity.difficulty);
        if (gameEntity.tag == null || TextUtils.isEmpty(gameEntity.tag)) {
            viewHolder.tvCompetitonTag.setVisibility(View.GONE);
        } else {
            viewHolder.tvCompetitonTag.setVisibility(View.VISIBLE);
            viewHolder.tvCompetitonTag.setText(gameEntity.tag);
        }
        viewHolder.tvDistance.setText("<" + gameEntity.distince + "km");
        viewHolder.location.setText("游戏区域：" + gameEntity.area);

        switch (sortIndex) {
            case SortIndex.NEAR_GAME:
                break;
            case SortIndex.ONLINE_PLAYABLE:
                viewHolder.tvDistance.setVisibility(View.GONE);
                viewHolder.location.setVisibility(View.GONE);
                break;
            case SortIndex.DIRECTION_RUN:
                break;
            case SortIndex.COMPETITION_ACTIVITY:
                break;
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_view)
        RoundedImageView imageView;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.tv_distance)
        TextView tvDistance;
        @BindView(R.id.location)
        TextView location;
        @BindView(R.id.ratingBar)
        RatingBar ratingBar;
        @BindView(R.id.tv_player_count)
        TextView tvPlayerCount;
        @BindView(R.id.tv_competition_tag)
        TextView tvCompetitonTag;
        //        @BindView(R.id.tv_direction_run_tag)
        //        TextView tvDirectionalRun;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
