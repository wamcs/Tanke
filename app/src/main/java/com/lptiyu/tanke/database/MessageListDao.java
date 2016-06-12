package com.lptiyu.tanke.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.lptiyu.tanke.database.MessageList;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MESSAGE_LIST".
*/
public class MessageListDao extends AbstractDao<MessageList, Integer> {

    public static final String TABLENAME = "MESSAGE_LIST";

    /**
     * Properties of entity MessageList.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Name = new Property(0, String.class, "name", false, "NAME");
        public final static Property IsRead = new Property(1, Boolean.class, "isRead", false, "IS_READ");
        public final static Property Content = new Property(2, String.class, "content", false, "CONTENT");
        public final static Property UserId = new Property(3, Long.class, "userId", false, "USER_ID");
        public final static Property Time = new Property(4, Long.class, "time", false, "TIME");
        public final static Property Type = new Property(5, Integer.class, "type", true, "TYPE");
    };


    public MessageListDao(DaoConfig config) {
        super(config);
    }
    
    public MessageListDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MESSAGE_LIST\" (" + //
                "\"NAME\" TEXT," + // 0: name
                "\"IS_READ\" INTEGER," + // 1: isRead
                "\"CONTENT\" TEXT," + // 2: content
                "\"USER_ID\" INTEGER," + // 3: userId
                "\"TIME\" INTEGER," + // 4: time
                "\"TYPE\" INTEGER PRIMARY KEY );"); // 5: type
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MESSAGE_LIST\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, MessageList entity) {
        stmt.clearBindings();
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(1, name);
        }
 
        Boolean isRead = entity.getIsRead();
        if (isRead != null) {
            stmt.bindLong(2, isRead ? 1L: 0L);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(3, content);
        }
 
        Long userId = entity.getUserId();
        if (userId != null) {
            stmt.bindLong(4, userId);
        }
 
        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(5, time);
        }
 
        Integer type = entity.getType();
        if (type != null) {
            stmt.bindLong(6, type);
        }
    }

    /** @inheritdoc */
    @Override
    public Integer readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5);
    }    

    /** @inheritdoc */
    @Override
    public MessageList readEntity(Cursor cursor, int offset) {
        MessageList entity = new MessageList( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // name
            cursor.isNull(offset + 1) ? null : cursor.getShort(offset + 1) != 0, // isRead
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // content
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // userId
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // time
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5) // type
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, MessageList entity, int offset) {
        entity.setName(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setIsRead(cursor.isNull(offset + 1) ? null : cursor.getShort(offset + 1) != 0);
        entity.setContent(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setUserId(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setTime(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setType(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Integer updateKeyAfterInsert(MessageList entity, long rowId) {
        return entity.getType();
    }
    
    /** @inheritdoc */
    @Override
    public Integer getKey(MessageList entity) {
        if(entity != null) {
            return entity.getType();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
