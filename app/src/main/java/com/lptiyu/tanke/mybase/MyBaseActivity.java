package com.lptiyu.tanke.mybase;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.lptiyu.tanke.RunApplication;

/**
 * Created by Jason on 2016/8/5.
 */
public class MyBaseActivity extends AppCompatActivity implements IBaseView {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        RunApplication.getInstance().addActivity(this);
    }

    @Override
    public void failLoad() {
        Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void failLoad(String errMsg) {
        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void netException() {

    }
}
