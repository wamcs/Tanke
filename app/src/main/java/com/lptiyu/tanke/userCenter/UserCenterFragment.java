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
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.UserDetails;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.widget.GradientProgressBar;
import com.mikhaellopez.circularimageview.CircularImageView;

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

  @BindView(R.id.user_progress_name)
  TextView mUserProgressName;

  @BindView(R.id.user_game_playing_num)
  TextView mUserGamePlayingNum;

  @BindView(R.id.user_game_finished_num)
  TextView mUserGameFinishedNum;

  private Subscription subscription;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = fromResLayout(inflater, container, R.layout.fragment_user_center);
    ButterKnife.bind(this, view);
    init();
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
              bind(userDetailsResponse.getData());
            } else {
              throw new RuntimeException(userDetailsResponse.getInfo());
            }
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            ToastUtil.TextToast(throwable.getMessage());
          }
        });
  }

  private void bind(UserDetails details) {
    Glide.with(this).load(details.getAvatar()).into(mUserAvatar);
    mUserNickname.setText(details.getNickname());
    //TODO sex need image
    mUserSex.setImageDrawable(null);
    mUserLocation.setText(details.getAddress());
    mUserUid.setText(String.valueOf(Accounts.getId()));
    mUserProgressLeft.setText(getString(R.string.user_level, 10));
    mUserProgressRight.setText(getString(R.string.user_level, 10 + 1));
    mUserProgress.setProgress(10f / 1000);
    mUserProgressName.setText("步道跑神");
    mUserGamePlayingNum.setText(String.valueOf(details.getPlayingGameNum()));
    mUserGameFinishedNum.setText(String.valueOf(details.getFinishedGameNum()));
  }

  @OnClick(R.id.user_game_playing)
  void gamePlayingClicked() {
  }

  @OnClick(R.id.user_game_finished)
  void user_game_finished() {
  }

  @OnClick(R.id.user_rewards)
  void user_rewards() {
    startActivity(new Intent(getContext(), UserRewardActivity.class));
  }

  @OnClick(R.id.setting)
  public void setting() {

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
