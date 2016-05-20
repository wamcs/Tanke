package com.lptiyu.tanke.gamedisplay;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lptiyu.tanke.base.recyclerview.BaseListAdapter;

import java.util.List;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/18
 *
 * @author ldx
 */
public class GameDisplayAdapter extends BaseListAdapter<GameDisplayAdapter.ViewHolder, Object> {

  public GameDisplayAdapter(List<Object> model, int layoutResId) {
    super(model, layoutResId);
  }

  @Override
  public ViewHolder newViewHolder(View view) {
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    public ViewHolder(View itemView) {
      super(itemView);
    }
  }

}
