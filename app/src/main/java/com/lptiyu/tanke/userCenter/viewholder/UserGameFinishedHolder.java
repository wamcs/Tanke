package com.lptiyu.tanke.userCenter.viewholder;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.media.MediaMetadataCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.gamedetails.GameDetailsActivity;
import com.lptiyu.tanke.gameplaying.GamePlayingActivity;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.pojo.GameFinishedEntity;
import com.lptiyu.tanke.widget.CustomTextView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/14
 *
 * @author ldx
 */
public class UserGameFinishedHolder extends BaseViewHolder<GameFinishedEntity>{

  @BindView(R.id.image_view)
  RoundedImageView mItemPicture;

  @BindView(R.id.item_finished_title)
  CustomTextView title;

  @BindView(R.id.item_finished_type)
  CustomTextView type;

  @BindView(R.id.item_finished_complete_time)
  CustomTextView completeTime;

  @BindView(R.id.item_finished_consuming_time)
  CustomTextView comsumingTime;

  @BindView(R.id.item_finished_exp)
  CustomTextView exp;

  @BindView(R.id.game_finished_list_item)
  RelativeLayout mItem;

  public UserGameFinishedHolder(ViewGroup parent) {
    super(fromResLayout(parent, R.layout.item_game_finished));
    ButterKnife.bind(this, itemView);
  }

  @Override
  public void bind(GameFinishedEntity entity) {
    if (entity == null || mItemPicture == null) {
      return;
    }
    final long gameId = entity.getGameId();
    Glide.with(getContext()).load(entity.getImg()).into(mItemPicture);

    title.setText(entity.getName());
    type.setText("");
    Date date = new Date();
    completeTime.setText(String.format(getContext().getString(R.string.complete_time_formatter),
        date.getYear() + 1900,
        date.getMonth() + 1,
        date.getDate(),
        date.getHours(),
        date.getMinutes()));
    comsumingTime.setText("1小时55分");
    exp.setText(String.valueOf(entity.getExpPoints()));

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
