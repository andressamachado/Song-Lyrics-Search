package com.andressamachado.songlyricssearch;

/****************************************************************************************
 Filename:SongLyricsDBOpener.java
 Author: Andressa Pessoa de Araujo Machado
 Date: 2020/08/17
 Purpose: Manage the database that we will be working with during the use of this application.
 It will create the table, upgrade de table when necessary, and downgrade it
 ***************************************************************************************/

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SongLyricsDBOpener extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "SongsSearchedDB";
    private static int VERSION_NUMBER = 1;

    //Table Definitions
    protected final static String TABLE_NAME = "SongLyricsTable";
    public static final String COL_SEARCH_ID = "_id";
    protected static final String COL_ARTISTS = "Artist";
    protected static final String COL_SONGS = "Song";
    protected static final String COL_IS_FAVORITE = "isFavorite";


    public SongLyricsDBOpener(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ARTISTS + " text,"
                + COL_SONGS + " text, " + COL_IS_FAVORITE + " Integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        VERSION_NUMBER++;
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        VERSION_NUMBER++;
    }

    public void onUpdate(SQLiteDatabase db, int favorite, int id){
        db.execSQL("update table " + TABLE_NAME + " set " + COL_IS_FAVORITE + " = " + favorite
                + " where " + COL_SEARCH_ID + " = " + id );
    }
}


