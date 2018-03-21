package com.inc.musyc.musyc.OfflineMusicPlayer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.inc.musyc.musyc.R;

import java.util.ArrayList;

//Loads list of videos

public class VideosLoaderActivity extends AppCompatActivity
{
    static ArrayList<EachVideoFormat> videosList = new ArrayList<>();
    static boolean hasLoaded=false;
    static VideoAdapterClass videoAdapterClass;
    static ListView videosListView;
    static Context contextOfThisActivity;
    static int videoIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos_loader);
        contextOfThisActivity=getApplicationContext();
        getVideosList();

        ImageButton backButton = (ImageButton) findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        videosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                KaraokeHubActivity.videoPath=videosList.get(position).getPath();    //Sets current video path
                KaraokeHubActivity.videoName=videosList.get(position).getTitle();   //Sets current video title
                //videoIndex=position;                                        //Sets current video index
                Intent intent = new Intent(VideosLoaderActivity.this, KaraokeHubActivity.class);
                startActivity(intent);
            }
        });
    }

    void getVideosList()
    {
        if(!hasLoaded)      //If videos haven't been loaded on once, manually loads default lyric videos onto list
        {
            hasLoaded=true;
            String videoPath=("android.resource://" + getPackageName() + "/" + R.raw.boulevard_of_broken_breams);
            videosList.add(new EachVideoFormat("Boulevard of Broken Dreams (Green Day)", videoPath));
            videoPath=("android.resource://" + getPackageName() + "/" + R.raw.fix_you);
            videosList.add(new EachVideoFormat("Fix You (Coldplay)", videoPath));
            videoPath=("android.resource://" + getPackageName() + "/" + R.raw.ill_be_there_for_you);
            videosList.add(new EachVideoFormat("I'll Be There For You (The Rembrandts)", videoPath));
            videoPath=("android.resource://" + getPackageName() + "/" + R.raw.summer_of_69);
            videosList.add(new EachVideoFormat("Summer of '69 (Bryan Adams)", videoPath));
        }
        videoAdapterClass = new VideoAdapterClass(this, videosList);
        videosListView = (ListView) findViewById(R.id.videolist);
        videosListView.setAdapter(videoAdapterClass);;
    }
}
