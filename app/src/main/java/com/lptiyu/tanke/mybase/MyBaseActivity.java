package com.lptiyu.tanke.mybase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.lptiyu.tanke.RunApplication;

/**
 * Created by Jason on 2016/8/5.
 */
public class MyBaseActivity extends AppCompatActivity implements IBaseView {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RunApplication.getInstance().addActivity(this);
    }

    @Override
    public void failLoad(String errMsg) {
        if (TextUtils.isEmpty(errMsg)) {
            Toast.makeText(this, "请求失败", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
        }
    }
}
