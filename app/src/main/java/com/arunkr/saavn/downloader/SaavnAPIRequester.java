package com.arunkr.saavn.downloader;

import com.arunkr.saavn.downloader.model.SaavnApi;
import com.arunkr.saavn.downloader.model.song.Show;
import com.arunkr.saavn.downloader.model.song.Song;
import com.arunkr.saavn.downloader.model.song.SongList;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Arun Kumar Shreevastava on 12/12/16.
 */

public class SaavnAPIRequester
{
    private Map<String,String> paramHashMap;
    SaavnApi service;

    public SaavnAPIRequester()
    {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(3,TimeUnit.MINUTES);

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl("http://www.saavn.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit
                .create(SaavnApi.class);
        initApiHashMap();
    }

    private void initApiHashMap()
    {
        paramHashMap = new HashMap<>();

        paramHashMap.put("ctx", "android");
        paramHashMap.put("_format", "json");
        paramHashMap.put("_marker", "0");

        paramHashMap.put("network_type", "WIFI");
        paramHashMap.put("network_subtype", "");
        paramHashMap.put("network_operator", "Reliance");
        paramHashMap.put("api_version", "4");
        paramHashMap.put("cc", "in");
        paramHashMap.put("v", "61");
        paramHashMap.put("readable_version", "5.4");
        paramHashMap.put("app_version", "5.4");
        paramHashMap.put("manufacturer", "Google");
        paramHashMap.put("model", "Pixel");
        paramHashMap.put("build", "2");
        paramHashMap.put("state", "logout");
        paramHashMap.put("session_device_id", RandomStringUtils.randomAlphanumeric(8)+"."
                +RandomUtils.nextInt(1471527612,1481527612));
    }

    public Call<List<Song>> makeSongRequest()
    {
        return service.makeSongRequest(paramHashMap);
    }

    public Call<SongList> makePlaylistRequest()
    {
        return service.makePlaylistRequest(paramHashMap);
    }

    public Call<Show> makeShowRequest()
    {
        return service.makeShowRequest(paramHashMap);
    }

    public Call<Song> makeEpisodeRequest()
    {
        return service.makeEpisodeRequest(paramHashMap);
    }

    //final Album album = new Album(ff.v(), ff.o(), ff.p(), ff.f(), ff.k(), ff.l(), 0, false, k.size(), ff.m(), k, k.size(), "", new JSONObject());
    //
    public void fillSongToken(String type, String token)
    {
        paramHashMap.put("__call", "content.decodeTokenAndFetchResults");
        paramHashMap.put("type", type); //song/album
        paramHashMap.put("token",token);
    }

    public void fillShowToken(String token, String season_no, String type)
    {
        paramHashMap.put("__call", "content.decodeTokenAndFetchResults");
        paramHashMap.put("token", token);
        paramHashMap.put("type", type);
        paramHashMap.put("season_number", season_no);
    }

    public void fillPlaylist(String username, String listname, String token)
    {
        paramHashMap.put("__call", "playlist.search");
        paramHashMap.put("username", username);
        paramHashMap.put("listname", listname);
        if (token != null) {
            paramHashMap.put("token", token);
        }
    }
}
