package com.lptiyu.tanke.database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "MESSAGE_NOTIFICATION_LIST".
 */
public class MessageNotificationList {

    private String name;
    private Boolean isRead;
    private String content;
    private Long userId;
    private Long time;
    private Integer type;

    public MessageNotificationList() {
    }

    public MessageNotificationList(Long userId) {
        this.userId = userId;
    }

    public MessageNotificationList(String name, Boolean isRead, String content, Long userId, Long time, Integer type) {
        this.name = name;
        this.isRead = isRead;
        this.content = content;
        this.userId = userId;
        this.time = time;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}
