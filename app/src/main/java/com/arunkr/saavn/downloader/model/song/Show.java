package com.arunkr.saavn.downloader.model.song;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by arunkr on 14/12/16.
 */

public class Show
{
    @SerializedName("episodes")
    @Expose
    public List<Song> episodes;
}
