package com.lptiyu.tanke.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.lptiyu.tanke.database.MessageNotification;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MESSAGE_NOTIFICATION".
*/
public class MessageNotificationDao extends AbstractDao<MessageNotification, Long> {

    public static final String TABLENAME = "MESSAGE_NOTIFICATION";

    /**
     * Properties of entity MessageNotification.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Alert = new Property(1, String.class, "alert", false, "ALERT");
        public final static Property Title = new Property(2, String.class, "title", false, "TITLE");
        public final static Property Time = new Property(3, Long.class, "time", false, "TIME");
        public final static Property Type = new Property(4, Integer.class, "type", false, "TYPE");
        public final static Property Image = new Property(5, String.class, "image", false, "IMAGE");
        public final static Property Url = new Property(6, String.class, "url", false, "URL");
    };


    public MessageNotificationDao(DaoConfig config) {
        super(config);
    }
    
    public MessageNotificationDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MESSAGE_NOTIFICATION\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"ALERT\" TEXT NOT NULL ," + // 1: alert
                "\"TITLE\" TEXT," + // 2: title
                "\"TIME\" INTEGER," + // 3: time
                "\"TYPE\" INTEGER," + // 4: type
                "\"IMAGE\" TEXT," + // 5: image
                "\"URL\" TEXT);"); // 6: url
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MESSAGE_NOTIFICATION\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, MessageNotification entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getAlert());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(3, title);
        }
 
        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(4, time);
        }
 
        Integer type = entity.getType();
        if (type != null) {
            stmt.bindLong(5, type);
        }
 
        String image = entity.getImage();
        if (image != null) {
            stmt.bindString(6, image);
        }
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(7, url);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public MessageNotification readEntity(Cursor cursor, int offset) {
        MessageNotification entity = new MessageNotification( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // alert
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // title
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // time
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // type
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // image
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // url
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, MessageNotification entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setAlert(cursor.getString(offset + 1));
        entity.setTitle(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTime(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setType(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setImage(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setUrl(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(MessageNotification entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(MessageNotification entity) {
        if(entity != null) {
            return entity.getId();
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
