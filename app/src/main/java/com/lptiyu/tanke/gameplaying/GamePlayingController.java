package com.lptiyu.tanke.gameplaying;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.gamedata.GameDataActivity;
import com.lptiyu.tanke.gameplaying.assist.ConsoleHelper;
import com.lptiyu.tanke.gameplaying.assist.LocateHelper;
import com.lptiyu.tanke.gameplaying.assist.MapHelper;
import com.lptiyu.tanke.gameplaying.assist.TimingTaskHelper;
import com.lptiyu.tanke.gameplaying.assist.zip.GameZipHelper;
import com.lptiyu.tanke.gameplaying.pojo.GAME_ACTIVITY_FINISH_TYPE;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.lptiyu.tanke.gameplaying.records.MemRecords;
import com.lptiyu.tanke.gameplaying.records.RecordsHandler;
import com.lptiyu.tanke.gameplaying.records.RecordsUtils;
import com.lptiyu.tanke.gameplaying.records.RunningRecord;
import com.lptiyu.tanke.gameplaying.task.GameTaskActivity;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.permission.PermissionDispatcher;
import com.lptiyu.tanke.permission.TargetMethod;
import com.lptiyu.tanke.pojo.GameDetailsEntity;
import com.lptiyu.tanke.trace.bean.HistoryTrackData;
import com.lptiyu.tanke.trace.history.HistoryTrackCallback;
import com.lptiyu.tanke.trace.history.HistoryTrackHelper;
import com.lptiyu.tanke.trace.history.IHistoryTrackHelper;
import com.lptiyu.tanke.trace.tracing.ITracingHelper;
import com.lptiyu.tanke.trace.tracing.TracingCallback;
import com.lptiyu.tanke.trace.tracing.TracingHelper;
import com.lptiyu.tanke.utils.ShaPreferManager;
import com.lptiyu.tanke.utils.TimeUtils;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.VibrateUtils;
import com.lptiyu.tanke.widget.BaseSpotScrollView;
import com.lptiyu.tanke.widget.TickView;
import com.lptiyu.tanke.widget.dialog.TextDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-22
 *         email: wonderfulifeel@gmail.com
 */
public class GamePlayingController extends ActivityController implements
        BDLocationListener,
        TracingCallback,
        MapHelper.OnMapMarkerClickListener,
        BaseSpotScrollView.OnSpotItemClickListener,
        HistoryTrackCallback {

    @BindView(R.id.map_view)
    TextureMapView mapView;
    @BindView(R.id.tick_view)
    TickView mTickView;
    @BindView(R.id.zoom_in)
    ImageView mZoomIn;
    @BindView(R.id.zoom_out)
    ImageView mZoomOut;

    boolean isReachedAttackPoint = false;
    boolean isGameFinished = false;

    long gameId;
    long teamId;
    String entityName;
    long queryHistoryTrackStartTime;
    long queryHistoryTrackEndTime;

    int currentAttackPointIndex = 0;
    Point currentAttackPoint;
    List<Point> mPoints;
    GameDetailsEntity mGameDetailsEntity;

    MapHelper mapHelper;
    LocateHelper locateHelper;
    ConsoleHelper consoleHelper;
    GameZipHelper gameZipHelper;
    ITracingHelper mTracingHelper;
    IHistoryTrackHelper mHistoryTrackHelper;
    TimingTaskHelper mTimingTaskHelper;

    AlertDialog mErrorDialog;
    AlertDialog mAlertDialog;
    AlertDialog mLoadingDialog;
    TextDialog mTextDialog;

    private static final int START_QUERY_TRACK = 1;
    private static final int STOP_QUERT_TRACK = 2;
    private static final long QUERY_HISTORY_TRACK_DELAY = TimeUtils.ONE_MINUTE_TIME;

    /**
     * 这个handler用来间隔的从百度鹰眼服务获取最新的纠偏轨迹
     * 自己纠偏的算法麻烦而且不精确，用百度纠偏好了的轨迹
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case START_QUERY_TRACK:
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mHistoryTrackHelper.queryHistoryTrack(entityName, (int) (queryHistoryTrackStartTime /
                                            TimeUtils.ONE_MINUTE_TIME),
                                    (int) (queryHistoryTrackEndTime / TimeUtils.ONE_MINUTE_TIME));
                        }
                    }, QUERY_HISTORY_TRACK_DELAY);
                    break;

                case STOP_QUERT_TRACK:
                    //do nothing
                    break;
            }
            return false;
        }
    });

    public GamePlayingController(AppCompatActivity activity, View view) {
        super(activity, view);
        ButterKnife.bind(this, view);
        init(view);
    }

    private void init(View view) {

        showLoadingDialog();

        gameZipHelper = new GameZipHelper();

        Intent intent = getIntent();
        gameId = intent.getLongExtra(Conf.GAME_ID, Conf.TEMP_GAME_ID);
        mGameDetailsEntity = intent.getParcelableExtra(Conf.GAME_DETAIL);
        queryHistoryTrackEndTime = System.currentTimeMillis() / TimeUtils.ONE_SECOND_TIME;
        teamId = intent.getLongExtra(Conf.TEAM_ID, Conf.TEMP_TEAM_ID);
        entityName = Conf.makeUpTrackEntityName(gameId, Accounts.getId());
        if (!gameZipHelper.checkAndParseGameZip(gameId) || gameZipHelper.getmPoints().size() == 0) {
            mLoadingDialog.dismiss();
            showErrorDialog();
            return;
        }
        mPoints = gameZipHelper.getmPoints();
        mapHelper = new MapHelper(getActivity(), mapView);
        mapHelper.bindData(mPoints);
        mapHelper.setmMapMarkerClickListener(this);
        consoleHelper = new ConsoleHelper(getActivity(), view, mPoints);
        consoleHelper.setOnSpotClickListener(this);
        mTracingHelper = new TracingHelper(getActivity().getApplicationContext(), this);
        mTracingHelper.entityName(entityName);
        mHistoryTrackHelper = new HistoryTrackHelper(getActivity().getApplicationContext(), this);
        locateHelper = new LocateHelper(getActivity().getApplicationContext());
        locateHelper.registerLocationListener(this);

        mTimingTaskHelper = new TimingTaskHelper(this, mTickView);

        //初始化游戏记录
        RecordsUtils.initRecordsHandler(new RecordsHandler.Builder(gameId, teamId).build());

        initRecords();
        moveToTarget();

        mHandler.sendEmptyMessage(START_QUERY_TRACK);
    }

    /**
     * 检验用户是否进入攻击点区域
     * Check whether user is in the area of attack point
     *
     * @param location user's current location
     * @return true if user in the circle of point, false for not
     */
    private boolean checkIfReachAttackPoint(BDLocation location) {
        double distance = DistanceUtil.getDistance(
                new LatLng(location.getLatitude(), location.getLongitude()),
                currentAttackPoint.getLatLng());
        return distance < Conf.POINT_RADIUS;
    }

    /**
     * 当该游戏的记录文件存在时，恢复点记录和任务记录
     * This method is to resume point history and task history
     * if the records file of this game exist
     */
    private void initRecords() {
        currentAttackPoint = mPoints.get(currentAttackPointIndex);
        mapHelper.initMapFlow();
        if (RecordsUtils.isGameStartedFromDisk(gameId) && RecordsUtils.getmRecordsHandler() != null) {
            RecordsUtils.getmRecordsHandler().dispatchResumeFromDisc(new RecordsHandler.ResumeCallback() {
                @Override
                public void dataResumed(List<RunningRecord> recordList) {
                    if (recordList == null) {
                        Timber.e("Resume from history records error");
                        return;
                    }
                    resumePointRecords(recordList);
                    resumeCurrentPointTaskRecords(recordList);
                    mLoadingDialog.dismiss();
                }
            });
        } else {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 通过记录列表，恢复点记录历史
     * Resume point history according to the record list
     *
     * @param recordList the records read from rile
     */
    private void resumePointRecords(List<RunningRecord> recordList) {
        recordList = findAppropriateRecords(recordList);
        for (RunningRecord record : recordList) {
            if (record.getPointId() == currentAttackPoint.getId()) {
                switch (record.getState()) {

                    case GAME_START:
                        queryHistoryTrackStartTime = record.getCreateTime();
                        if (currentAttackPoint.getPointIndex() == 0) {
                            onReachAttackPoint();
                            onNextPoint();
                        }
                        break;

                    case POINT_REACH:
                        onReachAttackPoint();
                        break;

                    case POINT_FINISH:
                        onNextPoint();
                        break;
                }
            } else {
                //TODO : the records are mixed = =, i don't what to do
            }
        }
    }

    /**
     * 为了避免当时间任务开始时，用户退出了App，需要恢复当前攻击点的任务记录，时间任务可以被计算
     * Resume the current attack point task record
     * in order to avoid user exit app when timing task start
     * according to the task start record, timing task's ending time can be calculate
     *
     * @param recordList all task records relate to this attack point
     */
    private void resumeCurrentPointTaskRecords(List<RunningRecord> recordList) {
        recordList = findCurrentPointTaskRecords(recordList);
        if (recordList != null && recordList.size() != 0) {
            RunningRecord record = recordList.get(recordList.size() - 1);
            if (record == null || record.getState() != RunningRecord.RECORD_TYPE.TASK_START) {
                return;
            }
            long currentTaskId = record.getTaskId();
            Task resultTask = checkIfIsTimingTask(currentTaskId);
            if (resultTask == null) {
                return;
            }
            long startTimeMillis = record.getCreateTime();
            long expectEndTimeMillis = Integer.valueOf(resultTask.getPwd()) * TimeUtils.ONE_MINUTE_TIME +
                    startTimeMillis;
            if (System.currentTimeMillis() < expectEndTimeMillis) {
                mTimingTaskHelper.startTimingTaskFlow(resultTask, startTimeMillis);
            } else {
                mTimingTaskHelper.startTimingTaskFlow(resultTask, Long.MIN_VALUE);
            }
            onNextPoint();
        }
    }

    /**
     * 此方法是为了获取地图上到达点的记录，只需要读取记录
     * This method is to find records about point reach
     * in the aspect of map, only need to read about point reach record
     *
     * @param records all records from disk file
     * @return the record list about point reach
     */
    private List<RunningRecord> findAppropriateRecords(List<RunningRecord> records) {
        List<RunningRecord> result = new ArrayList<>();
        for (RunningRecord record : records) {
            RunningRecord.RECORD_TYPE type = record.getState();
            if (type == RunningRecord.RECORD_TYPE.GAME_START ||
                    type == RunningRecord.RECORD_TYPE.POINT_REACH ||
                    type == RunningRecord.RECORD_TYPE.POINT_FINISH) {
                result.add(record);
            }
        }
        return result;
    }

    /**
     * 找到关于当前点的所有记录
     * This method is to find all records about current attack point
     *
     * @param records all records
     * @return return the record list
     */
    private List<RunningRecord> findCurrentPointTaskRecords(List<RunningRecord> records) {
        List<RunningRecord> result = new ArrayList<>();
        for (RunningRecord record : records) {
            RunningRecord.RECORD_TYPE type = record.getState();
            if ((record.getPointId() == currentAttackPoint.getId()) && (type == RunningRecord.RECORD_TYPE.TASK_START
                    || type == RunningRecord.RECORD_TYPE.TASK_FINISH)) {
                result.add(record);
            }
        }
        return result;
    }

    /**
     * 判断当前任务是否是时间任务
     * Check whether the task is timing task
     *
     * @param taskId
     * @return null for false, task instance for true
     */
    private Task checkIfIsTimingTask(long taskId) {
        String taskIdStr = String.valueOf(taskId);
        Task task = currentAttackPoint.getTaskMap().get(taskIdStr);
        if (task == null) {
            return null;
        }
        if (task.getType() == Task.TASK_TYPE.TIMING) {
            return task;
        }
        return null;
    }

    /**
     * 此方法用于当用户第一次到达攻击点时，通过震动和弹出dialog的形式提醒他们点击地图上的提示图片，开始当前攻击点的任务
     * This method to notify user when they first arrive the attack point
     * vibrate and show the dialog to tell them click the nail in the map
     * then begin the tasks in this attack point
     */
    private void onReachAttackPoint() {
        if (currentAttackPoint == null || currentAttackPointIndex < 0) {
            Timber.e("current attack point is error");
            return;
        }
        isReachedAttackPoint = true;
        mapHelper.onReachAttackPoint(currentAttackPointIndex);
        consoleHelper.onReachAttackPoint(currentAttackPointIndex);
        mapHelper.animateCameraToCurrentTarget();
    }

    private void onNextPoint() {
        if (currentAttackPointIndex < mPoints.size() - 1) {
            currentAttackPointIndex++;
            currentAttackPoint = mPoints.get(currentAttackPointIndex);
            consoleHelper.updateNextPoint(currentAttackPointIndex);
            mapHelper.updateNextPoint(currentAttackPointIndex);
            isReachedAttackPoint = false;
        } else {
            //TODO : all point has arrive and the game is finished
            isGameFinished = true;
            ToastUtil.TextToast("您已经完成了所有的攻击点");
            mTracingHelper.stop();
            if (!RecordsUtils.isGameFinishedFromDisk(gameId)) {
                RecordsUtils.dispatchTypeRecord(currentAttackPointIndex, currentAttackPoint.getId(), 0, RunningRecord
                        .RECORD_TYPE.GAME_FINISH);
                showTextDialog();
            }
        }
    }

    private void showTextDialog() {
        if (mTextDialog == null) {
            mTextDialog = new TextDialog(getContext());
            mTextDialog.cancelButton.setVisibility(View.GONE);
            mTextDialog.setmListener(new TextDialog.OnTextDialogButtonClickListener() {
                @Override
                public void onPositiveClicked() {
                    startGameDataActivity();
                }

                @Override
                public void onNegtiveClicked() {

                }
            });
        }
        mTextDialog.withMessage("您已完成所有攻击点,点击确定查看成绩单");
    }

    private void showAlertDialog(String message) {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(getActivity())
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (ShaPreferManager.getMobileVibrate()) {
                                VibrateUtils.cancel();
                            }
                        }
                    })
                    .setMessage(message)
                    .create();
        }
        mAlertDialog.show();
    }

    private void showErrorDialog() {
        if (mErrorDialog == null) {
            mErrorDialog = new AlertDialog.Builder(getActivity())
                    .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mErrorDialog.dismiss();
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .setMessage(getString(R.string.parse_zip_error))
                    .create();
        }
        mErrorDialog.show();
    }

    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, null);
            TextView textView = ((TextView) view.findViewById(R.id.loading_dialog_textview));
            textView.setText(getString(R.string.loading));
            mLoadingDialog = new AlertDialog.Builder(getActivity())
                    .setCancelable(false)
                    .setView(view)
                    .create();
        }
        mLoadingDialog.show();
    }

    @OnClick(R.id.game_data)
    void startGameDataActivity() {
        Intent intent = new Intent();
        intent.putExtra(Conf.GAME_ID, gameId);
        if (mPoints != null && mPoints instanceof ArrayList) {
            intent.putParcelableArrayListExtra(Conf.GAME_POINTS, ((ArrayList<? extends Parcelable>) mPoints));
        }
        if (mGameDetailsEntity != null) {
            intent.putExtra(Conf.GAME_DETAIL, mGameDetailsEntity);
        }
        if (isGameFinished) {
            intent.setClass(getActivity(), GameShareActivity.class);
        } else {
            if (RecordsUtils.getmRecordsHandler() != null) {
                MemRecords memRecords = RecordsUtils.getmRecordsHandler().getMemRecords();
                if (memRecords != null) {
                    List<RunningRecord> allRecords = memRecords.getAll();
                    if (allRecords != null && allRecords instanceof ArrayList) {
                        intent.putParcelableArrayListExtra(Conf.GAME_RECORDS, (((ArrayList<? extends Parcelable>)
                                allRecords)));
                    }
                }
            }
            intent.setClass(getActivity(), GameDataActivity.class);
        }
        startActivity(intent);
    }

    @OnClick(R.id.move_to_target)
    void moveToTarget() {
        if (mapHelper != null) {
            mapHelper.animateCameraToCurrentTarget();
        }
    }

    @OnClick(R.id.start_locate)
    void startLocateButtonClicked() {
        PermissionDispatcher.startLocateWithCheck(((BaseActivity) getActivity()));
    }

    @OnClick(R.id.start_animate)
    void startAnimateButtonClicked() {
        if (isGameFinished) {
            return;
        }
        isReachedAttackPoint = true;
        mTimingTaskHelper.finishTimingTask();
        onReachAttackPoint();
        // if the last point has the timing task, it should be finish either
        if (mTimingTaskHelper.isTimingTask()) {
            mTimingTaskHelper.dispatchTimingTaskRecord();
        }
        RecordsUtils.dispatchTypeRecord(currentAttackPointIndex, currentAttackPoint.getId(), 0, RunningRecord
                .RECORD_TYPE.POINT_REACH);
    }

    @OnClick(R.id.zoom_in)
    void zoomIn() {
        if (mapHelper.mapZoomIn()) {
            mZoomOut.setClickable(true);
        } else {
            mZoomIn.setClickable(false);
        }
    }

    @OnClick(R.id.zoom_out)
    void zoomOut() {
        if (mapHelper.mapZoomOut()) {
            mZoomIn.setClickable(true);
        } else {
            mZoomOut.setClickable(false);
        }
    }

    @Override
    public void onQueryHistoryTrackCallback(HistoryTrackData historyTrackData) {
        List<LatLng> latLngList = new ArrayList<>();
        if (historyTrackData != null && historyTrackData.getStatus() == 0) {
            List<LatLng> points = historyTrackData.getListPoints();
            if (points != null && points.size() > 0) {
                latLngList.addAll(points);
                if (mapHelper != null) {
                    mapHelper.drawHistoryTrack(latLngList);
                    if (isGameFinished) {
                        mHandler.sendEmptyMessage(STOP_QUERT_TRACK);
                    } else {
                        queryHistoryTrackStartTime = queryHistoryTrackEndTime;
                        queryHistoryTrackEndTime = System.currentTimeMillis() / TimeUtils.ONE_SECOND_TIME;
                        mHandler.sendEmptyMessage(START_QUERY_TRACK);
                    }
                }
            }
        }
    }

    @Override
    public void onRequestFailedCallback(String s) {

    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        mapHelper.onReceiveLocation(location);
        /**
         * Check whether user reach the attack point
         * when the game is not finished and the point is not reached
         */
        if (!isGameFinished && !isReachedAttackPoint) {
            if (checkIfReachAttackPoint(location)) {
                mTimingTaskHelper.finishTimingTask();
                onReachAttackPoint();
                if (ShaPreferManager.getMobileVibrate()) {
                    VibrateUtils.vibrate();
                }
                showAlertDialog(getString(R.string.reach_attack_point));
                // if the last point has the timing task, it should be finish either
                if (mTimingTaskHelper.isTimingTask()) {
                    mTimingTaskHelper.dispatchTimingTaskRecord();
                }
                RecordsUtils.dispatchTypeRecord(currentAttackPointIndex, currentAttackPoint.getId(), 0, RunningRecord
                        .RECORD_TYPE.POINT_REACH);
            }
        }
    }

    /**
     * This method is invoked when spot is clicked
     * need to animate camera to clicked spot's marker
     *
     * @param view     clicked spot view
     * @param position the position of clicked spot view
     */
    @Override
    public void onSpotItemClick(View view, int position) {
        if (mPoints == null) {
            return;
        }
        int result = position - currentAttackPoint.getPointIndex();
        if (result < 0) {
            mapHelper.animateCameraToMarkerByIndex(position);
            mapHelper.showPointInfoWindow(mPoints.get(position));
        } else if (result == 0) {
            mapHelper.animateCameraToMarkerByIndex(position);
            mapHelper.showPointInfoWindow(mPoints.get(position));
        } else {
            ToastUtil.TextToast("攻击点还未开启");
        }
    }

    @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_LOCATION)
    public void startLocateService() {
        if (!mTracingHelper.isStarted()) {
            mTracingHelper.start();
        }
        locateHelper.startLocate();
        mapHelper.animateCameraToCurrentPosition();
    }

    @Override
    public void onMarkerClicked(Point point) {
        if (currentAttackPoint == point) {
            if (!isReachedAttackPoint) {
                ToastUtil.TextToast("您还未到达该攻击点");
                return;
            }
        } else {
            if (point.getPointIndex() == 0) {
                ToastUtil.TextToast("游戏已经开始");
                return;
            }
        }
        if (mTimingTaskHelper.isTimingTask()) {
            ToastUtil.TextToast("请先完成当前定时任务");
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(), GameTaskActivity.class);
        intent.putExtra(Conf.CLICKED_POINT, point);
        intent.putExtra(Conf.GAME_ID, gameId);
        intent.putExtra(Conf.TEAM_ID, teamId);
        startActivityForResult(intent, Conf.REQUEST_CODE_TASK_ACTIVITY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Conf.REQUEST_CODE_TASK_ACTIVITY && resultCode == Conf.RESULT_CODE_TASK_ACTIVITY) {
            if (data != null) {
                GAME_ACTIVITY_FINISH_TYPE type = ((GAME_ACTIVITY_FINISH_TYPE) data.getSerializableExtra(Conf
                        .GAME_ACTIVITY_FINISH_TYPE));
                switch (type) {

                    case TIMING_TASK:
                        Task task = data.getParcelableExtra(Conf.TIMING_TASK);
                        mTimingTaskHelper.startTimingTaskFlow(task, System.currentTimeMillis());
                        onNextPoint();
                        break;

                    case USER_ACTION:
                        if (isGameFinished) {
                            return;
                        }
                        int clickedPointIndex = data.getIntExtra(Conf.IS_POINT_TASK_ALL_FINISHED_INDEX, -1);
                        if (clickedPointIndex != currentAttackPointIndex) {
                            return;
                        }
                        boolean isAllTaskFinished = data.getBooleanExtra(Conf.IS_POINT_TASK_ALL_FINISHED, false);
                        if (isAllTaskFinished) {
                            if (currentAttackPointIndex == 0) {
                                RecordsUtils.dispatchTypeRecord(currentAttackPointIndex, currentAttackPoint.getId(),
                                        0, RunningRecord.RECORD_TYPE.GAME_START);
                            } else {
                                RecordsUtils.dispatchTypeRecord(currentAttackPointIndex, mPoints.get
                                        (currentAttackPointIndex).getId(), 0, RunningRecord.RECORD_TYPE.POINT_FINISH);
                            }
                            onNextPoint();
                            mapHelper.animateCameraToCurrentTarget();
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void onTraceStart() {
        Timber.e("hawk eye service start");
    }

    @Override
    public void onTracePush(byte b, String s) {

    }

    @Override
    public void onTraceStop() {
        Timber.e("hawk eye service stop");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapHelper != null) {
            mapHelper.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapHelper != null) {
            mapHelper.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (mapHelper != null) {
            mapHelper.onDestroy();
        }
        if (locateHelper != null) {
            locateHelper.stopLocate();
            locateHelper.unRegisterLocationListener(this);
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessage(STOP_QUERT_TRACK);
        }

        super.onDestroy();
    }

    public Point getLastPoint() {
        if (currentAttackPointIndex > 0) {
            return mPoints.get(currentAttackPointIndex - 1);
        }
        return null;
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
    }

}
