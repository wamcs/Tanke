package com.lptiyu.tanke.base.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/18
 *
 * @author ldx
 */
public abstract class BaseAdapter<V extends RecyclerView.ViewHolder, M> extends RecyclerView.Adapter<V> {

  int layoutResId = 0;

  M model;

  public BaseAdapter(M model, int layoutResId) {
    this.model = model;
    this.layoutResId = layoutResId;
  }

  @Override
  public V onCreateViewHolder(ViewGroup parent, int viewType) {
    return newViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false));
  }

  public abstract V newViewHolder(View view);

  @Override
  public abstract void onBindViewHolder(V holder, int position);

  @Override
  public abstract int getItemCount();

}
