package com.inc.musyc.musyc.OfflineMusicPlayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//Loads songs onto PlaylistActivity activity

public class SongLoader
{
    static ArrayList<EachSongFormat> songsList = new ArrayList<>();
    static boolean hasLoadedOnce=false;
    static SongsAdapterClass songsAdapterClass;
    public static void getSongsList()
    {
        songsList = new ArrayList<>();
        hasLoadedOnce=true;     //Makes sure songs are loaded only once
        ContentResolver musicResolver = PlaylistActivity.contextOfThisActivity.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;    //Reads songs from external storage
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);     //Initializes cursor
        if(musicCursor!=null && musicCursor.moveToFirst())      //Starts from the first, if cursor isn't null
        {
            int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);    //song title data
            int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);         //song id data
            int artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);  //song artist data
            int albumID = musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID);                          //song album data
            do
            {
                long albumId = musicCursor.getLong(albumID);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisPath = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                Long mediaID = musicCursor.getLong(idColumn);
                songsList.add(new EachSongFormat(mediaID, thisTitle, thisArtist, thisPath, albumId)); //Creates new song class instance and adds to list
            }
            while (musicCursor.moveToNext());   //Moves to next data
        }

        musicUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;         //Reads songs from internal storage
        musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst())      //Starts from the first, if cursor isn't null
        {
            int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);    //song title data
            int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);         //song id data
            int artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);  //song artist data
            int albumID = musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID);                          //song album data
            do
            {
                long albumId = musicCursor.getLong(albumID);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisPath = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                Long mediaID = musicCursor.getLong(idColumn);
                songsList.add(new EachSongFormat(mediaID, thisTitle, thisArtist, thisPath, albumId)); //Creates new song class instance and adds to list
            }
            while (musicCursor.moveToNext());   //Moves to next data
        }
        if(songsList.isEmpty()) //If there are no songs in playlist
        {
            PlaylistActivity.textfieldIfPlaylistEmpty.setText("No songs found");
            PlaylistActivity.listViewForPlaylist.setEnabled(false);
        }
        else
        {
            Collections.sort(songsList, new Comparator<EachSongFormat>()
            {
                public int compare(EachSongFormat a, EachSongFormat b)
                {
                    return a.getTitle().compareTo(b.getTitle());
                }
            }); //Sorts songs alphabetically

            songsAdapterClass = new SongsAdapterClass(PlaylistActivity.contextOfThisActivity, songsList);     //Initializes and assigns adapter
            PlaylistActivity.listViewForPlaylist.setAdapter(songsAdapterClass);
            PlaylistActivity.listViewForPlaylist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            PlaylistActivity.listViewForPlaylist.setFocusable(false);
        }
    }
    public static int getSongIndex(String songPath)
    {
        for(int i=0;i<songsList.size();i++)
        {
            if(songsList.get(i).getPath().equals(songPath))return i;
        }
        return -1;
    }
    public static String getSongPath(int index)
    {
        return songsList.get(index).getPath();
    }
}
