package com.example.sachinf.roadsoft;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by sachinf on 8/23/2016.
 */
public final class SQLHelperHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
//    private static long FOREIGN_KEY;

    private String sessionID;
    private Long result;

    //DATABASE OPERATION TYPES
    public static final int VIDEO = 1;
    public static final int LOCATION = 2;
    public static final int DELETE = 3;



    //DATABASE NAME
    private static final String DATABASE_NAME ="roadsoft";

    //VIDEO TABLE
    private static final String VIDEO_TABLE_NAME ="roadsoft_video_table";
    private static final String VIDEO_TABLE_COLUMN_NAME_ID ="video_id";
    private static final String VIDEO_TABLE_COLUMN_NAME_VIDEO_NAME ="video_name";
    private static final String VIDEO_TABLE_COLUMN_NAME_VIDEO_FILE_PATH ="video_file_path";
    private static final String VIDEO_TABLE_COLUMN_NAME_VIDEO_CREATED_TIMESTAMP ="video_created";
    private static final String VIDEO_TABLE_COLUMN_SESSION_ID ="video_session_id";
    //private static final String VIDEO_TABLE_COLUMN_NAME_LAT_LNG_KEY ="lat_lng_key";

    //LAT_LNG TABLE
    private static final String LAT_LNG_TABLE_NAME ="roadsoft_lat_lng_table";
    private static final String LAT_LNG_TABLE_COLUMN_NAME_LAT_LNG_ID ="lat_lng_id";
    private static final String LAT_LNG_TABLE_COLUMN_NAME_LAT_LNG ="lat_lng";
    private static final String LAT_LNG_TABLE_COLUMN_NAME_TIMESTAMP ="lat_lng_timestamp";
    private static final String LAT_LNG_TABLE_COLUMN_NAME_SPEED ="lat_lng_speed";
//    private static final String LAT_LNG_TABLE_COLUMN_NAME_LAT_LNG_KEY = VIDEO_TABLE_COLUMN_NAME_ID;
    private static final String LAT_LNG_TABLE_COLUMN_SESSION_ID ="video_session_id";


    //CREATE TABLE STATEMENTS
    private static final String CREATE_VIDEO_TABLE = "CREATE TABLE "+ VIDEO_TABLE_NAME + "(" +
            VIDEO_TABLE_COLUMN_NAME_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            VIDEO_TABLE_COLUMN_NAME_VIDEO_NAME + " TEXT NOT NULL," +
            VIDEO_TABLE_COLUMN_NAME_VIDEO_FILE_PATH + " TEXT NOT NULL," +
            VIDEO_TABLE_COLUMN_NAME_VIDEO_CREATED_TIMESTAMP + " TEXT NOT NULL," +
            VIDEO_TABLE_COLUMN_SESSION_ID + " TEXT NOT NULL" +
            //" FOREIGN KEY ("+VIDEO_TABLE_COLUMN_NAME_LAT_LNG_KEY+") REFERENCES "+LAT_LNG_TABLE_NAME+"("+LAT_LNG_TABLE_COLUMN_NAME_LAT_LNG_KEY+")" +
            " )";//END MAIN QUERY


    private static final String CREATE_LAT_LNG_TABLE = "CREATE TABLE "+ LAT_LNG_TABLE_NAME + "(" +
            LAT_LNG_TABLE_COLUMN_NAME_LAT_LNG_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            LAT_LNG_TABLE_COLUMN_NAME_LAT_LNG + " TEXT NOT NULL," +
            LAT_LNG_TABLE_COLUMN_NAME_TIMESTAMP + " TEXT NOT NULL," +
            LAT_LNG_TABLE_COLUMN_NAME_SPEED + " TEXT NOT NULL," +
 //           LAT_LNG_TABLE_COLUMN_NAME_LAT_LNG_KEY + " INTEGER NOT NULL," +
            LAT_LNG_TABLE_COLUMN_SESSION_ID + " TEXT NOT NULL" +
            ")";//END MAIN QUERY

    private static final String SELECT_MAX_ID_QUERY = "SELECT MAX("+VIDEO_TABLE_COLUMN_NAME_ID+") FROM " + VIDEO_TABLE_NAME;

    public SQLHelperHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_VIDEO_TABLE);
        sqLiteDatabase.execSQL(CREATE_LAT_LNG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS"+ VIDEO_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS"+ LAT_LNG_TABLE_NAME);
        onCreate(sqLiteDatabase);
        //TODO remove drop table and implement data migration for future proofing like this
        //http://stackoverflow.com/questions/19793004/android-sqlite-database-why-drop-table-and-recreate-on-upgrade
        //        final SQLiteDatabase db, final int oldVersion,
        //        final int newVersion)
        //        {
        //            int upgradeTo = oldVersion + 1;
        //            while (upgradeTo &lt;= newVersion)
        //            {
        //                switch (upgradeTo)
        //                {
        //                    case 5:
        //                        db.execSQL(SQLiteSet.V5_ADD_LAST_CARD);
        //                        db.execSQL(SQLiteCard.V5_ADD_FAILED);
        //                        break;
        //                    case 6:
        //                        db.execSQL(SQLiteSet.V6_ADD_IMPORT_TYPE);
        //                        break;
        //                    case 7:
        //                        db.execSQL(SQLiteSet.V7_ADD_SHORT_FNAME);
        //                        break;
        //                }
        //                upgradeTo++;
        //            }

    }


    public boolean insertVideoData(String video_name,String video_file_path,String video_created_timestamp){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(VIDEO_TABLE_COLUMN_NAME_VIDEO_NAME,video_name);
        contentValues.put(VIDEO_TABLE_COLUMN_NAME_VIDEO_FILE_PATH,video_file_path);
        contentValues.put(VIDEO_TABLE_COLUMN_NAME_VIDEO_CREATED_TIMESTAMP,video_created_timestamp);
        contentValues.put(LAT_LNG_TABLE_COLUMN_SESSION_ID,MapsActivity.getSessionId());
        //GET ID of row that was inserted
        result = db.insert(VIDEO_TABLE_NAME,null,contentValues);
        if(result == -1){return false;}
        return true;
    }


    public boolean insertLatLngData(String lat_lng,String lat_lng_timestamp,String lat_lng_speed){
        sessionID =  MapsActivity.getSessionId();
//        currentSession = sessionID ;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LAT_LNG_TABLE_COLUMN_NAME_LAT_LNG,lat_lng);
        contentValues.put(LAT_LNG_TABLE_COLUMN_NAME_TIMESTAMP,lat_lng_timestamp);
        contentValues.put(LAT_LNG_TABLE_COLUMN_NAME_SPEED,lat_lng_speed);
        contentValues.put(LAT_LNG_TABLE_COLUMN_SESSION_ID,sessionID);
        result = db.insert(LAT_LNG_TABLE_NAME,null,contentValues);
        if(result == -1){return false;}
        return true;
    }


    //DELETE LATLNG WHEN USER CANCELS VIDEOS
    public boolean deleteCancelledVideoEntries(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(LAT_LNG_TABLE_NAME, LAT_LNG_TABLE_COLUMN_SESSION_ID + " = ?" ,new String[]{ MapsActivity.getSessionId()}) > 0;

    }


    public void makeDatabaseResultToast(Context context,Boolean result,int saveType){
        CharSequence text;

        switch (saveType){

            case VIDEO    : text = "Successfully saved video information in Database";
                            break;

            case LOCATION : text = "Successfully started saving Location";
                            break;

            case DELETE   : text = "Successfully deleted cancelled video locations";
                            break;

            default       : text = "Successfully completed operation";
                            break;

        }
        if(!result) {text = "Something went wrong while saving to the Database";}


        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }




}
