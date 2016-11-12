package com.lptiyu.tanke.entity.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Jason on 2016/11/10.
 */
@Entity
public class DRLocalData {
    @Id(autoincrement = true)
    public Long id;
    public String record_id;
    public String game_id;
    public String startTime;//总用时，单位：毫秒
    public String totalDistance;//总路程，单位：米
    public String previousPointId;
    public String previousPointLatitude;
    public String previousPointLongitude;
    public String lastPointLatitude;
    public String lastPointLongitude;
    public String fileName;
    @Generated(hash = 499242189)
    public DRLocalData(Long id, String record_id, String game_id, String startTime,
            String totalDistance, String previousPointId,
            String previousPointLatitude, String previousPointLongitude,
            String lastPointLatitude, String lastPointLongitude, String fileName) {
        this.id = id;
        this.record_id = record_id;
        this.game_id = game_id;
        this.startTime = startTime;
        this.totalDistance = totalDistance;
        this.previousPointId = previousPointId;
        this.previousPointLatitude = previousPointLatitude;
        this.previousPointLongitude = previousPointLongitude;
        this.lastPointLatitude = lastPointLatitude;
        this.lastPointLongitude = lastPointLongitude;
        this.fileName = fileName;
    }
    @Generated(hash = 218941470)
    public DRLocalData() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getRecord_id() {
        return this.record_id;
    }
    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }
    public String getGame_id() {
        return this.game_id;
    }
    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }
    public String getStartTime() {
        return this.startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getTotalDistance() {
        return this.totalDistance;
    }
    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }
    public String getPreviousPointId() {
        return this.previousPointId;
    }
    public void setPreviousPointId(String previousPointId) {
        this.previousPointId = previousPointId;
    }
    public String getPreviousPointLatitude() {
        return this.previousPointLatitude;
    }
    public void setPreviousPointLatitude(String previousPointLatitude) {
        this.previousPointLatitude = previousPointLatitude;
    }
    public String getPreviousPointLongitude() {
        return this.previousPointLongitude;
    }
    public void setPreviousPointLongitude(String previousPointLongitude) {
        this.previousPointLongitude = previousPointLongitude;
    }
    public String getLastPointLatitude() {
        return this.lastPointLatitude;
    }
    public void setLastPointLatitude(String lastPointLatitude) {
        this.lastPointLatitude = lastPointLatitude;
    }
    public String getLastPointLongitude() {
        return this.lastPointLongitude;
    }
    public void setLastPointLongitude(String lastPointLongitude) {
        this.lastPointLongitude = lastPointLongitude;
    }
    public String getFileName() {
        return this.fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
