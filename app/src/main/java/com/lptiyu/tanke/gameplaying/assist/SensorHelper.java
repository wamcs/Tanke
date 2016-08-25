package com.lptiyu.tanke.gameplaying.assist;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-23
 *         email: wonderfulifeel@gmail.com
 */
public class SensorHelper implements SensorEventListener {

  private SensorManager mSensorManager;
  private Sensor accelerometerSensor;
  private Sensor magneticSensor;

  float[] accelerometerValues = new float[3];
  float[] magneticFieldValues = new float[3];

  private float currentDegree = 0.0f;

  public SensorHelper(Context context) {
    mSensorManager = ((SensorManager) context.getSystemService(Context.SENSOR_SERVICE));
    accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    magneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
  }


  public void onResume() {
    registerSensorListener();
  }

  public void onPause() {
    unRegisterSensorListener();
  }

  public void onDestroy() {
    unRegisterSensorListener();
  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
      magneticFieldValues = sensorEvent.values;
    if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
      accelerometerValues = sensorEvent.values;
    calculateOrientation();
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }

  private void calculateOrientation() {
    float[] values = new float[3];
    float[] R = new float[9];
    SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
    SensorManager.getOrientation(R, values);
    // 要经过一次数据格式的转换，转换为度
    values[0] = (float) Math.toDegrees(values[0]);
    //values[1] = (float) Math.toDegrees(values[1]);
    //values[2] = (float) Math.toDegrees(values[2]);
    currentDegree = values[0];
  }

  private void registerSensorListener() {
    mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    mSensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL
    );
  }

  private void unRegisterSensorListener() {
    mSensorManager.unregisterListener(this, accelerometerSensor);
    mSensorManager.unregisterListener(this, magneticSensor);
  }

  public float getCurrentDegree() {
    return currentDegree;
  }


}
