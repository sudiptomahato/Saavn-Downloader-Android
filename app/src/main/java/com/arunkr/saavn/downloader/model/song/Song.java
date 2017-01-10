package com.arunkr.saavn.downloader.model.song;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arunkr on 14/12/16.
 */

public class Song extends SongBase
{
    @SerializedName("more_info")
    @Expose
    public MoreInfo moreInfo;
}
