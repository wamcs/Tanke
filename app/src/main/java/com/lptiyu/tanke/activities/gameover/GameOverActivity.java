package com.lptiyu.tanke.activities.gameover;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.entity.response.GameOverReward;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.ShareHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lptiyu.tanke.R.id.tv_add_exp;
import static com.lptiyu.tanke.R.id.tv_add_score;
import static com.lptiyu.tanke.utils.ScreenShotUtils.screenShot;

public class GameOverActivity extends MyBaseActivity implements GameOverContact.IGameOverView {

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
    @BindView(R.id.rl_red_wallet)
    RelativeLayout rlRedWallet;

    private Handler handler = new Handler();
    private int progress = 0;
    private int MAX_PROGRESS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                startAnime();
            }
        });

        GameOverPresenter presenter = new GameOverPresenter(this);
        presenter.loadGameOverReward(RunApplication.gameId);
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

    @OnClick({R.id.img_close, R.id.img_wechat_share, R.id.img_qq_share, R.id.img_wechat_moment_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                finish();
                break;
            case R.id.img_wechat_share:
                ShareHelper.shareImage(ShareHelper.SHARE_WECHAT_FRIENDS, screenShot(this));
                break;
            case R.id.img_qq_share:
                ShareHelper.shareImage(ShareHelper.SHARE_QQ, screenShot(this));
                break;
            case R.id.img_wechat_moment_share:
                ShareHelper.shareImage(ShareHelper.SHARE_WECHAT_CIRCLE, screenShot(this));
                break;
        }
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
            tvRedWalletValue.setText(gameOverReward.extra_money + "元现金红包");
        }
    }
}
