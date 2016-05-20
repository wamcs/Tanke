package com.lptiyu.tanke.base.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;

import com.lptiyu.tanke.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @author ldx
 */
public abstract class ActivityController extends ContextController {

  @BindView(R.id.tool_bar)
  Toolbar mToolbar;

  private View mRootView;

  private AppCompatActivity mActivity;

  public ActivityController(AppCompatActivity activity, View view) {
    super(activity);
    this.mRootView = view;
    this.mActivity = activity;
    if (isToolbarEnable()) {
      ButterKnife.bind(this, view);
    }
    baseInit();
  }

  protected boolean isToolbarEnable() {
    return true;
  }

  public AppCompatActivity getActivity() {
    return mActivity;
  }

  public Window getWindow() {
    return mActivity.getWindow();
  }

  public View findViewById(int resId) {
    return mActivity.findViewById(resId);
  }

  protected void baseInit() {
    if (isToolbarEnable()) {
      initToolbar();
    }
  }

  protected void initToolbar() {
    mActivity.setSupportActionBar(mToolbar);
    mToolbar.setContentInsetsAbsolute(0, 0);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(false);
      actionBar.setDisplayShowHomeEnabled(false);
      actionBar.setHomeButtonEnabled(false);
      actionBar.setDisplayShowCustomEnabled(true);
      actionBar.setDisplayShowTitleEnabled(false);
      actionBar.setHomeAsUpIndicator(null);
    }
  }

  public ActionBar getSupportActionBar() {
    return mActivity.getSupportActionBar();
  }

  public void onPostCreate() {

  }

  public void onStart() {

  }

  public void onResume() {
  }

  public void onPause() {
  }

  public void onStop() {

  }

  public void onSaveInstanceState(Bundle outState) {

  }

  public void onDestroy() {

  }


  public void onActivityResult(int requestCode, int resultCode, Intent data) {

  }

  public void onBackPressed() {

  }

  public View getRootView() {
    return mRootView;
  }

  public void startActivity(Intent intent) {
    mActivity.startActivity(intent);
  }

  public void startActivityForResult(Intent intent, int requestCode) {
    mActivity.startActivityForResult(intent, requestCode);
  }

  public void finish() {
    mActivity.finish();
  }

  public Intent getIntent() {
    return mActivity.getIntent();
  }

  public void performHomeClick() {
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addCategory(Intent.CATEGORY_HOME);
    startActivity(intent);
  }

  public void overridePendingTransition(int enterAnim, int exitAnim) {
    mActivity.overridePendingTransition(enterAnim, exitAnim);
  }
}
