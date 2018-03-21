package com.inc.musyc.musyc.OfflineMusicPlayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.inc.musyc.musyc.R;

//Loads all songs and custom playlists

public class PlaylistActivity extends Activity
{
    ImageButton menuButton;
    PopupMenu popupMenuButton;
    static ListView listViewForPlaylist;
    private Utilities converterClass;
    static ImageButton playButton;
    static TextView songsTitle;
    static TextView artistTitle;
    static ImageButton searchBarButton;
    static boolean playlistHasBeenLoadedOnce =false;
    EditText searchBarText;
    Button buttonForCustomPlaylists;
    static Context contextOfThisActivity;
    static TextView textfieldIfPlaylistEmpty;
    Button openListOfPlaylists;
    boolean isStartUp=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        contextOfThisActivity = getApplicationContext();

        songsTitle = (TextView) findViewById(R.id.songname);
        artistTitle = (TextView) findViewById(R.id.artistname);
        playButton = (ImageButton) findViewById(R.id.play);
        ImageButton forwardButton = (ImageButton) findViewById(R.id.next);
        ImageButton previousButton = (ImageButton) findViewById(R.id.prev);
        searchBarButton = (ImageButton) findViewById(R.id.searchicon);
        converterClass = new Utilities();
        if (PlaybackManagerClass.isPlaying) playButton.setImageResource(R.drawable.pause);
        searchBarText = (EditText) findViewById(R.id.search);
        searchBarText.setEnabled(true);
        buttonForCustomPlaylists = (Button)findViewById(R.id.customplaylist);
        textfieldIfPlaylistEmpty = (TextView) findViewById(R.id.emptylist);
        textfieldIfPlaylistEmpty.setEnabled(false);
        listViewForPlaylist = (ListView) findViewById(R.id.playlist);
        songsTitle.setText(PlaybackManagerClass.songTitle);
        artistTitle.setText(PlaybackManagerClass.artistsTitle);
        openListOfPlaylists = (Button) findViewById(R.id.customplaylist);

        ImageButton back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistHasBeenLoadedOnce=false;
                finish();
            }
        });

        Button feed = (Button) findViewById(R.id.feed);


        Button customplaylists = (Button) findViewById(R.id.customplaylist);
        customplaylists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PlaylistActivity.this, ListOfPlaylistsActivity.class);
                startActivity(i);
            }
        }); //Opens custom playlists activity


        searchBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = searchBarText.getText().toString();
                if(name==null || name.equals(""))Toast.makeText(PlaylistActivity.this, "Please enter song to search", Toast.LENGTH_LONG).show();
                else
                {
                    SearchActivity.searchingName=name;
                    searchBarText.setText("");
                    searchBarText.setHint("Search by song name");
                    Intent intent = new Intent(PlaylistActivity.this, SearchActivity.class);
                    startActivity(intent);
                }
            }
        });


        listViewForPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                PlaybackManagerClass.songIndex = position;
                PlaybackManagerClass.playSong(PlaybackManagerClass.songIndex);
                PlaybackManagerClass.isPlaylist=false;
            }
        });

        if (!SongLoader.hasLoadedOnce)      //If songs haven't been loaded yet once, loads songs
        {
            SongLoader.getSongsList();
            playlistHasBeenLoadedOnce = true;




        openListOfPlaylists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewOfThisActivity)
            {
                ListOfPlaylistsActivity.hasComeToAddToExistingPlaylist=false;
                Intent i = new Intent(PlaylistActivity.this, ListOfPlaylistsActivity.class);
                startActivity(i);
            }
        });

            menuButton = (ImageButton) findViewById(R.id.menu);
            menuButton.setOnClickListener(new View.OnClickListener()  //pop up menuButton open hoy
            {
                @Override
                public void onClick(View view) {
                    popupMenuButton = new PopupMenu(PlaylistActivity.this, menuButton);
                    popupMenuButton.getMenuInflater().inflate(R.menu.popup_menu, popupMenuButton.getMenu());
                    popupMenuButton.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getTitle().equals("Add to Playlist")) { //Opens custom listOfPlaylists activity
                                ListOfPlaylistsActivity.hasComeToAddToExistingPlaylist = true;
                                Intent i = new Intent(PlaylistActivity.this, ListOfPlaylistsActivity.class);
                                startActivity(i);
                            }
                            return true;
                        }
                    });
                    popupMenuButton.show();
                }
            });

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(SongLoader.songsList.isEmpty())Toast.makeText(PlaylistActivity.this, "No Songs Available", Toast.LENGTH_SHORT).show();
                    else
                    {
                        if(PlaybackManagerClass.isPlaying)
                        {
                            playButton.setImageResource(R.drawable.play);
                            PlaybackManagerClass.pauseSong();
                        }
                        else
                        {
                            playButton.setImageResource(R.drawable.pause);
                            PlaybackManagerClass.mediaPlayer.start();
                            PlaybackManagerClass.isPlaying = true;
                            NotificationManager.songState="Now Playing";
                            NotificationManager.updateNotification(SongLoader.songsList.get(PlaybackManagerClass.songIndex).getTitle());
                        }
                    }
                }
            });


            forwardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if(SongLoader.songsList.isEmpty())Toast.makeText(PlaylistActivity.this, "No Songs Available", Toast.LENGTH_SHORT).show();
                    else
                        PlaybackManagerClass.nextSong();
                }
            });

            previousButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if(SongLoader.songsList.isEmpty())Toast.makeText(PlaylistActivity.this, "No Songs Available", Toast.LENGTH_SHORT).show();
                    else
                        PlaybackManagerClass.previousSong();
                }
            });
        }
        if(isStartUp)
        {
            isStartUp=false;
            Intent intent = new Intent(PlaylistActivity.this, PlayerHubActivity.class); //If activity is starting up, redirects
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);                     //to Player Hub Activity
            startActivityIfNeeded(intent, 0);
        }
        ending = (Button) findViewById(R.id.ending);
        ending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });                 //Destroys activity

    }

    static Button ending;

    static void finishing()
    {
        PlaybackManagerClass.pauseSong();   //pauses song before destroying activity
        ending.performClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(PlayerHubActivity.isFinished)PlaybackManagerClass.pauseSong();
        ending.performClick();
    }
}
