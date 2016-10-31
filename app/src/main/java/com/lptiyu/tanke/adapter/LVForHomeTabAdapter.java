package com.lptiyu.tanke.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
 * Created by Jason on 2016/10/27.
 */

public class LVForHomeTabAdapter extends BaseAdapter {
    private List<HomeTabEntity> list;
    private Context context;
    private LayoutInflater inflater;
    private int sortIndex;

    public LVForHomeTabAdapter(Context context, List<HomeTabEntity> list, int sortIndex) {
        this.list = list;
        this.context = context;
        this.sortIndex = sortIndex;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder myViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_home_display, parent, false);
            myViewHolder = new ViewHolder(convertView);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (ViewHolder) convertView.getTag();
        }
        HomeTabEntity gameEntity = list.get(position);
        myViewHolder.title.setText(gameEntity.title + "");
        myViewHolder.tvPlayerCountDirectionRun.setText(gameEntity.player_num + "人在玩");
        myViewHolder.tvPlayerCountOnlinePlayable.setText(gameEntity.player_num + "人在玩");
        myViewHolder.tvPlayerCountNearGame.setText(gameEntity.player_num + "人在玩");
        Glide.with(context).load(gameEntity.pic).error(R.drawable.default_pic).into(myViewHolder.imageView);
        myViewHolder.ratingBar.setRating(gameEntity.difficulty);
        if (gameEntity.tag == null || TextUtils.isEmpty(gameEntity.tag)) {
            myViewHolder.tvCompetitionTag.setVisibility(View.GONE);
        } else {
            myViewHolder.tvCompetitionTag.setVisibility(View.VISIBLE);
            myViewHolder.tvCompetitionTag.setText(gameEntity.tag);
        }
        myViewHolder.tvDistance.setText(gameEntity.distince + "km");
        myViewHolder.location.setText(gameEntity.area);

        switch (sortIndex) {
            case SortIndex.NEAR_GAME:
                myViewHolder.tvPlayerCountDirectionRun.setVisibility(View.GONE);
                myViewHolder.tvPlayerCountOnlinePlayable.setVisibility(View.GONE);
                myViewHolder.rlDifficult.setVisibility(View.GONE);
                break;
            case SortIndex.ONLINE_PLAYABLE:
                myViewHolder.tvDistance.setVisibility(View.GONE);
                myViewHolder.location.setText("");
                myViewHolder.tvPlayerCountNearGame.setVisibility(View.GONE);
                myViewHolder.tvPlayerCountDirectionRun.setVisibility(View.GONE);
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
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.image_view)
        RoundedImageView imageView;
        @BindView(R.id.tv_competition_tag)
        TextView tvCompetitionTag;
        @BindView(R.id.tv_distance)
        TextView tvDistance;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.tv_player_count_online_playable)
        TextView tvPlayerCountOnlinePlayable;
        @BindView(R.id.location)
        TextView location;
        @BindView(R.id.tv_hard_level_tip)
        TextView tvHardLevelTip;
        @BindView(R.id.ratingBar)
        RatingBar ratingBar;
        @BindView(R.id.tv_player_count_direction_run)
        TextView tvPlayerCountDirectionRun;
        @BindView(R.id.rl_difficult)
        RelativeLayout rlDifficult;
        @BindView(R.id.tv_player_count_near_game)
        TextView tvPlayerCountNearGame;
        @BindView(R.id.item_root)
        RelativeLayout itemRoot;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
