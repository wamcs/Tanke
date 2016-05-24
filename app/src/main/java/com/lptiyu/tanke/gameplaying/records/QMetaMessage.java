package com.lptiyu.tanke.gameplaying.records;


import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 记录了一次活动中的基本信息。
 * 同时负责根据现有信息，以及传入信息，进行计算，并更新现有信息
 * <p/>
 * <p/>
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/3/13
 *
 * @author ldx
 */
public class QMetaMessage implements Serializable {

  // 截止上一个Spot为止，每个Spot之间所用的时间间隔
  private List<Long> times = new ArrayList<>();

  // 距离上一个点的时间 单位毫秒 通过System.currentTimeMillis()的差获得
  private long spotTime;

  // 截止上一个Spot为止，每个Spot之间所用的距离间隔
  private List<Float> distances = new ArrayList<>();

  // 距离上一个点的距离 单位meter
  private float spotDistance;

  private double lastSpotSiteX = Double.MIN_VALUE;

  private double lastSpotSiteY = Double.MIN_VALUE;

  private long lastSpotTime;

  public void clear() {
    times.clear();
    distances.clear();
    spotDistance = 0;
    lastSpotSiteY = lastSpotSiteX = Double.MIN_VALUE;
    lastSpotTime = 0;
  }

  /**
   * 将每个收到的record交给我，对当前数据进行更新>
   *
   * @param record
   */
  public void update(RunningRecord record) {

    updateSpotSite(record);
    updateSpotTime(record);

    if (record.getType() == RunningRecord.Type.OnSpotCompleted.type) {
      if (spotTime != 0) {
        store();
      }
    } else if (record.getType() == RunningRecord.Type.OnFinish.type) {
      if (spotTime != 0) {
        store();
      }
    }
  }

  /**
   * 根据运动过程中保存的信息，计算所有的时间
   *
   * @param metaMessage 倍计算的MetaMessage
   * @return 返回时间间隔，单位 毫秒
   */
  public static long getAllTime(QMetaMessage metaMessage) {
    List<Long> times = metaMessage.times;
    long allTime = 0l;
    for (Long time : times) {
      allTime += time;
    }
    return allTime + metaMessage.spotTime;
  }

  /**
   * 根据运动过程中保存的信息，计算所有的距离。
   *
   * @param metaMessage 被计算的metaMessage
   * @return 返回距离，单位 米
   */
  public static float getAllDistance(QMetaMessage metaMessage) {
    List<Float> distances = metaMessage.distances;
    float allDistance = 0f;
    for (Float distance : distances) {
      allDistance += distance;
    }
    return allDistance + metaMessage.spotDistance;
  }

  /**
   * 返回每个点之间经历的时间
   *
   * @return 返回一个List，包含到达每个点之间，经历的时间
   */
  public List<Long> getTimes() {
    return times;
  }

  /**
   * 返回每个点之间的距离
   *
   * @return 返回一个List，包含到达每个点之间，走的距离。
   */
  public List<Float> getDistances() {
    return distances;
  }

  public File save(File file) throws IOException {
    ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
    outputStream.writeObject(this);
    return file;
  }

  public static QMetaMessage read(File file) throws IOException, ClassNotFoundException {
    ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
    return (QMetaMessage) inputStream.readObject();
  }

  private void store() {
    times.add(spotTime);
    distances.add(spotDistance);
    spotTime = 0;
    spotDistance = 0;
  }

  private void updateSpotTime(RunningRecord record) {
    spotTime += (record.getTime() - lastSpotTime);
    lastSpotTime = record.getTime();
  }

  private void updateSpotSite(RunningRecord record) {
    if (record.getType() == RunningRecord.Type.Normal.type) {
      double rX = Double.valueOf(record.getX());
      double rY = Double.valueOf(record.getY());
      spotDistance +=
          lastSpotSiteX == Double.MIN_VALUE || lastSpotSiteY == Double.MIN_VALUE ? 0 :
              DistanceUtil.getDistance(new LatLng(lastSpotSiteX, lastSpotSiteY), new LatLng(rX, rY));
      lastSpotSiteX = rX;
      lastSpotSiteY = rY;
    }
  }

}
