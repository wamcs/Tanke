package com.lptiyu.tanke.userCenter.viewholder;

import android.view.ViewGroup;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.pojo.GameFinishedEntity;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/14
 *
 * @author ldx
 */
public class UserGameFinishedHolder extends BaseViewHolder<GameFinishedEntity>{

  public UserGameFinishedHolder(ViewGroup parent) {
    super(fromResLayout(parent, R.layout.item_game_finished));
  }

  @Override
  public void bind(GameFinishedEntity entity) {

  }
}
