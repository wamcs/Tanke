package com.lptiyu.tanke.gamedata;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.pojo.GameDataEntity;
import com.lptiyu.tanke.utils.TimeUtils;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-6
 *         email: wonderfulifeel@gmail.com
 */
public class GameDataViewHolder extends BaseViewHolder<GameDataEntity> {

  @BindView(R.id.item_game_data_task_name)
  TextView taskName;
  @BindView(R.id.item_game_data_task_type)
  TextView taskType;
  @BindView(R.id.item_game_data_task_complete_person_num)
  TextView completePersonNum;
  @BindView(R.id.item_game_data_task_complete_time)
  TextView completeTime;
  @BindView(R.id.item_game_data_task_complete_consuming_time)
  TextView completeComsumingTime;
  @BindView(R.id.item_game_data_task_exp)
  TextView taskExp;

  public GameDataViewHolder(ViewGroup parent) {
    super(fromResLayout(parent, R.layout.item_game_data_task));
    ButterKnife.bind(this, itemView);
  }

  @Override
  public void bind(GameDataEntity entity) {
    taskName.setText(entity.getTaskName());
    completeComsumingTime.setText(TimeUtils.getFriendlyTime(entity.getCompleteComsumingTime()));
    taskExp.setText(String.valueOf(entity.getExp()));

    Context context = itemView.getContext();
    String completePersonNumFormatter = context.getString(R.string.complete_person_num);
    completePersonNum.setText(String.format(completePersonNumFormatter, entity.getCompletePersonNum()));

    String completeTimeFormatter = context.getString(R.string.complete_time);
    Date date = new Date(entity.getCompleteTime());
    completeTime.setText(String.format(completeTimeFormatter, date.getYear() + 1900, date.getMonth() + 1, date.getDate(), date.getHours(), date.getMinutes()));
    String typeStr = "扫码任务";
    switch (entity.getType()) {
      case SCAN_CODE:
        typeStr = "扫码任务";
        break;
      case LOCATE:
        typeStr = "定位任务";
        break;
      case RIDDLE:
        typeStr = "谜底任务";
        break;
      case DISTINGUISH:
        typeStr = "扫描任务";
        break;
      case TIMING:
        typeStr = "定时任务";
        break;
      case FINISH:
        typeStr = "结束任务";
        break;
    }
    taskType.setText(typeStr);
  }

  @Override
  public void bind(List<GameDataEntity> entities) {
    for (GameDataEntity entity : entities) {
      bind(entity);
    }
  }

}
