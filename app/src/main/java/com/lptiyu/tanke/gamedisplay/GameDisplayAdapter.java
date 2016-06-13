package com.lptiyu.tanke.gamedisplay;

import android.view.ViewGroup;

import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.pojo.GameDisplayEntity;

import java.util.List;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/18
 *
 * @author ldx
 */
public class GameDisplayAdapter extends BaseAdapter<GameDisplayEntity> {

  private GameDisplayFragment fragment;

  public GameDisplayAdapter(GameDisplayFragment fragment) {
    this.fragment = fragment;
  }

  @SuppressWarnings("unchecked")
  @Override
  public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == 1) {
      return new ElasticHeaderViewHolder(parent, fragment);
    } else {
      return new NormalViewHolder(parent, fragment);
    }
  }

  @Override
  public void onBindViewHolder(BaseViewHolder<GameDisplayEntity> holder, int position) {
    if (position == 0) {
      ((ElasticHeaderViewHolder) holder).bind(dataList.subList(0, 3));
    } else {
      holder.bind(dataList.get(position - 1));
    }
  }


  @Override
  public int getItemViewType(int position) {
    return position == 0 ? 1 : 0;
  }

  @Override
  public int getItemCount() {
    return Math.max(0, dataList == null || dataList.size() < 2 ? 0 : dataList.size() - 2);
  }

}
