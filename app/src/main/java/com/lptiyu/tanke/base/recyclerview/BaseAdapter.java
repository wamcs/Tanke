package com.lptiyu.tanke.base.recyclerview;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/22
 *
 * @author ldx
 */
public abstract class BaseAdapter<DataType> extends RecyclerView.Adapter<BaseViewHolder<DataType>> {

  public abstract void addData(List<DataType> data);

  public abstract void setData(List<DataType> data);
}

