package com.inc.musyc.musyc.OfflineMusicPlayer;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//Loads list of playlists

public class ListOfPlaylistsLoader
{
    static ArrayList<String> listOfPlaylists;
    static ListOfPlaylistsDatabase playlistDB = new ListOfPlaylistsDatabase(ListOfPlaylistsActivity.contextForThisActivity);
    public static void getPlaylistsList()                   //Loads list of playlists from database onto an arraylist
    {
        listOfPlaylists = new ArrayList<>();
        listOfPlaylists = playlistDB.getList();
        if(listOfPlaylists.isEmpty() || listOfPlaylists==null) Toast.makeText(ListOfPlaylistsActivity.contextForThisActivity, "No playlists available", Toast.LENGTH_SHORT).show();
        else
        {
            Collections.sort(listOfPlaylists, new Comparator<String>() {
                @Override
                public int compare(String a, String b) {        //Sorts the playlist names alphabetically
                    return a.compareTo(b);
                }
            });     //compare and sort the playlists alphabetically
            PlayListAdapterClass playListAdapterClass = new PlayListAdapterClass(ListOfPlaylistsActivity.contextForThisActivity, listOfPlaylists);
            ListOfPlaylistsActivity.listOfPlaylists.setAdapter(playListAdapterClass);
        }
    }
}

