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
import com.lptiyu.tanke.base.controller.FragmentController;
import com.lptiyu.tanke.base.ui.BaseFragment;
import com.lptiyu.tanke.enums.Platform;
import com.lptiyu.tanke.enums.RequestCode;
import com.lptiyu.tanke.enums.ResultCode;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.initialization.ui.SignUpActivity;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.UserDetails;
import com.lptiyu.tanke.userCenter.ui.ModifyUserInfoActivity;
import com.lptiyu.tanke.userCenter.ui.SettingActivity;
import com.lptiyu.tanke.userCenter.ui.UserGameFinishedListActivity;
import com.lptiyu.tanke.userCenter.ui.UserGamePlayingListActivity;
import com.lptiyu.tanke.utils.ExpUtils;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.widget.CircularImageView;
import com.lptiyu.tanke.widget.GradientProgressBar;

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

    private Subscription subscription;
    private UserDetails mUserDetails;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = fromResLayout(inflater, container, R.layout.fragment_user_center);
        ButterKnife.bind(this, view);
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
                        } else {
                            throw new RuntimeException(userDetailsResponse.getInfo());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (!NetworkUtil.checkIsNetworkConnected()) {
                            ToastUtil.TextToast(R.string.no_network);
                            return;
                        }
                        ToastUtil.TextToast("获取用户信息失败");
                    }
                });
    }

    private void bind(UserDetails details) {
        if (details == null) {
            return;
        }

        String serverAvatar = details.getAvatar();
        checkAndLoadUserAvatar(serverAvatar);
        Accounts.setNickName(details.getNickname());
        Accounts.setPhoneNumber(details.getPhone() + "");

        showBindTelTip();

        mUserNickname.setText(details.getNickname());
        mUserLocation.setText(details.getAddress());
        mUserUid.setText(String.valueOf(Accounts.getId()));
        mUserGamePlayingNum.setText(String.valueOf(details.getPlayingGameNum()));
        mUserGameFinishedNum.setText(String.valueOf(details.getFinishedGameNum()));

        if(details.getTaskCount() > 0)
        {
            mUserJudgeGame.setVisibility(RelativeLayout.VISIBLE);
        }

        String gender = details.getSex();
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
            //            rlPopup.setBackgroundResource(R.color.grey08);
            //            rlPopup.setEnabled(false);
            rlPopup.setVisibility(View.GONE);
        }
    }

    private void parseLevelAndExp() {
        int currentLevel = mUserDetails.getLevel();
        int currentExp = mUserDetails.getExp();
        int nextExp = mUserDetails.getNextExp();

        int currentLevelNeedExp = ExpUtils.calculateExpByLevel(currentLevel);

        mUserProgressLeft.setText(getString(R.string.user_level, currentLevel));
        mUserProgressRight.setText(getString(R.string.user_level, currentLevel + 1));
        mUserProgress.setProgress(((float) (currentExp - currentLevelNeedExp) / (float) (nextExp -
                currentLevelNeedExp)));
        mUserProgressNeedExp.setText(String.format(getString(R.string.need_exp_formatter), currentExp,
                nextExp));
    }

    /**
     * Check the cache url is equal with avatar url from server
     * if the url is match, do nothing
     * reload the avatar the url is not match
     *
     * @param serverAvatar avatar url from server
     */
    private void checkAndLoadUserAvatar(String serverAvatar) {
        //    String avatar = Accounts.getAvatar();
        //    Timber.e("1");
        //    if (avatar == null || avatar.length() == 0) {
        //      Timber.e("2");
        //      // local url is null or not exist, load avatar from server
        //      loadUserAvatar(serverAvatar);
        //    } else {
        //      Timber.e("3");
        //      // the local url is exist, check is need load serverAvatar or not
        //      if (serverAvatar == null || serverAvatar.length() == 0) {
        //        Timber.e("4");
        //        Glide.with(this).load(Uri.parse(avatar)).error(R.mipmap.default_avatar).into(mUserAvatar);
        //      } else {
        //        Timber.e("5");
        //        if (!avatar.equals(serverAvatar)) {
        //          Timber.e("6");
        loadUserAvatar(serverAvatar);
        //        }
        //      }
        //    }
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
        Bundle bundle = new Bundle();
        bundle.putParcelable(Conf.USER_DETAIL, mUserDetails);
        intent.putExtra(Conf.DATA_TO_INFO_MODIFY, bundle);
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

    //  @OnClick(R.id.user_rewards)
    //  void user_rewards() {
    //    startActivity(playing Intent(getContext(), UserRewardActivity.class));
    //  }

    @OnClick(R.id.setting)
    public void setting() {
        startActivity(new Intent(getContext(), SettingActivity.class));
    }

    @OnClick(R.id.user_judge_game)
    public void user_judge_game() {
        startActivity(new Intent(getContext(), UserManagerGameActivity.class));
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
