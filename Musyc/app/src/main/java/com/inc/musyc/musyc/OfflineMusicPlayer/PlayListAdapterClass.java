package com.inc.musyc.musyc.OfflineMusicPlayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inc.musyc.musyc.R;

import java.util.ArrayList;

//List adapter for playlists
//Not implemented yet

public class PlayListAdapterClass extends BaseAdapter
{
    private ArrayList<String> listOfPlaylists;
    private LayoutInflater songInf;
    Context c;

    public PlayListAdapterClass(Context c, ArrayList<String> listOfPlaylists)    //Initializes using context and arraylist of listOfPlaylists names
    {
        this.listOfPlaylists=listOfPlaylists;
        songInf=LayoutInflater.from(c);
        this.c=c;
    }
    @Override
    public int getCount() {
        return listOfPlaylists.size();
    }

    @Override
    public Object getItem(int i) {
        return listOfPlaylists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        LinearLayout songLay = (LinearLayout) songInf.inflate(R.layout.playlistlist, parent, false);
        TextView title = (TextView)songLay.findViewById(R.id.playlistname);
        if(listOfPlaylists!=null)
        {
            String currSong = listOfPlaylists.get(position);
            if(currSong!=null)title.setText(currSong);
            songLay.setTag(position);
        }
        return songLay;
    }
}
