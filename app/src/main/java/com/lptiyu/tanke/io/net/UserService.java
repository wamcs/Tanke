package com.lptiyu.tanke.io.net;


import android.support.annotation.IntDef;

import com.lptiyu.tanke.pojo.GameFinishedEntity;
import com.lptiyu.tanke.pojo.GameManageEntity;
import com.lptiyu.tanke.pojo.GamePlayingEntity;
import com.lptiyu.tanke.pojo.Reward;
import com.lptiyu.tanke.pojo.UserDetails;
import com.lptiyu.tanke.pojo.UserEntity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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

  @GET("Login/Register")
  Observable<Response<UserEntity>> register(@Query("phone") String phone, // 手机号
                                            @Query("pwd") String pwd, // 密码
                                            @Query("code") String code,
                                            @Query("type") @UserType int type); // 验证码

  /**
   * 2.3 登录
   */
  @GET("Login/Login")
  Observable<Response<UserEntity>> login(@Query("phone") String phone,
                                         @Query("pwd") String pwd);

  /**
   * 2.4 第三方登录
   */
  @GET("Login/Login_san")
  Observable<Response<UserEntity>> loginThirdParty(@Query("openid") String openId,
                                                   @Query("type") @UserType int type);

  /**
   * 2.5 忘记密码
   */
  @GET("Login/Forgetpwd")
  Observable<Response<Void>> forgetPassword(@Query("phone") String phone,
                                            @Query("newpwd") String newPwd,
                                            @Query("code") String code);


  @IntDef({
      REGISTER, RESET_PSW
  })
  @Retention(RetentionPolicy.SOURCE)
  public @interface VERITY_CODE {
  }

  public static final int REGISTER = 1;
  public static final int RESET_PSW = 2;

  /**
   * 2.6 获取验证码 type {1:注册,2:忘记密码}
   *
   * @param type 注册时，后台缓存的有数据，需要上传type来确定
   *             是通过哪种方式注册，
   */
  @GET("Login/GetCode")
  Observable<Response<Void>> getVerifyCode(
      @Query("status") @VERITY_CODE int type,
      @Query("phone") String phone);


  /**
   * 2.7 修改密码
   */
  @GET("Login/Password")
  Observable<Response<Void>> resetPassword(
      @Query("uid") long uid,
      @Query("token") String token,
      @Query("pwd") String oldPwd,
      @Query("newpwd") String newPwd
  );

  /**
   * 2.8 获取用户信息
   */
  @GET("User/User")
  Observable<Response<UserDetails>> getUserDetail(
      @Query("uid") long uid,
      @Query("token") String token
  );

  /**
   * 2.9 个人信息上传图片
   */
  @Multipart
  @POST("User/Userphoto")
  Observable<Response<String>> uploadUserAvatar(
      @Query("uid") long uid,
      @Query("token") String token,
      @Part("image") RequestBody file);

  /**
   * 完善信息上传
   */
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


  /**
   * 2.11 修改用户信息
   *
   * @param type 类型  1：昵称 2：生日 3：性别 4：身高 5：体重 ， 见上
   */
  @GET("User/Update_user")
  Observable<Response<Void>> resetUserDetails(
      @Query("uid") long uid,
      @Query("token") String token,
      @Query("type") @UserDetailType int type,
      @Query("content") String message);

  /**
   * 2.2 获取用户协议
   */
  @GET("Login/User")
  Observable<Response<String>> userProtocol();

  @GET("My/Nowranks?page=1")
  Observable<Response<List<GamePlayingEntity>>> gamePlaying(
      @Query("uid") long uid,
      @Query("token") String token
  );


  /**
   * 2.24 获取我正在玩儿的游戏
   */
  @GET("My/Nowranks")
  Observable<Response<GamePlayingEntity>> gamePlaying(
      @Query("uid") long uid,
      @Query("token") String token,
      @Query("page") int page
  );

  /**
   * 2.25 获取用户已完成的游戏 默认page = 1
   */
  @GET("My/Finishranks?page=1")
  Observable<Response<List<GameFinishedEntity>>> gameFinished(
      @Query("uid") long uid,
      @Query("token") String token
  );

  /**
   * 2.25 获取用户已完成的游戏
   */
  @GET("My/Finishranks")
  Observable<Response<GameFinishedEntity>> gameFinished(
      @Query("uid") long uid,
      @Query("token") String token,
      @Query("page") int page
  );

  /**
   * 2.26 获取奖品信息
   */
  @GET("My/Reward")
  Observable<Response<List<Reward>>> getRewards(
      @Query("uid") long uid,
      @Query("token") String token,
      @Query("page") int page);

  /**
   * 2.26 获取奖品信息 page 默认为 1
   */
  @GET("My/Reward?page=1")
  Observable<Response<List<Reward>>> getRewards(
      @Query("uid") long uid,
      @Query("token") String token);

  /**
   * 2.27 获取我的裁判任务, page默认为1
   */
  @GET("My/Task")
  Observable<Response<List<GameManageEntity>>> getManagerTask(
      @Query("uid") long ui,
      @Query("token") String token,
      @Query("page") int page
  );

  /**
   * 2.27 获取我的裁判任务, page默认为1
   */
  @GET("My/Task?page=1")
  Observable<Response<List<GameManageEntity>>> getManagerTask(
      @Query("uid") long uid,
      @Query("token") String token);

  /**
   * 2.30 绑定设备的installationId，以便后台推送信息
   */
  @GET("System/Installation?")
  Observable<Response<Void>> registerInstallation(
      @Query("uid") long uid,
      @Query("token") String token,
      @Query("installation_id") String installationId
  );

}
