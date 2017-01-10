package com.arunkr.saavn.downloader;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.compat.BuildConfig;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

/**
 * Created by Arun Kumar Shreevastava on 15/12/16.
 */

public class DownloadCompleteReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction().equals("android.intent.action.DOWNLOAD_COMPLETE"))
        {
            DatabaseHelper helper = new DatabaseHelper(context, "metadata.db", null, BuildConfig.VERSION_CODE);
            Bundle extras = intent.getExtras();
            Long downloaded_id = extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID);
            SQLiteDatabase db = helper.getReadableDatabase();
            String query = "SELECT * FROM Metadata WHERE download_id = ?;";
            String[] whereArgs = new String[]{
                    downloaded_id.toString()
            };
            Cursor cursor = db.rawQuery(query, whereArgs);
            if (cursor.getCount() > 0)
            {
                DownloadManager downloadManager = (DownloadManager) context
                        .getSystemService(Context.DOWNLOAD_SERVICE);
                File filename = new File(downloadManager.getUriForDownloadedFile(downloaded_id).getPath());
                try
                {
                    AudioFile f = AudioFileIO.read(filename);
                    Tag tag = f.getTagOrCreateAndSetDefault();
                    cursor.moveToFirst();
                    tag.setField(FieldKey.ALBUM, cursor.getString(cursor.getColumnIndexOrThrow("album")));
                    tag.setField(FieldKey.ARTIST, cursor.getString(cursor.getColumnIndexOrThrow("artist")));
                    tag.setField(FieldKey.YEAR, cursor.getString(cursor.getColumnIndexOrThrow("year")));
                    tag.setField(FieldKey.LANGUAGE, cursor.getString(cursor.getColumnIndexOrThrow("language")));
                    tag.setField(FieldKey.GENRE, cursor.getString(cursor.getColumnIndexOrThrow("language")));
                    AudioFileIO.write(f);

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                db.execSQL("DELETE FROM Metadata WHERE download_id=?;",whereArgs);
            }
            cursor.close();
            db.close();
            helper.close();
        }
    }
}
