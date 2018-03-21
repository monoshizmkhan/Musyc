package com.inc.musyc.musyc.OfflineMusicPlayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.inc.musyc.musyc.R;

import java.util.ArrayList;

//Search results activity

public class SearchActivity extends AppCompatActivity
{
    static String searchingName="";
    ArrayList<EachSongFormat> eachSongFormatList;
    ImageButton backButton;
    ListView songListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);

        backButton = (ImageButton) findViewById(R.id.back);
        songListView = (ListView) findViewById(R.id.playlist);

        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                eachSongFormatList = new ArrayList<>();     //Clears search results before destroying activity
                finish();
            }
        });

        findAndListSongs();

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                EachSongFormat currEachSongFormat = eachSongFormatList.get(position);
                int toPlay = SongLoader.getSongIndex(currEachSongFormat.getPath());
                PlaybackManagerClass.songIndex=toPlay;
                PlaybackManagerClass.playSong(toPlay);
                eachSongFormatList = new ArrayList<>();
                finish();
            }
        });
    }


    void findAndListSongs()
    {
        eachSongFormatList = new ArrayList<>();         //Creates new arraylist to store search results
        String toFind=searchingName.toUpperCase();      //Converts the searching keywords to upper case
        String checkIfSame="";
        for(int i=0;i<SongLoader.songsList.size()-1;i++)
        {
            checkIfSame=SongLoader.songsList.get(i).getTitle().toUpperCase();   //Converts each song title to upper case
            if(checkIfSame.contains(toFind))            //If song title contains key word, adds to array list
            {
                eachSongFormatList.add(SongLoader.songsList.get(i));
            }
        }
        SongsAdapterClass songsAdapterClass = new SongsAdapterClass(this, eachSongFormatList);
        songListView.setAdapter(songsAdapterClass);
        if(eachSongFormatList.isEmpty()) Toast.makeText(SearchActivity.this, "No matching results for "+searchingName, Toast.LENGTH_LONG).show();
    }                                                   //If no songs match keyword, sends toast
}
