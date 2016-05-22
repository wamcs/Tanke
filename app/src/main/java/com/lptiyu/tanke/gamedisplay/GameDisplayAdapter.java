package com.lptiyu.tanke.gamedisplay;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.bean.GameEntry;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/18
 *
 * @author ldx
 */
public class GameDisplayAdapter extends BaseAdapter<GameDisplayAdapter.ViewHolder, List<GameEntry>> {

  private List<GameEntry> dataList;

  private GameDisplayFragment fragment;

  public GameDisplayAdapter(GameDisplayFragment fragment) {
    this.fragment = fragment;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(fromResLayout(parent, R.layout.fragment_game_display));
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.bind(dataList.get(position));
  }

  @Override
  public int getItemCount() {
    return dataList == null ? 0 : dataList.size();
  }

  @Override
  public void setData(List<GameEntry> data) {
    this.dataList = data;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.image_view)
    ImageView imageView;

    @BindView(R.id.text_view)
    TextView textView;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void bind(GameEntry entry) {
      Glide.with(fragment).load(entry.url).asBitmap().into(imageView);
      textView.setText(entry.text);
    }
  }

}
