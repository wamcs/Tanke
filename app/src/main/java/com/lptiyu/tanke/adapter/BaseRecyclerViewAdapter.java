package com.lptiyu.tanke.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.lptiyu.tanke.interfaces.OnRecyclerViewItemClickListener;

import java.util.Collection;
import java.util.List;

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter {
    protected Context context;

    protected List<T> list;

    protected LayoutInflater inflater;

    public BaseRecyclerViewAdapter(Context mContext, List<T> mDataList) {
        this.context = mContext;
        this.list = mDataList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public List<T> getDataList() {
        return list;
    }

    public void refreshData() {
        notifyDataSetChanged();
    }

    public void addAll(Collection<T> list) {
        int lastIndex = this.list.size();
        if (this.list.addAll(list)) {
            notifyItemRangeInserted(lastIndex, list.size());
        }
    }

    public void remove(int position) {
        if (this.list.size() > 0) {
            list.remove(position);
            notifyItemRemoved(position);
        }

    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public OnRecyclerViewItemClickListener clickListener;

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.clickListener = listener;
    }

    //    public OnRecyclerViewScrollListener scrollListener;
    //
    //    public void setOnRecyclerViewScrollListener(OnRecyclerViewScrollListener listener) {
    //        this.scrollListener = listener;
    //    }
}
