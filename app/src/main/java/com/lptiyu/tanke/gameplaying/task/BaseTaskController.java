package com.lptiyu.tanke.gameplaying.task;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.lptiyu.tanke.gameplaying.records.MemRecords;
import com.lptiyu.tanke.gameplaying.records.RecordsHandler;
import com.lptiyu.tanke.gameplaying.records.RunningRecord;
import com.lptiyu.tanke.global.Conf;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-28
 *         email: wonderfulifeel@gmail.com
 */
public class BaseTaskController extends ActivityController {

  @BindView(R.id.default_tool_bar_textview)
  TextView mToolbarTitle;
  @BindView(R.id.web_view)
  WebView mWebView;
  @BindView(R.id.task_answer_area)
  RelativeLayout mAnswerArea;

  private long teamId;
  private long gameId;
  private long DEFAULT_TEAM_ID = -1L;
  private long DEFAULT_GAME_ID = -1L;

  private AlertDialog mLoadingDialog;
  private AlertDialog mExitDialog;
  private RecordsHandler mRecordsHandler;

  private Point mPoint;
  private Task currentTask;
  private int currentTaskIndex = 0;
  private List<String> taskIds;
  private Map<String, Task> taskMap;

  public BaseTaskController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);

    WebSettings webSettings = mWebView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setAllowFileAccess(true);
    webSettings.setLoadsImagesAutomatically(true);
    mWebView.setWebViewClient(new WebViewClient());

    initLoadingDialog();
    mLoadingDialog.show();

    Intent intent = getIntent();
    gameId = intent.getLongExtra(Conf.GAME_ID, DEFAULT_GAME_ID);
    teamId = intent.getLongExtra(Conf.TEAM_ID, DEFAULT_TEAM_ID);
    mPoint = intent.getParcelableExtra(Conf.CLICKED_POINT);

    if (mPoint != null) {
      taskIds = mPoint.getTaskId();
      taskMap = mPoint.getTaskMap();
      if (taskIds != null && taskMap != null) {
        init();
      }
    }
  }

  private void init() {
    currentTask = taskMap.get(taskIds.get(currentTaskIndex));
    mRecordsHandler = new RecordsHandler.Builder(gameId, teamId).build();
    updateTaskDisplay();
    checkAndResumeTaskStatus();
  }

  private void checkAndResumeTaskStatus() {
    MemRecords memRecords = mRecordsHandler.getMemRecords();
    if (memRecords == null) {
      return;
    }
    List<RunningRecord> allRecords = memRecords.getAll();
    int recordStartIndex = findCurrentPointReachIndex(allRecords);
    List<RunningRecord> runningRecordList = allRecords.subList(recordStartIndex, allRecords.size());
    for (RunningRecord record : runningRecordList) {
      switch (record.getType()) {

        case GAME_START:
          // no sense
          break;

        case POINT_REACH:

          break;

        case TASK_START:

          break;

        case TASK_FINISH:
          if (currentTask.getId() == record.getTaskId()) {
            if (currentTaskIndex < taskIds.size()) {
              currentTaskIndex++;
              currentTask = taskMap.get(taskIds.get(currentTaskIndex));
              updateTaskDisplay();
            }
          }
          break;

        case GAME_FINISH:

          break;

      }
    }
    mLoadingDialog.dismiss();
  }

  private void updateTaskDisplay() {
    switch (currentTask.getType()) {

      case SCAN_CODE:
        String url = currentTask.getContent();
        mWebView.loadUrl(url);
        Timber.e("here");
        break;

      case LOCATE:

        break;

      case RIDDLE:

        break;

      case DISTINGUISH:

        break;

      case TIMING:

        break;

      case FINISH:

        break;
    }
  }

  private int findCurrentPointReachIndex(List<RunningRecord> memRecords) {
    int result = memRecords.size();
    for (RunningRecord r : memRecords) {
      if (r.getPointId() == mPoint.getId()) {
        result = memRecords.indexOf(r);
        break;
      }
    }
    return result;
  }

  private void initLoadingDialog() {
    if (mLoadingDialog != null) {
      return;
    }
    View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, null);
    mLoadingDialog = new AlertDialog.Builder(getActivity())
        .setOnKeyListener(new DialogInterface.OnKeyListener() {
          @Override
          public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.KEYCODE_BACK) {
              showExitDialog(getString(R.string.exit_task_activity_when_loading));
              return true;
            }
            return false;
          }
        })
        .setView(view)
        .create();
  }

  private void showExitDialog(String str) {
    if (mExitDialog == null) {
      mExitDialog = new AlertDialog.Builder(getActivity())
          .setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              finish();
            }
          })
          .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              mExitDialog.dismiss();
            }
          })
          .create();
    }
    mExitDialog.setMessage(str);
    mExitDialog.show();
  }

  @OnClick(R.id.default_tool_bar_imageview)
  void back() {
    if (currentTaskIndex >= taskIds.size()) {
      finish();
    } else {
      showExitDialog(getString(R.string.exit_task_activity_when_doing));
    }
  }

  @Override
  public void onBackPressed() {
    if (mLoadingDialog == null) {
      return;
    }
    back();
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }
}
