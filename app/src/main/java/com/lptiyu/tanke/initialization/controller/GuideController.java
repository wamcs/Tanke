package com.lptiyu.tanke.initialization.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.initialization.ui.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author:wamcs
 * date:2016/5/19
 * email:kaili@hustunique.com
 */
public class GuideController extends ActivityController implements ViewPager.OnPageChangeListener{

    @BindView(R.id.guide_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.guide_skip_button)
    TextView mSkipView;
    @BindView(R.id.guide_viewpager_indicator)
    LinearLayout mIndicator;

    private List<ImageView> mIndicatorList;
    private int mCurrentPosition = 0;


    public GuideController(AppCompatActivity activity, View view) {
        super(activity, view);
        ButterKnife.bind(this,view);
        initImage(activity);
        initIndicator();
    }

    private void initImage(Context context){
        List<View> mImageList = new ArrayList<>();
        GuideClickListener clickListener = new GuideClickListener();
        for (int i=0; i<2; i++){
            ImageView v =new ImageView(context);
            v.setScaleType(ImageView.ScaleType.CENTER_CROP);
            switch (i){
                case 0:
                    v.setBackgroundResource(R.mipmap.splash_activity_bg_1);
                    break;
                case 1:
                    v.setBackgroundResource(R.mipmap.splash_activity_bg_2);
                    break;

            }
            mImageList.add(v);
        }

        View mEndView = LayoutInflater.from(context).inflate(R.layout.layout_guide_end, null);
        Button mEndButton = (Button) mEndView.findViewById(R.id.guide_next_button);
        mEndButton.setOnClickListener(clickListener);
        mSkipView.setOnClickListener(clickListener);
        mImageList.add(mEndView);


        ViewAdapter adapter =new ViewAdapter(mImageList);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(this);


    }

    private void initIndicator(){
        mIndicatorList =new ArrayList<>();
        mIndicatorList.add((ImageView) mIndicator.findViewById(R.id.guide_indicator_1));
        mIndicatorList.add((ImageView) mIndicator.findViewById(R.id.guide_indicator_2));
        mIndicatorList.get(0).setEnabled(false);
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }


    @Override
    public void onPageSelected(int position) {
        if (position == 2){
            mSkipView.setVisibility(View.GONE);
            mIndicator.setVisibility(View.GONE);
            return;
        }else {
            mSkipView.setVisibility(View.VISIBLE);
            mIndicator.setVisibility(View.VISIBLE);
        }

        mIndicatorList.get(mCurrentPosition).setEnabled(true);
        mCurrentPosition = position;
        mIndicatorList.get(mCurrentPosition).setEnabled(false);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class GuideClickListener implements View.OnClickListener {


        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.guide_next_button:
                    Intent intent =new Intent();
                    intent.setClass(getContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.guide_skip_button:
                    mViewPager.setCurrentItem(2);
                    break;
            }
        }
    }

    class ViewAdapter extends PagerAdapter{

        private List<View> imageList;

        public ViewAdapter(List<View> views){
            imageList = views;
        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imageList.get(position));
            return imageList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageList.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
