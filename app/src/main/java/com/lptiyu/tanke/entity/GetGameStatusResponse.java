package com.lptiyu.tanke.entity;

/**
 * Created by Jason on 2016/7/19.
 */
public class GetGameStatusResponse {
    public int play_statu;
    public String game_zip;

    @Override
    public String toString() {
        return "GetGameStatusResponse{" +
                "play_statu=" + play_statu +
                ", game_zip='" + game_zip + '\'' +
                '}';
    }
}
