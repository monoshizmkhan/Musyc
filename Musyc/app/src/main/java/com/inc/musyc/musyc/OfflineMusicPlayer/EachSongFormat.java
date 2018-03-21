package com.inc.musyc.musyc.OfflineMusicPlayer;


//Format for every song

public class EachSongFormat
{
    private long id, albumID;
    private String title, artist, path;
    public EachSongFormat(long id, String title, String artist, String path, long albumID)
    {
        this.id=id;             //song id
        this.title=title;       //song title
        this.artist=artist;     //artist of song
        this.path=path;         //path to song
        this.albumID=albumID;   //album id
    }

    long getID()
    {
        return id;
    }
    String getTitle()
    {
        return title;
    }
    String getArtist()
    {
        return artist;
    }
    String getPath() {return path;}
    long getAlbumID(){return albumID;}
}
