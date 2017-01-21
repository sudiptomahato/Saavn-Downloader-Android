package com.arunkr.saavn.downloader.activity_frag;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.arunkr.saavn.downloader.DownloadService;
import com.arunkr.saavn.downloader.R;
import com.arunkr.saavn.downloader.Utils;
import com.arunkr.saavn.downloader.PreferenceKeys;
import com.arunkr.saavn.downloader.SaavnAPIRequester;
import com.arunkr.saavn.downloader.activity_frag.SongsListAdapter;
import com.arunkr.saavn.downloader.model.SongInfo;
import com.arunkr.saavn.downloader.model.song.Show;
import com.arunkr.saavn.downloader.model.song.Song;
import com.arunkr.saavn.downloader.model.song.SongList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arun Kumar Shreevastava on 25/10/16.
 */


public class MainActivity extends AppCompatActivity
{
    final private int MY_PERMISSIONS_REQUEST_STORAGE = 0;
    public static Cipher decrypter;

    ArrayList<SongInfo> songs_list = new ArrayList<>();
    SongsListAdapter adapter;
    ListView listView;
    ClipboardManager myClipboard;
    ProgressDialog progressDialog;
    ProgressDialog downloadingDialog;
    private static final String host_address = "http://www.saavn.com";

    SharedPreferences sharedPreferences;
    DownloadManager downloadManager;

    static boolean active;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null && type.equals("text/plain"))
        {
            handleSendText(intent); // Handle text being sent
        }
        else
        {
            boolean has_done_first_time = sharedPreferences.contains(PreferenceKeys.getFirstTimePreferenceKey());
            if(!has_done_first_time)
            {
                final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
                alertDialog.setTitle(R.string.help_title);
                alertDialog.setMessage(R.string.usage_text);
                alertDialog.setPositiveButton(R.string.help_positive, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intent = new Intent(MainActivity.this,HelpActivity.class);
                        startActivity(intent);
                    }
                });
                alertDialog.setNegativeButton(R.string.help_negative, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
                setFirstTimeFlag();
            }
            try
            {
                loadUrlFromClipboard();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private void setFirstTimeFlag()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PreferenceKeys.getFirstTimePreferenceKey(), true);
        editor.apply();
    }

    void handleSendText(Intent intent)
    {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null && sharedText.contains(host_address))
        {
            setFirstTimeFlag();
            new ProcessLink().execute(sharedText.substring(sharedText.indexOf(host_address)).trim());
        }
    }

    void loadUrlFromClipboard()
    {
        ClipData abc = myClipboard.getPrimaryClip();
        if(abc!=null)
        {
            ClipData.Item clip_item = abc.getItemAt(0);
            String text = clip_item.getText().toString();

            if (text.contains(host_address))
            {
                new ProcessLink().execute(text.substring(text.indexOf(host_address)));
            }
        }
    }

    void init()
    {
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading");
        progressDialog.setCanceledOnTouchOutside(false);

        downloadingDialog = new ProgressDialog(MainActivity.this);
        downloadingDialog.setMessage("Downloading...");
        downloadingDialog.setCanceledOnTouchOutside(false);

        downloadManager = (DownloadManager)getApplicationContext()
                .getSystemService(Context.DOWNLOAD_SERVICE);

        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

        try
        {
            decrypter = Cipher.getInstance("DES/ECB/PKCS5Padding");
            decrypter.init(2, new SecretKeySpec("38346591".getBytes(), "DES"));

        } catch (Exception ex)
        {
            ex.printStackTrace();
            finish();
        }

        listView = (ListView)findViewById(R.id.songs_listview);

        adapter = new SongsListAdapter(this,R.layout.item_download_link,songs_list,this);
        listView.setAdapter(adapter);

        listView.setEnabled(true);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                ((ListView) parent).setItemChecked(position,
                        ((ListView) parent).isItemChecked(position));
                return false;
            }
        });

        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
        {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
            {
                // Calls toggleSelection method from ListViewAdapter Class
                adapter.toggleSelection(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu)
            {
                mode.getMenuInflater().inflate(R.menu.activity_main_upload, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu)
            {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.download:
                        // Calls getSelectedIds method from ListViewAdapter Class
                        SparseBooleanArray selected = adapter.getSelectedIds();
                        // Captures all selected ids with a loop
                        for (int i = (selected.size() - 1); i >= 0; i--)
                        {
                            if (selected.valueAt(i))
                            {
                                //send to upload service
                                SongInfo song = adapter.getItem(selected.keyAt(i));
                                downloadSong(song);
                            }
                        }
                        // Close CAB
                        mode.finish();
                        return true;
                    case R.id.select_all:
                        for(int i=0;i<songs_list.size();i++)
                        {
                            listView.setItemChecked(i,true);
                        }
                        adapter.selectAll();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode)
            {
                adapter.removeSelection();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.btn_down_all:
                downThemAll();
                break;
            case R.id.btn_add:
                showAddDialog();
                break;
            case R.id.btn_help:
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_about:
                Intent intent2 = new Intent(this, AboutActivity.class);
                startActivity(intent2);
                break;
            case R.id.btn_settings:
                openSettings();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void downThemAll()
    {
        for(SongInfo song:songs_list)
        {
            downloadSong(song);
        }
    }

    void downloadSong(SongInfo song)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestStoragePermission();
                // return for now - the application should try to reopen the app if permission is granted
                return;
            }
        }

        if(!Utils.checkIfFileExists(song,this))
        {
            Intent intent = new Intent(this, DownloadService.class);
            //Intent intent = new Intent(this, DownService.class);
            intent.putExtra(DownloadService.DOWNLOAD_SONG, song);
            startService(intent);
        }
        else
        {
            makeToast("File :"+song.getSong_name()+" already exists!",Toast.LENGTH_SHORT);
        }
    }

    void openSettings()
    {
        MyPreferenceFragment fragment = new MyPreferenceFragment();
        // use commitAllowingStateLoss() instead of commit(), does to "java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState" crash seen on Google Play
        // see http://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform-this-action-after-onsaveinstancestate-wit
        getFragmentManager().beginTransaction().add(R.id.prefs_container, fragment, "PREFERENCE_FRAGMENT").addToBackStack(null).commitAllowingStateLoss();
    }

    @Override
    protected void onPause()
    {
        active = false;
        downloadingDialog.dismiss();
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        active = true;
        super.onResume();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showRequestPermissionRationale(final int permission_code) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            return;
        }

        boolean ok = true;
        String[] permissions = null;
        int message_id = 0;
        if (permission_code == MY_PERMISSIONS_REQUEST_STORAGE)
        {
            permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            message_id = R.string.permission_rationale_storage;
        }
        else
        {
            ok = false;
        }

        if (ok) {
            final String[] permissions_f = permissions;
            new android.app.AlertDialog.Builder(this)
                    .setTitle(R.string.permission_rationale_title)
                    .setMessage(message_id)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.ok, null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        public void onDismiss(DialogInterface dialog)
                        {
                            ActivityCompat.requestPermissions(MainActivity.this, permissions_f, permission_code);
                        }
                    }).show();
        }
    }

    void requestStoragePermission()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            showRequestPermissionRationale(MY_PERMISSIONS_REQUEST_STORAGE);
        } else {
            // Can go ahead and request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
        }
    }

    void showAddDialog()
    {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View add_url_view = layoutInflater.inflate(R.layout.add_url, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(add_url_view);

        final EditText input_url = (EditText) add_url_view
                .findViewById(R.id.input_url);

        ClipData abc = myClipboard.getPrimaryClip();
        if(abc!=null)
        {
            ClipData.Item clip_item = abc.getItemAt(0);
            String text = clip_item.getText().toString();

            if (text.contains(host_address))
            {
                input_url.setText(text);
            }
        }

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                new ProcessLink().execute(input_url.getText().toString().trim());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    void makeToast(final String msg, final int length)
    {
        this.runOnUiThread(new Runnable() {
            public void run() {
                //Your code here
                Toast.makeText(MainActivity.this,msg,length).show();
            }
        });
    }

    public class ProcessLink extends AsyncTask<String, Void,Void>
    {
        String L = "hindi|english|tamil|telugu|punjabi|marathi|gujarati|bengali|kannada|bhojpuri|malayalam|urdu|rajasthani|odia";

        SaavnAPIRequester apiRequester;

        Pattern song_album = Pattern.compile("^/(s|p|search)/(song|album)/(" + L + ")?/?([^/]*/)([^/]*/)?(.*)$");
        Pattern show = Pattern.compile("^/(s|search|p|play)/show/(.*)$");
        Pattern playlist = Pattern.compile("^/(s|search)/(playlist|featured|genres)/(" + L + ")?/?([^/]*/)?([^/]*/)?(.*)$");

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            apiRequester = new SaavnAPIRequester();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
        }

        void showError()
        {
            progressDialog.dismiss();
            makeToast("Error while processing link", Toast.LENGTH_LONG);
        }

        @Override
        protected Void doInBackground(String... params)
        {
            String saavn_link = params[0].replace(host_address,"");
            Matcher matcher = song_album.matcher(saavn_link);
            if (matcher.find())
            {
                process_song_album(matcher);
            }
            else if((matcher = show.matcher(saavn_link)).find())
            {
                process_show(matcher);
            }
            else if((matcher = playlist.matcher(saavn_link)).find())
            {
                process_playlist(matcher);
            }
            return null;
        }

        void process_song_album(Matcher matcher)
        {
            String type = matcher.group(2);
            String token = matcher.group(6);
            if(token != null)
            {
                int index = token.indexOf("?");
                if(index>0)
                    token = token.substring(0, index);
            }
            if (type.equals("song"))
            {
                if (token == null || token.length() == 0)
                {
                    showError();
                    return;
                }
                else
                {
                    apiRequester.fillSongToken("song", token);
                }
            }
            else if (type.equals("album"))
            {
                if (token == null || token.length() == 0)
                {
                    showError();
                    return;
                } else
                {
                    apiRequester.fillSongToken("album", token);
                }
            }
            apiRequester.makeSongRequest().enqueue(callback_song_album);
        }

        void process_show(Matcher matcher)
        {
            String[] arr = matcher.group(2).split("/");
            if (arr.length == 3)
            {
                int index = arr[2].indexOf("?");
                if(index>0)
                    arr[2] = arr[2].substring(0,index);
                apiRequester.fillShowToken(arr[2],arr[1],"show");
                apiRequester.makeShowRequest().enqueue(callback_show);
            }
            else if (arr.length == 4)
            {
                int index = arr[3].indexOf("?");
                if(index>0)
                    arr[3] = arr[3].substring(0,index);
                apiRequester.fillShowToken(arr[3],arr[1],"episode");
                apiRequester.makeEpisodeRequest().enqueue(callback_episode);
            }
        }

        void process_playlist(Matcher matcher)
        {
            String username;
            String listname = matcher.group(2);
            String group4 = matcher.group(4);
            String group5 = matcher.group(5);
            String token = matcher.group(6);

            if (listname != null)
            {
                if(token !=null)
                {
                    int index = token.indexOf("?");
                    if(index>0)
                        token = token.substring(0, index);
                }
                if (listname.contentEquals("playlist"))
                {
                    if (group4 == null || (group5 == null && token == null)) {
                        return;
                    }
                    username = group4.replace("/", "");
                    if (group5 != null)
                    {
                        listname = group5.replace("/", "").replaceAll("[\\+\\_]", " ");
                    }
                    else
                    {
                        listname = token.replace("/", "").replaceAll("[\\+\\_]", " ");
                    }
                    apiRequester.fillPlaylist(username,listname,token);
                }
                else if (listname.contentEquals("featured"))
                {
                    listname = group4 == null ? token : group4;
                    listname = listname.replace("/", "").replaceAll("[\\+\\_]", " ");
                    apiRequester.fillPlaylist("username",listname,token);
                }
                apiRequester.makePlaylistRequest().enqueue(callback_playlist);
            }
        }

        Callback<List<Song>> callback_song_album = new Callback<List<Song>>()
        {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response)
            {
                List<Song> result = response.body();
                if(result.size()>0)
                    songs_list.clear();
                else
                    return;
                for (Song s : result)
                {
                    String encUrl = s.moreInfo.encryptedMediaUrl;
                    if(encUrl==null || encUrl.equals("") || encUrl.equalsIgnoreCase("us98KHesG0c=")) //check if url is null
                    {
                        continue;
                    }
                    SongInfo song = new SongInfo();
                    song.setSong_name(s.title);
                    song.setLanguage(s.language);
                    song.setYear(s.year);
                    song.setAlbum_name(s.moreInfo.album);
                    song.setDownload_folder(s.moreInfo.album);
                    if(s.moreInfo.artistMap.primaryArtists.size()>=1)
                    {
                        song.setArtist_name(s.moreInfo.artistMap.primaryArtists.get(0).name);
                    }
                    if(Boolean.parseBoolean(s.moreInfo._320kbps)
                            && Utils.getIsMaxQualityRequested(MainActivity.this))
                    {
                        song.setDownload_url(encUrl,true);
                    }
                    else
                    {
                        song.setDownload_url(encUrl,false);
                    }
                    songs_list.add(song);
                }
                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t)
            {
                makeToast("Error while processing link",Toast.LENGTH_LONG);
            }
        };

        Callback<Show> callback_show = new Callback<Show>()
        {
            @Override
            public void onResponse(Call<Show> call, Response<Show> response)
            {
                List<Song> result = response.body().episodes;
                if(result.size()>0)
                    songs_list.clear();
                else
                    return;
                for (Song s : result)
                {
                    String encUrl = s.moreInfo.encryptedMediaUrl;
                    if(encUrl==null || encUrl.equals("") || encUrl.equalsIgnoreCase("us98KHesG0c=")) //check if url is null
                    {
                        continue;
                    }
                    SongInfo song = new SongInfo();
                    song.setSong_name(s.title);
                    song.setLanguage(s.language);
                    song.setYear(s.year);
                    song.setAlbum_name(s.moreInfo.showTitle);
                    song.setDownload_folder(s.moreInfo.showTitle,s.moreInfo.seasonTitle);
                    if(s.moreInfo.artistMap.primaryArtists.size()>=1)
                    {
                        song.setArtist_name(s.moreInfo.artistMap.primaryArtists.get(0).name);
                    }
                    song.setDownload_url(encUrl,false);
                    songs_list.add(song);
                }
                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Show> call, Throwable t)
            {
                showError();
            }
        };

        Callback<Song> callback_episode = new Callback<Song>()
        {
            @Override
            public void onResponse(Call<Song> call, Response<Song> response)
            {
                Song s = response.body();
                if(s != null)
                    songs_list.clear();
                else
                    return;
                String encUrl = s.moreInfo.encryptedMediaUrl;
                if(encUrl==null || encUrl.equals("") || encUrl.equalsIgnoreCase("us98KHesG0c=")) //check if url is null
                {
                    return;
                }
                SongInfo song = new SongInfo();
                song.setSong_name(s.title);
                song.setLanguage(s.language);
                song.setYear(s.year);
                song.setAlbum_name(s.moreInfo.showTitle);
                song.setDownload_folder(s.moreInfo.showTitle,s.moreInfo.seasonTitle);
                if(s.moreInfo.artistMap.primaryArtists.size()>=1)
                {
                    song.setArtist_name(s.moreInfo.artistMap.primaryArtists.get(0).name);
                }
                song.setDownload_url(encUrl,false);
                songs_list.add(song);

                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Song> call, Throwable t)
            {
                showError();
            }
        };

        Callback<SongList> callback_playlist = new Callback<SongList>()
        {
            @Override
            public void onResponse(Call<SongList> call, Response<SongList> response)
            {
                String playlist_name = response.body().title;
                List<Song> result = response.body().list;
                if(result.size()>0)
                    songs_list.clear();
                else
                    return;
                for (Song s : result)
                {
                    String encUrl = s.moreInfo.encryptedMediaUrl;
                    if(encUrl==null || encUrl.equals("") || encUrl.equalsIgnoreCase("us98KHesG0c=")) //check if url is null
                    {
                        continue;
                    }
                    SongInfo song = new SongInfo();
                    song.setSong_name(s.title);
                    song.setLanguage(s.language);
                    song.setYear(s.year);
                    song.setAlbum_name(s.moreInfo.album);
//                    if(Utils.getSaveInAlbum(MainActivity.this))
//                    {
//                        song.setDownload_folder(s.moreInfo.album);
//                    }
//                    else
//                    {
                        song.setDownload_folder(playlist_name);
//                    }
                    if(s.moreInfo.artistMap.primaryArtists.size()>=1)
                    {
                        song.setArtist_name(s.moreInfo.artistMap.primaryArtists.get(0).name);
                    }
                    if(Boolean.getBoolean(s.moreInfo._320kbps)
                            && Utils.getIsMaxQualityRequested(MainActivity.this))
                    {
                        song.setDownload_url(encUrl,true);
                    }
                    else
                    {
                        song.setDownload_url(encUrl,false);
                    }
                    songs_list.add(song);
                }
                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SongList> call, Throwable t)
            {
                makeToast("Error while processing link",Toast.LENGTH_LONG);
            }
        };

    }
}
