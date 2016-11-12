package com.lptiyu.tanke.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.entity.response.DRRecordEntity;
import com.lptiyu.tanke.utils.TimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/10/31.
 */

public class DRRecordListAdapter extends BaseRecyclerViewAdapter<DRRecordEntity> {
    public DRRecordListAdapter(Context mContext, List<DRRecordEntity> mDataList) {
        super(mContext, mDataList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_direction_run_record_list, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onClick(viewHolder.getAdapterPosition());
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DRRecordEntity record = list.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.tvDistanceValue.setText(record.distance);
        viewHolder.tvLineName.setText(record.title);
        viewHolder.tvStartTime.setText(TimeUtils.stampToDateStr(Long.parseLong(record.start_time) * 1000, TimeUtils
                .PATTERN1) + "");
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_distance_value)
        TextView tvDistanceValue;
        @BindView(R.id.tv_line_name)
        TextView tvLineName;
        @BindView(R.id.tv_start_time)
        TextView tvStartTime;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
