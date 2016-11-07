package com.lptiyu.tanke.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.widget.CircularImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/9/23.
 */

public class PointListAdapter extends BaseRecyclerViewAdapter<Point> {

    public PointListAdapter(Context context, List<Point> list) {
        super(context, list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_recyclerview_point_list, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onClick(viewHolder.getAdapterPosition());
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (clickListener != null) {
                    clickListener.onLongClick(viewHolder.getAdapterPosition());
                }
                return true;
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Point point = list.get(position);
        ViewHolder vh = (ViewHolder) holder;
        vh.tvPointName.setText(point.point_title + "");
        Glide.with(context).load(point.point_img).error(R.drawable.default_pic).crossFade().into(vh.img);

        switch (point.status) {
            case PointTaskStatus.UNSTARTED://未开启
                vh.tvPointName.setText("未解锁");
                vh.tvLabel.setVisibility(View.GONE);
                vh.imgLock.setVisibility(View.VISIBLE);
                vh.imgTransparent.setVisibility(View.VISIBLE);
                break;
            case PointTaskStatus.PLAYING://正在玩
                vh.tvPointName.setText(point.point_title);
                vh.tvLabel.setVisibility(View.VISIBLE);
                vh.tvLabel.setText("进行中");
                vh.tvLabel.setBackgroundResource(R.drawable.shape_point_state_playing);
                vh.imgLock.setVisibility(View.GONE);
                vh.imgTransparent.setVisibility(View.GONE);
                break;
            case PointTaskStatus.FINISHED://已完成
                vh.tvPointName.setText(point.point_title);
                vh.tvLabel.setVisibility(View.VISIBLE);
                vh.tvLabel.setText("已完成");
                vh.tvLabel.setBackgroundResource(R.drawable.shape_point_state_finished);
                vh.imgLock.setVisibility(View.GONE);
                vh.imgTransparent.setVisibility(View.GONE);
                break;
        }
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
        @BindView(R.id.tv_label)
        TextView tvLabel;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
