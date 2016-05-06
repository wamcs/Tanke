package com.lptiyu.tanke;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lptiyu.zxinglib.android.CaptureActivity;

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

  @OnClick(R.id.scanner) public void onClick() {
    Timber.d("OnClick");
    startActivity(new Intent(this, CaptureActivity.class));
  }

}
