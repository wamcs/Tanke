package com.lptiyu.tanke.userCenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.FragmentController;
import com.lptiyu.tanke.base.ui.BaseFragment;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
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

  private Subscription subscription;
  private UserDetails mUserDetails;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
    Glide.with(this).load(details.getAvatar()).error(R.mipmap.default_avatar).into(mUserAvatar);
    mUserNickname.setText(details.getNickname());
    //TODO sex need image
    mUserSex.setImageDrawable(null);
    mUserLocation.setText(details.getAddress());
    mUserUid.setText(String.valueOf(Accounts.getId()));
    mUserGamePlayingNum.setText(String.valueOf(details.getPlayingGameNum()));
    mUserGameFinishedNum.setText(String.valueOf(details.getFinishedGameNum()));
    parseLevelAndExp(101);
  }

  private void parseLevelAndExp(int exp) {
    int currentLevel = ExpUtils.calculateCurrentLevel(exp);
    int currentLevelNeedExp = ExpUtils.calculateExpByLevel(currentLevel);
    int nextLevelNeedExp = ExpUtils.calculateExpByLevel(currentLevel + 1);
    mUserProgressLeft.setText(getString(R.string.user_level, currentLevel));
    mUserProgressRight.setText(getString(R.string.user_level, currentLevel + 1));
    mUserProgress.setProgress(((float) (exp - currentLevelNeedExp) / (float) (nextLevelNeedExp - currentLevelNeedExp)));
    mUserProgressNeedExp.setText(String.format(getString(R.string.need_exp_formatter), exp - currentLevelNeedExp, nextLevelNeedExp - currentLevelNeedExp));
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

  @OnClick(R.id.user_rewards)
  void user_rewards() {
    startActivity(new Intent(getContext(), UserRewardActivity.class));
  }

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

}
