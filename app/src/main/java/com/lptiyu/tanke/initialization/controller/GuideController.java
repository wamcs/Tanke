package com.lptiyu.tanke.initialization.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.initialization.ui.LoginActivity;
import com.lptiyu.tanke.utils.Display;
import com.lptiyu.tanke.widget.CustomTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * author:wamcs
 * date:2016/5/19
 * email:kaili@hustunique.com
 */
public class GuideController extends ActivityController implements ViewPager.OnPageChangeListener {

  @BindView(R.id.splash_activity_viewpager)
  ViewPager mViewPager;
  @BindView(R.id.splash_activity_viewpager_indicator)
  LinearLayout mIndicatorParent;
  @BindView(R.id.splash_activity_textview_skip)
  TextView mSkip;

  private GuidePageScrollAdapter scrollAdapter;
  private ViewPagerAdapter mAdapter;
  private List<ImageView> mIndicators;
  private int mCurrentIndex = 0;


  public GuideController(AppCompatActivity context, View view) {
    super(context, view);
    ButterKnife.bind(this, view);
    initViewPager(context);
    initIndicator();
  }

  private void initViewPager(AppCompatActivity context) {
    scrollAdapter = new GuidePageScrollAdapter(context);
    mAdapter = new ViewPagerAdapter(scrollAdapter.getViews());
    mViewPager.setAdapter(mAdapter);
    mViewPager.addOnPageChangeListener(this);
  }

  private void initIndicator() {
    mIndicators = new ArrayList<>();
    mIndicators.add((ImageView) mIndicatorParent.findViewById(R.id.splash_indicator_1));
    mIndicators.add((ImageView) mIndicatorParent.findViewById(R.id.splash_indicator_2));
    mIndicators.add((ImageView) mIndicatorParent.findViewById(R.id.splash_indicator_3));
    mIndicators.get(0).setEnabled(false);
  }

  @Override
  protected boolean isToolbarEnable() {
    return false;
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    scrollAdapter.onPageScrolled(position, positionOffset);
  }

  @Override
  public void onPageSelected(int position) {
    if (2 == position) {
      mIndicatorParent.setVisibility(View.GONE);
      mSkip.setVisibility(View.GONE);
    } else {
      mIndicatorParent.setVisibility(View.VISIBLE);
      mSkip.setVisibility(View.VISIBLE);
    }
    mIndicators.get(mCurrentIndex).setEnabled(true);
    mCurrentIndex = position;
    mIndicators.get(mCurrentIndex).setEnabled(false);
  }

  @Override
  public void onPageScrollStateChanged(int state) {

  }

  class GuidePageHolder {

    View mRoot;

    @BindView(R.id.layout_splash_cover)
    ImageView mCover;
    @BindView(R.id.layout_splash_title)
    CustomTextView mTitle;
    @BindView(R.id.layout_splash_mid_bar)
    ImageView mMidBar;
    @BindView(R.id.layout_splash_brief)
    CustomTextView mBrief;
    @BindView(R.id.layout_splash_ect)
    ImageView mEct;

    int width = Display.width();

    public GuidePageHolder(View root) {
      mRoot = root;
      ButterKnife.bind(this, mRoot);
    }

    public void bindData(int coverRes, String title, String brief) {
      mCover.setBackgroundResource(coverRes);
      mTitle.setText(title);
      mBrief.setText(brief);
    }

    public void onPageScroll(float offset) {
      mTitle.setTranslationX(offset * width * 0.6f);
      mMidBar.setTranslationX(offset * width * 0.7f);
      mBrief.setTranslationX(offset * width * 0.8f);
      mEct.setTranslationX(offset* width * 0.9f);
    }

  }

  class GuidePageScrollAdapter {

    private View mEndView;
    private SplashActivityClickListener mClickListener;

    private List<View> mViews;
    private List<GuidePageHolder> holders;

    public GuidePageScrollAdapter(Context context) {
      holders = new ArrayList<>(3);
      mViews = new ArrayList<>(3);
      mClickListener = new SplashActivityClickListener();

      View mNormalView1 = LayoutInflater.from(context).inflate(R.layout.layout_guide_normal, null);
      GuidePageHolder holder1 = new GuidePageHolder(mNormalView1);
      holder1.bindData(R.mipmap.img_splash_header_1, getString(R.string.splash_title_1), getString(R.string.splash_brief_1));
      holders.add(holder1);
      mViews.add(mNormalView1);

      View mNormalView2 = LayoutInflater.from(context).inflate(R.layout.layout_guide_normal, null);
      GuidePageHolder holder2 = new GuidePageHolder(mNormalView2);
      holder2.bindData(R.mipmap.img_splash_header_2,getString(R.string.splash_title_2), getString(R.string.splash_brief_2));
      holders.add(holder2);
      mViews.add(mNormalView2);

      mEndView = LayoutInflater.from(context).inflate(R.layout.layout_guide_end, null);
      GuidePageHolder holder3 = new GuidePageHolder(mEndView);
      holder3.bindData(R.mipmap.img_splash_header_3, getString(R.string.splash_title_3), getString(R.string.splash_brief_3));
      holders.add(holder3);
      mViews.add(mEndView);

      mEndView.findViewById(R.id.enter).setOnClickListener(mClickListener);
      mSkip.setOnClickListener(mClickListener);
    }

    public List<View> getViews() {
      return mViews;
    }

    public void onPageScrolled(int position, float positionOffset) {
      holders.get(position).onPageScroll(-positionOffset);
    }

  }

  class ViewPagerAdapter extends PagerAdapter {

    List<View> views;

    public ViewPagerAdapter(List<View> views) {
      this.views = views;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      container.removeView(views.get(position));
    }

    //初始化arg1位置的界面
    @Override
    public Object instantiateItem(View arg0, int arg1) {
      ((ViewPager) arg0).addView(views.get(arg1), 0);
      return views.get(arg1);
    }

    @Override
    public void destroyItem(View container, int position, Object object) {

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      return view == object;
    }

    @Override
    public int getCount() {
      if (null == views) return 0;
      return views.size();
    }
  }

  class SplashActivityClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      switch (v.getId()) {

        case R.id.enter:
          Intent i1 = new Intent();
          i1.setClass(getContext(), LoginActivity.class);
          startActivity(i1);
          finish();
          break;

        case R.id.splash_activity_textview_skip:
          mViewPager.setCurrentItem(3, true);
          break;
      }
    }
  }
}
