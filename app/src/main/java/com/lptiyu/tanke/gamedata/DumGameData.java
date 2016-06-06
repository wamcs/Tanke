package com.lptiyu.tanke.gamedata;

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
      addData(10000000000L + i, TimeUtils.ONE_MINUTE_TIME * i, i);
    }
  }

  static void addData(long taskId, long millisConsuming, int exp) {
    GameDataEntity entity = new GameDataEntity();
    entity.setTaskId(taskId);
    entity.setMillisConsuming(millisConsuming);
    entity.setExp(exp);
    entities.add(entity);
  }

}
