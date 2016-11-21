package com.lptiyu.tanke.userCenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.initialization.ui.SignUpActivity;
import com.lptiyu.tanke.activities.redwallet.RedWalletActivity;
import com.lptiyu.tanke.activities.scorestore.ScoreStoreActivity;
import com.lptiyu.tanke.base.controller.FragmentController;
import com.lptiyu.tanke.base.ui.BaseFragment;
import com.lptiyu.tanke.entity.eventbus.ReloadUserInfo;
import com.lptiyu.tanke.enums.Platform;
import com.lptiyu.tanke.enums.RequestCode;
import com.lptiyu.tanke.enums.ResultCode;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.net.HttpService;
import com.lptiyu.tanke.net.Response;
import com.lptiyu.tanke.pojo.UserDetails;
import com.lptiyu.tanke.userCenter.ui.ModifyUserInfoActivity;
import com.lptiyu.tanke.userCenter.ui.SettingActivity;
import com.lptiyu.tanke.userCenter.ui.UserGameFinishedListActivity;
import com.lptiyu.tanke.userCenter.ui.UserGamePlayingListActivity;
import com.lptiyu.tanke.activities.userdirectionrunlist.UserDirectionRunListActivity;
import com.lptiyu.tanke.utils.ExpUtils;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.widget.CircularImageView;
import com.lptiyu.tanke.widget.CustomTextView;
import com.lptiyu.tanke.widget.GradientProgressBar;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * author:wamcs
 * date:2016/5/19
 * email:kaili@hustunique.com
 */
public class UserCenterFragment extends BaseFragment {

    @BindView(R.id.user_avatar)
    CircularImageView mUserAvatar;
    @BindView(R.id.user_nickname)
    TextView mUserNickname;
    @BindView(R.id.user_sex)
    ImageView mUserSex;
    @BindView(R.id.user_location)
    TextView mUserLocation;
    @BindView(R.id.user_uid)
    TextView mUserUid;
    @BindView(R.id.user_progress)
    GradientProgressBar mUserProgress;
    @BindView(R.id.user_progress_left)
    TextView mUserProgressLeft;
    @BindView(R.id.user_progress_right)
    TextView mUserProgressRight;
    @BindView(R.id.user_progress_need_exp)
    TextView mUserProgressNeedExp;
    @BindView(R.id.user_game_playing_num)
    TextView mUserGamePlayingNum;
    @BindView(R.id.user_game_finished_num)
    TextView mUserGameFinishedNum;
    @BindView(R.id.tv_platform_tel_info)
    TextView tvPlatformTelInfo;
    @BindView(R.id.rl_popup)
    RelativeLayout rlPopup;
    @BindView(R.id.user_judge_game)
    RelativeLayout mUserJudgeGame;
    @BindView(R.id.default_tool_bar_imageview)
    ImageView defaultToolBarImageview;
    @BindView(R.id.default_tool_bar_textview)
    CustomTextView defaultToolBarTextview;
    @BindView(R.id.tv_score_total)
    TextView tvScore;
    @BindView(R.id.tv_red_wallet_total)
    TextView tvRedWallet;

    private Subscription subscription;
    private UserDetails mUserDetails;
    private boolean isToastShowed;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = fromResLayout(inflater, container, R.layout.fragment_user_center);
        ButterKnife.bind(this, view);
        defaultToolBarImageview.setVisibility(View.GONE);
        defaultToolBarTextview.setText("用户中心");
        return view;
    }

    private void init() {
        subscription = HttpService.getUserService()
                .getUserDetail(Accounts.getId(), Accounts.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<UserDetails>>() {
                    @Override
                    public void call(Response<UserDetails> userDetailsResponse) {
                        if (userDetailsResponse.getStatus() == Response.RESPONSE_OK) {
                            mUserDetails = userDetailsResponse.getData();
                            bind(mUserDetails);
                            EventBus.getDefault().post(new ReloadUserInfo());
                        } else {
                            throw new RuntimeException(userDetailsResponse.getInfo());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (!NetworkUtil.checkIsNetworkConnected()) {
                            if (!isToastShowed) {
                                ToastUtil.TextToast(R.string.no_network);
                                isToastShowed = true;
                            }
                        } else {
                            ToastUtil.TextToast("获取用户信息失败");
                        }
                    }
                });
    }

    private void bind(UserDetails details) {
        if (details == null) {
            return;
        }

        String serverAvatar = details.img;
        loadUserAvatar(serverAvatar);
        Accounts.setNickName(details.name);
        Accounts.setPhoneNumber(details.phone);

        showBindTelTip();

        mUserNickname.setText(details.name);
        mUserLocation.setText(details.address);
        mUserUid.setText(String.valueOf(Accounts.getId()));
        mUserGamePlayingNum.setText(String.valueOf(details.num));
        mUserGameFinishedNum.setText(String.valueOf(details.finish_num));
        tvScore.setText(details.points);
        tvRedWallet.setText(Integer.parseInt(details.money) / 100.0f + "元");
        if (details.task_count > 0) {
            mUserJudgeGame.setVisibility(RelativeLayout.VISIBLE);
        }

        String gender = details.sex;
        if (gender == null || gender.length() == 0) {
            mUserSex.setImageResource(R.mipmap.img_gender_male);
        } else {
            if (gender.equals("男")) {
                mUserSex.setImageResource(R.mipmap.img_gender_male);
            } else {
                mUserSex.setImageResource(R.mipmap.img_gender_female);
            }
        }

        //获取经验值
        parseLevelAndExp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case ResultCode.BIND_TEL:
                showBindTelTip();
                break;
        }
    }

    private void showBindTelTip() {
        String platform = "";
        switch (Accounts.getPlatform()) {
            case Platform.QQ:
                platform = "QQ";
                rlPopup.setVisibility(View.VISIBLE);
                break;
            case Platform.WEIXIN:
                platform = "微信";
                rlPopup.setVisibility(View.VISIBLE);
                break;
            case Platform.TEL:
                rlPopup.setVisibility(View.GONE);
                break;
        }
        String phoneNumber = Accounts.getPhoneNumber();
        Log.i("jason", "Accounts.getPhoneNumber():" + phoneNumber);
        if (phoneNumber == null || phoneNumber.equals("") || phoneNumber.equals("null")) {
            tvPlatformTelInfo.setText(String.format(getContext().getString(R.string.platform_info_unbind_tel)
                    , platform));
            rlPopup.setBackgroundResource(R.color.orange);
            rlPopup.setEnabled(true);
        } else {
            tvPlatformTelInfo.setText(String.format(getContext().getString(R.string.platform_info_bind_tel)
                    , platform, Accounts.getPhoneNumber()));
            rlPopup.setVisibility(View.GONE);
        }
    }

    private void parseLevelAndExp() {
        int currentLevel = mUserDetails.level;
        int currentExp = mUserDetails.experience;
        int nextExp = mUserDetails.experiencelast;

        int currentLevelNeedExp = ExpUtils.calculateExpByLevel(currentLevel);

        mUserProgressLeft.setText(getString(R.string.user_level, currentLevel));
        mUserProgressRight.setText(getString(R.string.user_level, currentLevel + 1));
        mUserProgress.setProgress(((float) (currentExp - currentLevelNeedExp) / (float) (nextExp -
                currentLevelNeedExp)));
        mUserProgressNeedExp.setText(String.format(getString(R.string.need_exp_formatter), currentExp,
                nextExp));
    }

    /**
     * Load the avatar from server and store the url
     *
     * @param serverAvatar avatar url from server
     */
    private void loadUserAvatar(String serverAvatar) {
        Glide.with(this).load(serverAvatar).error(R.mipmap.default_avatar).listener(new RequestListener<String,
                GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean
                    isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                           boolean isFromMemoryCache, boolean isFirstResource) {
                Accounts.setAvatar(model);
                return false;
            }
        }).into(mUserAvatar);
    }

    @OnClick(R.id.user_message_layout)
    void modifyUserInfo() {
        Intent intent = new Intent(getActivity(), ModifyUserInfoActivity.class);
        intent.putExtra(Conf.USER_DETAIL, mUserDetails);
        startActivity(intent);
    }

    @OnClick(R.id.user_game_playing)
    void gamePlayingClicked() {
        startActivity(new Intent(getContext(), UserGamePlayingListActivity.class));
    }

    @OnClick(R.id.user_game_finished)
    void user_game_finished() {
        startActivity(new Intent(getContext(), UserGameFinishedListActivity.class));
    }

    @OnClick(R.id.setting)
    public void setting() {
        startActivity(new Intent(getContext(), SettingActivity.class));
    }

    @OnClick(R.id.user_judge_game)
    public void user_judge_game() {
        startActivity(new Intent(getContext(), UserManagerGameActivity.class));
    }

    @OnClick(R.id.user_direction_run)
    public void user_direction_run() {
        startActivity(new Intent(getContext(), UserDirectionRunListActivity.class));
    }

    @OnClick(R.id.rl_red_wallet)
    public void redWallet() {
        startActivity(new Intent(getContext(), RedWalletActivity.class));
    }

    @OnClick(R.id.rl_score_store)
    public void scoreStore() {
        startActivity(new Intent(getContext(), ScoreStoreActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public FragmentController getController() {
        return null;
    }

    @OnClick({R.id.img_close_popup, R.id.rl_popup})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_close_popup:
                rlPopup.setVisibility(View.GONE);
                break;
            case R.id.rl_popup:
                //跳转到注册页面
                Intent intent = new Intent(getActivity(), SignUpActivity.class);
                intent.putExtra(Conf.SIGN_UP_CODE, Conf.BIND_TEL);
                startActivityForResult(intent, RequestCode.BIND_TEL);
                break;
        }
    }
}
