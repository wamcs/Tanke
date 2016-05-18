package com.lptiyu.tanke;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.TextureMapView;
import com.lptiyu.tanke.trace.bean.HistoryTrackData;
import com.lptiyu.tanke.trace.history.HistoryTrackCallback;
import com.lptiyu.tanke.trace.history.HistoryTrackHelper;
import com.lptiyu.tanke.trace.history.IHistoryTrackHelper;
import com.lptiyu.tanke.trace.realtime.IRealTimeTrackHelper;
import com.lptiyu.tanke.trace.realtime.RealTimeTrackCallback;
import com.lptiyu.tanke.trace.realtime.RealTimeTrackHelper;
import com.lptiyu.tanke.trace.tracing.ITracingHelper;
import com.lptiyu.tanke.trace.tracing.TracingCallback;
import com.lptiyu.tanke.trace.tracing.TracingHelper;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.zxinglib.android.CaptureActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    Timber.d("onCreate");
  }

  @OnClick(R.id.scanner)
  public void onClick() {
    startActivity(new Intent(this, CaptureActivity.class));
  }

}
