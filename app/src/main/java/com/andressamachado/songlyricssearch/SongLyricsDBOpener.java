package com.andressamachado.songlyricssearch;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.appcompat.app.AppCompatActivity;

/******************************************************************************************
 *  Manage the database that we will be working with during the use of this application.
 *  It will create the table, upgrade de table when necessary, and downgrade it
 *
 * @author Andressa Machado
 * @version 1.0
 * @see AppCompatActivity
 * @since 2020/08/17
 ******************************************************************************************/
public class SongLyricsDBOpener extends SQLiteOpenHelper{
    /**Database Constants*/
    private static final String DATABASE_NAME = "SongsSearchedDB";
    private static int VERSION_NUMBER = 1;

    /**Table constants*/
    protected final static String TABLE_NAME = "SongLyricsTable";
    public static final String COL_SEARCH_ID = "_id";
    protected static final String COL_ARTISTS = "Artist";
    protected static final String COL_SONGS = "Song";
    protected static final String COL_IS_FAVORITE = "isFavorite";

    public SongLyricsDBOpener(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUMBER);
    }

    /**
     * OnCreate method is called when the database does not exist yet.
     *
     * @param db database object given by android for running SQL commands
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //query passed as argument to create the table when the app is load for the first time
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ARTISTS + " text,"
                + COL_SONGS + " text, " + COL_IS_FAVORITE + " Integer);");
    }

    /**
     * Called when the database needs to be updated. When the database does exist on the device
     * and the version in the constructor is newer than the version that exists on the device.
     *
     * @param db database object given by android for running SQL commands
     * @param oldVersion database version on the device
     * @param newVersion database version on the constructor
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        //not quite sure if it is correct
        VERSION_NUMBER++;
    }

    /**
     * Called when the database does exist and the version number in the constructor is lower
     * than the version number that exists on the device.
     *
     * @param db database object given by android for running SQL commands
     * @param oldVersion database version on the device
     * @param newVersion database version on the constructor
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        VERSION_NUMBER++;
    }

//    public void onUpdate(SQLiteDatabase db, int favorite, int id){
//        db.execSQL("update table " + TABLE_NAME + " set " + COL_IS_FAVORITE + " = " + favorite
//                + " where " + COL_SEARCH_ID + " = " + id );
//    }
}


