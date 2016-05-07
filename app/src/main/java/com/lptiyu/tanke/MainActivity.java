package com.lptiyu.tanke;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.TextureMapView;
import com.lptiyu.tanke.trace.ITraceHelper;
import com.lptiyu.tanke.trace.TraceCallback;
import com.lptiyu.tanke.trace.TraceHelper;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.zxinglib.android.CaptureActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity implements TraceCallback {

  private ITraceHelper helper;

  @BindView(R.id.entity_name)
  EditText entityName;
  @BindView(R.id.map)
  TextureMapView mapView;

  private BaiduMap map;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    Timber.d("onCreate");

    helper = new TraceHelper(getApplicationContext(), this);
    helper.interval(2, 10);

    initMap();

  }

  private void initMap() {
    map = mapView.getMap();
  }

  @Override
  public void onTraceStart() {
    ToastUtil.TextToast("onTraceStart");
  }

  @Override
  public void onTracePush(byte b, String s) {
    ToastUtil.TextToast(s);
  }

  @Override
  public void onTraceStop() {
    ToastUtil.TextToast("onTraceStop");
  }

  @OnClick(R.id.scanner)
  public void onClick() {
    startActivity(new Intent(this, CaptureActivity.class));
  }

  @OnClick(R.id.start_trace)
  public void onStartTraceClick() {
    if (entityName.getText() != null && entityName.getText().length() != 0) {
      helper.entityName(entityName.getText().toString());
    }
    helper.start();
  }

  @OnClick(R.id.stop_trace)
  public void onStopTraceClick() {
    helper.stop();
  }

  @OnClick(R.id.history_trace)
  public void onHistoryTraceClick() {

  }

  @OnClick(R.id.current_trace)
  public void onCurrentTraceClick() {

  }

}
