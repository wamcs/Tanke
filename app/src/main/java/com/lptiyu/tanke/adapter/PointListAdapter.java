package com.lptiyu.tanke.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.widget.CircularImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/9/23.
 */

public class PointListAdapter extends BaseRecyclerViewAdapter<Point> {

    public PointListAdapter(Context context, List<Point> list) {
        this.mDataList = list;
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_recyclerview_point_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Point point = mDataList.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.tvPointName.setText(point.point_title + "");
        Glide.with(mContext).load(point.point_img).error(R.drawable.default_pic).into(viewHolder.img);

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img)
        CircularImageView img;
        @BindView(R.id.img_transparent)
        ImageView imgTransparent;
        @BindView(R.id.img_lock)
        ImageView imgLock;
        @BindView(R.id.ctv_pointName)
        TextView tvPointName;
        @BindView(R.id.img_label)
        ImageView imgLabel;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
