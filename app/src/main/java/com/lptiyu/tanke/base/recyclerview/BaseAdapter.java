package com.lptiyu.tanke.base.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/22
 *
 * @author ldx
 */
public abstract class BaseAdapter<VH extends RecyclerView.ViewHolder, DataType> extends RecyclerView.Adapter<VH> {

  public View fromResLayout(ViewGroup parent, int layoutId) {
    return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
  }

  public View fromResLayout(Context context, int layoutId) {
    return LayoutInflater.from(context).inflate(layoutId, null);
  }

  public abstract void setData(DataType data);
}

