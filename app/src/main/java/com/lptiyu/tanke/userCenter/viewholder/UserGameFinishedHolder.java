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
  SimpleDraweeView imageView;

  @BindView(R.id.item_finished_title)
  TextView title;

  @BindView(R.id.item_finished_content)
  TextView content;

  @BindView(R.id.game_finished_list_item)
  RelativeLayout mItem;

  public UserGameFinishedHolder(ViewGroup parent) {
    super(fromResLayout(parent, R.layout.item_game_finished));
    ButterKnife.bind(this, itemView);
  }

  @Override
  public void bind(GameFinishedEntity entity) {
    if (entity == null || imageView == null) {
      return;
    }
    final long gameId = entity.getGameId();
    imageView.setImageURI(Uri.parse(entity.getImg()));
    title.setText(entity.getName());
    content.setText(String.format("获得经验值%d 用时%s ".toLowerCase(), entity.getExpPoints(), entity.getTime()));
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
