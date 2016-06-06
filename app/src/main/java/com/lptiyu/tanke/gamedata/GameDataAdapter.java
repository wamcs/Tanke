package com.lptiyu.tanke.gamedata;

import android.view.View;
import android.view.ViewGroup;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.pojo.GameDataEntity;
import com.lptiyu.tanke.utils.Inflater;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-6
 *         email: wonderfulifeel@gmail.com
 */
public class GameDataAdapter extends BaseAdapter<GameDataEntity> {

  private List<GameDataEntity> gameDataEntities;

  private static final int HEADER = 0;
  private static final int NORMAL_TOP = 1;
  private static final int NORMAL_MID = 2;
  private static final int NORMAL_BOTTOM = 3;

  @Override
  public BaseViewHolder<GameDataEntity> onCreateViewHolder(ViewGroup parent, int viewType) {
    int layoutId = R.layout.item_game_data_normal_mid;
    switch (viewType) {
      case HEADER:

        break;

      case NORMAL_TOP:
        layoutId = R.layout.item_game_data_normal_top;
        break;

      case NORMAL_MID:
        layoutId = R.layout.item_game_data_normal_mid;
        break;

      case NORMAL_BOTTOM:
        layoutId = R.layout.item_game_data_normal_bottom;
        break;

    }
    return new GameDataViewHolder(parent, layoutId);
  }

  @Override
  public void onBindViewHolder(BaseViewHolder<GameDataEntity> holder, int position) {
    holder.bind(gameDataEntities.get(position));
  }

  @Override
  public int getItemCount() {
    if (gameDataEntities == null) {
      return 0;
    }
    return gameDataEntities.size();
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return NORMAL_TOP;
    }
    if (position == getItemCount() - 1) {
      return NORMAL_BOTTOM;
    }
    return NORMAL_MID;
  }

  @Override
  public void addData(List<GameDataEntity> data) {
    if (gameDataEntities == null) {
      gameDataEntities = new ArrayList<>();
    }
    gameDataEntities.addAll(data);
  }

  @Override
  public void setData(List<GameDataEntity> data) {
    gameDataEntities = data;
  }

}
