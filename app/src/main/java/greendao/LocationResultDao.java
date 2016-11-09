package greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.lptiyu.tanke.entity.greendao.LocationResult;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "LOCATION_RESULT".
*/
public class LocationResultDao extends AbstractDao<LocationResult, Long> {

    public static final String TABLENAME = "LOCATION_RESULT";

    /**
     * Properties of entity LocationResult.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Accuracy = new Property(1, float.class, "accuracy", false, "ACCURACY");
        public final static Property AdCode = new Property(2, String.class, "adCode", false, "AD_CODE");
        public final static Property Adress = new Property(3, String.class, "adress", false, "ADRESS");
        public final static Property Altitude = new Property(4, double.class, "altitude", false, "ALTITUDE");
        public final static Property AoiName = new Property(5, String.class, "aoiName", false, "AOI_NAME");
        public final static Property Bearing = new Property(6, float.class, "bearing", false, "BEARING");
        public final static Property City = new Property(7, String.class, "city", false, "CITY");
        public final static Property CityCode = new Property(8, String.class, "cityCode", false, "CITY_CODE");
        public final static Property Country = new Property(9, String.class, "country", false, "COUNTRY");
        public final static Property District = new Property(10, String.class, "district", false, "DISTRICT");
        public final static Property ErrorCode = new Property(11, int.class, "errorCode", false, "ERROR_CODE");
        public final static Property ErrorInfo = new Property(12, String.class, "errorInfo", false, "ERROR_INFO");
        public final static Property Latitude = new Property(13, double.class, "latitude", false, "LATITUDE");
        public final static Property LocationDetail = new Property(14, String.class, "locationDetail", false, "LOCATION_DETAIL");
        public final static Property LocationType = new Property(15, int.class, "locationType", false, "LOCATION_TYPE");
        public final static Property Longitude = new Property(16, double.class, "longitude", false, "LONGITUDE");
        public final static Property PoiName = new Property(17, String.class, "poiName", false, "POI_NAME");
        public final static Property Provider = new Property(18, String.class, "provider", false, "PROVIDER");
        public final static Property Province = new Property(19, String.class, "province", false, "PROVINCE");
        public final static Property Street = new Property(20, String.class, "street", false, "STREET");
        public final static Property Satellites = new Property(21, int.class, "satellites", false, "SATELLITES");
        public final static Property Speed = new Property(22, float.class, "speed", false, "SPEED");
        public final static Property StreetNum = new Property(23, String.class, "streetNum", false, "STREET_NUM");
        public final static Property Time = new Property(24, long.class, "time", false, "TIME");
    }


    public LocationResultDao(DaoConfig config) {
        super(config);
    }
    
    public LocationResultDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"LOCATION_RESULT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"ACCURACY\" REAL NOT NULL ," + // 1: accuracy
                "\"AD_CODE\" TEXT," + // 2: adCode
                "\"ADRESS\" TEXT," + // 3: adress
                "\"ALTITUDE\" REAL NOT NULL ," + // 4: altitude
                "\"AOI_NAME\" TEXT," + // 5: aoiName
                "\"BEARING\" REAL NOT NULL ," + // 6: bearing
                "\"CITY\" TEXT," + // 7: city
                "\"CITY_CODE\" TEXT," + // 8: cityCode
                "\"COUNTRY\" TEXT," + // 9: country
                "\"DISTRICT\" TEXT," + // 10: district
                "\"ERROR_CODE\" INTEGER NOT NULL ," + // 11: errorCode
                "\"ERROR_INFO\" TEXT," + // 12: errorInfo
                "\"LATITUDE\" REAL NOT NULL ," + // 13: latitude
                "\"LOCATION_DETAIL\" TEXT," + // 14: locationDetail
                "\"LOCATION_TYPE\" INTEGER NOT NULL ," + // 15: locationType
                "\"LONGITUDE\" REAL NOT NULL ," + // 16: longitude
                "\"POI_NAME\" TEXT," + // 17: poiName
                "\"PROVIDER\" TEXT," + // 18: provider
                "\"PROVINCE\" TEXT," + // 19: province
                "\"STREET\" TEXT," + // 20: street
                "\"SATELLITES\" INTEGER NOT NULL ," + // 21: satellites
                "\"SPEED\" REAL NOT NULL ," + // 22: speed
                "\"STREET_NUM\" TEXT," + // 23: streetNum
                "\"TIME\" INTEGER NOT NULL );"); // 24: time
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"LOCATION_RESULT\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, LocationResult entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindDouble(2, entity.getAccuracy());
 
        String adCode = entity.getAdCode();
        if (adCode != null) {
            stmt.bindString(3, adCode);
        }
 
        String adress = entity.getAdress();
        if (adress != null) {
            stmt.bindString(4, adress);
        }
        stmt.bindDouble(5, entity.getAltitude());
 
        String aoiName = entity.getAoiName();
        if (aoiName != null) {
            stmt.bindString(6, aoiName);
        }
        stmt.bindDouble(7, entity.getBearing());
 
        String city = entity.getCity();
        if (city != null) {
            stmt.bindString(8, city);
        }
 
        String cityCode = entity.getCityCode();
        if (cityCode != null) {
            stmt.bindString(9, cityCode);
        }
 
        String country = entity.getCountry();
        if (country != null) {
            stmt.bindString(10, country);
        }
 
        String district = entity.getDistrict();
        if (district != null) {
            stmt.bindString(11, district);
        }
        stmt.bindLong(12, entity.getErrorCode());
 
        String errorInfo = entity.getErrorInfo();
        if (errorInfo != null) {
            stmt.bindString(13, errorInfo);
        }
        stmt.bindDouble(14, entity.getLatitude());
 
        String locationDetail = entity.getLocationDetail();
        if (locationDetail != null) {
            stmt.bindString(15, locationDetail);
        }
        stmt.bindLong(16, entity.getLocationType());
        stmt.bindDouble(17, entity.getLongitude());
 
        String poiName = entity.getPoiName();
        if (poiName != null) {
            stmt.bindString(18, poiName);
        }
 
        String provider = entity.getProvider();
        if (provider != null) {
            stmt.bindString(19, provider);
        }
 
        String province = entity.getProvince();
        if (province != null) {
            stmt.bindString(20, province);
        }
 
        String street = entity.getStreet();
        if (street != null) {
            stmt.bindString(21, street);
        }
        stmt.bindLong(22, entity.getSatellites());
        stmt.bindDouble(23, entity.getSpeed());
 
        String streetNum = entity.getStreetNum();
        if (streetNum != null) {
            stmt.bindString(24, streetNum);
        }
        stmt.bindLong(25, entity.getTime());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, LocationResult entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindDouble(2, entity.getAccuracy());
 
        String adCode = entity.getAdCode();
        if (adCode != null) {
            stmt.bindString(3, adCode);
        }
 
        String adress = entity.getAdress();
        if (adress != null) {
            stmt.bindString(4, adress);
        }
        stmt.bindDouble(5, entity.getAltitude());
 
        String aoiName = entity.getAoiName();
        if (aoiName != null) {
            stmt.bindString(6, aoiName);
        }
        stmt.bindDouble(7, entity.getBearing());
 
        String city = entity.getCity();
        if (city != null) {
            stmt.bindString(8, city);
        }
 
        String cityCode = entity.getCityCode();
        if (cityCode != null) {
            stmt.bindString(9, cityCode);
        }
 
        String country = entity.getCountry();
        if (country != null) {
            stmt.bindString(10, country);
        }
 
        String district = entity.getDistrict();
        if (district != null) {
            stmt.bindString(11, district);
        }
        stmt.bindLong(12, entity.getErrorCode());
 
        String errorInfo = entity.getErrorInfo();
        if (errorInfo != null) {
            stmt.bindString(13, errorInfo);
        }
        stmt.bindDouble(14, entity.getLatitude());
 
        String locationDetail = entity.getLocationDetail();
        if (locationDetail != null) {
            stmt.bindString(15, locationDetail);
        }
        stmt.bindLong(16, entity.getLocationType());
        stmt.bindDouble(17, entity.getLongitude());
 
        String poiName = entity.getPoiName();
        if (poiName != null) {
            stmt.bindString(18, poiName);
        }
 
        String provider = entity.getProvider();
        if (provider != null) {
            stmt.bindString(19, provider);
        }
 
        String province = entity.getProvince();
        if (province != null) {
            stmt.bindString(20, province);
        }
 
        String street = entity.getStreet();
        if (street != null) {
            stmt.bindString(21, street);
        }
        stmt.bindLong(22, entity.getSatellites());
        stmt.bindDouble(23, entity.getSpeed());
 
        String streetNum = entity.getStreetNum();
        if (streetNum != null) {
            stmt.bindString(24, streetNum);
        }
        stmt.bindLong(25, entity.getTime());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public LocationResult readEntity(Cursor cursor, int offset) {
        LocationResult entity = new LocationResult( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getFloat(offset + 1), // accuracy
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // adCode
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // adress
            cursor.getDouble(offset + 4), // altitude
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // aoiName
            cursor.getFloat(offset + 6), // bearing
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // city
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // cityCode
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // country
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // district
            cursor.getInt(offset + 11), // errorCode
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // errorInfo
            cursor.getDouble(offset + 13), // latitude
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // locationDetail
            cursor.getInt(offset + 15), // locationType
            cursor.getDouble(offset + 16), // longitude
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // poiName
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // provider
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // province
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // street
            cursor.getInt(offset + 21), // satellites
            cursor.getFloat(offset + 22), // speed
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23), // streetNum
            cursor.getLong(offset + 24) // time
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, LocationResult entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setAccuracy(cursor.getFloat(offset + 1));
        entity.setAdCode(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAdress(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setAltitude(cursor.getDouble(offset + 4));
        entity.setAoiName(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setBearing(cursor.getFloat(offset + 6));
        entity.setCity(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setCityCode(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setCountry(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setDistrict(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setErrorCode(cursor.getInt(offset + 11));
        entity.setErrorInfo(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setLatitude(cursor.getDouble(offset + 13));
        entity.setLocationDetail(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setLocationType(cursor.getInt(offset + 15));
        entity.setLongitude(cursor.getDouble(offset + 16));
        entity.setPoiName(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setProvider(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setProvince(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setStreet(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setSatellites(cursor.getInt(offset + 21));
        entity.setSpeed(cursor.getFloat(offset + 22));
        entity.setStreetNum(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
        entity.setTime(cursor.getLong(offset + 24));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(LocationResult entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(LocationResult entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(LocationResult entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}