package com.lptiyu.tanke.activities.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.lptiyu.tanke.RunApplication;

/**
 * Created by Jason on 2016/8/5.
 */
public class MyBaseActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        RunApplication.getInstance().addActivity(this);
    }
}
