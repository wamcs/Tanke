package com.lptiyu.tanke.userCenter.viewholder;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.gamedetails.GameDetailsActivity;
import com.lptiyu.tanke.global.Conf;
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
  public void bind(GamePlayingEntity entity) {
    final long gameId = entity.getGameId();
    mItemName.setText(entity.getName());
    mItemProgressNumber.setText(entity.getProgress() * 100 + "%");
    Glide.with(getContext()).load(entity.getImg()).error(R.mipmap.need_to_remove).into(mItemPicture);
    mItemProgress.setProgress(entity.getProgress());
    mItem.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getContext(), GameDetailsActivity.class);
        intent.putExtra(Conf.GAME_ID, gameId);
        getContext().startActivity(intent);
      }
    });
  }

}
