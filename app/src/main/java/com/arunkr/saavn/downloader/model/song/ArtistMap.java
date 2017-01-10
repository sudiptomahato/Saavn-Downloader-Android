
package com.arunkr.saavn.downloader.model.song;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArtistMap {

    @SerializedName("primary_artists")
    @Expose
    public List<PrimaryArtist> primaryArtists = null;
//    @SerializedName("featured_artists")
//    @Expose
//    public SongList<Object> featuredArtists = null;
//    @SerializedName("artists")
//    @Expose
//    public SongList<Artist> artists = null;

}
