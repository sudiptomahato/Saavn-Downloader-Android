
package com.arunkr.saavn.downloader.model.song;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MoreInfo {

//    @SerializedName("music")
//    @Expose
//    public String music;
//    @SerializedName("album_id")
//    @Expose
//    public String albumId;
    @SerializedName("album")
    @Expose
    public String album;
//    @SerializedName("label")
//    @Expose
//    public String label;
//    @SerializedName("origin")
//    @Expose
//    public String origin;
    @SerializedName("320kbps")
    @Expose
    public String _320kbps;
    @SerializedName("encrypted_media_url")
    @Expose
    public String encryptedMediaUrl;
//    @SerializedName("album_url")
//    @Expose
//    public String albumUrl;
//    @SerializedName("duration")
//    @Expose
//    public String duration;
//    @SerializedName("cache_state")
//    @Expose
//    public String cacheState;
//    @SerializedName("has_lyrics")
//    @Expose
//    public String hasLyrics;
//    @SerializedName("starred")
//    @Expose
//    public String starred;
    @SerializedName("artistMap")
    @Expose
    public ArtistMap artistMap;
//    @SerializedName("lyrics_id")
//    @Expose
//    public String lyricsId;
    @SerializedName("show_title")
    @Expose
    public String showTitle;
    @SerializedName("season_title")
    @Expose
    public String seasonTitle;
}
