package com.lptiyu.tanke.base.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/1
 *
 * @author ldx
 */
public abstract class BaseViewHolder<Bean> extends RecyclerView.ViewHolder {

  public BaseViewHolder(View itemView) {
    super(itemView);
  }

  public BaseViewHolder(ViewGroup parent, int layoutId) {
    super(fromResLayout(parent, layoutId));
  }

  public static View fromResLayout(ViewGroup parent, int layoutId) {
    return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
  }

  public static View fromResLayout(Context context, int layoutId) {
    return LayoutInflater.from(context).inflate(layoutId, null);
  }

  public Context getContext() {
    return itemView.getContext();
  }

  public abstract void bind(Bean entity);

}
