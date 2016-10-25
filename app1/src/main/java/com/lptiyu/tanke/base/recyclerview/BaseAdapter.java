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

  protected List<DataType> dataList;

  @Override
  public void onBindViewHolder(BaseViewHolder<DataType> holder, int position) {
    holder.bind(dataList.get(position));
  }

  @Override
  public int getItemCount() {
    return dataList == null ? 0 : dataList.size();
  }

  public void addData(List<DataType> data) {
    if (dataList == null) {
      setData(data);
      return;
    }
    int i = dataList.size();
    dataList.addAll(data);
    notifyItemInserted(i);
  }

  public void setData(List<DataType> data) {
    dataList = data;
    notifyDataSetChanged();
  }

  public void setDataInner(List<DataType> data) {
    dataList = data;
  }

  public List<DataType> getData() {
    return dataList;
  }
}

