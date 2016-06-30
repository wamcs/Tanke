package com.lptiyu.tanke.io.net;

import com.lptiyu.tanke.pojo.Team;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/24
 *
 * @author ldx
 */
public interface TeamService {

  /**
   * 2.14 组建团队
   *
   * @return teamId
   */
  @GET("Home/Ranks")
  Observable<Response<Integer>> setupTeam(
      @Query("uid") long uid,
      @Query("game_id") long gameId,
      @Query("name") String teamName
  );

  /**
   * 2.15 获取团队信息
   */
  @GET("Home/Getranks")
  Observable<Response<Team>> getTeamMessages(
      @Query("uid") long uid,
      @Query("token") String token,
      @Query("ranks_id") long teamId
  );


  /**
   * 2.19 退出团队
   */
  @GET("Home/Reranks")
  Observable<Response<Void>> exitTeam(
      @Query("uid") long uid,
      @Query("token") String token,
      @Query("ranks_id") long teamId);

  /**
   * 2.20 移除团队成员
   */
  @GET("Home/Moveranks")
  Observable<Response<Void>> removeTeamMember(
      @Query("uid") long uid,
      @Query("token") String token,
      @Query("ranks_id") long teamId,
      //被移除的用户的id
      @Query("user_id") long removedUserId
  );

  /**
   * 2.30 解散队伍
   */
  @GET("Home/Delranks")
  Observable<Response<Void>> dismissTeam(
      @Query("uid") int uid,
      @Query("token") String token,
      @Query("ranks_id") int teamId
  );


}
