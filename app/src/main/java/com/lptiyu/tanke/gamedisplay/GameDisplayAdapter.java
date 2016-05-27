package com.lptiyu.tanke.gamedisplay;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.pojo.GameDisplayEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/18
 *
 * @author ldx
 */
public class GameDisplayAdapter extends BaseAdapter<GameDisplayAdapter.ViewHolder, GameDisplayEntity> {

  private List<GameDisplayEntity> dataList;

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
    holder.bind(dataList.get(position), position);
  }

  @Override
  public int getItemCount() {
    return dataList == null ? 0 : dataList.size();
  }

  @Override
  public void addData(List<GameDisplayEntity> data) {
    if (dataList == null) {
      setData(data);
      return;
    }
    int i = dataList.size();
    dataList.addAll(data);
    notifyItemInserted(i);
  }

  @Override
  public void setData(List<GameDisplayEntity> data) {
    this.dataList = data;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.image_view)
    ImageView imageView;

    @BindView(R.id.text_view)
    TextView textView;

    int position = -1;

    GameDisplayEntity gameDisplayEntity;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    void bind(GameDisplayEntity entry, int position) {
      Glide.with(fragment).load(entry.getImg()).asBitmap().into(imageView);
      textView.setText(entry.getTitle());
      this.position = position;
      this.gameDisplayEntity = entry;
    }

    @OnClick(R.id.item_root)
    void onItemClick() {
      GameDisplayController controller = fragment.getController();
      if (controller == null) {
        Timber.e("GameDisplayFragment get Controller is null");
        return;
      }

      controller.onItemClick(gameDisplayEntity, position);
    }
  }

}
