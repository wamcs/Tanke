package com.lptiyu.tanke.gameplaying.task;

import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.controller.FragmentController;
import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.lptiyu.tanke.gameplaying.records.RunningRecord;
import com.lptiyu.tanke.utils.ToastUtil;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

import java.util.List;

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
  @BindView(R.id.seal_not_open)
  ImageView mSealNotOpen;
  @BindView(R.id.seal_finished)
  ImageView mSealFinished;

  AlertDialog mLoadingDialog;

  int taskIndex;
  Task mTask;
  List<RunningRecord> recordList;
  GameTaskController mActivityController;

  public MultiplyTaskController(Fragment fragment, ActivityController controller, View view) {
    super(fragment, controller, view);
    ButterKnife.bind(this, view);
    if (!(controller instanceof GameTaskController)) {
      throw new RuntimeException(controller.toString()
          + " must be the subclass of GameTaskController");
    }
    mActivityController = (GameTaskController) controller;
    taskIndex = FragmentPagerItem.getPosition(fragment.getArguments());
    init();
  }

  private void init() {
    mTask = mActivityController.getTaskAtPosition(taskIndex);
    initLoadingDialog();
    mLoadingDialog.show();
    initWebView();
    if (taskIndex == 0) {
      openSealAndInitTask();
    }
    checkAndResumeTaskStatus();
  }

  private void initWebView() {
    WebSettings webSettings = mWebView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setAllowFileAccess(true);
    webSettings.setLoadsImagesAutomatically(true);
    mWebView.setWebViewClient(new WebViewClient());
  }

  private void checkAndResumeTaskStatus() {
    recordList = mActivityController.getAppropriateRecordList();
    for (RunningRecord record : recordList) {
      switch (record.getType()) {

        case GAME_START:
          break;

        case POINT_REACH:
          break;

        case TASK_START:
          if (mTask.getId() == record.getTaskId()) {
            openSealAndInitTask();
          }
          break;

        case TASK_FINISH:
          if (mTask.getId() == record.getTaskId()) {
            finishTask();
          }
          break;

        case POINT_FINISH:

          break;

        case GAME_FINISH:
          break;
      }
    }
    mLoadingDialog.dismiss();
  }

  public void openSealAndInitTask() {
    mSealNotOpen.setVisibility(View.GONE);
    mAnswerArea.setVisibility(View.VISIBLE);
    initTaskView();
  }

  public void finishTask() {
    mSealFinished.setVisibility(View.VISIBLE);
    mAnswerArea.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ToastUtil.TextToast("此任务您已完成");
      }
    });
  }

  private void initLoadingDialog() {
    if (mLoadingDialog != null) {
      return;
    }
    View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, null);
    TextView textView = ((TextView) view.findViewById(R.id.loading_dialog_textview));
    textView.setText(getString(R.string.loading));
    mLoadingDialog = new AlertDialog.Builder(getFragment().getActivity())
        .setCancelable(false)
        .setView(view)
        .create();
  }

  public abstract void initTaskView();
}
