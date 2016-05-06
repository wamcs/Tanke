package com.lptiyu.tanke;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.Trace;

import java.util.ArrayList;
import java.util.List;

public class TraceActivity extends AppCompatActivity implements
    OnStartTraceListener,
    OnStopTraceListener {

  LBSTraceClient client;
  Trace trace;
  long serviceId = 115944L;
  String entityName = "xiaoda";
  int traceType = 2;
  int protocolType = 0;

  EditText traceName;
  Button start;
  Button end;
  Button history;
  TextureMapView mapView;
  BaiduMap map;

  // 起点图标覆盖物
  private static MarkerOptions startMarker = null;
  // 终点图标覆盖物
  private static MarkerOptions endMarker = null;
  // 路线覆盖物
  private static PolylineOptions polyline = null;

  private MapStatusUpdate msUpdate = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_trace);
    traceName = (EditText) findViewById(R.id.trace_name);
    start = (Button) findViewById(R.id.start);
    end = (Button) findViewById(R.id.end);
    history = (Button) findViewById(R.id.history);
    mapView = (TextureMapView) findViewById(R.id.map_view);
    map = mapView.getMap();

    client = new LBSTraceClient(getApplicationContext());
    client.setLocationMode(LocationMode.High_Accuracy);
    client.setInterval(2, 2);
    client.setProtocolType(protocolType);


    start.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (traceName.getText() != null && traceName.getText().length() != 0) {
          entityName = traceName.getText().toString();
        }
        trace = new Trace(getApplicationContext(), serviceId, entityName, traceType);
        client.startTrace(trace, TraceActivity.this);
      }
    });

    end.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        client.stopTrace(trace, TraceActivity.this);
      }
    });

    history.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (traceName.getText() != null && traceName.getText().length() != 0) {
          entityName = traceName.getText().toString();
        }
        //是否返回精简的结果（0 : 将只返回经纬度，1 : 将返回经纬度及其他属性信息）
        int simpleReturn = 0;
        //开始时间（Unix时间戳）
        int startTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
        //结束时间（Unix时间戳）
        int endTime = (int) (System.currentTimeMillis() / 1000);
        //分页大小
        int pageSize = 1000;
        //分页索引
        int pageIndex = 1;

        client.queryHistoryTrack(serviceId, entityName, simpleReturn, startTime, endTime, pageSize, pageIndex, new OnTrackListener() {

          @Override
          public void onRequestFailedCallback(String s) {

          }

          @Override
          public void onQueryHistoryTrackCallback(String s) {
            showHistoryTrack(s);
            super.onQueryHistoryTrackCallback(s);
          }

        });
      }
    });
  }

  //开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
  @Override
  public void onTraceCallback(int arg0, String arg1) {

  }

  //轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
  @Override
  public void onTracePushCallback(byte arg0, String arg1) {
  }

  // 轨迹服务停止成功
  @Override
  public void onStopTraceSuccess() {

  }

  // 轨迹服务停止失败（arg0 : 错误编码，arg1 : 消息内容，详情查看类参考）
  @Override
  public void onStopTraceFailed(int arg0, String arg1) {

  }

  /**
   * 显示历史轨迹
   */
  protected void showHistoryTrack(String historyTrack) {

    HistoryTrackData historyTrackData = GsonService.parseJson(historyTrack,
        HistoryTrackData.class);

    List<LatLng> latLngList = new ArrayList<LatLng>();
    if (historyTrackData != null && historyTrackData.getStatus() == 0) {
      if (historyTrackData.getListPoints() != null) {
        latLngList.addAll(historyTrackData.getListPoints());
      }

      // 绘制历史轨迹
      drawHistoryTrack(latLngList, historyTrackData.distance);

    }

  }

  /**
   * 绘制历史轨迹
   *
   * @param points
   */
  private void drawHistoryTrack(final List<LatLng> points, final double distance) {
    // 绘制新覆盖物前，清空之前的覆盖物
    map.clear();

    if (points == null || points.size() == 0) {
      Looper.prepare();
      Toast.makeText(TraceActivity.this, "当前查询无轨迹点", Toast.LENGTH_SHORT).show();
      Looper.loop();
    } else if (points.size() > 1) {
      LatLng llC = points.get(0);
      LatLng llD = points.get(points.size() - 1);
      LatLngBounds bounds = new LatLngBounds.Builder()
          .include(llC).include(llD).build();
      msUpdate = MapStatusUpdateFactory.newLatLngBounds(bounds);
      // 添加路线（轨迹）
      polyline = new PolylineOptions().width(10)
          .color(Color.RED).points(points);
      map.addOverlay(polyline);
      Looper.prepare();
      Toast.makeText(TraceActivity.this, "当前轨迹里程为 : " + (int) distance + "米", Toast.LENGTH_SHORT).show();
      Looper.loop();
    }

  }

}
