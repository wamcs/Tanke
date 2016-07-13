package com.lptiyu.tanke.gameplaying2;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.adapter.GVForGamePlayingAdapter;
import com.lptiyu.tanke.gameplaying.assist.zip.GameZipHelper;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.pojo.GameDetailsEntity;
import com.lptiyu.tanke.widget.CustomTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GamePlaying2Activity extends Activity {

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.game_detail_title)
    CustomTextView gameDetailTitle;
    @BindView(R.id.share_btn)
    ImageView shareBtn;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.gv)
    GridView gv;

    long gameId;
    long teamId;
    String entityName;
    long queryHistoryTrackEndTime;

    GameDetailsEntity mGameDetailsEntity;
    GameZipHelper gameZipHelper;

    AlertDialog mErrorDialog;
    AlertDialog mLoadingDialog;

    private GamePlayingPresenter presenter;
    private List<Point> list_points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_playing2);
        ButterKnife.bind(this);

        presenter = new GamePlayingPresenter(this);

        initData();
    }

    private void initData() {
        //        showLoadingDialog();

        gameZipHelper = new GameZipHelper();

        Intent intent = getIntent();
        //获取gameId
        gameId = intent.getLongExtra(Conf.GAME_ID, Conf.TEMP_GAME_ID);
        //获取游戏详情实体类
        mGameDetailsEntity = intent.getParcelableExtra(Conf.GAME_DETAIL);
        //        queryHistoryTrackEndTime = System.currentTimeMillis() / TimeUtils.ONE_SECOND_TIME;
        //获取团队Id
        teamId = intent.getLongExtra(Conf.TEAM_ID, Conf.TEMP_TEAM_ID);
        //将gameId和teamId格式化为"37_31"，即游戏包名（"37_31_1467789574"）的前两部分
        entityName = Conf.makeUpTrackEntityName(gameId, Accounts.getId());
        //检查游戏包是否存在或者游戏解压后为空，判断完后游戏包已经被解压缩，并且已经将文件解析成实体类对象，此时可以直接从内存中取数据了
        if (!gameZipHelper.checkAndParseGameZip(gameId) || gameZipHelper.getmPoints().size() == 0) {
            //            mLoadingDialog.dismiss();
            showErrorDialog();
            return;
        }
        //获取游戏攻击点集合
        list_points = gameZipHelper.getmPoints();
        //        if (list_points != null) {
        //            Log.i("jason", "攻击点数目：" + list_points.size() + ", 所有攻击点信息：" + list_points);
        //            for (Point point : list_points) {
        //                Log.i("jason", "TaskId:" + point.getTaskId());
        //                Map<String, Task> taskMap = point.getTaskMap();
        //                for (String key : taskMap.keySet()) {
        //                    Log.i("jason", "key= " + key + ", value= " + taskMap.get(key));
        //                }
        //            }
        //        }
        /**
         /storage/emulated/0/Android/data/com.lptiyu.tanke/files/temp/39_33_1467790230
         */
        String unZippedDir = gameZipHelper.unZippedDir;
        Log.i("jason", "解压包绝对路径：" + unZippedDir);

        gv.setAdapter(new GVForGamePlayingAdapter(list_points, unZippedDir, this));

    }

    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.layout_loading, null);
            TextView textView = ((TextView) view.findViewById(R.id.loading_dialog_textview));
            textView.setText(getString(R.string.loading));
            mLoadingDialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setView(view)
                    .create();
        }
        mLoadingDialog.show();
    }

    private void showErrorDialog() {
        if (mErrorDialog == null) {
            mErrorDialog = new AlertDialog.Builder(this)
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

    public void click(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
        }
    }
    //
    //    @Override
    //    public ActivityController getController() {
    //        return null;
    //    }
}
