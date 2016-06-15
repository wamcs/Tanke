package com.lptiyu.tanke.userCenter.controller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.io.net.UserService;
import com.lptiyu.tanke.pojo.UserDetails;
import com.lptiyu.tanke.userCenter.ui.LocateActivity;
import com.lptiyu.tanke.userCenter.ui.ModifyTextActivity;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.widget.dialog.DatePickerDialog;
import com.lptiyu.tanke.widget.dialog.GenderChooseDialog;
import com.lptiyu.tanke.widget.dialog.ImageChooseDialog;
import com.lptiyu.tanke.widget.dialog.LoadingDialog;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * author:wamcs
 * date:2016/6/9
 * email:kaili@hustunique.com
 */
public class ModifyUserInfoController extends ActivityController {


  @BindView(R.id.default_tool_bar_textview)
  TextView mTitle;

  @BindView(R.id.modify_user_info_avatar_image)
  SimpleDraweeView mAvatarImage;

  @BindView(R.id.modify_user_info_nickname_text)
  TextView mNicknameText;

  @BindView(R.id.modify_user_info_birthday_text)
  TextView mBirthdayText;

  @BindView(R.id.modify_user_info_gender_text)
  TextView mGenderText;

  @BindView(R.id.modify_user_info_height_text)
  TextView mHeightText;

  @BindView(R.id.modify_user_info_weight_text)
  TextView mWeightText;

  @BindView(R.id.modify_user_info_location_text)
  TextView mLocationText;

  private ImageChooseDialog mImageChooseDialog;
  private GenderChooseDialog mGenderChooseDialog;
  private LoadingDialog mLoadingDialog;
  private DatePickerDialog mDatePickerDialog;
  private long userId;
  private String token;
  private UserDetails details;

  public ModifyUserInfoController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    init();
  }

  private void init() {
    mTitle.setText("资料修改");
    Bundle bundle = getIntent().getBundleExtra(Conf.DATA_TO_INFO_MODIFY);
    if (bundle == null) {
      return;
    }
    details = bundle.getParcelable(Conf.USER_DETAIL);
    if (details == null) {
      return;
    }
    mAvatarImage.setImageURI(Uri.parse(details.getAvatar()));
    mBirthdayText.setText(details.getBirthday());
    mNicknameText.setText(details.getNickname());
    mGenderText.setText(details.getSex());
    mHeightText.setText(details.getHeight());
    mWeightText.setText(details.getWeight());
    mLocationText.setText(details.getAddress());

    mLoadingDialog = new LoadingDialog(getContext());
    userId = Accounts.getId();
    token = Accounts.getToken();
  }

  @OnClick(R.id.modify_user_info_avatar_button)
  void modifyAvatar() {
    if (null == mImageChooseDialog) {
      mImageChooseDialog = new ImageChooseDialog(getContext(), this);
      mImageChooseDialog.setOnImageChoosedListener(new ImageChooseDialog.OnImageChoosedListener() {
        @Override
        public void onImageChoosed(File file) {
          mLoadingDialog.setDialogText("图片上传中");
          mLoadingDialog.show();
          RequestBody body = RequestBody.create(MediaType.parse("application/x-jpg"), file);
          HttpService.getUserService().uploadUserAvatar(userId, token, body)
              .observeOn(AndroidSchedulers.mainThread())
              .subscribeOn(Schedulers.io())
              .subscribe(new Action1<Response<String>>() {
                @Override
                public void call(Response<String> stringResponse) {
                  int status = stringResponse.getStatus();
                  if (status != 1) {
                    ToastUtil.TextToast(stringResponse.getInfo());
                    mLoadingDialog.cancel();
                    return;
                  }
                  mAvatarImage.setImageURI(Uri.parse(stringResponse.getData()));
                  mLoadingDialog.cancel();
                }
              });

        }
      });
    }
    mImageChooseDialog.show();
  }

  @OnClick(R.id.modify_user_info_nickname_button)
  void modifyNickname() {
    Intent intent = new Intent(getActivity(), ModifyTextActivity.class);
    intent.putExtra(Conf.USER_INFO_TYPE, UserService.USER_DETAIL_NICKNAME);
    intent.putExtra(Conf.USER_INFO, details.getNickname());
    startActivityForResult(intent, Conf.REQUEST_CODE_NICKNAME);
  }

  @OnClick(R.id.modify_user_info_birthday_button)
  void modifyBirthday() {
    if (null == mDatePickerDialog) {
      mDatePickerDialog = new DatePickerDialog(getContext());
      mDatePickerDialog.setOnDateChoosedListener(new DatePickerDialog.OnDateChoosedListener() {
        @Override
        public void onDateChoosed(String date) {
          final String mData = date;
          mLoadingDialog.setDialogText("生日修改中");
          mLoadingDialog.show();
          HttpService.getUserService()
              .resetUserDetails(userId, token, UserService.USER_DETAIL_BIRTHDAY, date)
              .observeOn(AndroidSchedulers.mainThread())
              .subscribeOn(Schedulers.io())
              .subscribe(new Action1<Response<Void>>() {
                @Override
                public void call(Response<Void> voidResponse) {
                  int status = voidResponse.getStatus();
                  if (status != 1) {
                    ToastUtil.TextToast(voidResponse.getInfo());
                    mLoadingDialog.cancel();
                    return;
                  }
                  mBirthdayText.setText(mData);
                }
              });
        }
      });
    }
    mDatePickerDialog.show();
  }

  @OnClick(R.id.modify_user_info_gender_button)
  void modifyGender() {
    if (null == mGenderChooseDialog) {
      mGenderChooseDialog = new GenderChooseDialog(getContext());
      mGenderChooseDialog.setOnGenderChoosedListener(new GenderChooseDialog.OnGenderChoosedListener() {
        @Override
        public void onGenderChoosed(String gender) {
          final String sex = gender;
          mLoadingDialog.setDialogText("性别修改中");
          mLoadingDialog.show();
          HttpService.getUserService()
              .resetUserDetails(userId, token, UserService.USER_DETAIL_SEX, gender)
              .observeOn(AndroidSchedulers.mainThread())
              .subscribeOn(Schedulers.io())
              .subscribe(new Action1<Response<Void>>() {
                @Override
                public void call(Response<Void> voidResponse) {
                  int status = voidResponse.getStatus();
                  if (status != 1) {
                    ToastUtil.TextToast(voidResponse.getInfo());
                    mLoadingDialog.cancel();
                    return;
                  }
                  mGenderText.setText(sex);
                }
              });
        }
      });
    }
    mGenderChooseDialog.show(mGenderText.getText().toString());
  }

  @OnClick(R.id.modify_user_info_height_button)
  void modifyHeight() {
    Intent intent = new Intent(getActivity(), ModifyTextActivity.class);
    intent.putExtra(Conf.USER_INFO_TYPE, UserService.USER_DETAIL_HEIGHT);
    intent.putExtra(Conf.USER_INFO, details.getHeight());
    startActivityForResult(intent, Conf.REQUEST_CODE_HEIGHT);
  }

  @OnClick(R.id.modify_user_info_weight_button)
  void modifyWeight() {
    Intent intent = new Intent(getActivity(), ModifyTextActivity.class);
    intent.putExtra(Conf.USER_INFO_TYPE, UserService.USER_DETAIL_WEIGHT);
    intent.putExtra(Conf.USER_INFO, details.getWeight());
    startActivityForResult(intent, Conf.REQUEST_CODE_WEIGHT);
  }

  @OnClick(R.id.modify_user_info_location_button)
  void modifyLocation() {
    Intent intent = new Intent(getActivity(), LocateActivity.class);
    startActivityForResult(intent, Conf.REQUEST_CODE_LOCATION);
  }

  @OnClick(R.id.default_tool_bar_imageview)
  void back() {
    finish();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_CANCELED) {
      return;
    }
    switch (requestCode) {
      case Conf.REQUEST_CODE_NICKNAME:
        mNicknameText.setText(data.getStringExtra(Conf.USER_INFO));
        break;
      case Conf.REQUEST_CODE_HEIGHT:
        mHeightText.setText(data.getStringExtra(Conf.USER_INFO) + "CM");
        break;
      case Conf.REQUEST_CODE_WEIGHT:
        mWeightText.setText(data.getStringExtra(Conf.USER_INFO) + "KG");
        break;
      case Conf.REQUEST_CODE_LOCATION:
        mLocationText.setText(data.getStringExtra(Conf.USER_LOCATION));
    }
  }

  @Override
  public boolean onBackPressed() {
    finish();
    return true;
  }

}
