package com.lptiyu.tanke;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

  private Button locate;
  private Button trace;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    locate = (Button) findViewById(R.id.map);
    trace = (Button) findViewById(R.id.trace);

    locate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i1 = new Intent();
        i1.setClass(MainActivity.this, MapActivity.class);
        startActivity(i1);
      }
    });

    trace.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i1 = new Intent();
        i1.setClass(MainActivity.this, TraceActivity.class);
        startActivity(i1);
      }
    });
  }

}
