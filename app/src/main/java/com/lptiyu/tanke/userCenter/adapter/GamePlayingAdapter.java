package com.lptiyu.tanke.userCenter.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.pojo.GamePlayingEntity;

import java.util.List;

/**
 * author:wamcs
 * date:2016/6/13
 * email:kaili@hustunique.com
 */
public class GamePlayingAdapter extends BaseAdapter<GamePlayingEntity> {

    private Context context;

    public GamePlayingAdapter(Context context){
        this.context = context;
    }

    @Override
    public void addData(List<GamePlayingEntity> data) {

    }

    @Override
    public void setData(List<GamePlayingEntity> data) {

    }

    @Override
    public BaseViewHolder<GamePlayingEntity> onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<GamePlayingEntity> holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
