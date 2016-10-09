package com.lptiyu.tanke.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.List;

public class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter {
    protected Context mContext;

    protected List<T> mDataList;

    protected LayoutInflater mLayoutInflater;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public List<T> getDataList() {
        return mDataList;
    }

    //    public void setDataList(Collection<T> list) {
    //        this.mDataList.clear();
    //        this.mDataList.addAll(list);
    //        notifyDataSetChanged();
    //    }
    public void refreshData() {
        notifyDataSetChanged();
    }

    public void addAll(Collection<T> list) {
        int lastIndex = this.mDataList.size();
        if (this.mDataList.addAll(list)) {
            notifyItemRangeInserted(lastIndex, list.size());
        }
    }

    public void remove(int position) {
        if (this.mDataList.size() > 0) {
            mDataList.remove(position);
            notifyItemRemoved(position);
        }

    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }
}
