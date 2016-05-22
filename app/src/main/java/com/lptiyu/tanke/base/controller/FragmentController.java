package com.lptiyu.tanke.base.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import butterknife.ButterKnife;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 15/11/22
 *
 * @author ldx
 */
public abstract class FragmentController extends ContextController {

  private Fragment mFragment;

  public FragmentController(Fragment fragment, ActivityController controller, View view) {
    super(controller.getContext());
    mFragment = fragment;
    ButterKnife.bind(this, view);
  }

  public void onResume() {

  }

  public void onPause() {

  }

  public void onDestroy() {

  }

  public void onSaveInstanceState(Bundle outState) {

  }

  public void onDetach() {

  }

  public void onDestroyView() {

  }

  public Fragment getFragment() {
    return mFragment;
  }

  public void startActivity(Intent intent) {
    mFragment.startActivity(intent);
  }

  public void startActivity(Intent intent, @Nullable Bundle bundle) {
    mFragment.startActivity(intent, bundle);
  }

  public void startActivityForResult(Intent intent, int requestCode) {
    mFragment.startActivityForResult(intent, requestCode);
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {

  }

  public FragmentManager getChildFragmentManager() {
    return mFragment.getChildFragmentManager();
  }

  public FragmentManager getFragmentManager() {
    return mFragment.getFragmentManager();
  }

}
