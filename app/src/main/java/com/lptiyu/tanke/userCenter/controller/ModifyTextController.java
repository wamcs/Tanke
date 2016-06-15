package com.lptiyu.tanke.userCenter.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.io.net.UserService;
import com.lptiyu.tanke.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * author:wamcs
 * date:2016/6/9
 * email:kaili@hustunique.com
 */
public class ModifyTextController extends ActivityController {

  @BindView(R.id.activity_modify_toolbar_textview)
  TextView mTitle;
  @BindView(R.id.modify_text_edit)
  EditText mEditText;

  private int type;
  private int requestCode;
  private String content;

  public ModifyTextController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    init();
  }

  private void init() {
    type = getIntent().getIntExtra(Conf.USER_INFO_TYPE, 0);
    content = getIntent().getStringExtra(Conf.USER_INFO);
    if (type == 0) {
      throw new IllegalStateException("there are not this state");
    }
    mEditText.setHintTextColor(getResources().getColor(R.color.grey06));
    switch (type) {
      case UserService.USER_DETAIL_NICKNAME:
        mTitle.setText(getString(R.string.change_nickname));
        requestCode = Conf.REQUEST_CODE_NICKNAME;
        mEditText.setHint(getString(R.string.change_nickname_hint));
        if (content != null && content.length() != 0) {
          mEditText.setText(content);
        }
        break;
      case UserService.USER_DETAIL_HEIGHT:
        mTitle.setText(getString(R.string.change_height));
        requestCode = Conf.REQUEST_CODE_HEIGHT;
        mEditText.setHint(getString(R.string.change_height_hint));
        mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (content != null && content.length() != 0) {
          mEditText.setText(content.substring(0, 2));
        }
        break;
      case UserService.USER_DETAIL_WEIGHT:
        mTitle.setText(getString(R.string.change_weight));
        requestCode = Conf.REQUEST_CODE_WEIGHT;
        mEditText.setHint(R.string.change_weight_hint);
        mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (content != null && content.length() != 0) {
          if (content.length() == 5) {
            mEditText.setText(content.substring(0, 2));
          } else {
            mEditText.setText(content.substring(0, 1));
          }
        }
        break;
    }
  }

  @OnClick(R.id.activity_modify_toolbar_imageview_left)
  void back() {
    getActivity().setResult(0);
    finish();
  }

  @OnClick(R.id.modify_text_save)
  void save() {
    Editable editable = mEditText.getText();
    final String content = editable.toString();
    switch (type) {
      case UserService.USER_DETAIL_NICKNAME:
        if (editable.length() == 0) {
          ToastUtil.TextToast(getString(R.string.change_height_error));
          return;
        }
        break;
      case UserService.USER_DETAIL_HEIGHT:
        if (Integer.parseInt(content) < Conf.MIN_HEIGHT || Integer.parseInt(content) > Conf.MAX_HEIGHT) {
          ToastUtil.TextToast(String.format(getString(R.string.change_height_error), Conf.MIN_HEIGHT, Conf.MAX_HEIGHT));
          return;
        }
        break;
      case UserService.USER_DETAIL_WEIGHT:
        if (Integer.parseInt(content) < Conf.MIN_WEIGHT || Integer.parseInt(content) > Conf.MAX_WEIGHT) {
          ToastUtil.TextToast(String.format(getString(R.string.change_weight_error), Conf.MIN_WEIGHT, Conf.MAX_WEIGHT));
          return;
        }
        break;
    }

    if (content.equals(this.content)) {
      ToastUtil.TextToast("修改之后才能进行数据上传");
      return;
    }

    HttpService.getUserService().resetUserDetails(Accounts.getId(), Accounts.getToken(), type, content)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Action1<Response<Void>>() {
          @Override
          public void call(Response<Void> voidResponse) {
            int status = voidResponse.getStatus();
            if (status != 1) {
              ToastUtil.TextToast(voidResponse.getInfo());
              return;
            }
            Intent intent = new Intent();
            intent.putExtra(Conf.USER_INFO, content);
            getActivity().setResult(requestCode, intent);
            finish();
          }
        });
  }

  @Override
  public boolean onBackPressed() {
    getActivity().setResult(0);
    return false;
  }
}
