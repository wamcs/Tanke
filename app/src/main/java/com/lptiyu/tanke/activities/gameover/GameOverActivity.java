package com.lptiyu.tanke.activities.gameover;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.entity.response.GameOverReward;
import com.lptiyu.tanke.entity.response.GetScoreAfterShare;
import com.lptiyu.tanke.enums.Where;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.DisplayUtils;
import com.lptiyu.tanke.utils.ImageWaterMarkerUtils;
import com.lptiyu.tanke.utils.LogUtils;
import com.lptiyu.tanke.utils.ShareHelper;

import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

import static com.lptiyu.tanke.R.id.tv_add_exp;
import static com.lptiyu.tanke.R.id.tv_add_score;
import static com.lptiyu.tanke.utils.ScreenShotUtils.screenShot;

public class GameOverActivity extends MyBaseActivity implements GameOverContact.IGameOverView, PlatformActionListener {

    @BindView(R.id.tv_game_name)
    TextView tvGameName;
    @BindView(R.id.tv_game_introduction)
    TextView tvGameIntroduction;
    @BindView(tv_add_score)
    TextView tvAddScore;
    @BindView(R.id.progressBar_score_value)
    ProgressBar progressBarScoreValue;
    @BindView(tv_add_exp)
    TextView tvAddExp;
    @BindView(R.id.progressBar_exp_value)
    ProgressBar progressBarExpValue;
    @BindView(R.id.tv_red_wallet_value)
    TextView tvRedWalletValue;
    @BindView(R.id.tv_share_score_tip)
    TextView tvShareScoreTip;
    @BindView(R.id.rl_red_wallet)
    RelativeLayout rlRedWallet;
    @BindView(R.id.img_close)
    ImageView imgClose;
    @BindView(R.id.rl_share_tip)
    RelativeLayout rlShareScoreTip;
    @BindView(R.id.ll_share_platform_list)
    LinearLayout llPlatformList;

    private Handler handler = new Handler();
    private int progress = 0;
    private int MAX_PROGRESS = 1000;
    private GameOverPresenter presenter;
    private Bitmap source;
    private Bitmap waterMarker;
    private Bitmap waterMarkerBitmap;
    private long recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                startAnime();
            }
        }, Conf.POST_DELAY);

        initData();

        presenter = new GameOverPresenter(this);
        presenter.loadGameOverReward(RunApplication.gameId, recordId);
    }

    private void initData() {
        if (RunApplication.where == Where.GAME_FINISH) {
            recordId = RunApplication.recordId;
        } else {
            recordId = -1;
        }
        if (isFirstShareOnCurrentDay()) {
            tvShareScoreTip.setVisibility(View.VISIBLE);
        } else {
            tvShareScoreTip.setVisibility(View.GONE);
        }
    }

    private boolean isFirstShareOnCurrentDay() {
        //每天只能获取一次分享积分
        int dayIndex = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        //如果当前的天数跟存储的天数不相等，则表示用户上一次获取分享积分是在昨天
        if (dayIndex != Accounts.getDayIndex()) {
            return true;
        } else {
            if (!Accounts.isShareScoreGot()) {
                return true;
            } else {
                return false;
            }
        }
    }

    private void startAnime() {
        progressBarScoreValue.setMax(MAX_PROGRESS);
        progressBarExpValue.setMax(MAX_PROGRESS);
        progress = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progress <= MAX_PROGRESS) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBarScoreValue.setProgress(progress);
                            progressBarExpValue.setProgress(progress);
                            progress++;
                        }
                    });
                }
            }
        }).start();
    }

    private int platform = 0;
    private ProgressDialog shareDialog;

    @OnClick({R.id.img_close, R.id.img_wechat_share, R.id.img_qq_share, R.id.img_wechat_moment_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                finish();
                break;
            case R.id.img_wechat_share:
                platform = ShareHelper.SHARE_WECHAT_FRIENDS;
                share();
                break;
            case R.id.img_qq_share:
                platform = ShareHelper.SHARE_QQ;
                share();
                break;
            case R.id.img_wechat_moment_share:
                platform = ShareHelper.SHARE_WECHAT_CIRCLE;
                share();
                break;
        }
    }

    private void share() {
        rlShareScoreTip.setVisibility(View.GONE);
        llPlatformList.setVisibility(View.GONE);
        imgClose.setVisibility(View.GONE);
        shareDialog = ProgressDialog.show(this, "", "加载中...", true, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap waterMarkerBitmap = getWaterMarkerBitmap();//此步骤比较耗时，最好放到子线程中操作
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShareHelper.shareImage(platform, waterMarkerBitmap, GameOverActivity.this);
                        rlShareScoreTip.setVisibility(View.VISIBLE);
                        llPlatformList.setVisibility(View.VISIBLE);
                        imgClose.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (shareDialog != null && shareDialog.isShowing()) {
            shareDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseBitmap();
    }

    private void releaseBitmap() {
        if (source != null && !source.isRecycled()) {
            source.recycle();
            source = null;
        }
        if (waterMarker != null && !waterMarker.isRecycled()) {
            waterMarker.recycle();
            waterMarker = null;
        }
        if (waterMarkerBitmap != null && !waterMarkerBitmap.isRecycled()) {
            waterMarkerBitmap.recycle();
            waterMarkerBitmap = null;
        }
        System.gc();
    }

    private Bitmap getWaterMarkerBitmap() {
        String imagePath = screenShot(this);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 2;//像素变为原来的1/2
        source = BitmapFactory.decodeFile(imagePath, options);
        waterMarker = BitmapFactory.decodeResource(getResources(), R.drawable.water_marker, options);
        waterMarkerBitmap = ImageWaterMarkerUtils.createWaterMaskLeftBottom(this, source, waterMarker,
                DisplayUtils.dp2px(10), 0);
        return waterMarkerBitmap;
        //        if (waterMarkerBitmap != null) {
        //            try {
        //                FileOutputStream out = new FileOutputStream(imagePath);
        //                waterMarkerBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        //                LogUtils.i("水印添加成功：" + imagePath);
        //                return imagePath;
        //            } catch (FileNotFoundException e) {
        //                e.printStackTrace();
        //            }
        //        }
        //        return null;
    }

    @Override
    public void successLoadGameOverReward(GameOverReward gameOverReward) {
        if (gameOverReward != null) {
            bindData(gameOverReward);
        } else {
            Toast.makeText(this, "没有获取到奖励", Toast.LENGTH_SHORT).show();
        }
    }

    public void bindData(GameOverReward gameOverReward) {
        tvGameName.setText(gameOverReward.pass_tit + "");
        tvGameIntroduction.setText(gameOverReward.pass_intro + "");
        tvAddScore.setText("+" + gameOverReward.points);
        tvAddExp.setText("+" + gameOverReward.exp);
        if (gameOverReward.extra_money == null || gameOverReward.extra_money.equals("") || gameOverReward.extra_money
                .equals("0")) {
            rlRedWallet.setVisibility(View.GONE);
        } else {
            rlRedWallet.setVisibility(View.VISIBLE);
            tvRedWalletValue.setText(gameOverReward.extra_money + "元现金红包");
        }
    }

    @Override
    public void successGetScore(GetScoreAfterShare getScoreAfterShare) {
        tvShareScoreTip.setVisibility(View.GONE);
        Accounts.setShareScoreGot(true);
        Accounts.setDayIndex(Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
        if (getScoreAfterShare != null) {
            Toast.makeText(this, getScoreAfterShare.tip, Toast.LENGTH_SHORT).show();
        }
    }

    private void getShareScore() {
        if (isFirstShareOnCurrentDay()) {
            presenter.getScoreAfterShare();
        }
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        //分享成功回调
        getShareScore();//分享到QQ或者空间时有个一个bug，分享完毕后，选择返回步道探秘是捕捉不到回调事件的，但是选择留在QQ或空间，再点击返回是可以捕捉得到回调事件的
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        //分享失败回调
        LogUtils.i("GameOverActivity--->onError()");
    }

    @Override
    public void onCancel(Platform platform, int i) {
        //分享取消回调
        LogUtils.i("GameOverActivity--->onCancel()");
    }
}
