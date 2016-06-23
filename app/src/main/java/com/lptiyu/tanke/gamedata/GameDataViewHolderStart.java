package com.lptiyu.tanke.gamedata;

import android.view.ViewGroup;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.pojo.GameDataEntity;
import com.lptiyu.tanke.pojo.GameDataNormalEntity;
import com.lptiyu.tanke.pojo.GameDataStartEntity;
import com.lptiyu.tanke.widget.CustomTextView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-23
 *         email: wonderfulifeel@gmail.com
 */
public class GameDataViewHolderStart extends BaseViewHolder<GameDataEntity> {

  @BindView(R.id.item_game_data_start_image)
  RoundedImageView roundedImageView;
  @BindView(R.id.item_game_data_start_game_title)
  CustomTextView gameTitle;
  @BindView(R.id.item_game_data_start_game_location)
  CustomTextView gameLocation;
  @BindView(R.id.item_game_data_start_game_complete_num)
  CustomTextView gameCompleteNum;
  @BindView(R.id.item_game_data_task_complete_time)
  CustomTextView gameCompleteTime;

  public GameDataViewHolderStart(ViewGroup parent) {
    super(fromResLayout(parent, R.layout.item_game_data_start));
    ButterKnife.bind(this, itemView);
  }

  @Override
  public void bind(GameDataEntity entity) {
    GameDataStartEntity startEntity = ((GameDataStartEntity) entity);
    Date date = new Date(startEntity.getStartTime());
    gameCompleteTime.setText(String.format(getContext().getString(R.string.complete_time_formatter),
        1900 + date.getYear(),
        1 + date.getMonth(),
        date.getDate(),
        date.getHours(),
        date.getMinutes()));
    //TODO : load info from server
  }
}
