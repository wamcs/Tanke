package com.lptiyu.tanke.gameplaying.task;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.controller.FragmentController;
import com.lptiyu.tanke.base.ui.BaseFragment;
import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MultiplyTaskFragment extends BaseFragment {

  @BindView(R.id.web_view)
  WebView mWebView;
  @BindView(R.id.task_answer_area)
  RelativeLayout mAnswerArea;

  private int taskIndex;
  private Task mTask;
  private BaseTaskController mActivityController;

  public MultiplyTaskFragment() {
  }

  public static MultiplyTaskFragment newInstance() {
    MultiplyTaskFragment fragment = new MultiplyTaskFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_multiply_task, container, false);
    ButterKnife.bind(this, view);
    taskIndex = FragmentPagerItem.getPosition(savedInstanceState);
    init();
    return view;
  }

  private void init() {
    ActivityController activityController = getActivityController();
    if (!(activityController instanceof BaseTaskController)) {
      throw new RuntimeException(activityController.toString()
          + " must be the subclass of BaseTaskController");
    }
    mActivityController = (BaseTaskController) activityController;
    mTask = mActivityController.getTaskAtPosition(taskIndex);
    initWebView();
    initTask();
  }

  private void initWebView() {
    WebSettings webSettings = mWebView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setAllowFileAccess(true);
    webSettings.setLoadsImagesAutomatically(true);
    mWebView.setWebViewClient(new WebViewClient());
  }

  private void initTask() {

  }

  @Override
  public FragmentController getController() {
    return null;
  }

}
