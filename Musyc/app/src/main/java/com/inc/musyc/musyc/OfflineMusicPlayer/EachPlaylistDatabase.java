package com.inc.musyc.musyc.OfflineMusicPlayer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class EachPlaylistDatabase extends SQLiteOpenHelper
{
    static String databaseName="";
    public EachPlaylistDatabase(Context c, String databaseName)
    {
        super(c, databaseName, null, 1);
        this.databaseName=databaseName;     //Opens instance of database object for specific playlist
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+databaseName+"(Song_Index varchar(5));");
    }                                                                               //Opens playlist if playlist does not
                                                                                    //exist yet.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+databaseName+";");
        onCreate(sqLiteDatabase);
    }

    public void addToList(String songIndex)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("insert into "+databaseName+" values('"+songIndex+"');");        //Add song to playlist
    }

    public void removeFromList(String songIndex)
    {
        SQLiteDatabase db = this.getWritableDatabase();                             //Remove song from playlist
        db.execSQL("delete from "+databaseName+" where "+databaseName+".Song_Index='"+songIndex+"';");
    }

    public ArrayList<String> getList()
    {
        ArrayList<String> songsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select Song_Index from "+databaseName+";", null);  //Loads all the song indexes
        cursor.moveToFirst();                                                           //stored in the database onto an
        if(cursor.getCount()>0)                                                         //arraylist and returns the list.
        {
            do
            {
                String nameOfEachList = cursor.getString(cursor.getColumnIndex("Song_Index"));
                songsList.add(nameOfEachList);
            }
            while(cursor.moveToNext());
        }
        return songsList;
    }
}
