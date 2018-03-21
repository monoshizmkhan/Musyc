package com.inc.musyc.musyc.OfflineMusicPlayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inc.musyc.musyc.R;

import java.util.ArrayList;

//List adapter for eachSongFormats being loaded onto PlaylistActivity activity

public class SongsAdapterClass extends BaseAdapter
{
    private ArrayList<EachSongFormat> eachSongFormats;
    private LayoutInflater songInf;
    Context c;
    ImageLoaderClass imageLoaderClass = new ImageLoaderClass();

    public SongsAdapterClass(Context c, ArrayList<EachSongFormat> theEachSongFormats)
    {
        eachSongFormats = theEachSongFormats;
        songInf=LayoutInflater.from(c);                 //initialize in context
        this.c=c;
    }


    @Override
    public int getCount()
    {
        return eachSongFormats.size();
    }       //total song count

    @Override
    public Object getItem(int i)
    {
        return eachSongFormats.get(i);
    }   //Returns an instance of a song

    @Override
    public long getItemId(int i)
    {
        return eachSongFormats.get(i).getID();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent)
    {
        RelativeLayout songLay = (RelativeLayout) songInf.inflate(R.layout.activity_list, parent, false);
        TextView songView = (TextView)songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
        ImageView cover = (ImageView) songLay.findViewById(R.id.cover);
        EachSongFormat currEachSongFormat = eachSongFormats.get(position);
        songView.setText(currEachSongFormat.getTitle());          //Places song name on list
        artistView.setText(currEachSongFormat.getArtist());       //Places artist name on list
        long albumID = eachSongFormats.get(position).getAlbumID();
        if(!currEachSongFormat.getArtist().equals("<unknown>")) imageLoaderClass.loadAndPlaceImage(albumID, cover);  //Loads album image
        if(cover.getDrawable()==null || currEachSongFormat.getArtist().equals("<unknown>"))cover.setImageResource(R.drawable.album);  //If album image is not available, loads default image
        songLay.setTag(position);
        return songLay;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }

}
