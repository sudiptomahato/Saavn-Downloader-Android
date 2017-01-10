package com.arunkr.saavn.downloader;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.arunkr.saavn.downloader.model.SongInfo;

import java.io.File;

/**
 * Created by Arun Kumar Shreevastava on 24/10/16.
 */

public class Utils
{
    public static boolean checkIfFileExists(SongInfo song, Context context)
    {

        File file = new File(getSaveLocation(context),
                song.getDownload_folder()+"/"+song.getSong_name()+song.getExtension());
        return file.exists();
    }

    public static File getBaseFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
    }

    public static File getMusicFolder(String folder_name) {
        File file = null;
        if( folder_name.length() > 0 && folder_name.lastIndexOf('/') == folder_name.length()-1 ) {
            // ignore final '/' character
            folder_name = folder_name.substring(0, folder_name.length()-1);
        }
        //if( folder_name.contains("/") ) {
        if( folder_name.startsWith("/") ) {
            file = new File(folder_name);
        }
        else {
            file = new File(getBaseFolder(), folder_name);
        }
        return file;
    }

    public static String getSaveLocation(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(PreferenceKeys.getSaveLocationPreferenceKey(),
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath());
    }

    /*
    public static boolean getSaveInAlbum(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(PreferenceKeys.getAlbumNamePreferenceKey(), false);
    }
    */

    public static boolean getIsMaxQualityRequested(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(PreferenceKeys.getIsMaxQualityPreferenceKey(), true);
    }
}
