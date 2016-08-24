package com.lptiyu.tanke.userCenter.adapter;

import android.view.ViewGroup;

import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.pojo.GamePlayingEntity;
import com.lptiyu.tanke.userCenter.viewholder.GamePlayingViewHolder;

/**
 * author:wamcs
 * date:2016/6/13
 * email:kaili@hustunique.com
 */
public class GamePlayingAdapter extends BaseAdapter<GamePlayingEntity> {


    public GamePlayingAdapter(){
    }

    @Override
    public BaseViewHolder<GamePlayingEntity> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GamePlayingViewHolder(parent);
    }

}
