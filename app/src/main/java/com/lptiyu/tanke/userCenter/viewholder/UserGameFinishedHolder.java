package com.lptiyu.tanke.userCenter.viewholder;

import android.support.v4.media.MediaMetadataCompat;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.pojo.GameFinishedEntity;

import butterknife.BindView;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/14
 *
 * @author ldx
 */
public class UserGameFinishedHolder extends BaseViewHolder<GameFinishedEntity>{

  @BindView(R.id.image_view)
  ImageView imageView;

  @BindView(R.id.item_finished_title)
  TextView title;

  @BindView(R.id.item_finished_content)
  TextView content;

  public UserGameFinishedHolder(ViewGroup parent) {
    super(fromResLayout(parent, R.layout.item_game_finished));
  }

  @Override
  public void bind(GameFinishedEntity entity) {
    Glide.with(getContext()).load(entity.getImg()).into(imageView);
    title.setText(entity.getName());
    content.setText(String.format("获得经验值%d 用时%s ".toLowerCase(), entity.getExpPoints(), entity.getTime()));
  }


}
