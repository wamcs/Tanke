package com.lptiyu.tanke.gamedata;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.io.net.GameService;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.GameDataEntity;
import com.lptiyu.tanke.utils.TimeUtils;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

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
  TextView completeConsumingTime;
  @BindView(R.id.item_game_data_task_exp)
  TextView taskExp;

  public GameDataViewHolder(ViewGroup parent) {
    super(fromResLayout(parent, R.layout.item_game_data_task));
    ButterKnife.bind(this, itemView);
  }

  @Override
  public void bind(final GameDataEntity entity) {
    taskName.setText(entity.getTaskName());
    final Context context = itemView.getContext();

    String completeConsumingTimeFormatter = context.getString(R.string.complete_consuming_time_formatter);
    Date date = new Date(entity.getCompleteConsumingTime());
    completeConsumingTime.setText(String.format(completeConsumingTimeFormatter, date.getMinutes(), date.getSeconds()));

    String expFormatter = context.getString(R.string.get_exp_formatter);
    taskExp.setText(String.format(expFormatter, entity.getExp()));

    String completeTimeFormatter = context.getString(R.string.complete_time_formatter);
    date.setTime(entity.getCompleteTime());
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
    HttpService.getGameService().getTaskFinishedNum(entity.getTaskId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Response<Integer>>() {
          @Override
          public void call(Response<Integer> integerResponse) {
            String completePersonNumFormatter = context.getString(R.string.complete_person_num_formatter);
            if (integerResponse == null || integerResponse.getStatus() != 1) {
              completePersonNum.setText(String.format(completePersonNumFormatter, 0));
              return;
            }
            entity.setCompletePersonNum(integerResponse.getData());
            completePersonNum.setText(String.format(completePersonNumFormatter, entity.getCompletePersonNum()));
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            entity.setCompletePersonNum(0);
            completePersonNum.setText(String.format(context.getString(R.string.complete_person_num_formatter), entity.getCompletePersonNum()));
          }
        });
  }

  public void bind(List<GameDataEntity> entities) {
    for (GameDataEntity entity : entities) {
      bind(entity);
    }
  }

}
