package com.lptiyu.tanke;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lptiyu.tanke.permission.PermissionDispatcher;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

  }

  @OnClick(R.id.scanner)
  public void onClick() {
    PermissionDispatcher.showCameraWithCheck(this);
  }

}
