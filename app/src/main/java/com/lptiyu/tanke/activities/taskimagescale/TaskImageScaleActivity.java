package com.lptiyu.tanke.activities.taskimagescale;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.widget.ZoomImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskImageScaleActivity extends AppCompatActivity {

    @BindView(R.id.zoomImg)
    ZoomImageView zoomImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams
                .FLAG_FULLSCREEN);//去掉信息栏

        setContentView(R.layout.activity_task_image_scale);
        ButterKnife.bind(this);

        String imgUrl = getIntent().getStringExtra(Conf.TASK_IMG);
        Glide.with(this).load(imgUrl).error(R.drawable.ic_launcher).placeholder(R.drawable.default_pic).into(zoomImg);
    }
}
