package com.lptiyu.tanke.gamedisplay;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.gameplaying2.GamePlaying2Activity;
import com.lptiyu.tanke.adapter.BannerPagerAdapter;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.enums.GameType;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.gamedetails.GameDetailsActivity;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.GameDisplayEntity;
import com.lptiyu.tanke.pojo.GetGameStatusResponse;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.ShaPrefer;
import com.lptiyu.tanke.utils.XUtilsDownloader;
import com.lptiyu.tanke.utils.xutils3.RequestParamsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsRequestCallBack;
import com.lptiyu.tanke.utils.xutils3.XUtilsUrls;
import com.lptiyu.tanke.utils.xutils3.response.Banner;
import com.lptiyu.tanke.utils.xutils3.response.BannerResponse;
import com.lptiyu.tanke.widget.CircularImageView;
import com.lptiyu.tanke.widget.CustomTextView;

import org.xutils.http.RequestParams;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/1
 * <p/>
 * 控制RecyclerView的首部
 *
 * @author ldx
 */
public class ElasticHeaderViewHolder extends BaseViewHolder<GameDisplayEntity> {

    @BindView(R.id.left_image_view)
    CircularImageView leftImageView;

    @BindView(R.id.middle_image_view)
    CircularImageView middleImageView;

    @BindView(R.id.right_image_view)
    CircularImageView rightImageView;

    @BindView(R.id.left_title)
    CustomTextView leftTitle;

    @BindView(R.id.middle_title)
    CustomTextView middleTitle;

    @BindView(R.id.right_title)
    CustomTextView rightTitle;

    GameDisplayFragment fragment;

    android.app.AlertDialog m_loadDlg;

    //    @BindView(R.id.jelly_view)
    //    ElasticHeaderLayout elasticHeaderLayout;

    ObjectAnimator animator = new ObjectAnimator();

    List<GameDisplayEntity> gameDisplayEntities;
    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.rg_dot)
    RadioGroup rgDot;
    @BindView(R.id.left_img_game_type)
    ImageView leftImgGameType;
    @BindView(R.id.middle_img_game_type)
    ImageView middleImgGameType;
    @BindView(R.id.right_img_game_type)
    ImageView rightImgGameType;
    private ProgressDialog progressDialog;
    private GameDisplayEntity gameDisplayEntity;

    //    private GameZipScanner mGameZipScanner;
    private List<Banner> list_banner;
    private BannerPagerAdapter pagerAdapter;

    //    private final int EMPTY_MSG_FLAG = 0;
    //    private final long DELAY = 2000;
    //    private boolean isStop = false;
    //    private Handler handler = new Handler() {
    //        @Override
    //        public void handleMessage(Message msg) {
    //            super.handleMessage(msg);
    //            switch (msg.what) {
    //                case EMPTY_MSG_FLAG:
    //                    if (++currentIndex < list_banner.size()) {
    //                        vp.setCurrentItem(currentIndex);
    //                    } else {
    //                        vp.setCurrentItem(0);
    //                    }
    //                    break;
    //            }
    //        }
    //    };
    private int currentIndex;
    //    private String gameZipUrl;

    public ElasticHeaderViewHolder(ViewGroup parent, GameDisplayFragment fragment) {
        super(fromResLayout(parent, R.layout.item_game_display_header));
        ButterKnife.bind(this, itemView);
        this.fragment = fragment;
        startLoadBanner();
        //        init();
    }

    private void startLoadBanner() {
        if (NetworkUtil.checkIsNetworkConnected()) {
            loadBanner();
        } else {
            PopupWindowUtils.getInstance().showNetExceptionPopupwindow(getContext(), new PopupWindowUtils
                    .OnNetExceptionListener() {
                @Override
                public void onClick(View view) {
                    startLoadBanner();
                }
            });
        }
    }

    public void loadBanner() {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.GET_BANNER);
        String cityCode = ShaPrefer.getCity().getId();
        Log.i("jason", "cityCode:" + cityCode);
        params.addBodyParameter("city_code", cityCode + "");
        XUtilsHelper.getInstance().get(params, new
                XUtilsRequestCallBack<BannerResponse>() {
                    @Override
                    protected void onSuccess(BannerResponse response) {
                        if (response.status == Response.RESPONSE_OK && response.data.size() > 0) {
                            list_banner = response.data;
                            //动态绘制RadioButton
                            //                            drawRadioButton(list_banner.size());
                            setVPAdapter();
                            //                            startAutoPlay();
                        }
                    }

                    @Override
                    protected void onFailed(String errorMsg) {
                        //TODO 请求失败处理
                    }
                }, BannerResponse.class);
    }

    //    private void startAutoPlay() {
    //        new Thread(new Runnable() {
    //            @Override
    //            public void run() {
    //                while (!isStop) {
    //                    //每个两秒钟发一条消息到主线程，更新viewpager界面
    //                    SystemClock.sleep(DELAY);
    //                    handler.sendEmptyMessageDelayed(EMPTY_MSG_FLAG, DELAY);
    //                }
    //            }
    //        }).start();
    //    }
    //
    //    /**
    //     * 动态绘制RadioButton
    //     *
    //     * @param count
    //     */
    //    private void drawRadioButton(int count) {
    //        for (int i = 0; i < count; i++) {
    //            RadioButton rb = new RadioButton(getContext());
    //            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(20, 20);
    //            params.setMargins(10, 10, 10, 10);
    //            //            rb.setWidth(20);
    //            //            rb.setHeight(20);
    //            rb.setLayoutParams(params);
    //            rb.setButtonDrawable(android.R.color.transparent);
    //            rb.setBackgroundResource(R.drawable.selector_rb_dot);
    //            rgDot.addView(rb);
    //        }
    //        ((RadioButton) rgDot.getChildAt(0)).setChecked(true);
    //    }

    private void setVPAdapter() {
        pagerAdapter = new BannerPagerAdapter(getContext(), list_banner);
        vp.setAdapter(pagerAdapter);
        vp.setCurrentItem(0);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //                currentIndex = position;
                //                ((RadioButton) rgDot.getChildAt(position)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.left_image_view)
    public void left_image_view() {
        jump(1);
    }

    @OnClick(R.id.middle_image_view)
    public void middle_image_view() {
        jump(0);
    }

    @OnClick(R.id.right_image_view)
    public void right_image_view() {
        jump(2);
    }

    public void jump(int index) {
        if (gameDisplayEntities == null || gameDisplayEntities.size() < index + 1 || gameDisplayEntities.get(index)
                == null) {
            return;
        }

        gameDisplayEntity = gameDisplayEntities.get(index);
        int play_statu = gameDisplayEntity.getPlayStatu();
        Log.i("jason", "gameDisplayEntity.getPlayStatu():" + gameDisplayEntity.getPlayStatu());
        String tempGameZipUrl = gameDisplayEntity.getGameZipUrl();
        if (play_statu == PlayStatus.NO_STATUS) {
            loadNetWorkData(index);
        } else if (play_statu != PlayStatus.NEVER_ENTER_GANME && (tempGameZipUrl == null || tempGameZipUrl.isEmpty())
                ) {//为-1时，本身就是没有返回游戏包，因为路径还没有确定
            loadNetWorkData(index);
        } else//如果之前已经请求过状态了，就记录下来，避免每次网络请求
        {
            startGameByPlayStatu(play_statu);
        }


    }

    private void loadNetWorkData(int index) {
        if (NetworkUtil.checkIsNetworkConnected()) {
            startActivity(index);
        } else {
            showNetUnConnectDialog(index);
        }
    }

    private void startGameByPlayStatu(int play_statu) {
        //判断游戏状态
        switch (play_statu) {
            case PlayStatus.NEVER_ENTER_GANME://从未玩过游戏，进入到游戏详情界面
                Intent intent = new Intent();
                intent.setClass(getContext(), GameDetailsActivity.class);
                intent.putExtra(Conf.GAME_ID, gameDisplayEntity.getId());
                intent.putExtra(Conf.FROM_WHERE, Conf.ElasticHeaderViewHolder);
                getContext().startActivity(intent);
                break;
            case PlayStatus.GAME_OVER://游戏结束，暂不考虑
                //TODO 需要进入到游戏完成界面
            case PlayStatus.HAVE_ENTERED_bUT_NOT_START_GAME://进入过但没开始游戏，进入到玩游戏界面
            case PlayStatus.HAVE_STARTED_GAME://进入并且已经开始游戏，进入到玩游戏界面
                //进入到玩游戏界面之前，先检测游戏包是否存在，存在则直接进入，否则要先下载游戏包
                String tempGameZipUrl = gameDisplayEntity.getGameZipUrl();
                if (tempGameZipUrl == null || tempGameZipUrl == "")
                    return;
                new XUtilsDownloader(getContext(), tempGameZipUrl, gameDisplayEntity.getId(), new
                        XUtilsDownloader.FinishDownloadCallback() {
                            @Override
                            public void onFinishedDownload() {
                                startPlayingGame();
                            }
                        });
                break;
            default:
                break;
        }

    }

    // 网络异常对话框
    private void showNetUnConnectDialog(final int index) {
        PopupWindowUtils.getInstance().showNetExceptionPopupwindow(getContext(), new PopupWindowUtils
                .OnNetExceptionListener() {
            @Override
            public void onClick(View view) {
                loadNetWorkData(index);
            }
        });
    }

    private void startActivity(int index) {

        //请求游戏状态接口获取游戏状态，如果是进入过游戏，则直接跳转到玩游戏界面，如果这是第一次玩，则进入到游戏详情界面
        gameDisplayEntity = gameDisplayEntities.get(index);
        HttpService.getGameService().getGameStatus(Accounts.getId(), gameDisplayEntity.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<GetGameStatusResponse>>() {
                    @Override
                    public void call(Response<GetGameStatusResponse> response) {
                        if (response.getStatus() == Response.RESPONSE_OK) {


                            String tempGameZipUrl = "";
                            if (response.getData().game_zip != null) {
                                tempGameZipUrl = response.getData().game_zip;
                            }

                            gameDisplayEntity.setGameZipUrl(tempGameZipUrl);
                            gameDisplayEntity.setPlayStatu(response.getData().play_statu);
                            startGameByPlayStatu(gameDisplayEntity.getPlayStatu());

                        } else {
                            Log.i("jason", "获取游戏状态错误信息：" + response.getInfo());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i("jason", "获取游戏状态错误信息throw：" + throwable.getMessage());
                    }
                });
    }

    //    private void init() {
    //        animator.setInterpolator(new ElasticOutInterpolator());
    //        animator.setTarget(elasticHeaderLayout);
    //        animator.setPropertyName("percent");
    //        animator.setDuration(400);
    //    }
    //
    //    public void setPercent(float percent) {
    //        elasticHeaderLayout.setPercent(percent);
    //    }
    //
    //    public float getPercent() {
    //        return elasticHeaderLayout.getPercent();
    //    }
    //
    //    public void smoothBack() {
    //        if (elasticHeaderLayout.getPercent() == 0 && !animator.isRunning()) {
    //            return;
    //        }
    //        animator.cancel();
    //        animator.setFloatValues(elasticHeaderLayout.getPercent(), 0);
    //        animator.start();
    //    }

    public void bind(List<GameDisplayEntity> entities) {
        if (entities == null || entities.size() == 0) {
            hideHeader();
            return;
        }
        showHeader();
        bindEntity(middleImageView, middleImgGameType, middleTitle, entities.size() < 1 ? null : entities.get(0));
        bindEntity(leftImageView, leftImgGameType, leftTitle, entities.size() < 2 ? null : entities.get(1));
        bindEntity(rightImageView, rightImgGameType, rightTitle, entities.size() < 3 ? null : entities.get(2));
        this.gameDisplayEntities = entities;
    }

    public void bindEntity(ImageView imageView, ImageView imgGameType, TextView textView, GameDisplayEntity entity) {
        if (entity == null) {
            return;
        }
        Glide.with(fragment).load(entity.getImg())
                .error(R.drawable.default_pic)
                .into(imageView);
        textView.setText(entity.getTitle());
        if (entity.getType() == GameType.TEAM_TYPE) {
            imgGameType.setVisibility(View.VISIBLE);
        } else {
            imgGameType.setVisibility(View.GONE);
        }
    }

    @Override
    public void bind(GameDisplayEntity entity) {

    }

    private void hideHeader() {
        if (middleImageView != null) {
            middleImageView.setVisibility(View.GONE);
        }
        if (middleTitle != null) {
            middleTitle.setVisibility(View.GONE);
        }
        if (leftImageView != null) {
            leftImageView.setVisibility(View.GONE);
        }
        if (leftTitle != null) {
            leftTitle.setVisibility(View.GONE);
        }
        if (rightImageView != null) {
            rightImageView.setVisibility(View.GONE);
        }
        if (rightTitle != null) {
            rightTitle.setVisibility(View.GONE);
        }
    }

    private void showHeader() {
        if (middleImageView != null) {
            middleImageView.setVisibility(View.VISIBLE);
        }
        if (middleTitle != null) {
            middleTitle.setVisibility(View.VISIBLE);
        }
        if (leftImageView != null) {
            leftImageView.setVisibility(View.VISIBLE);
        }
        if (leftTitle != null) {
            leftTitle.setVisibility(View.VISIBLE);
        }
        if (rightImageView != null) {
            rightImageView.setVisibility(View.VISIBLE);
        }
        if (rightTitle != null) {
            rightTitle.setVisibility(View.VISIBLE);
        }
    }

    public void startPlayingGame() {
        Intent intent = new Intent(getContext(), GamePlaying2Activity.class);
        intent.putExtra(Conf.GAME_ID, gameDisplayEntity.getId());
        intent.putExtra(Conf.GAME_DISPLAY_ENTITY, gameDisplayEntity);
        fragment.startActivity(intent);
    }


    /**
     * 请求游戏记录弹出的对话框
     */
    private void showLoadDlg() {
        if (m_loadDlg == null) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, null);
            TextView textView = ((TextView) view.findViewById(R.id.loading_dialog_textview));
            textView.setText(getContext().getString(R.string.loading));
            m_loadDlg = new android.app.AlertDialog.Builder(getContext())
                    .setCancelable(true)
                    .setView(view)
                    .create();
        }
        m_loadDlg.show();
    }
}
