package com.lptiyu.tanke.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.entity.response.HomeTabEntity;
import com.lptiyu.tanke.enums.GameState;
import com.lptiyu.tanke.enums.SortIndex;

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
        myViewHolder.tvPlayerCountOnlinePlayable.setText(gameEntity.player_num + "人在玩");
        Glide.with(context).load(gameEntity.pic).error(R.drawable.default_pic).crossFade().into(myViewHolder.imageView);
        myViewHolder.ratingBar.setRating(gameEntity.difficulty);
        String distance = "500m";
        if (gameEntity.distince >= 1) {
            distance = gameEntity.distince + "km";
        }
        myViewHolder.tvDistance.setText(distance);
        myViewHolder.location.setText(gameEntity.area);

        if (sortIndex == SortIndex.NEAR_GAME) {
            myViewHolder.tvPlayerCountOnlinePlayable.setVisibility(View.GONE);
        }
        if (sortIndex == SortIndex.ONLINE_PLAYABLE) {
            myViewHolder.location.setText("");
            myViewHolder.tvDistance.setVisibility(View.GONE);
        }
        if (sortIndex == SortIndex.DIRECTION_RUN) {
            myViewHolder.tvPlayerCountOnlinePlayable.setVisibility(View.GONE);
        }
        if (sortIndex == SortIndex.COMPETITION_ACTIVITY) {
            myViewHolder.tvPlayerCountOnlinePlayable.setVisibility(View.GONE);
        }
        //如果有内测标签则只显示内测标签，否则显示定向乐跑的标签
        switch (gameEntity.state) {
            case GameState.ALPHA_TEST:
                myViewHolder.tvCompetitonTag.setVisibility(View.VISIBLE);
                myViewHolder.tvCompetitonTag.setText(GameState.ALPHA_TEST_STR);
                break;
            case GameState.FINISHED:
                myViewHolder.tvCompetitonTag.setVisibility(View.VISIBLE);
                myViewHolder.tvCompetitonTag.setText(GameState.FINISHED_STR);
                break;
            case GameState.MAINTAINING:
                myViewHolder.tvCompetitonTag.setVisibility(View.VISIBLE);
                myViewHolder.tvCompetitonTag.setText(GameState.MAINTAINING_STR);
                break;
            case GameState.NORMAL:
            default:
                if (sortIndex == SortIndex.NEAR_GAME) {
                    if (TextUtils.isEmpty(gameEntity.tag)) {
                        myViewHolder.tvCompetitonTag.setVisibility(View.GONE);
                    } else {
                        myViewHolder.tvCompetitonTag.setVisibility(View.VISIBLE);
                        myViewHolder.tvCompetitonTag.setText(gameEntity.tag);
                    }
                }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_view)
        ImageView imageView;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.tv_distance)
        TextView tvDistance;
        @BindView(R.id.location)
        TextView location;
        @BindView(R.id.ratingBar)
        RatingBar ratingBar;
        @BindView(R.id.tv_player_count_online_playable)
        TextView tvPlayerCountOnlinePlayable;
        @BindView(R.id.tv_tag)
        TextView tvCompetitonTag;
        @BindView(R.id.rl_difficult)
        RelativeLayout rlDifficult;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
