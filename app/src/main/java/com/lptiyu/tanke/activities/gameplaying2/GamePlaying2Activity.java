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

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.baidumapmode.GameMapShowActivity;
import com.lptiyu.tanke.activities.guessriddle.GuessRiddleActivity;
import com.lptiyu.tanke.adapter.GVForGamePlayingAdapter;
import com.lptiyu.tanke.database.DBPointRecord;
import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.enums.GameRecordAndPointStatus;
import com.lptiyu.tanke.enums.GameType;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.pojo.UpLoadGameRecord;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.WebViewUtils;
import com.lptiyu.tanke.widget.CustomTextView;
import com.lptiyu.zxinglib.android.CaptureActivity;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GamePlaying2Activity extends Activity implements GamePlayingContract.IGamePlayingView {

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.game_playing_title)
    CustomTextView gamePlayingTitle;
    @BindView(R.id.gv)
    GridView gv;

    long teamId;
    long gameId;


    AlertDialog parseGameZipErrorDialog;
    AlertDialog loadGameRecordDialog;

    private GamePlayingPresenter presenter;
    private List<Point> list_points;
    //    LocateHelper locateHelper;
    //    private double latitude;
    //    private double longitude;
    private String unZippedDir;
    private GameRecord gameRecord;

    //    GameDetailsEntity entity;

    private Task currentTask;
    private Point currentPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_playing2);
        ButterKnife.bind(this);

        presenter = new GamePlayingPresenter(this);

        showLoadingDialog();
        //接受从游戏详情传过来的数据
        presenter.initData(this);
    }

    private void getLongitudeAndlatitude() {
        //        //获取当前位置的经纬度
        //        locateHelper = new LocateHelper(this);
        //        locateHelper.startLocate();
        //        locateHelper.registerLocationListener(new BDLocationListener() {
        //            @Override
        //            public void onReceiveLocation(BDLocation bdLocation) {
        //                longitude = bdLocation.getLongitude();//获取当前经度
        //                latitude = bdLocation.getLatitude();//获取当前纬度
        //                //                Log.i("jason", "当前经纬度：longitude:" + longitude + " ,latitude:" + latitude);
        //            }
        //        });
    }


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

    int times = 0;

    public void click(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.btn_baiduMapMode:
                Intent intent = new Intent(GamePlaying2Activity.this, GameMapShowActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_submitRecord:
                if (times >= list_points.size()) {
                    times = 0;
                }
                UpLoadGameRecord record = new UpLoadGameRecord();
                record.uid = Accounts.getId();
                record.game_id = gameId;
                record.play_statu = PlayStatus.HAVE_STARTED_GAME;
                record.type = GameType.INDIVIDUAL_TYPE;
                record.point_id = list_points.get(times++).getId();
                record.task_id = Long.parseLong(list_points.get(times++).getTaskId().get(0));
                record.point_statu = GameRecordAndPointStatus.FINISHED;//攻击点状态
                presenter.upLoadRecord(record);
                break;
        }
    }

    /**
     * 上传游戏记录
     */
    private void uploadRecord(long play_statu) {
        UpLoadGameRecord record = new UpLoadGameRecord();
        record.uid = Accounts.getId();
        record.game_id = gameId;
        record.play_statu = play_statu;
        Log.i("jason", "当前游戏类型：" + gameType);
        record.type = gameType;
        record.point_id = currentPoint.getId();
        record.task_id = currentTask.getId();
        record.point_statu = GameRecordAndPointStatus.FINISHED;//因为一个章节点只有一个任务，所以一旦该攻击点任务完成，该任务点就完成
        presenter.upLoadRecord(record);
    }

    @Override
    public void successDownLoadRecord(GameRecord gameRecord) {
        if (loadGameRecordDialog != null)
            loadGameRecordDialog.dismiss();
        this.gameRecord = gameRecord;
        //根据请求的游戏记录核对每个任务点的状态
        checkGameRecordAndPointStatus();
        Log.i("jason", "游戏点信息：" + list_points);
        setAdapter();
    }

    /**
     * 请求游戏记录失败时回调
     */
    @Override
    public void failLoadRecord() {
        if (loadGameRecordDialog != null) {
            loadGameRecordDialog.dismiss();
        }
    }

    //上传游戏记录后回调
    @Override
    public void successUpLoadRecord() {
        //将该任务的游戏记录存储到本地
        presenter.insertTask(currentTask.getId(), currentTask.getExp());
        //将该章节点的游戏记录存储到本地
        presenter.insertPoint(currentPoint.getId(), GameRecordAndPointStatus.FINISHED);

        //查询当前游戏是否完成
        if (list_points == null) {
            throw new RuntimeException("nul points");
        }
        boolean isCurrentGameFinished = presenter.isCurrentGameFinished(list_points.size());
        if (isCurrentGameFinished) {
            //将当前游戏记录上传到服务器
            UpLoadGameRecord record = new UpLoadGameRecord();
            record.play_statu = PlayStatus.GAME_OVER;
            record.task_id = currentTask.getId();
            record.game_id = gameId;
            record.point_id = currentPoint.getId();
            record.type = gameType;
            record.uid = Accounts.getId();
            presenter.gameOver(record);
        }
    }

    //游戏结束时上传游戏记录完成后回调
    @Override
    public void gameOver() {
        //将当前游戏记录存到数据库中
        presenter.insertGameRecord(gameId, GameRecordAndPointStatus.FINISHED);
    }

    private void setAdapter() {
        gv.setAdapter(new GVForGamePlayingAdapter(this, list_points, unZippedDir));
        setListener();
    }

    private void setListener() {
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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
            ctv_getAnser.setText("已完成，经验值+" + task.getExp());
            ctv_getAnser.setClickable(false);
            img_indicate.setImageResource(R.drawable.done);
        }
        if (point.getState() == GameRecordAndPointStatus.UNFINISHED) {
            ctv_getAnser.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            ctv_getAnser.getPaint().setAntiAlias(true);//抗锯齿
            ctv_getAnser.setTextColor(getResources().getColor(R.color.colorPrimary));
            ctv_getAnser.setText("寻找答案");
            ctv_getAnser.setClickable(true);
            img_indicate.setImageResource(R.drawable.indicate_clue);
        }
        WebViewUtils.setWebView(this, webView);
        webView.loadUrl(task.getContent());
        final AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();
        dialog.show();

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ctv_getAnser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (task.getType() == Task.TASK_TYPE.TIMING) {//限时任务
                    Log.i("jason", "限时任务");
                }
                if (task.getType() == Task.TASK_TYPE.DISTINGUISH) {//拍照上传任务
                    Log.i("jason", "拍照任务");
                }
                if (task.getType() == Task.TASK_TYPE.LOCATE) {//定位任务
                    Log.i("jason", "定位任务");
                    //这里只是为了测试才这么写的，实际上是进入定位任务的界面
                    Intent intent = new Intent(GamePlaying2Activity.this, CaptureActivity.class);
                    startActivityForResult(intent, CAMERA_PERMISSION_REQUEST_CODE);
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
                    intent.putExtra("task", task);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
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
                Toast.makeText(this, "任务完成", Toast.LENGTH_SHORT).show();
                //将游戏记录存储到数据库中
                saveGameRecord();

            } else {
                Toast.makeText(this, "答案错误，任务失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 将游戏记录存储到本地数据库
     */
    private void saveGameRecord() {
        //同时将该任务的游戏记录上传到服务器
        uploadRecord(PlayStatus.HAVE_STARTED_GAME);
    }

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;

    @Override
    public void checkZipExistOver() {
        showErrorDialog();
    }

    private boolean isPlayingStateExist = false;
    private int maxFinishedIndex;

    /**
     * 根据获取的游戏记录，确定每个任务点的状态
     */
    private void checkGameRecordAndPointStatus() {
        //        List<DBPointRecord> list_records = gameRecord.getRecord_text();
        Log.i("jason", "服务器获取的游戏记录：" + gameRecord);
        if (gameRecord != null && gameRecord.getRecord_text() != null && gameRecord.getRecord_text().size() != 0) {
            for (int i = 0; i < gameRecord.getRecord_text().size(); i++) {
                DBPointRecord record = gameRecord.getRecord_text().get(i);
                long pointId = record.getId();
                for (int j = 0; j < list_points.size(); j++) {
                    Point point = list_points.get(j);
                    if (point.getId() == pointId) {
                        if (record.getStatu().equals(GameRecordAndPointStatus.UNFINISHED)) {
                            point.setState(GameRecordAndPointStatus.UNFINISHED);
                            isPlayingStateExist = true;
                        } else {
                            point.setState(GameRecordAndPointStatus.FINISHED);
                            maxFinishedIndex = j;
                        }
                    } else {
                        point.setState(GameRecordAndPointStatus.UNSTARTED);
                    }
                }
            }
            //通过游戏记录为每个攻击点确定好状态后还要再遍历一遍，看看所有攻击点中是不是只有已完成和未开启的，如果是，这要将最后一个已完成后面的那个攻击点的状态设置为进行中
            if (!isPlayingStateExist) {
                list_points.get(maxFinishedIndex + 1).setState(GameRecordAndPointStatus.UNFINISHED);
            }
        } else {
            //没有游戏记录，也就是所有攻击点都没有玩过
            for (int i = 0; i < list_points.size(); i++) {
                Point point = list_points.get(i);
                if (i == 0) {
                    point.setState(GameRecordAndPointStatus.UNFINISHED);
                } else {
                    point.setState(GameRecordAndPointStatus.UNSTARTED);
                }
            }
        }
    }

    @Override
    public void getData(List<Point> list_points, String unZippedDir, GameRecord gameRecord, long
            gameId, long gameType, String gameName) {
        this.list_points = list_points;
        this.unZippedDir = unZippedDir;
        this.gameRecord = gameRecord;
        this.gameId = gameId;
        this.gameType = gameType;
        gamePlayingTitle.setText(gameName + "");

        //        //请求进入游戏接口
        //        presenter.enterGame();
        presenter.downLoadRecord();
    }

    long gameType;

    /**
     * 进入游戏成功回调
     */
    @Override
    public void successEnterGame() {
        presenter.downLoadRecord();
    }
}
