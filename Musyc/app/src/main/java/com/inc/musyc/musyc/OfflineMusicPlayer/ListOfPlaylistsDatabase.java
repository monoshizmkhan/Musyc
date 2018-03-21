package com.inc.musyc.musyc.OfflineMusicPlayer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class ListOfPlaylistsDatabase extends SQLiteOpenHelper
{
    public static final String databaseName="Playlists";
    public ListOfPlaylistsDatabase(Context c)
    {
        super(c, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+databaseName+"(Playlist_Name varchar(30));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Playlists");
        onCreate(sqLiteDatabase);
    }

    public void createNewPlaylist(String playlistName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("insert into "+databaseName+" values('"+playlistName+"');"); //Creates new playlist using string passed
    }                                                                           //as parameter

    public void removePlaylist(String playlistName)
    {
        SQLiteDatabase db = this.getWritableDatabase();                         //Removes playlist from list
        db.execSQL("delete from "+databaseName+" where "+databaseName+".Playlist_Name='"+playlistName+"';");
    }

    public ArrayList<String> getList()
    {
        ArrayList<String> listNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select Playlist_Name from Playlists;", null);  //Loads names of playlists from database
        cursor.moveToFirst();                                                       //onto an arraylist and returns it
        if(cursor.getCount()>0)
        {
            do
            {
                String nameOfEachList = cursor.getString(cursor.getColumnIndex("Playlist_Name"));
                listNames.add(nameOfEachList);
            }
            while(cursor.moveToNext());
        }
        return listNames;
    }
}
