package com.lptiyu.tanke.userCenter.viewholder;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.gameplaying.GamePlayingActivity;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.enums.GameState;
import com.lptiyu.tanke.enums.Where;
import com.lptiyu.tanke.pojo.GamePlayingEntity;
import com.lptiyu.tanke.widget.GradientProgressBar;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author:wamcs
 * date:2016/6/13
 * email:kaili@hustunique.com
 */
public class GamePlayingViewHolder extends BaseViewHolder<GamePlayingEntity> {

    @BindView(R.id.game_playing_list_item_picture)
    RoundedImageView mItemPicture;

    @BindView(R.id.game_playing_list_item_name)
    TextView mItemName;

    @BindView(R.id.game_playing_list_item_progress)
    GradientProgressBar mItemProgress;

    @BindView(R.id.game_playing_list_item_progress_number)
    TextView mItemProgressNumber;

    @BindView(R.id.game_playing_list_item)
    LinearLayout mItem;


    public GamePlayingViewHolder(ViewGroup parent) {
        super(fromResLayout(parent, R.layout.item_game_playing));
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bind(final GamePlayingEntity entity) {
        mItemName.setText(entity.name);
        mItemProgressNumber.setText(Math.floor(entity.progress * 100) + "%");
        Glide.with(getContext()).load(entity.img).error(R.drawable.default_pic).into(mItemPicture);
        mItemProgress.setProgress(entity.progress);
        mItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entity.states == GameState.FINISHED) {
                    Toast.makeText(getContext(), "该游戏已下线", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (entity.states == GameState.MAINTAINING) {
                    Toast.makeText(getContext(), "该游戏正在维护中", Toast.LENGTH_SHORT).show();
                    return;
                }

                RunApplication.gameId = entity.gameId;
                RunApplication.type = entity.type;
                RunApplication.entity = entity;
                RunApplication.entity.title = entity.name;
                RunApplication.where = Where.GAME_PLAYING;
                RunApplication.recordId = -1;
                getContext().startActivity(new Intent(getContext(), GamePlayingActivity.class));
            }
        });
    }
}
