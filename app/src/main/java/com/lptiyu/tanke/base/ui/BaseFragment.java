package com.lptiyu.tanke.base.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.controller.BaseController;
import com.lptiyu.tanke.base.controller.ControllerHolder;
import com.lptiyu.tanke.base.controller.FragmentController;


/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 15/11/22
 *
 * @author ldx
 */
public abstract class BaseFragment extends Fragment implements ControllerHolder {


  private ActivityController mActivityController;

  public BaseFragment() {

  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof ControllerHolder) {
      BaseController controller = ((ControllerHolder) context).getController();
      if (controller instanceof ActivityController) {
        mActivityController = (ActivityController) controller;
      } else {
        System.err.println("controller = [" + controller + "] is not ActivityController.");
      }
    } else {
      System.err.println(context.toString()
          + " must implement ControllerHolder");
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (null != getController()) {
      getController().onResume();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (null != getController()) {
      getController().onPause();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (null != getController()) {
      getController().onDestroy();
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    if (null != getController()) {
      getController().onDetach();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (null != getController()) {
      getController().onSaveInstanceState(outState);
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (null != getController()) {
      getController().onDestroyView();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (null != getController()) {
      getController().onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (null != getController()) {
      getController().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  public ActivityController getActivityController() {
    if (mActivityController == null) {
      throw new RuntimeException("Must be called after Fragment.attach()");
    }
    return mActivityController;
  }

  public abstract FragmentController getController();

}
