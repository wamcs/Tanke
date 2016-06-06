package com.lptiyu.tanke.database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "MESSAGE_LIST".
 */
public class MessageList {

    private Long id;
    private String name;
    private Boolean isRead;
    private String content;
    private Long userId;
    private Long time;
    private Integer type;

    public MessageList() {
    }

    public MessageList(Long id) {
        this.id = id;
    }

    public MessageList(Long id, String name, Boolean isRead, String content, Long userId, Long time, Integer type) {
        this.id = id;
        this.name = name;
        this.isRead = isRead;
        this.content = content;
        this.userId = userId;
        this.time = time;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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