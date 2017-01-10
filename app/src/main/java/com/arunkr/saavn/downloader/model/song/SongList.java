package com.arunkr.saavn.downloader.model.song;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by arunkr on 13/12/16.
 */

public class SongList extends SongBase
{
    @SerializedName("list")
    @Expose
    public List<Song> list;
}
