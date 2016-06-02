package com.lptiyu.tanke.gameplaying.task;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.controller.FragmentController;
import com.lptiyu.tanke.base.ui.BaseFragment;
import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

public class MultiplyTaskFragment extends BaseFragment {

  private FragmentController mFragmentController;

  public MultiplyTaskFragment() {
  }

  public static MultiplyTaskFragment newInstance() {
    MultiplyTaskFragment fragment = new MultiplyTaskFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_multiply_task, container, false);
    ActivityController activityController = getActivityController();
    if (!(activityController instanceof GameTaskController)) {
      throw new RuntimeException(activityController.toString()
          + " must be the subclass of GameTaskController");
    }
    GameTaskController mActivityController = (GameTaskController) activityController;
    Task mTask = mActivityController.getTaskAtPosition(FragmentPagerItem.getPosition(getArguments()));
    switch (mTask.getType()) {
      case SCAN_CODE:
        mFragmentController = new ScanTaskController(this, activityController, view);
        break;
      case LOCATE:
        mFragmentController = new LocateTaskController(this, activityController, view);
        break;
      case RIDDLE:
        mFragmentController = new RiddleTaskController(this, activityController, view);
        break;
      case DISTINGUISH:
        mFragmentController = new DistinguishTaskController(this, activityController, view);
        break;
      case TIMING:
        mFragmentController = new TimingTaskController(this, activityController, view);
        break;
      case FINISH:
        mFragmentController = new FinishTaskController(this, activityController, view);
        break;
    }
    return view;
  }

  @Override
  public FragmentController getController() {
    return mFragmentController;
  }

}
