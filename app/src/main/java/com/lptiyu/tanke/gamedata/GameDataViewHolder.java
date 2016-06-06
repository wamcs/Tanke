package com.lptiyu.tanke.gamedata;

import android.view.ViewGroup;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.pojo.GameDataEntity;
import com.lptiyu.tanke.utils.TimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-6
 *         email: wonderfulifeel@gmail.com
 */
public class GameDataViewHolder extends BaseViewHolder<GameDataEntity> {

  @BindView(R.id.item_game_data_index)
  TextView mIndex;
  @BindView(R.id.item_game_data_title)
  TextView mTitle;
  @BindView(R.id.item_game_data_time_consuming)
  TextView mComsuming;
  @BindView(R.id.item_game_data_exp)
  TextView mExp;

  public GameDataViewHolder(ViewGroup parent, int layoutId) {
    super(fromResLayout(parent, layoutId));
    ButterKnife.bind(this, itemView);
  }

  @Override
  public void bind(GameDataEntity entity) {
    mIndex.setText("" + getPosition());
    mTitle.setText(String.format("这是第%d个任务", getPosition()));
    mComsuming.setText(TimeUtils.getFriendlyTime(entity.getMillisConsuming()));
    mExp.setText(String.format("%d exp", entity.getExp()));
  }

  @Override
  public void bind(List<GameDataEntity> entities) {
    for (GameDataEntity entity : entities) {
      bind(entity);
    }
  }

}
