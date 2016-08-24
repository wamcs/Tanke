package com.Tanke;


import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String[] args) throws Exception {
        /**
         * 参数一：当前数据库版本
         * 参数二：生成DAO类的包路径
         */
        Schema schema = new Schema(1, "com.lptiyu.tanke.database");
        addNote(schema);
        //执行生成过程，其中./app/src/main/java(相对路径，也可以写工程的绝对路径)请提前创建,否则会报错.
        new DaoGenerator().generateAll(schema, "./app/src/main/java");
    }

    private static void addNote(Schema schema) {
        //系统头像直接加在工程中
        //每一个Entity代表一个表，这个语句会创建一个名叫Message的表
        Entity pushMessage = schema.addEntity("MessageNotification");
        //创建一个ID作为主键
        pushMessage.addIdProperty();
        //增加字段的方法(在生成的实体类中,int类型为自动转为long类型).
        pushMessage.addStringProperty("alert").notNull();
        pushMessage.addStringProperty("title");
        pushMessage.addLongProperty("time");
        pushMessage.addIntProperty("type");//1.官方资讯 2.系统推送
        pushMessage.addStringProperty("image");//图片消息，第一版没有
        pushMessage.addStringProperty("url");

        //消息中心的item，一条记录就是一个item, 用户消息的type就存放用户的id,一个用户一个item，官方资讯以及系统推送的type固定不变唯一
        Entity messageList = schema.addEntity("MessageNotificationList");
        messageList.addStringProperty("name"); //消息发送方名称 (eg:官方||系统||XXX
        messageList.addBooleanProperty("isRead");//消息是否已读
        messageList.addStringProperty("content");//最近一条消息内容
        messageList.addLongProperty("userId").primaryKey();//用户ID，只对用户有效，第一版暂时不用，根据id获取头像
        messageList.addLongProperty("time");//Unix 时间戳
        messageList.addIntProperty("type");//1.官方资讯 2.系统推送 3.用户消息

        Entity gameRecord = schema.addEntity("DBGameRecord");
        Property gameRecordId = gameRecord.addIdProperty().getProperty();
        gameRecord.addStringProperty("join_time");
        gameRecord.addStringProperty("start_time");
        gameRecord.addStringProperty("last_task_ftime");
        gameRecord.addStringProperty("play_statu");
        gameRecord.addStringProperty("ranks_id");
        gameRecord.addStringProperty("game_id");
        gameRecord.addStringProperty("line_id");
        gameRecord.addStringProperty("uid");

        Entity point = schema.addEntity("DBPointRecord");
        Property pointRecordId = point.addIdProperty().getProperty();
        point.addStringProperty("statu");
        point.addStringProperty("point_id");

        Entity task = schema.addEntity("DBTaskRecord");
        Property taskRecordId = task.addIdProperty().getProperty();
        task.addStringProperty("ftime");
        task.addStringProperty("taskId");
        task.addStringProperty("exp");

        //游戏与攻击点是一对多(1:n)关系
        gameRecord.addToMany(point, pointRecordId, "record_text");
        //攻击点与任务是一对多(1:n)关系
        point.addToMany(task, taskRecordId, "task");
    }
}
