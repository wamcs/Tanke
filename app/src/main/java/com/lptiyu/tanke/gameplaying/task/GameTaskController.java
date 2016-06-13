package com.lptiyu.tanke.gameplaying.task;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.gameplaying.pojo.GAME_ACTIVITY_FINISH_TYPE;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.lptiyu.tanke.gameplaying.records.MemRecords;
import com.lptiyu.tanke.gameplaying.records.RecordsHandler;
import com.lptiyu.tanke.gameplaying.records.RecordsUtils;
import com.lptiyu.tanke.gameplaying.records.RunningRecord;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.utils.ToastUtil;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-28
 *         email: wonderfulifeel@gmail.com
 */
public class GameTaskController extends ActivityController {

  @BindView(R.id.default_tool_bar_textview)
  TextView mToolbarTitle;
  @BindView(R.id.view_pager_tab)
  SmartTabLayout mSmartTabLayout;
  @BindView(R.id.view_pager)
  ViewPager mViewPager;

  private long teamId;
  private long gameId;
  private long DEFAULT_TEAM_ID = -1L;
  private long DEFAULT_GAME_ID = -1L;

  private AlertDialog mExitDialog;

  private FragmentPagerItemAdapter fragmentPagerItemAdapter;

  private Point mPoint;

  private Task currentTask;
  private int currentTaskIndex = 0;

  private boolean isAllTaskDone = false;
  private List<String> taskIds;
  private Map<String, Task> taskMap;

  public GameTaskController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);

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
    mToolbarTitle.setText(currentTask.getTaskName());
    resumeFromMemRecords();
    initViewPager();
  }

  private void resumeFromMemRecords() {
    List<RunningRecord> records = getAppropriateRecordList();
    for (RunningRecord record : records) {
      if (RunningRecord.RECORD_TYPE.TASK_FINISH == record.getType()) {
        if (currentTask.getId() == record.getTaskId()) {
          onNextTask();
        }
      }
    }
  }

  private void initViewPager() {
    FragmentPagerItems.Creator creator = FragmentPagerItems.with(getContext());
    for (String taskId : taskIds) {
      Task task = taskMap.get(taskId);
      creator.add(task.getTaskName(), MultiplyTaskFragment.class);
    }
    fragmentPagerItemAdapter = new FragmentPagerItemAdapter(
        getSupportFragmentManager(), creator.create());
    mViewPager.setAdapter(fragmentPagerItemAdapter);
    mSmartTabLayout.setViewPager(mViewPager);
    mSmartTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        if (position < taskIds.size()) {
          String id = taskIds.get(position);
          Task t = taskMap.get(id);
          if (t != null) {
            mToolbarTitle.setText(t.getTaskName());
          }
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
  }

  private List<RunningRecord> findAppropriateRecordList(List<RunningRecord> memRecords) {
    List<RunningRecord> result = new ArrayList<>();
    for (RunningRecord record : memRecords) {
      if (record.getPointId() == mPoint.getId()) {
        result.add(record);
      }
    }
    return result;
  }

  private void showExitDialog(String str) {
    if (mExitDialog == null) {
      mExitDialog = new AlertDialog.Builder(getActivity())
          .setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              finishGameTaskActivityByType(GAME_ACTIVITY_FINISH_TYPE.USER_ACTION, null);
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

  private boolean onNextTask() {
    if (currentTaskIndex < taskIds.size() - 1) {
      currentTaskIndex++;
      currentTask = taskMap.get(taskIds.get(currentTaskIndex));
      return true;
    } else {
      ToastUtil.TextToast("您已经完成了此攻击点所有任务");
      isAllTaskDone = true;
      return false;
    }
  }

  private void dispatchTaskRecord(RunningRecord.RECORD_TYPE type, long taskId) {
    RecordsUtils.dispatchTypeRecord(34.123123, 114.321321, mPoint.getId(), taskId, type);
  }

  private void cacheTaskRecord(RunningRecord.RECORD_TYPE type, long taskId) {
    RecordsUtils.cacheTypeRecord(34.123123, 114.321321, mPoint.getId(), taskId, type);
  }

  public void openNextTaskIfExist() {
    int pointIndex = mPoint.getPointIndex();
    if (currentTaskIndex == 0) {
      if (pointIndex != 0) {
        dispatchTaskRecord(RunningRecord.RECORD_TYPE.TASK_START, currentTask.getId());
      } else {
        cacheTaskRecord(RunningRecord.RECORD_TYPE.TASK_START, currentTask.getId());
      }
    }
    if (pointIndex != 0) {
      dispatchTaskRecord(RunningRecord.RECORD_TYPE.TASK_FINISH, currentTask.getId());
    } else {
      cacheTaskRecord(RunningRecord.RECORD_TYPE.TASK_FINISH, currentTask.getId());
    }
    if (onNextTask()) {
      mViewPager.setCurrentItem(currentTaskIndex);
      MultiplyTaskController controller = ((MultiplyTaskController) ((MultiplyTaskFragment) fragmentPagerItemAdapter.getPage(currentTaskIndex)).getController());
      if (controller != null) {
        if (pointIndex != 0) {
          dispatchTaskRecord(RunningRecord.RECORD_TYPE.TASK_START, currentTask.getId());
        } else {
          cacheTaskRecord(RunningRecord.RECORD_TYPE.TASK_START, currentTask.getId());
        }
        controller.openSealAndInitTask();
      }
    }
  }

  public void dispatchStartCurrentTaskRecord() {
    dispatchTaskRecord(RunningRecord.RECORD_TYPE.TASK_START, currentTask.getId());
  }

  public void dispatchFinishCurrentTaskRecord() {
    dispatchTaskRecord(RunningRecord.RECORD_TYPE.TASK_FINISH, currentTask.getId());
  }

  @OnClick(R.id.default_tool_bar_imageview)
  void back() {
    if (isAllTaskDone) {
      finishGameTaskActivityByType(GAME_ACTIVITY_FINISH_TYPE.USER_ACTION, null);
    } else {
      showExitDialog(getString(R.string.exit_task_activity_when_doing));
    }
  }

  @Override
  public boolean onBackPressed() {
    back();
    return true;
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

  public Task getTaskAtPosition(int position) {
    if (position < 0 || taskIds == null || position >= taskIds.size() || taskMap == null || taskMap.size() == 0) {
      return null;
    }
    return taskMap.get(taskIds.get(position));
  }

  public List<RunningRecord> getAppropriateRecordList() {
    List<RunningRecord> result = new ArrayList<>();
    RecordsHandler handler = RecordsUtils.getmRecordsHandler();
    if (handler != null) {
      MemRecords memRecords = handler.getMemRecords();
      if (memRecords != null && memRecords.getAll() != null) {
        result = memRecords.getAll();
      }
    }
    return findAppropriateRecordList(result);
  }

  public void finishGameTaskActivityByType(GAME_ACTIVITY_FINISH_TYPE type, @Nullable Task timingTask) {
    Intent intent = new Intent();
    intent.putExtra(Conf.GAME_ACTIVITY_FINISH_TYPE, type);
    switch (type) {

      case TIMING_TASK:
        intent.putExtra(Conf.TIMING_TASK, timingTask);
        break;

      case USER_ACTION:
        intent.putExtra(Conf.IS_POINT_TASK_ALL_FINISHED_INDEX, mPoint.getPointIndex());
        intent.putExtra(Conf.IS_POINT_TASK_ALL_FINISHED, isAllTaskDone);
        break;
    }
    getActivity().setResult(Conf.RESULT_CODE_TASK_ACTIVITY, intent);
    finish();
  }

}
