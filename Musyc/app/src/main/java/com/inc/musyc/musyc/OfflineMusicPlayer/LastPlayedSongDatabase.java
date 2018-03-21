package com.inc.musyc.musyc.OfflineMusicPlayer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LastPlayedSongDatabase extends SQLiteOpenHelper
{
    public static final String databaseName="LastSong";
    static SQLiteDatabase db;
    public LastPlayedSongDatabase(Context c)
    {
        super(c, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        db=sqLiteDatabase;
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+databaseName+"(Song_Index varchar(5))");
        init(sqLiteDatabase);
    }

    public void init(SQLiteDatabase db)
    {
        setDefaultLabel(db);
    }

    public void setDefaultLabel(SQLiteDatabase db)
    {
        if(db==null)db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from LastSong", null);
        cursor.moveToFirst();
        int cnt = cursor.getCount();
        if(cnt<=0)
        {
            db.execSQL("insert into LastSong values('0');");        //If database is empty, provides index of 0 to play first song
        }
        cursor.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS LastSong");
        onCreate(sqLiteDatabase);
    }

    public void updateLastSong(int songIndex)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("update LastSong set Song_Index="+songIndex+";");    //Updates last played song
    }

    public int getLastSong()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select Song_Index from LastSong;", null);  //Gets last played song index
        cursor.moveToFirst();
        int lastSongIndex=0;
        if(cursor.getCount()>0)lastSongIndex = Integer.parseInt(cursor.getString(0));
        cursor.close();
        return lastSongIndex;
    }
}
