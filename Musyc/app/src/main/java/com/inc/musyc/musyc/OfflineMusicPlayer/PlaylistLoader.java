package com.inc.musyc.musyc.OfflineMusicPlayer;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//Loads custom playlists onto ListOfPlaylistsActivity activity

public class PlaylistLoader
{
    static ArrayList<EachSongFormat> listOfPlaylists;
    static PlayListAdapterClass adapterForListOfPlaylists;
    public static void loadPlaylist()   //Loads the playlists alphabetically
    {
        listOfPlaylists = new ArrayList<>();
        if(listOfPlaylists.isEmpty()) Toast.makeText(ListOfPlaylistsActivity.contextForThisActivity, "No playlists available", Toast.LENGTH_SHORT).show();
        else
        {
            Collections.sort(listOfPlaylists, new Comparator<EachSongFormat>() {
                @Override
                public int compare(EachSongFormat a, EachSongFormat b) {
                    return a.getTitle().compareTo(b.getTitle());
                }
            });
            adapterForListOfPlaylists = new PlayListAdapterClass(ListOfPlaylistsActivity.contextForThisActivity, ListOfPlaylistsLoader.listOfPlaylists);
            ListOfPlaylistsActivity.listOfPlaylists.setAdapter(adapterForListOfPlaylists);
        }
    }
}
