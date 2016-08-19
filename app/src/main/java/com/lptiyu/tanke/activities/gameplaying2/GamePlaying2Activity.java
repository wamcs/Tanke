package com.lptiyu.tanke.activities.gameplaying2;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.base.MyBaseActivity;
import com.lptiyu.tanke.activities.pointtask.PointTaskActivity;
import com.lptiyu.tanke.adapter.GVForGamePlayingAdapter;
import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.PointRecord;
import com.lptiyu.tanke.entity.Task;
import com.lptiyu.tanke.entity.ThemeLine;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.enums.RequestCode;
import com.lptiyu.tanke.enums.ResultCode;
import com.lptiyu.tanke.gamedetails.GameDetailsActivity;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.pojo.GameDetailResponse;
import com.lptiyu.tanke.pojo.GameDisplayEntity;
import com.lptiyu.tanke.pojo.GameFinishedEntity;
import com.lptiyu.tanke.pojo.GamePlayingEntity;
import com.lptiyu.tanke.utils.GameZipUtils;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.widget.CustomTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GamePlaying2Activity extends MyBaseActivity implements GamePlaying2Contract.IGamePlayingView {

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.game_playing_title)
    CustomTextView gamePlayingTitle;
    @BindView(R.id.gv)
    GridView gv;
    @BindView(R.id.ctv_have_gone_throungh_game)
    CustomTextView ctv_throungh_game;

    long gameId;
    long gameType;

    AlertDialog loadGameRecordDialog;
    //    @BindView(R.id.map_view)
    //    TextureMapView mapView;
    @BindView(R.id.img_zoom_full_screen)
    ImageView imgZoomFullScreen;

    private GamePlaying2Presenter presenter;

    private List<Point> list_points;
    private String unZippedDir;
    private ThemeLine themeLine;

    private GameRecord gameRecord;
    private boolean isGameOver;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int IMAGE_DISTINGUISH = 2;
    private GVForGamePlayingAdapter adapter;
    private GameZipUtils gameZipUtils;
    private ArrayList<Task> list_task;
    private Toast toast;
    private int duration = 2000;
    private Thread thread;

    private int currentPointIndex;
    private Point currentPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_playing2);
        ButterKnife.bind(this);

        //        mapView = (TextureMapView) findViewById(R.id.map_view);

        presenter = new GamePlaying2Presenter(this);

        Intent intent = getIntent();
        //获取gameId
        gameId = intent.getLongExtra(Conf.GAME_ID, Conf.TEMP_GAME_ID);
        //获取游戏列表实体类（从游戏列表进来）
        GameDisplayEntity gameDisplayEntity = intent.getParcelableExtra(Conf.GAME_DISPLAY_ENTITY);

        //接受过来的数据
      //  initData();

        showLoadGameRecordDialog();
        //从服务器请求游戏数据(扔到onResume()里面做了)
        //        presenter.downLoadGameRecord(gameId, gameType);
    }

    /**
     * 玩游戏界面可以从四个入口进来：游戏列表、游戏详情、正在玩的游戏、已经完成的游戏，要分开考虑
     */
    private void initData() {
        Intent intent = getIntent();
        //获取gameId
        gameId = intent.getLongExtra(Conf.GAME_ID, Conf.TEMP_GAME_ID);
        //获取游戏列表实体类（从游戏列表进来）
        GameDisplayEntity gameDisplayEntity = intent.getParcelableExtra(Conf.GAME_DISPLAY_ENTITY);
        //获取游戏详情实体类（从游戏详情进来）
        GameDetailResponse gameDetailsResponse = intent.getParcelableExtra(Conf.GAME_DETAIL);
        //获取游戏详情实体类（从正在玩的游戏进来）
        GamePlayingEntity gamePlayingEntity = intent.getParcelableExtra(Conf.GAME_PLAYING_ENTITY);
        //获取游戏详情实体类（从已完成的游戏进来）
        GameFinishedEntity gameFinishedEntity = intent.getParcelableExtra(Conf.GAME_FINISHED_ENTITY);
        //获取游戏标题（从首页Banner进来）
        String title = intent.getStringExtra(Conf.BANNER_TITLE);
        String gameName = "";
        if (gameDisplayEntity != null) {
            Log.i("jason", "接收的游戏列表的实体类：" + gameDisplayEntity);
            gameType = gameDisplayEntity.getType();
            gameName = gameDisplayEntity.getTitle();
        }
        if (gameDetailsResponse != null) {
            Log.i("jason", "接收的游戏详情的实体类：" + gameDetailsResponse);
            gameType = gameDetailsResponse.type;
            gameName = gameDetailsResponse.title;
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
        if (title != null) {
            gameName = title;
        }

        gamePlayingTitle.setText(gameName + "");

        gameZipUtils = new GameZipUtils();
        String parsedFilePath = gameZipUtils.isParsedFileExist(gameId);
        if (parsedFilePath != null) {
            unZippedDir = parsedFilePath;
            boolean isSuccess = gameZipUtils.transformParsedFileToEntity(parsedFilePath);
            if (isSuccess) {
                list_points = gameZipUtils.mPoints;
                list_task = gameZipUtils.mTasks;
                themeLine = gameZipUtils.mThemeLine;
                //将task存到point当中
                addTaskToPoint();
            } else {
                Toast.makeText(GamePlaying2Activity.this, "游戏包解析错误", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(GamePlaying2Activity.this, "游戏包不存在", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 将Task添加到对应的Point中
     */
    private void addTaskToPoint() {
        //先对list_point进行排序
        Collections.sort(list_points);
        //将Task添加到Point中
        for (Point point : list_points) {
            point.isNew = false;
            ArrayList<Task> list_tasks = new ArrayList<>();
            for (Task task : list_task) {
                if (task.point_id.equals(point.id)) {
                    list_tasks.add(task);
                }
            }
            point.list_task = list_tasks;
            //对list_task进行排序
            Collections.sort(list_tasks);
        }
    }

    @Override
    public void successDownLoadRecord(GameRecord gameRecord) {
        if (loadGameRecordDialog != null) {
            loadGameRecordDialog.dismiss();
        }
        this.gameRecord = gameRecord;
        //根据游戏记录核对每个任务点的状态
        checkPointStatus();
        setAdapter();
        if (Integer.parseInt(this.gameRecord.play_statu) == 2) {
            ctv_throungh_game.setVisibility(View.VISIBLE);
        } else {
            ctv_throungh_game.setVisibility(View.GONE);
        }
    }


    /**
     * 请求游戏记录弹出的对话框
     */
    private void showLoadGameRecordDialog() {
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

    @Override
    protected void onResume() {
        super.onResume();
        loadNetWorkData();
    }

    private void loadNetWorkData() {
        if (NetworkUtil.checkIsNetworkConnected()) {
            presenter.downLoadGameRecord(gameId);
        } else {
            showNetUnConnectDialog();
        }
    }

    // 网络异常对话框
    private void showNetUnConnectDialog() {
        PopupWindowUtils.getInstance().showNetExceptionPopupwindow(this, new PopupWindowUtils.OnNetExceptionListener() {
            @Override
            public void onClick(View view) {
                loadNetWorkData();
            }
        });
    }

    @Override
    public void failDownLoadRecord() {
        Toast.makeText(this, "网络异常", Toast.LENGTH_SHORT).show();
    }

    private void initTimeTask() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(duration);
                    thread = null;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initToast();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void setAdapter() {
        initTimeTask();
        initToast();
        adapter = new GVForGamePlayingAdapter(this, list_points, unZippedDir);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentPointIndex = i;
                currentPoint = list_points.get(i);
                if (currentPoint.isNew) {
                    currentPoint.isNew = false;
                }
                adapter.notifyDataSetChanged();
//                Log.i("jason", "点击的point：" + currentPoint);
                if (currentPoint.state == PointTaskStatus.UNSTARTED) {
                    //控制Toast的显示
                    if (toast != null) {
                        Log.i("jason", "执行了");
                        toast.show();
                        toast = null;
                        if (thread == null) {
                            initTimeTask();
                        }
                    }
                    return;
                }

                Intent intent = new Intent(GamePlaying2Activity.this, PointTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Conf.POINT, currentPoint);
                bundle.putLong(Conf.GAME_ID, gameId);
//                bundle.putLong(Conf.GAME_TYPE, gameType);
                bundle.putString(Conf.UNZIPPED_DIR, unZippedDir);
                if (gameRecord.record_text != null) {
                    for (PointRecord record : gameRecord.record_text) {
                        if (record.id == Long.parseLong(currentPoint.id)) {
                            bundle.putParcelable(Conf.POINT_RECORD, record);
                            break;
                        }
                    }
                } else {
                    bundle.putParcelable(Conf.POINT_RECORD, null);
                }
                if (currentPoint.state == PointTaskStatus.FINISHED) {
                    bundle.putBoolean(Conf.IS_FINISHED_POINT, true);
                } else {
                    bundle.putBoolean(Conf.IS_FINISHED_POINT, false);
                }
                intent.putExtras(bundle);
                startActivityForResult(intent, RequestCode.SKIP_TO_TASK_ACTIVITY);
            }
        });
    }

    private void initToast() {
        toast = Toast.makeText(GamePlaying2Activity.this, "该章节点未解锁", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.SKIP_TO_TASK_ACTIVITY && resultCode == ResultCode.POINT_OVER && currentPoint
                .state != PointTaskStatus.FINISHED) {
            //TODO 此处的逻辑都在onReSume()生命周期方法里
            //            presenter.downLoadGameRecord(gameId, gameType);
            list_points.get(currentPointIndex).state = PointTaskStatus.FINISHED;
            if (currentPointIndex == list_points.size() - 1) {
                isGameOver = true;
            } else {
                list_points.get(currentPointIndex + 1).state = PointTaskStatus.PLAYING;
                list_points.get(currentPointIndex + 1).isNew = true;
            }
            //            adapter.notifyDataSetChanged();
            //            if (isGameOver) {
            //                if (gameOverDialog == null) {
            //                    showGameOverDialog();
            //                }
            //                gameOverDialog.show();
            //            }
        }
        //放弃游戏回调
        if (requestCode == RequestCode.LEAVE_GAME && resultCode == ResultCode.LEAVE_GAME) {
            finish();
        }
    }

    private int maxFinishedIndex;

    /**
     * 根据获取的游戏记录，确定每个任务点的状态
     */
    private void checkPointStatus() {
        if (gameRecord != null && gameRecord.record_text != null && gameRecord.record_text.size() > 0) {
            //将所有完成的章节点设置状态为完成状态
            for (int i = 0; i < gameRecord.record_text.size(); i++) {
                PointRecord pointRecord = gameRecord.record_text.get(i);
                for (int j = 0; j < list_points.size(); j++) {
                    Point point = list_points.get(j);
                    if (Long.parseLong(point.id) == pointRecord.id) {
                        if (pointRecord.statu.equals(PointTaskStatus.FINISHED + "")) {
                            point.state = PointTaskStatus.FINISHED;
                            maxFinishedIndex = j;
                        }
                        break;
                    }
                }
            }
            //看看所有攻击点中是不是只有已完成和未开启的，如果是，这要将最后一个已完成后面的那个攻击点的状态设置为进行中，其他的设置为未开启
            if (maxFinishedIndex != list_points.size() - 1) {
                list_points.get(maxFinishedIndex + 1).state = PointTaskStatus.PLAYING;
                //                list_points.get(maxFinishedIndex + 1).isNew = true;
                if (maxFinishedIndex + 2 < list_points.size()) {
                    for (int k = maxFinishedIndex + 2; k < list_points.size(); k++) {
                        list_points.get(k).state = PointTaskStatus.UNSTARTED;
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
                    point.state = PointTaskStatus.PLAYING;
                } else {
                    point.state = PointTaskStatus.UNSTARTED;
                }
            }
        }
    }

    @OnClick({R.id.img_game_detail, R.id.img_zoom_full_screen, R.id.back_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_game_detail:
                //进入游戏详情页面
                Intent intent = new Intent(GamePlaying2Activity.this, GameDetailsActivity.class);
                intent.putExtra(Conf.GAME_ID, gameId);
                intent.putExtra(Conf.FROM_WHERE, Conf.GamePlay2Activity);
                startActivityForResult(intent, RequestCode.LEAVE_GAME);
                break;
            case R.id.img_zoom_full_screen:
                //TODO 全屏显示地图模式
                break;
            case R.id.back_btn:
                finish();
                break;
        }
    }


    //上传游戏记录后回调
    @Override
    public void successUpLoadRecord() {
    }
}
