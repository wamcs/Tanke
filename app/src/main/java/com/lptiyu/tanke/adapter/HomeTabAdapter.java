package com.lptiyu.tanke.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.entity.response.HomeTabEntity;
import com.lptiyu.tanke.enums.SortIndex;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/9/23.
 */

public class HomeTabAdapter extends BaseRecyclerViewAdapter<HomeTabEntity> {
    private int sortIndex;

    public HomeTabAdapter(Context context, List<HomeTabEntity> list, int sortIndex) {
        super(context, list);
        this.sortIndex = sortIndex;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_home_display, parent, false);
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
        HomeTabEntity gameEntity = list.get(position);

        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.title.setText(gameEntity.title + "");
        myViewHolder.tvPlayerCountDiretionRun.setText(gameEntity.player_num + "人在玩");
        myViewHolder.tvPlayerCountOnlinePlayable.setText(gameEntity.player_num + "人在玩");
        myViewHolder.tvPlayerCountNearGame.setText(gameEntity.player_num + "人在玩");
        Glide.with(context).load(gameEntity.pic).error(R.drawable.default_pic).placeholder(R.drawable.default_pic)
                .into(myViewHolder.imageView);
        myViewHolder.ratingBar.setRating(gameEntity.difficulty);
        if (gameEntity.tag == null || TextUtils.isEmpty(gameEntity.tag)) {
            myViewHolder.tvCompetitonTag.setVisibility(View.GONE);
        } else {
            myViewHolder.tvCompetitonTag.setVisibility(View.VISIBLE);
            myViewHolder.tvCompetitonTag.setText(gameEntity.tag);
        }
        myViewHolder.tvDistance.setText(gameEntity.distince + "km");
        myViewHolder.location.setText(gameEntity.area);

        switch (sortIndex) {
            case SortIndex.NEAR_GAME:
                myViewHolder.tvPlayerCountDiretionRun.setVisibility(View.GONE);
                myViewHolder.tvPlayerCountOnlinePlayable.setVisibility(View.GONE);
                myViewHolder.rlDifficult.setVisibility(View.GONE);
                break;
            case SortIndex.ONLINE_PLAYABLE:
                myViewHolder.tvDistance.setVisibility(View.GONE);
                myViewHolder.location.setText("");
                myViewHolder.tvPlayerCountNearGame.setVisibility(View.GONE);
                myViewHolder.tvPlayerCountDiretionRun.setVisibility(View.GONE);
                myViewHolder.tvDistance.setVisibility(View.GONE);
                break;
            case SortIndex.DIRECTION_RUN:
                myViewHolder.tvPlayerCountNearGame.setVisibility(View.GONE);
                myViewHolder.tvPlayerCountOnlinePlayable.setVisibility(View.GONE);
                break;
            case SortIndex.COMPETITION_ACTIVITY:
                myViewHolder.tvPlayerCountNearGame.setVisibility(View.GONE);
                myViewHolder.tvPlayerCountOnlinePlayable.setVisibility(View.GONE);
                break;
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
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
        @BindView(R.id.tv_player_count_direction_run)
        TextView tvPlayerCountDiretionRun;
        @BindView(R.id.tv_player_count_near_game)
        TextView tvPlayerCountNearGame;
        @BindView(R.id.tv_player_count_online_playable)
        TextView tvPlayerCountOnlinePlayable;
        @BindView(R.id.tv_competition_tag)
        TextView tvCompetitonTag;
        @BindView(R.id.rl_difficult)
        RelativeLayout rlDifficult;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
