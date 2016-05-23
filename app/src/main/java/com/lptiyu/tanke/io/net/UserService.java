package com.lptiyu.tanke.io.net;


import android.support.annotation.IntDef;

import com.lptiyu.tanke.bean.User;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/23
 *
 * @author ldx
 */
public interface UserService {

  @IntDef({USER_TYPE_NORMAL, USER_TYPE_QQ, USER_TYPE_WEIXIN, USER_TYPE_WEIBO})
  @Retention(RetentionPolicy.SOURCE)
  @interface UserType {
  }

  public static final int USER_TYPE_NORMAL = 1;

  public static final int USER_TYPE_QQ = 2;

  public static final int USER_TYPE_WEIXIN = 3;

  public static final int USER_TYPE_WEIBO = 4;

  @GET("/Login/Register")
  Observable<Response<User>> register(@Query("phone") String phone, // 手机号
                                      @Query("pwd") String pwd, // 密码
                                      @Query("code") String code,
                                      @Query("type") @UserType int type); // 验证码


  @GET("/Login/Login")
  Observable<Response<User>> login(@Query("phone") String phone,
                                   @Query("pwd") String pwd,
                                   @Query("type") @UserType int type);

  @GET("/Login/Login_san")
  Observable<Response<User>> loginThirdParty(@Query("openid") String openId,
                                             @Query("name") String nickname,
                                             @Query("type") int type);

  @GET("/Login/Forgetpwd")
  Observable<Response<Void>> forgetPassword(@Query("phone") String phone,
                                            @Query("newpwd") String newPwd,
                                            @Query("code") String code);


  @IntDef({VERIFY_CODE_REGISTER, VERIFY_CODE_FORGOT_PWD})
  @Retention(RetentionPolicy.SOURCE)
  @interface VerifyCodeStatus {
  }

  public static final int VERIFY_CODE_REGISTER = 1;

  public static final int VERIFY_CODE_FORGOT_PWD = 2;


  @GET("/Login/GetCode")
  Observable<Response<Void>> getVerifyCode(
      @Query("status") @VerifyCodeStatus int status,
      @Query("type") @UserType int type,
      @Query("phone") String phone);

  @GET("/Login/Password")
  Observable<Response<Void>> resetPassword(
      @Query("uid") String uid,
      @Query("token") String token,
      @Query("pwd") String oldPwd,
      @Query("newpwd") String newPwd
  );

  @GET("/User/User")
  Observable<Response<User>> getUserDetail(
      @Query("uid") int uid,
      @Query("token") int token
  );

  @GET("/User/Userphoto")
  Observable<Response<String>> uploadUserAvatar(
      //TODO need to check
  );

  @IntDef({
      USER_DETAIL_NICKNAME,
      USER_DETAIL_BIRTHDAY,
      USER_DETAIL_SEX,
      USER_DETAIL_HEIGHT,
      USER_DETAIL_WEIGHT})
  @Retention(RetentionPolicy.SOURCE)
  @interface UserDetailType {
  }

  public static final int USER_DETAIL_NICKNAME = 1;
  public static final int USER_DETAIL_BIRTHDAY = 2;
  public static final int USER_DETAIL_SEX = 3;
  public static final int USER_DETAIL_HEIGHT = 4;
  public static final int USER_DETAIL_WEIGHT = 5;


  @GET("/User/Update_user")
  Observable<Response<Void>> resetUserDetails(
      @Query("uid") int uid,
      @Query("token") int token,
      @Query("type") @UserDetailType int type,
      @Query("content") String message);


  @GET("/Login/User")
  Observable<Response<String>> userProtocol();

}
