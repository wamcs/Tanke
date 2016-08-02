package com.lptiyu.tanke.activities.gameplaying2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.baidumapmode.GameMapShowActivity;
import com.lptiyu.tanke.activities.guessriddle.GuessRiddleActivity;
import com.lptiyu.tanke.activities.imagedistinguish.ImageDistinguishActivity;
import com.lptiyu.tanke.adapter.GVForGamePlayingAdapter;
import com.lptiyu.tanke.database.DBGameRecord;
import com.lptiyu.tanke.database.DBGameRecordDao;
import com.lptiyu.tanke.database.DBHelper;
import com.lptiyu.tanke.database.DBPointRecord;
import com.lptiyu.tanke.database.DBTaskRecord;
import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.entity.PointRecord;
import com.lptiyu.tanke.entity.TaskRecord;
import com.lptiyu.tanke.enums.GameRecordAndPointStatus;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.enums.RequestCode;
import com.lptiyu.tanke.enums.ResultCode;
import com.lptiyu.tanke.gameplaying.assist.LocateHelper;
import com.lptiyu.tanke.gameplaying.assist.MapHelper;
import com.lptiyu.tanke.gameplaying.assist.zip.GameZipHelper;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.lptiyu.tanke.gameplaying.pojo.ThemeLine;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.pojo.GameDetailsEntity;
import com.lptiyu.tanke.pojo.GameDisplayEntity;
import com.lptiyu.tanke.pojo.GameFinishedEntity;
import com.lptiyu.tanke.pojo.GamePlayingEntity;
import com.lptiyu.tanke.pojo.UpLoadGameRecord;
import com.lptiyu.tanke.utils.TimeUtils;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.WebViewUtils;
import com.lptiyu.tanke.widget.CustomTextView;
import com.lptiyu.zxinglib.android.CaptureActivity;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class GamePlaying2Activity extends Activity implements GamePlaying2Contract.IGamePlayingView {

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.game_playing_title)
    CustomTextView gamePlayingTitle;
    @BindView(R.id.gv)
    GridView gv;

    long gameId;
    long teamId;

    long gameType;

    AlertDialog parseGameZipErrorDialog;
    AlertDialog loadGameRecordDialog;
    @BindView(R.id.map_view)
    TextureMapView mapView;
    @BindView(R.id.img_zoom_full_screen)
    ImageView imgZoomFullScreen;

    private GamePlaying2Presenter presenter;
    private List<Point> list_points;
    private String unZippedDir;
    private GameRecord gameRecord;

    private Task currentTask;
    private Point currentPoint;
    private ThemeLine themeLine;
    private String gameName;
    private boolean isGameOver;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int IMAGE_DISTINGUISH = 2;
    private GVForGamePlayingAdapter adapter;
    private AlertDialog taskDialog;
    private int currentPointIndex;
    private AlertDialog finishTaskAndGameOverDialog;
    private GameZipHelper gameZipHelper;
    private List<Point> mPoints;
    private ArrayList<LatLng> mPonitLatLngs;
    private MapHelper mapHelper;
    private LocateHelper locateHelper;
    int currentAttackPointCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_playing2);
        ButterKnife.bind(this);

        presenter = new GamePlaying2Presenter(this);

        init();

        try {
            readResume();
        } catch (SQLDataException e) {
            e.printStackTrace();
            finish();
        }
        initView();

        showLoadingDialog();
        //接受从游戏详情传过来的数据
        initData();
        //从服务器请求游戏数据
        presenter.downLoadGameRecord(gameId, gameType);
        //        //从本地数据库查询游戏记录
        //        DBGameRecord dbGameRecord = presenter.queryGameRecord(gameId);
        //        Log.i("jason", "数据库查询结果：" + dbGameRecord);
        //        if (loadGameRecordDialog != null)
        //            loadGameRecordDialog.dismiss();
        //        if (dbGameRecord == null) {
        //            //本地数据库没有，再从服务器请求游戏记录的数据
        //            Log.i("jason", "本地数据库暂无游戏记录，开始从服务器请求");
        //            presenter.downLoadGameRecord(gameId, gameType);
        //            return;
        //        } else {
        //            initGameRecord(dbGameRecord);
        //        }
        //        //根据游戏记录核对每个任务点的状态
        //        checkGameRecordAndPointStatus();
        //        setAdapter();
    }

    private void init() {

        gameZipHelper = new GameZipHelper();

        Intent intent = getIntent();
        gameId = intent.getLongExtra(Conf.GAME_ID, Conf.TEMP_GAME_ID);
        teamId = intent.getLongExtra(Conf.TEAM_ID, Conf.TEMP_TEAM_ID);
        gameZipHelper.checkAndParseGameZip(gameId);

        mPoints = gameZipHelper.getmPoints();
        mPonitLatLngs = new ArrayList<>();
        mapHelper = new MapHelper(this, mapView);
        mapHelper.bindData(mPoints);

        locateHelper = new LocateHelper(getApplicationContext());
        //        locateHelper.registerLocationListener(this);

    }

    private void readResume() throws SQLDataException {
        if (currentAttackPointCount > 1) {
            return;
        }
        List<DBGameRecord> gameRecordList = DBHelper.getInstance().getDBGameRecordDao()
                .queryBuilder().where(DBGameRecordDao.Properties.Game_id.eq(gameId)).list();
        if (gameRecordList.size() > 1) {
            Timber.e("game_id should be only,but size is %d", gameRecordList.size());
            throw new SQLDataException("game_id should be only");
        } else if (gameRecordList.size() == 0) {
            currentAttackPointCount = 1;
            return;
        }
        DBGameRecord gameRecord = gameRecordList.get(0);
        currentAttackPointCount = gameRecord.getRecord_text().size();
    }

    private void initView() {

        for (Point p : mPoints.subList(0, currentAttackPointCount)) {
            mapHelper.showNextPoint(mPoints.indexOf(p));
            mPonitLatLngs.add(new LatLng(p.getLatitude(), p.getLongitude()));
        }
        mapHelper.drawPolyLine(mPonitLatLngs);
        moveToTarget();
    }

    void moveToTarget() {
        if (mapHelper != null) {
            mapHelper.animateCameraToCurrentTarget();
        }
    }

    /**
     * 根据数据库查询结果初始化游戏记录
     *
     * @param dbGameRecord
     */
    private void initGameRecord(DBGameRecord dbGameRecord) {
        gameRecord = new GameRecord();
        gameRecord.uid = dbGameRecord.getUid();
        gameRecord.join_time = dbGameRecord.getJoin_time();
        gameRecord.play_statu = dbGameRecord.getPlay_statu();
        gameRecord.start_time = dbGameRecord.getStart_time();
        gameRecord.ranks_id = dbGameRecord.getRanks_id();
        gameRecord.game_id = dbGameRecord.getGame_id();
        gameRecord.id = dbGameRecord.getId();
        gameRecord.last_task_ftime = dbGameRecord.getLast_task_ftime();
        gameRecord.line_id = dbGameRecord.getLine_id();
        List<DBPointRecord> list_dbPointRecord = dbGameRecord.getRecord_text();
        List<PointRecord> list_pointRecord = new ArrayList<>();
        for (DBPointRecord dbPointRecord : list_dbPointRecord) {
            PointRecord pointRecord = new PointRecord();
            pointRecord.id = dbGameRecord.getId();
            pointRecord.point_id = dbPointRecord.getPoint_id();
            pointRecord.statu = dbGameRecord.getPlay_statu();
            List<DBTaskRecord> list_dbTaskRecord = dbPointRecord.getTask();
            List<TaskRecord> list_taskRecord = new ArrayList<>();
            for (DBTaskRecord dbTaskRecord : list_dbTaskRecord) {
                TaskRecord taskRecord = new TaskRecord();
                taskRecord.ftime = dbTaskRecord.getFtime();
                taskRecord.exp = dbTaskRecord.getExp();
                taskRecord.taskId = dbTaskRecord.getTaskId();
                taskRecord.id = dbTaskRecord.getId();
                list_taskRecord.add(taskRecord);
            }
            pointRecord.task = list_taskRecord;
            list_pointRecord.add(pointRecord);
        }
        gameRecord.record_text = list_pointRecord;
        Log.i("jason", "赋值结果：" + gameRecord);
    }

    /**
     * 玩游戏界面可以从四个入口进来：游戏列表、游戏详情、正在玩的游戏、已经完成的游戏，要分开考虑
     */
    private void initData() {
        GameZipHelper gameZipHelper = new GameZipHelper();
        Intent intent = getIntent();
        //获取gameId
        gameId = intent.getLongExtra(Conf.GAME_ID, Conf.TEMP_GAME_ID);
        //获取游戏列表实体类（从游戏列表进来）
        GameDisplayEntity gameDisplayEntity = intent.getParcelableExtra(Conf.GAME_DISPLAY_ENTITY);
        //获取游戏详情实体类（从游戏详情进来）
        GameDetailsEntity gameDetailsEntity = intent.getParcelableExtra(Conf.GAME_DETAIL);
        //获取游戏详情实体类（从正在玩的游戏进来）
        GamePlayingEntity gamePlayingEntity = intent.getParcelableExtra(Conf.GAME_PLAYING_ENTITY);
        //获取游戏详情实体类（从已完成的游戏进来）
        GameFinishedEntity gameFinishedEntity = intent.getParcelableExtra(Conf.GAME_FINISHED_ENTITY);
        if (gameDisplayEntity != null) {
            Log.i("jason", "接收的游戏列表的实体类：" + gameDisplayEntity);
            gameType = gameDisplayEntity.getType().value;
            gameName = gameDisplayEntity.getTitle();
        }
        if (gameDetailsEntity != null) {
            Log.i("jason", "接收的游戏详情的实体类：" + gameDetailsEntity);
            gameType = gameDetailsEntity.getType().value;
            gameName = gameDetailsEntity.getTitle();
        }
        if (gamePlayingEntity != null) {
            Log.i("jason", "接收的正在玩的游戏实体类：" + gamePlayingEntity);
            gameType = gamePlayingEntity.getType();
            gameName = gamePlayingEntity.getName();
        }
        if (gameFinishedEntity != null) {
            Log.i("jason", "接收的完成的游戏实体类：" + gameFinishedEntity);
            gameType = gameFinishedEntity.getType();
            gameName = gameFinishedEntity.getName();
        }

        gamePlayingTitle.setText(gameName + "");

        //检查游戏包是否存在或者游戏解压后为空，判断完后游戏包已经被解压缩，并且已经将文件解析成实体类对象，此时可以直接从内存中取数据了
        if (!gameZipHelper.checkAndParseGameZip(gameId) || gameZipHelper.getmPoints().size() == 0) {
            showErrorDialog();
        } else {
            //获取游戏攻击点集合
            list_points = gameZipHelper.getmPoints();
            Log.i("jason", list_points.size() + "个章节点，章节点详情：" + list_points);
            themeLine = gameZipHelper.getmThemeLine();

            /**
             * 游戏包绝对路径
             /storage/emulated/0/Android/data/com.lptiyu.tanke/files/temp/39_33_1467790230
             */
            unZippedDir = gameZipHelper.unZippedDir;
            Log.i("jason", "游戏包绝对路径：" + unZippedDir);
        }
    }

    /**
     * 请求游戏记录弹出的对话框
     */
    private void showLoadingDialog() {
        if (loadGameRecordDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.layout_loading, null);
            TextView textView = ((TextView) view.findViewById(R.id.loading_dialog_textview));
            textView.setText(getString(R.string.loading));
            loadGameRecordDialog = new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setView(view)
                    .create();
        }
        loadGameRecordDialog.show();
    }

    /**
     * 游戏包解析失败后弹出错误提示框
     */
    private void showErrorDialog() {
        if (parseGameZipErrorDialog == null) {
            parseGameZipErrorDialog = new AlertDialog.Builder(this)
                    .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            parseGameZipErrorDialog.dismiss();
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .setMessage(getString(R.string.parse_zip_error))
                    .create();
        }
        parseGameZipErrorDialog.show();
    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.btn_baiduMapMode:
                Intent intent = new Intent(GamePlaying2Activity.this, GameMapShowActivity.class);
                ArrayList<Point> list = new ArrayList<>();
                for (Point point : list_points) {
                    list.add(point);
                }
                intent.putExtra(Conf.GAME_ID, gameId);
                intent.putExtra(Conf.TEAM_ID, teamId);
                startActivity(intent);
                break;
            case R.id.btn_submitRecord:
                break;
        }
    }

    //上传游戏记录后回调
    @Override
    public void successUpLoadRecord() {
        //存储任务记录到本地
        TaskRecord taskRecord = new TaskRecord();
        taskRecord.id = currentTask.getId();
        taskRecord.taskId = currentTask.getId() + "";
        taskRecord.exp = currentTask.getExp() + "";
        taskRecord.ftime = System.currentTimeMillis() + "";
        presenter.insertTask(taskRecord);

        //存储章节点记录到本地
        PointRecord pointRecord = new PointRecord();
        pointRecord.statu = currentPoint.getState() + "";
        pointRecord.point_id = currentPoint.getId() + "";
        pointRecord.id = currentPoint.getId();
        List<TaskRecord> list_taskRecord = new ArrayList<>();
        list_taskRecord.add(taskRecord);
        pointRecord.task = list_taskRecord;
        presenter.insertPoint(pointRecord);

        //存储游戏记录到本地
        GameRecord gameRecord = new GameRecord();
        gameRecord.game_id = gameId + "";
        gameRecord.id = gameId;
        gameRecord.line_id = themeLine.getId() + "";
        if (isGameOver) {
            gameRecord.play_statu = PlayStatus.GAME_OVER + "";
        } else {
            gameRecord.play_statu = PlayStatus.HAVE_STARTED_GAME + "";
        }
        gameRecord.ranks_id = "-1";//TODO 现在还没有团队赛，暂时不考虑
        List<PointRecord> list_pointRecord = new ArrayList<>();
        list_pointRecord.add(pointRecord);
        gameRecord.record_text = list_pointRecord;
        gameRecord.uid = Accounts.getId() + "";
        presenter.insertGameRecord(gameRecord);
        Log.i("jason", "游戏记录存储到数据库完毕");
    }

    @Override
    public void successDownLoadRecord(GameRecord gameRecord) {
        if (loadGameRecordDialog != null) {
            loadGameRecordDialog.dismiss();
        }
        this.gameRecord = gameRecord;
        //        //把服务端请求到的游戏记录数据存储到本地数据库
        //        presenter.insertGameRecord(gameRecord);
        //        if (gameRecord.record_text != null && gameRecord.record_text.size() > 0) {
        //            for (PointRecord pointRecord : gameRecord.record_text) {
        //                presenter.insertPoint(pointRecord);
        //                if (pointRecord.task != null && pointRecord.task.size() > 0) {
        //                    for (TaskRecord taskRecord : pointRecord.task) {
        //                        presenter.insertTask(taskRecord);
        //                    }
        //                }
        //            }
        //        }
        //根据游戏记录核对每个任务点的状态
        checkGameRecordAndPointStatus();
        setAdapter();
        if (isGameOver) {
            showFinishTaskAndGameOverDialog();
            finishTaskAndGameOverDialog.setMessage("恭喜你，游戏通关");
            finishTaskAndGameOverDialog.show();
        }
    }

    @Override
    public void failDownLoadRecord() {
        Toast.makeText(this, "请求游戏记录失败", Toast.LENGTH_SHORT).show();
    }

    private void setAdapter() {
        adapter = new GVForGamePlayingAdapter(this, list_points, unZippedDir);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentPointIndex = i;
                Point point = list_points.get(i);
                GamePlaying2Activity.this.currentPoint = point;
                Log.i("jason", "点击的point：" + point);
                if (point.getState() == GameRecordAndPointStatus.UNSTARTED) {
                    Toast.makeText(GamePlaying2Activity.this, "该攻击点还没开启", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, Task> taskMap = point.getTaskMap();
                Task task = taskMap.get(point.getTaskId().get(0));//目前一个章节点只有一个任务，所以取0就可以了
                GamePlaying2Activity.this.currentTask = task;
                showTaskDialog(GamePlaying2Activity.this, task, point);

            }
        });
    }

    /**
     * 弹出任务对话框
     *
     * @param context
     * @param task
     * @param point
     */
    public void showTaskDialog(Context context, final Task task, Point point) {
        View view = LayoutInflater.from(context).inflate(R.layout.popupwindow_gameclue, null);
        CustomTextView ctv_taskName = (CustomTextView) view.findViewById(R.id.ctv_taskName);
        WebView webView = (WebView) view.findViewById(R.id.webView);
        ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
        ImageView img_indicate = (ImageView) view.findViewById(R.id.img_indicate);
        CustomTextView ctv_getAnser = (CustomTextView) view.findViewById(R.id.ctv_getAnswer);
        ctv_taskName.setText(task.getTaskName());

        if (point.getState() == GameRecordAndPointStatus.FINISHED) {
            ctv_getAnser.setTextColor(Color.GRAY);
            ctv_getAnser.setText("已完成，" + task.getFinishTime() + " +" + task.getExp());
            ctv_getAnser.setClickable(false);
            img_indicate.setImageResource(R.drawable.done);
        }
        if (point.getState() == GameRecordAndPointStatus.PLAYING) {
            ctv_getAnser.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            ctv_getAnser.getPaint().setAntiAlias(true);//抗锯齿
            ctv_getAnser.setTextColor(getResources().getColor(R.color.colorPrimary));
            ctv_getAnser.setText("寻找答案");
            ctv_getAnser.setClickable(true);
            img_indicate.setImageResource(R.drawable.indicate_clue);

            //当CustomTextView显示寻找线索时才设置点击监听器
            ctv_getAnser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (task.getType() == Task.TASK_TYPE.TIMING) {//限时任务
                        Log.i("jason", "限时任务");
                        //TODO 这里只是为了测试才这么写的，实际上是进入限时任务的界面
                        Intent intent = new Intent(GamePlaying2Activity.this, ImageDistinguishActivity.class);
                        startActivityForResult(intent, IMAGE_DISTINGUISH);
                    }
                    if (task.getType() == Task.TASK_TYPE.DISTINGUISH) {//图像识别任务
                        Log.i("jason", "图像识别任务");
                        Intent intent = new Intent(GamePlaying2Activity.this, ImageDistinguishActivity.class);
                        String imgUrl = unZippedDir + "/" + currentPointIndex + "/" + currentTask.getId() + "/" +
                                currentTask.getPwd();
                        Log.i("jason", "要识别的图片路径：" + imgUrl);
                        intent.putExtra(Conf.IMG_DISTINGUISH_URL, imgUrl);
                        startActivityForResult(intent, IMAGE_DISTINGUISH);
                    }
                    if (task.getType() == Task.TASK_TYPE.LOCATE) {//定位任务
                        Log.i("jason", "定位任务");
                        //TODO 这里只是为了测试才这么写的，实际上是进入定位任务的界面

                    }
                    if (task.getType() == Task.TASK_TYPE.FINISH) {
                        Log.i("jason", "完成任务");
                    }
                    if (task.getType() == Task.TASK_TYPE.SCAN_CODE) {//扫码任务
                        Log.i("jason", "扫码任务");
                        Intent intent = new Intent(GamePlaying2Activity.this, CaptureActivity.class);
                        startActivityForResult(intent, CAMERA_PERMISSION_REQUEST_CODE);
                    }
                    if (task.getType() == Task.TASK_TYPE.RIDDLE) {//猜谜任务
                        Log.i("jason", "猜谜任务");
                        Intent intent = new Intent(GamePlaying2Activity.this, GuessRiddleActivity.class);
                        intent.putExtra(Task.class.getName(), task);
                        startActivityForResult(intent, RequestCode.GUESS_RIDDLE);
                    }
                }
            });
        }
        WebViewUtils.setWebView(this, webView);
        webView.loadUrl(task.getContent());
        taskDialog = new AlertDialog.Builder(context).setView(view).create();
        taskDialog.show();

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskDialog.dismiss();
            }
        });
    }

    /**
     * 任务完成后回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {//扫码识别返回
            Bundle b = data.getExtras();
            String str = b.getString(CaptureActivity.QR_CODE_DATA);
            if (str == null || str.length() == 0) {
                ToastUtil.TextToast(getString(R.string.scan_error));
                return;
            }
            String pwd = currentTask.getPwd();
            if (pwd == null || pwd.length() == 0) {
                throw new IllegalArgumentException("the pwd must not be null or \"\" ");
            }
            if (str.equals(currentTask.getPwd())) {
                finishedCurrentTast();

            } else {
                Toast.makeText(this, "答案错误，任务失败", Toast.LENGTH_SHORT).show();
            }
        }
        if (resultCode == ResultCode.GUESS_RIDDLE) {//猜谜返回
            finishedCurrentTast();
        }
        if (resultCode == ResultCode.IMAGE_DISTINGUISH) {//图像识别返回
            finishedCurrentTast();
        }
    }

    /**
     * 任务完成后要执行的
     */
    private void finishedCurrentTast() {
        if (finishTaskAndGameOverDialog == null) {
            showFinishTaskAndGameOverDialog();
        }
        finishTaskAndGameOverDialog.setMessage("恭喜你找出线索");
        finishTaskAndGameOverDialog.show();
        currentTask.setFinishTime(TimeUtils.parseFinishTimeForTaskFinished());
        //刷新数据源
        reLoadData();
        //上传游戏记录
        upLoadAndSaveGameRecord();
    }

    /**
     * 当游戏完成之后需要更新章节点状态，具体是在上传游戏记录之后更新还是上传之前更新有待商榷
     */
    private void reLoadData() {
        if (taskDialog != null)
            taskDialog.dismiss();
        currentPoint.setState(GameRecordAndPointStatus.FINISHED);//当前章节点结束
        int nextPointIndex = currentPointIndex + 1;
        if (nextPointIndex >= list_points.size()) {
            //游戏结束
            isGameOver = true;
            if (finishTaskAndGameOverDialog == null) {
                showFinishTaskAndGameOverDialog();
            } else {
                finishTaskAndGameOverDialog.setMessage("恭喜你，游戏通关");
                finishTaskAndGameOverDialog.show();
            }
        } else {
            isGameOver = false;
            list_points.get(nextPointIndex).setState(GameRecordAndPointStatus.PLAYING);//下一个章节点开启
        }
        adapter.notifyDataSetChanged();
    }

    //游戏通关后弹出对话框
    private void showFinishTaskAndGameOverDialog() {
        finishTaskAndGameOverDialog = new AlertDialog.Builder(this)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create();
    }

    /**
     * 先将游戏记录上传到服务器，获取服务器返回的joint_time和start_time,再将游戏记录存储到本地数据库（游戏记录依然以本地数据库的为准）
     */
    private void upLoadAndSaveGameRecord() {
        UpLoadGameRecord record = new UpLoadGameRecord();
        record.uid = Accounts.getId() + "";
        record.type = gameType + "";
        record.point_id = currentPoint.getId() + "";
        record.game_id = gameId + "";
        if (isGameOver) {
            record.play_statu = PlayStatus.GAME_OVER + "";
        } else {
            record.play_statu = PlayStatus.HAVE_STARTED_GAME + "";
        }
        record.point_statu = GameRecordAndPointStatus.FINISHED + "";
        record.task_id = currentTask.getId() + "";
        presenter.upLoadRecord(record);
    }

    private int maxFinishedIndex;

    /**
     * 根据获取的游戏记录，确定每个任务点的状态
     */
    private void checkGameRecordAndPointStatus() {
        if (gameRecord != null && gameRecord.record_text != null && gameRecord.record_text.size() != 0) {
            //将所有完成的章节点设置状态为完成状态
            for (int i = 0; i < gameRecord.record_text.size(); i++) {
                PointRecord pointRecord = gameRecord.record_text.get(i);
                for (int j = 0; j < list_points.size(); j++) {
                    Point point = list_points.get(j);
                    if (point.getId() == pointRecord.id) {
                        if (pointRecord.statu.equals(GameRecordAndPointStatus.FINISHED + "")) {
                            point.setState(GameRecordAndPointStatus.FINISHED);
                            point.getTaskMap().get(point.getTaskId().get(0)).setFinishTime(pointRecord.task.get(0)
                                    .ftime);
                            maxFinishedIndex = j;
                        }
                        break;
                    }
                }
            }
            //看看所有攻击点中是不是只有已完成和未开启的，如果是，这要将最后一个已完成后面的那个攻击点的状态设置为进行中，其他的设置为未开启
            if (maxFinishedIndex != list_points.size() - 1) {
                list_points.get(maxFinishedIndex + 1).setState(GameRecordAndPointStatus.PLAYING);
                if (maxFinishedIndex + 2 < list_points.size()) {
                    for (int k = maxFinishedIndex + 2; k < list_points.size(); k++) {
                        list_points.get(k).setState(GameRecordAndPointStatus.UNSTARTED);
                    }
                }
            } else {
                //该游戏中没有哪个章节点是进行中的，这个时候需要判断是否所有章节点都已完成（即是否游戏已经结束了）或者都还未开启
                if (maxFinishedIndex == list_points.size() - 1) {
                    isGameOver = true;
                } else {
                    isGameOver = false;
                }
            }
        } else {
            //没有游戏记录，也就是所有攻击点都没有玩过
            for (int i = 0; i < list_points.size(); i++) {
                Point point = list_points.get(i);
                if (i == 0) {
                    point.setState(GameRecordAndPointStatus.PLAYING);
                } else {
                    point.setState(GameRecordAndPointStatus.UNSTARTED);
                }
            }
        }
    }
}
