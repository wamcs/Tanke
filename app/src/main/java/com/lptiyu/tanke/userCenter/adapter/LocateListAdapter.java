package com.lptiyu.tanke.userCenter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.pojo.City;
import com.lptiyu.tanke.utils.Inflater;
import com.lptiyu.tanke.widget.CustomTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author:wamcs
 * date:2016/6/10
 * email:kaili@hustunique.com
 */
public class LocateListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public class CityViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.locate_activity_list_normal_item)
        CustomTextView mCity;

        public CityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    private List<City> list;
    private OnCityItemClickListener listener;

    public LocateListAdapter(List<City> list){
        this.list = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CityViewHolder(Inflater.inflate(R.layout.item_locate_list_open_city,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CityViewHolder holder1 = (CityViewHolder) holder;
        final int positionFinal = holder1.getAdapterPosition();
        holder1.mCity.setText(list.get(positionFinal).getName());
        holder1.mCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnCityItemClick(positionFinal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnCityItemClickListener(OnCityItemClickListener listener){
        this.listener = listener;
    }

    public interface OnCityItemClickListener{
        void OnCityItemClick(int position);
    }
}
