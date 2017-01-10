package com.arunkr.saavn.downloader;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.compat.BuildConfig;

import com.arunkr.saavn.downloader.model.SongInfo;

import java.io.File;

/**
 * Created by Arun Kumar Shreevastava on 23/10/16.
 */

public class DownloadService extends IntentService
{
    private static DownloadManager downloadManager;
    public static final String DOWNLOAD_SONG = "DOWNLOADMANAGER.SONG";
    boolean downloadNext = true;
    long last_download_id=-1;
    SongInfo song;


    public DownloadService()
    {
        super("DownloadService");
    }

    public DownloadService(String name)
    {
        super(name);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        while (!downloadNext)
        {
            try
            {
                Thread.sleep(5000);  //sleep for 5 sec
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        downloadNext = false;  //disable download till this download completes
        song = intent.getParcelableExtra(DOWNLOAD_SONG);
        if(!Utils.checkIfFileExists(song,getApplicationContext()))  //file doesn't exist
        {
            addToDownload(song);
        }
        else
        {
            downloadNext=true;
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(onComplete);
    }

    void addToDownload(SongInfo currentSong)
    {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(currentSong.getDownload_url()));

        request.setDescription(currentSong.getAlbum_name())
                .setTitle(currentSong.getSong_name())
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationUri(Uri.fromFile(new File(Utils.getSaveLocation(getApplicationContext()),
                        currentSong.getDownload_folder()+"/"
                        + currentSong.getSong_name() + currentSong.getExtension()))
                );
        if(downloadManager == null)
        {
            downloadManager = (DownloadManager)getApplicationContext()
                    .getSystemService(Context.DOWNLOAD_SERVICE);
        }

        last_download_id =downloadManager.enqueue(request);
        insert_into_database(currentSong,last_download_id);
    }

    void insert_into_database(SongInfo song, Long downloaded_id)
    {
        DatabaseHelper helper = new DatabaseHelper(getApplicationContext(),
                "metadata.db", null, BuildConfig.VERSION_CODE);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("download_id",downloaded_id);
        values.put("album",song.getAlbum_name());
        values.put("artist",song.getArtist_name());
        values.put("year",song.getYear());
        values.put("language",song.getLanguage());

        db.insert("Metadata",null,values);
        db.close();
    }

    BroadcastReceiver onComplete=new BroadcastReceiver()
    {
        public void onReceive(Context ctxt, Intent intent)
        {
            Bundle extras = intent.getExtras();
            Long downloaded_id = extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID);
            if(downloaded_id == last_download_id)
            {
                downloadNext = true;
            }
        }
    };
}
