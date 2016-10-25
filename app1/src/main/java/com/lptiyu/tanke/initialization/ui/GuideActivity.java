package com.lptiyu.tanke.initialization.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.base.MyBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author:wamcs
 * date:2016/5/19
 * email:kaili@hustunique.com
 */
public class GuideActivity extends MyBaseActivity {

    @BindView(R.id.splash_activity_viewpager)
    ViewPager viewpager;
    @BindView(R.id.rb_blue)
    RadioButton rbBlue;
    @BindView(R.id.rb_orange)
    RadioButton rbOrange;
    @BindView(R.id.rb_green)
    RadioButton rbGreen;
    @BindView(R.id.rg_guide_dot)
    RadioGroup rgGuideDot;
    private int[] imgsArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);

        initData();

        initView();
    }

    private void initData() {
        imgsArr = new int[]{R.drawable.guide_page_1, R.drawable.guide_page_2, R.drawable.guide_page_3};
    }

    private void initView() {
        viewpager.setAdapter(new MyGuidePagerAdatper(this));
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((RadioButton) rgGuideDot.getChildAt(position)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        rbBlue.setChecked(true);
        rgGuideDot.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_blue:
                        viewpager.setCurrentItem(0);
                        break;
                    case R.id.rb_orange:
                        viewpager.setCurrentItem(1);
                        break;
                    case R.id.rb_green:
                        viewpager.setCurrentItem(2);
                        break;
                }
            }
        });
    }

    class MyGuidePagerAdatper extends PagerAdapter {

        private final LayoutInflater inflater;

        public MyGuidePagerAdatper(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return imgsArr.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = inflater.inflate(R.layout.item_vp_guide, container, false);
            ImageView img = (ImageView) view.findViewById(R.id.img);
            TextView tv_skip = (TextView) view.findViewById(R.id.tv_skip);
            TextView tv_enter_now = (TextView) view.findViewById(R.id.tv_enter_now);
            img.setImageResource(imgsArr[position]);

            if (position == 2) {
                tv_enter_now.setVisibility(View.VISIBLE);
                tv_skip.setVisibility(View.GONE);
            } else {
                tv_enter_now.setVisibility(View.GONE);
                tv_skip.setVisibility(View.VISIBLE);
            }

            tv_skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewpager.setCurrentItem(2);
                }
            });
            tv_enter_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(GuideActivity.this, LoginActivity.class);
                    startActivity(intent);
                    GuideActivity.this.finish();
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
