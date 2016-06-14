package com.lptiyu.tanke.userCenter.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.pojo.GamePlayingEntity;
import com.lptiyu.tanke.userCenter.viewholder.GamePlayingViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * author:wamcs
 * date:2016/6/13
 * email:kaili@hustunique.com
 */
public class GamePlayingAdapter extends BaseAdapter<GamePlayingEntity> {

    private Context context;
    private List<GamePlayingEntity> list;

    public GamePlayingAdapter(Context context){
        this.context = context;
    }

    @Override
    public void addData(List<GamePlayingEntity> data) {
        if (null == list){
            list = new ArrayList<>();
        }
        list.addAll(data);
    }

    @Override
    public void setData(List<GamePlayingEntity> data) {
        list = data;
    }

    @Override
    public BaseViewHolder<GamePlayingEntity> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GamePlayingViewHolder(parent,context);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<GamePlayingEntity> holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
