package com.arunkr.saavn.downloader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Arun Kumar Shreevastava on 15/12/16.
 */

public class DatabaseHelper extends SQLiteOpenHelper
{
    //tag.setField(FieldKey.ALBUM,song.getAlbum_name());
    //tag.setField(FieldKey.ARTIST,song.getArtist_name());
    //tag.setField(FieldKey.YEAR,song.getYear());
    //tag.setField(FieldKey.LANGUAGE,song.getLanguage());
   // tag.setField(FieldKey.GENRE,song.getLanguage());
    private static String METADATA_TABLE = "CREATE TABLE IF NOT EXISTS Metadata" +
            "( " +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "download_id NUMERIC UNIQUE,"+
            "album VARCHAR(128)," +
            "artist VARCHAR(128)," +
            "year CHAR(4)," +
            "language VARCHAR(20)"+
            ");";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(METADATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS Metadata;");
        onCreate(db);
    }
}
