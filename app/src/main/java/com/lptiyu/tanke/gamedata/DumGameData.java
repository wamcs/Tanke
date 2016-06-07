package com.lptiyu.tanke.gamedata;

import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.lptiyu.tanke.pojo.GameDataEntity;
import com.lptiyu.tanke.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-6
 *         email: wonderfulifeel@gmail.com
 */
public class DumGameData {

  public static List<GameDataEntity> entities = new ArrayList<>();

  static {
    for (int i = 10; i < 20; i++) {
      addData("小桥流水", Task.TASK_TYPE.SCAN_CODE, i + 123, System.currentTimeMillis(), i * TimeUtils.ONE_MINUTE_TIME, i + 5);
    }
  }

  static void addData(String taskName, Task.TASK_TYPE type, int completePersonNum, long completeTime, long completeComsumingTime, int exp) {
    GameDataEntity entity = new GameDataEntity();
    entity.setTaskName(taskName);
    entity.setType(type);
    entity.setCompletePersonNum(completePersonNum);
    entity.setCompleteTime(completeTime);
    entity.setCompleteComsumingTime(completeComsumingTime);
    entity.setExp(exp);
    entities.add(entity);
  }

}
