package com.lptiyu.tanke.activities.gameplaying;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.pointtask.PointTaskActivity;
import com.lptiyu.tanke.adapter.GVForGamePlayingAdapter;
import com.lptiyu.tanke.entity.response.GameDetail;
import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.enums.RequestCode;
import com.lptiyu.tanke.enums.ResultCode;
import com.lptiyu.tanke.gamedetails.GameDetailsActivity;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.pojo.GameDisplayEntity;
import com.lptiyu.tanke.pojo.GameFinishedEntity;
import com.lptiyu.tanke.pojo.GamePlayingEntity;
import com.lptiyu.tanke.utils.BitMapUtils;
import com.lptiyu.tanke.utils.GameZipUtils;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.widget.CustomTextView;
import com.lptiyu.tanke.widget.DragLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lptiyu.tanke.RunApplication.gameRecord;

public class GamePlayingActivity extends MyBaseActivity implements GamePlayingContract.IGamePlayingView {

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
    String m_title;

    //    @BindView(R.id.map_view)
    //    TextureMapView mapView;
    @BindView(R.id.img_zoom_full_screen)
    ImageView imgZoomFullScreen;
    @BindView(R.id.dragview)
    ImageView dragview;
    @BindView(R.id.drag_layout)
    DragLayout dragLayout;

    private GamePlayingPresenter presenter;

    private String unZippedDir;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int IMAGE_DISTINGUISH = 2;
    private GVForGamePlayingAdapter adapter;
    private GameZipUtils gameZipUtils;
    private Toast toast;
    private int duration = 2000;
    private Thread thread;

    private int currentPointIndex;
    private Point currentPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_playing);
        ButterKnife.bind(this);

        //        mapView = (TextureMapView) findViewById(R.id.map_view);

        presenter = new GamePlayingPresenter(this);


        gamePlayingTitle.setText("加载中...");

        //接受过来的数据
        initData();


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
        GameDetail gameDetailsResponse = intent.getParcelableExtra(Conf.GAME_DETAIL);
        //获取游戏详情实体类（从正在玩的游戏进来）
        GamePlayingEntity gamePlayingEntity = intent.getParcelableExtra(Conf.GAME_PLAYING_ENTITY);
        //获取游戏详情实体类（从已完成的游戏进来）
        GameFinishedEntity gameFinishedEntity = intent.getParcelableExtra(Conf.GAME_FINISHED_ENTITY);
        //获取游戏标题（从首页Banner进来）
        String title = intent.getStringExtra(Conf.BANNER_TITLE);
        m_title = "";
        if (gameDisplayEntity != null) {
            //            Log.i("jason", "接收的游戏列表的实体类：" + gameDisplayEntity);
            gameType = gameDisplayEntity.getType();
            m_title = gameDisplayEntity.getTitle();
        }
        if (gameDetailsResponse != null) {
            //            Log.i("jason", "接收的游戏详情的实体类：" + gameDetailsResponse);
            gameType = gameDetailsResponse.type;
            m_title = gameDetailsResponse.title;
        }
        if (gamePlayingEntity != null) {
            //            Log.i("jason", "接收的正在玩的游戏实体类：" + gamePlayingEntity);
            gameType = gamePlayingEntity.getType();
            m_title = gamePlayingEntity.getName();
        }
        if (gameFinishedEntity != null) {
            //            Log.i("jason", "接收的完成的游戏实体类：" + gameFinishedEntity);
            gameType = gameFinishedEntity.getType();
            m_title = gameFinishedEntity.getName();
        }
        if (title != null) {
            m_title = title;
        }

        //        ThemeLine gameRecord = RunApplication.getInstance().getPlayingThemeLine();


        gameZipUtils = new GameZipUtils();
        String parsedFilePath = gameZipUtils.isParsedFileExist(gameId);
        if (parsedFilePath != null) {
            unZippedDir = parsedFilePath;

            //之前有的直接使用
            if (gameRecord != null && Long.parseLong(gameRecord.game_id) == gameId) {

            } else {
                boolean isSuccess = gameZipUtils.transformParsedFileToEntity(parsedFilePath);
                if (isSuccess) {

                    //                    RunApplication.getInstance().setgetPlayingThemeLine(gameZipUtils.mThemeLine);
                    //                    gameRecord = RunApplication.getInstance().getPlayingThemeLine();
                    //                    gameRecord.uid = Accounts.getId() + "";//存上账户信息
                    if (gameRecord == null)
                        return;

                    //                    gameRecord.point_list = gameZipUtils.mPoints;


                    //                    //将任务添加到攻击点中去
                    //                    Collections.sort(gameRecord.point_list);
                    //                    //将Task添加到Point中
                    //                    for (Point point : gameRecord.point_list) {
                    //                        point.isNew = false;
                    //                        ArrayList<Task> list_tasks = new ArrayList<>();
                    //                        for (Task task : gameZipUtils.mTasks) {
                    //                            if (task.point_id.equals(point.id)) {
                    //                                list_tasks.add(task);
                    //                            }
                    //                        }
                    //                        point.task_list = list_tasks;
                    //                        //对list_task进行排序
                    //                        Collections.sort(point.task_list);
                    //                    }

                } else {
                    Toast.makeText(GamePlayingActivity.this, "游戏包解析错误", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(GamePlayingActivity.this, "游戏包不存在", Toast.LENGTH_SHORT).show();
        }

        //如果用户是第一次进入此Activity，则显示导航提示
        if (AppData.isFirstInGamePlaying2Activity()) {


            Drawable drawable = BitMapUtils.decodeLargeResourceImage(this.getResources(), R.drawable.game_list_guide);
            if (drawable != null && dragLayout != null && dragview != null) {
                dragview.setImageDrawable(drawable);

                dragLayout.setChildView(dragview);
                dragLayout.setOnDragOverListener(new DragLayout.OnDragOverListener() {
                    @Override
                    public void onDrag() {
                        dragLayout.setVisibility(View.GONE);
                    }
                });

                dragLayout.setVisibility(View.VISIBLE);
            }


        } else {
            if (dragLayout != null)
                dragLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void successDownLoadRecord(GameRecord gameRecord) {
        gamePlayingTitle.setText(m_title + "");
        //根据游戏记录核更新 每个任务点的状态
        initPointStatuByGameRecord(gameRecord);
        setAdapter();
        if (Integer.parseInt(gameRecord.play_statu) == 2) {
            ctv_throungh_game.setVisibility(View.VISIBLE);
        } else {
            ctv_throungh_game.setVisibility(View.GONE);
        }
    }


    private void initPointStatuByGameRecord(GameRecord gameRecord) {

        //        ThemeLine gameRecord = RunApplication.getInstance().getPlayingThemeLine();
        if (RunApplication.gameRecord == null || gameRecord == null)
            return;

        //        //如果不一致返回
        //        if (!gameRecord.id.equals(gameRecord.line_id))
        //            return;

        //        gameRecord.play_statu = gameRecord.play_statu;

        //        if (gameRecord.record_text != null && gameRecord.record_text.size() > 0) {
        //            //将所有完成的章节点设置状态为完成状态
        //            int i = 0;
        //            for (; i < gameRecord.record_text.size(); i++) {
        //                PointRecord pointRecord = gameRecord.record_text.get(i);
        //                //应该是一一对应的
        //                for (int j = 0; j < gameRecord.point_list.size(); j++) {
        //                    Point point = gameRecord.point_list.get(j);
        //                    if (Long.parseLong(point.id) == pointRecord.id) {
        //                        //游戏攻击点的状态更新
        //                        point.status = Integer.parseInt(pointRecord.statu);
        //
        //                        //更新任务状态
        //                        for (int i1 = 0; i1 < pointRecord.task.size(); i1++) {
        //                            TaskRecord task_record = pointRecord.task.get(i1);
        //                            int j1 = 0;
        //                            for (; j1 < point.task_list.size(); j1++) {
        //                                Task task = point.task_list.get(j1);
        //                                if (task_record.id == Integer.parseInt(task.id)) {
        //                                    //                                    task.status = PointTaskStatus
        // .FINISHED;
        //                                    task.ftime = task_record.ftime;
        //                                    task.exp = task_record.exp;
        //                                    break;
        //                                }
        //
        //                            }
        //
        //                            //如果当前点还没有结束，则判断此时处于哪个任务
        //                            if (point.status == PointTaskStatus.PLAYING) {
        //                                if (i1 == pointRecord.task.size() && i1 > 0 && point.task_list.size() >= i1) {
        //                                    //                                    point.task_list.get(i1).status =
        //                                    // PointTaskStatus.PLAYING;
        //                                }
        //                            }
        //
        //                        }
        //
        //                        break;
        //                    }
        //                }
        //            }
        //
        //
        //            //更新下一个点的状态
        //            if (i == gameRecord.record_text.size() && i > 0 && gameRecord.point_list.size() > i) {
        //                //如果当前点已经完成，则标记下一个点的状态和任务
        //                if (gameRecord.point_list.get(i - 1).status == PointTaskStatus.FINISHED) {
        //                    gameRecord.point_list.get(i).status = PointTaskStatus.PLAYING;
        //                    //                    gameRecord.point_list.get(i).task_list.get(0).status =
        // PointTaskStatus.PLAYING;
        //                }
        //            }
        //
        //        } else {
        //            //没有游戏记录，也就是所有攻击点都没有玩过，第一个点标记为正在进行中
        //            Point point = gameRecord.point_list.get(0);
        //            point.status = PointTaskStatus.PLAYING;
        //            //            point.task_list.get(0).status = PointTaskStatus.PLAYING;
        //
        //        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //loadNetWorkData();

        //        ThemeLine gameRecord = RunApplication.getInstance().getPlayingThemeLine();
        //        if (gameRecord == null || gameRecord.play_statu == null || gameRecord.play_statu == "") {
        //            loadNetWorkData();
        //        } else {
        //            //恢复时直接读取本地数据
        //
        //            gamePlayingTitle.setText(m_title + "");
        //            setAdapter();
        //            if (Integer.parseInt(gameRecord.play_statu) == 2) {
        //                ctv_throungh_game.setVisibility(View.VISIBLE);
        //            } else {
        //                ctv_throungh_game.setVisibility(View.GONE);
        //            }
        //
        //        }
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
        PopupWindowUtils.getInstance().showNetExceptionPopupwindow(this, new PopupWindowUtils.OnRetryCallback() {
            @Override
            public void onRetry() {
                loadNetWorkData();
            }
        });
    }

    @Override
    public void failDownLoadRecord() {
        showNetUnConnectDialog();
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

        //        ThemeLine gameRecord = RunApplication.getInstance().getPlayingThemeLine();
        if (gameRecord == null)
            return;

        initTimeTask();
        initToast();
        //        adapter = new GVForGamePlayingAdapter(this, gameRecord.point_list, unZippedDir);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //                ThemeLine gameRecord = RunApplication.getInstance().getPlayingThemeLine();
                if (gameRecord == null)
                    return;
                currentPointIndex = i;
                //                currentPoint = gameRecord.point_list.get(i);
                if (currentPoint.isNew) {
                    currentPoint.isNew = false;
                }
                adapter.notifyDataSetChanged();
                //                Log.i("jason", "点击的point：" + currentPoint);
                if (currentPoint.status == PointTaskStatus.UNSTARTED) {
                    //控制Toast的显示
                    if (toast != null) {
                        //                        Log.i("jason", "执行了");
                        toast.show();
                        toast = null;
                        if (thread == null) {
                            initTimeTask();
                        }
                    }
                    return;
                }

                Intent intent = new Intent(GamePlayingActivity.this, PointTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(Conf.POINT, currentPointIndex);
                bundle.putLong(Conf.GAME_ID, gameId);
                //                bundle.putLong(Conf.GAME_TYPE, gameType);
                bundle.putString(Conf.UNZIPPED_DIR, unZippedDir);
                intent.putExtras(bundle);
                startActivityForResult(intent, RequestCode.SKIP_TO_TASK_ACTIVITY);
            }
        });
    }

    private void initToast() {
        toast = Toast.makeText(GamePlayingActivity.this, "该章节点未解锁", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.SKIP_TO_TASK_ACTIVITY && resultCode == ResultCode.POINT_OVER && currentPoint
                .status != PointTaskStatus.FINISHED) {

        }
        //放弃游戏回调
        if (requestCode == RequestCode.LEAVE_GAME && resultCode == ResultCode.LEAVE_GAME) {

            setResult(ResultCode.LEAVE_GAME);
            finish();
        }
    }


    @OnClick({R.id.img_game_detail, R.id.img_zoom_full_screen, R.id.back_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_game_detail:
                //进入游戏详情页面
                Intent intent = new Intent(GamePlayingActivity.this, GameDetailsActivity.class);
                intent.putExtra(Conf.GAME_ID, gameId);
                intent.putExtra(Conf.FROM_WHERE, Conf.GAME_PLAYing_V2_ACTIVITY);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dragview != null && dragview.getDrawable() != null) {

            Bitmap oldBitmap = ((BitmapDrawable) dragview.getDrawable()).getBitmap();

            dragview.setImageDrawable(null);


            if (oldBitmap != null) {

                oldBitmap.recycle();

                oldBitmap = null;

            }

        }

    }

    //上传游戏记录后回调
    @Override
    public void successUpLoadRecord() {
    }
}
