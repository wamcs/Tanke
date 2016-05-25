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

  // Return team_id
  @GET("Home/Ranks")
  Observable<Response<Integer>> setupTeam(@Query("uid") int uid,
                                          @Query("token") String token,
                                          @Query("game_id") int gameId,
                                          @Query("name") String teamName);

  @GET("Home/Getranks")
  Observable<Response<Team>> getTeamMessages(
      @Query("uid") int uid,
      @Query("token") int token,
      @Query("ranks_id") int teamId);


  @GET("Home/Reranks")
  Observable<Response<Void>> exitTeam(
      @Query("uid") int uid,
      @Query("token") String token,
      @Query("ranks_id") int teamId);

  @GET("Home/Moveranks")
  Observable<Response<Void>> removeTeamMember(
      @Query("uid") int uid,
      @Query("token") String token,
      @Query("ranks_id") int teamId,
      //被移除的用户的id
      @Query("user_id") int userId
  );

  @GET("Home/Delranks")
  Observable<Response<Void>> dismissTeam(
      @Query("uid") int uid,
      @Query("token") String token,
      @Query("ranks_id") int teamId
  );


}
