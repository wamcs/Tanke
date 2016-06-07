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

  @Override
  public BaseViewHolder<GameDataEntity> onCreateViewHolder(ViewGroup parent, int viewType) {
    return new GameDataViewHolder(parent);
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

//  @Override
//  public int getItemViewType(int position) {
//
//  }

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
