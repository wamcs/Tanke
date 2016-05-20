package com.lptiyu.tanke.base.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/18
 *
 * @author ldx
 */
public abstract class BaseListAdapter<V extends RecyclerView.ViewHolder, Bean> extends BaseAdapter<V, List<Bean>> {

  public BaseListAdapter(List<Bean> model, int layoutResId) {
    super(model, layoutResId);
  }

  @Override
  public abstract V newViewHolder(View view);

  @Override
  public abstract void onBindViewHolder(V holder, int position);

  @Override
  public int getItemCount() {
    return model.size();
  }

}
