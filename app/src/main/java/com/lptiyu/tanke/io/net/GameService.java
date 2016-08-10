package com.lptiyu.tanke.io.net;

import android.support.annotation.IntDef;

import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.pojo.City;
import com.lptiyu.tanke.pojo.EnterGameResponse;
import com.lptiyu.tanke.pojo.GameDetailResponse;
import com.lptiyu.tanke.pojo.GameDisplayEntity;
import com.lptiyu.tanke.pojo.GetGameStatusResponse;
import com.lptiyu.tanke.pojo.MessageEntity;
import com.lptiyu.tanke.pojo.UploadGameRecordResponse;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/18
 *
 * @author ldx
 */
public interface GameService {

    /**
     * 2.12 首页接口
     */
    @GET("Home/Index")
    Observable<Response<List<GameDisplayEntity>>> getGamePage(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("city") String location, // 城市
            @Query("page") long page);

    /**
     * 2.13 游戏详情
     */
    @GET("Home/Details")
    Observable<Response<GameDetailResponse>> getGameDetails(
            @Query("game_id") long gameId);

    /**
     * 2.17 把生成的二维码信息上传后台
     */
    @GET("Home/Code?type=1")
    Observable<Response<Void>> regenerateTeamQRCode(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("ranks_id") int ranksId,
            @Query("content") String content
    );

    @GET("Home/Code?type=2")
    Observable<Response<Void>> regenerateJudgementQRCode(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("task_id") int task_id,
            @Query("content") String content
    );

    /**
     * 2.18 扫码
     * 加入团队，或者扫码通关
     */
    @GET("Home/Subcode")
    Observable<Response<Void>> verifyQRCode(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("content") String content
    );

    int RECORD_TYPE_GAME_START = 1;
    int RECORD_TYPE_SPOT_REACHED = 2;
    int RECORD_TYPE_TASK_START = 3;
    int RECORD_TYPE_TASK_FINISH = 4;
    int RECORD_TYPE_SPOT_FINISHED = 5;
    int RECORD_TYPE_GAME_FINISH = 6;

    @IntDef({RECORD_TYPE_GAME_START,
            RECORD_TYPE_SPOT_REACHED,
            RECORD_TYPE_TASK_START,
            RECORD_TYPE_TASK_FINISH,
            RECORD_TYPE_SPOT_FINISHED,
            RECORD_TYPE_GAME_FINISH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RecordType {
    }

    int TEAM_RECORD = 1;
    int USER_RECORD = 2;

    @IntDef({TEAM_RECORD,
            USER_RECORD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TeamOrUserRecord {
    }

    /**
     * 2.23 提交用户记录
     */
    @GET("System/Rankslog?type=2")
    Observable<Response<Void>> uploadPersonalGameRecords(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("game_id") int gameId,
            @Query("point_id") int pointId,
            @Query("distance") String distanceFromRecords,
            @Query("x") String x,
            @Query("y") String y,
            @Query("state") @RecordType int state
    );

    /**
     * 2.23 提交用户记录
     */
    @GET("System/Rankslog?type=1")
    Observable<Response<Void>> uploadTeamGameRecords(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("index") int index,
            @Query("game_id") long gameId,
            @Query("ranks_id") long rankId,
            @Query("point_id") long pointId,
            @Query("task_id") long taskId,
            @Query("distance") String distanceFromRecords,
            @Query("x") String x,
            @Query("y") String y,
            @Query("type") @TeamOrUserRecord int type,
            @Query("state") int state
    );

    /**
     * 2.34 下载个人赛zip包
     */
    @GET("Home/game_zip?type=2")
    Observable<Response<String>> getIndividualGameZipUrl(@Query("uid") long uid,
                                                         @Query("token") String token,
                                                         @Query("game_id") long gameId
    );


    /**
     * 2.34 下载团队赛zip包
     */
    @GET("Home/game_zip?type=1")
    Observable<Response<String>> getTeamGameZipUrl(@Query("uid") long uid,
                                                   @Query("token") String token,
                                                   @Query("game_id") long gameId,
                                                   @Query("ranks_id") long ranksId
    );

    @Streaming
    @GET
    Observable<retrofit2.Response<ResponseBody>> downloadGameZip(@Url String url);

    /**
     * 2.28 获取开通城市
     */
    @GET("System/City?page=1")
    Observable<Response<List<City>>> getSupportedCities();

    /**
     * 2.29 游戏结束后获取分享接口
     *
     * @param uid    用户id
     * @param token  判断用户合法的随机数
     * @param gameId 游戏Id
     * @param teamId 团队Id
     * @return Response, String 分享的链接
     */
    @GET("Home/Share")
    Observable<Response<String>> getShareUrl(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("game_id") long gameId,
            @Query("ranks_id") long teamId
    );

    /**
     * 2.35 获取任务完成的人数
     *
     * @param taskId 目标任务Id
     * @return Response, Integer 该任务完成的人数
     */
    @GET("System/Task_num")
    Observable<Response<Integer>> getTaskFinishedNum(
            @Query("task_id") long taskId
    );

    /**
     * 2.36 获取游戏完成的人数
     *
     * @param gameId 目标游戏id
     * @return Response, Integer 该游戏完成的人数
     */
    @GET("System/Game_num")
    Observable<Response<Integer>> getGameFinishedNum(
            @Query("game_id") long gameId
    );

    /**
     * 2.38 获取游戏资讯信息
     *
     * @param userId
     * @return
     */
    @GET("System/News")
    Observable<Response<List<MessageEntity>>> getSystemMessage(
            @Query("uid") long userId
    );

    //    /**
    //     * 2.39下载游戏日志信息
    //     *
    //     * @param userId
    //     * @param gameId
    //     * @param gameType
    //     * @return
    //     */
    //    @GET("System/Getrankslog")
    //    Observable<Response<ArrayList<RunningRecord>>> getRunningRecords(
    //            @Query("uid") long userId,
    //            @Query("game_id") long gameId,
    //            @Query("type") int gameType
    //    );

    /**
     * 2.39下载游戏日志信息
     *
     * @param uid
     * @param gameId
     * @param gameType
     * @return
     */
    @GET("System/Getrankslog")
    Observable<Response<GameRecord>> getGameRecord(
            @Query("uid") long uid,
            @Query("game_id") long gameId,
            @Query("type") long gameType
    );

    /**
     * 2.23 上传游戏记录
     *
     * @param uid
     * @param gameId
     * @param pointid
     * @param taskid
     * @param type
     * @param pointStatus
     * @return
     */
    @GET("System/Rankslog")
    Observable<Response<UploadGameRecordResponse>> upLoadGameRecord(
            @Query("uid") long uid,
            @Query("game_id") long gameId,
            @Query("point_id") long pointid,
            @Query("task_id") long taskid,
            @Query("type") long type,//1为团队赛，2为个人赛
            @Query("point_statu") long pointStatus
    );

    /**
     * 2.23 游戏结束时上传游戏记录
     *
     * @param uid
     * @param gameId
     * @param pointid
     * @param taskid
     * @param type
     * @param pointStatus
     * @param playStatus
     * @return
     */
    @GET("System/Rankslog")
    Observable<Response<Void>> gameOver(
            @Query("uid") long uid,
            @Query("game_id") long gameId,
            @Query("point_id") long pointid,
            @Query("task_id") long taskid,
            @Query("type") long type,
            @Query("point_statu") long pointStatus,
            @Query("play_statu") long playStatus
    );

    /**
     * 进入游戏
     *
     * @param uid
     * @param gameId
     * @param type
     * @return
     */
    @GET("system/join_game")
    Observable<Response<EnterGameResponse>> enterGame(
            @Query("uid") long uid,
            @Query("game_id") long gameId,
            @Query("type") long type
    );

    /**
     * 放弃游戏
     *
     * @param uid
     * @param gameId
     * @return
     */
    @GET("system/leave_game")
    Observable<Response<Void>> leaveGame(
            @Query("uid") long uid,
            @Query("game_id") long gameId
    );

    /**
     * 获取游戏状态
     *
     * @param uid
     * @param gameId
     * @param type
     * @return
     */
    @GET("system/get_game_statu")
    Observable<Response<GetGameStatusResponse>> getGameStatus(
            @Query("uid") long uid,
            @Query("game_id") long gameId,
            @Query("type") long type
    );

}
