package com.arunkr.saavn.downloader.activity_frag;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arunkr.saavn.downloader.R;

import com.arunkr.saavn.downloader.model.SongInfo;

import java.util.ArrayList;

/**
 * Created by Arun Kumar Shreevastava on 23/10/16.
 */

public class SongsListAdapter extends ArrayAdapter<SongInfo>
{
    private Context context;
    private int layoutResourceId;
    private ArrayList<SongInfo> data = new ArrayList();
    private SparseBooleanArray mSelectedItemsIds;
    private MainActivity main_activity;

    public SongsListAdapter(Context context, int resource)
    {
        super(context, resource);
    }

    public SongsListAdapter(Context context, int layoutResourceId, ArrayList<SongInfo> objects, MainActivity activity)
    {
        super(context, layoutResourceId, objects);

        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = objects;
        mSelectedItemsIds = new SparseBooleanArray(data.size());
        this.main_activity =activity;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View row = convertView;

        if(convertView == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }

        ImageButton button = (ImageButton) row.findViewById(R.id.item_dowload);
        TextView song_name = (TextView)row.findViewById(R.id.item_name);
        TextView album_name = (TextView)row.findViewById(R.id.item_album);

        SongInfo songInfo = data.get(position);

        song_name.setText(songInfo.getSong_name());
        album_name.setText(songInfo.getAlbum_name());

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SongInfo currentSong = data.get(position);
                main_activity.downloadSong(currentSong);
            }
        });

        return row;
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public void removeSelection()
    {
        mSelectedItemsIds = new SparseBooleanArray();
    }

    public void selectAll()
    {
        for(int i=0;i<data.size();i++)
        {
            mSelectedItemsIds.put(i,true);
        }
        //notifyDataSetChanged();
    }

    public void selectView(int position, boolean value)
    {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        //notifyDataSetChanged();
    }

    public void toggleSelection(int position)
    {
        selectView(position, !mSelectedItemsIds.get(position));
    }
}
