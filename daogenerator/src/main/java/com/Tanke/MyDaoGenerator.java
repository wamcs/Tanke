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
        Entity pushMessage = schema.addEntity("PushMessage");
        pushMessage.addIdProperty();
        pushMessage.addStringProperty("alert").notNull();
        pushMessage.addStringProperty("title");
        pushMessage.addStringProperty("url");
        pushMessage.addStringProperty("image");
        pushMessage.addLongProperty("time");
        pushMessage.addIntProperty("type");
        pushMessage.addBooleanProperty("isRead");

    }
}
