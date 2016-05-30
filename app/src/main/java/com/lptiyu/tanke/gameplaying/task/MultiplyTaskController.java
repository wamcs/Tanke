package com.lptiyu.tanke.gameplaying.task;

import android.support.v4.app.Fragment;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.controller.FragmentController;
import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-30
 *         email: wonderfulifeel@gmail.com
 */
public abstract class MultiplyTaskController extends FragmentController {

  @BindView(R.id.web_view)
  WebView mWebView;
  @BindView(R.id.task_answer_area)
  RelativeLayout mAnswerArea;

  int taskIndex;
  Task mTask;
  BaseTaskController mActivityController;

  public MultiplyTaskController(Fragment fragment, ActivityController controller, View view) {
    super(fragment, controller, view);
    ButterKnife.bind(this, view);
    if (!(controller instanceof BaseTaskController)) {
      throw new RuntimeException(controller.toString()
          + " must be the subclass of BaseTaskController");
    }
    mActivityController = (BaseTaskController) controller;
    taskIndex = FragmentPagerItem.getPosition(fragment.getArguments());
    init();
  }

  private void init() {
    mTask = mActivityController.getTaskAtPosition(taskIndex);
    initWebView();
    updateTaskDisplay();
  }

  private void initWebView() {
    WebSettings webSettings = mWebView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setAllowFileAccess(true);
    webSettings.setLoadsImagesAutomatically(true);
    mWebView.setWebViewClient(new WebViewClient());
  }

  public void updateTaskDisplay() {
    mWebView.loadUrl(mTask.getContent());
    initTaskView();
  }

  public abstract void initTaskView();
}
