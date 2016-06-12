package com.Tanke;


import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1,"com.lptiyu.tanke.database");
        addNote(schema);
        new DaoGenerator().generateAll(schema, "./app/src/main/java");
    }

    private static void addNote(Schema schema){
        //系统头像直接加在工程中
        Entity pushMessage = schema.addEntity("Message");
        pushMessage.addIdProperty();
        pushMessage.addStringProperty("alert").notNull();
        pushMessage.addStringProperty("title");
        pushMessage.addLongProperty("time");
        pushMessage.addIntProperty("type");//1.官方资讯 2.系统推送
        pushMessage.addStringProperty("image");//图片消息，第一版没有

        Entity messageList = schema.addEntity("MessageList");
        messageList.addStringProperty("name"); //消息发送方名称 (eg:官方||系统||XXX
        messageList.addBooleanProperty("isRead");//消息是否已读
        messageList.addStringProperty("content");//最近一条消息内容
        messageList.addLongProperty("userId");//用户ID，只对用户有效，第一版暂时不用，根据id获取头像
        messageList.addLongProperty("time");//Unix 时间戳
        messageList.addIntProperty("type").primaryKey();//1.官方资讯 2.系统推送 3.用户消息

    }
}
