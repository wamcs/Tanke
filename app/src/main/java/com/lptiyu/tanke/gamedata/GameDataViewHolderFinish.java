package com.lptiyu.tanke.gamedata;

import android.view.ViewGroup;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.pojo.GameDataEntity;
import com.lptiyu.tanke.pojo.GameDataFinishEntity;
import com.lptiyu.tanke.pojo.GameDataNormalEntity;
import com.lptiyu.tanke.pojo.GameFinishedEntity;
import com.lptiyu.tanke.widget.CustomTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-23
 *         email: wonderfulifeel@gmail.com
 */
public class GameDataViewHolderFinish extends BaseViewHolder<GameDataEntity> {

  @BindView(R.id.item_game_data_finish_complete_person_num)
  CustomTextView mCompletePersonNum;
  @BindView(R.id.item_game_data_finish_complete_time)
  CustomTextView mCompleteTime;
  @BindView(R.id.item_game_data_finish_comsume_time)
  CustomTextView mConsumeTime;
  @BindView(R.id.item_game_data_finish_total_exp)
  CustomTextView mTotalExp;
  @BindView(R.id.item_game_data_finish_progress_left)
  CustomTextView mProgressLeft;
  @BindView(R.id.item_game_data_finish_progress_right)
  CustomTextView mProgressRight;
  @BindView(R.id.item_game_data_finish_progress_exp_detail)
  CustomTextView mExpDetail;

  public GameDataViewHolderFinish(ViewGroup parent) {
    super(fromResLayout(parent, R.layout.item_game_data_finish));
    ButterKnife.bind(this, itemView);
  }

  @Override
  public void bind(GameDataEntity entity) {
    GameDataFinishEntity finishedEntity = ((GameDataFinishEntity) entity);
    mTotalExp.setText(String.format(getContext().getString(R.string.get_exp_formatter), finishedEntity.getTotalExp()));

  }

}
