package com.arunkr.saavn.downloader.model;

import com.arunkr.saavn.downloader.model.song.Show;
import com.arunkr.saavn.downloader.model.song.Song;
import com.arunkr.saavn.downloader.model.song.SongList;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Arun Kumar Shreevastava on 12/12/16.
 */

public interface SaavnApi
{
    @GET("/api.php")
    Call<List<Song>> makeSongRequest(
            @QueryMap(encoded=true) Map<String, String> options
    );

    @GET("/api.php")
    Call<SongList> makePlaylistRequest(
            @QueryMap(encoded=true) Map<String, String> options
    );

    @GET("/api.php")
    Call<Show> makeShowRequest(
            @QueryMap(encoded=true) Map<String, String> options
    );

    @GET("/api.php")
    Call<Song> makeEpisodeRequest(
            @QueryMap(encoded=true) Map<String, String> options
    );
}
