package com.inc.musyc.musyc.OfflineMusicPlayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.inc.musyc.musyc.R;

//Music player UI
//Controls media player

public class PlayerHubActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener
{
    Button menuButton;
    PopupMenu popupMenuButton;
    static SeekBar seekBar;
    static TextView songName;
    static TextView artistName;
    ImageButton shuffleButton;
    ImageButton repeatButton;
    static ImageButton loopButton;
    static ImageView albumCoverImage;
    static ImageButton playButton;
    ImageButton stopButton;
    ImageButton forwardButton;
    ImageButton backwardButton;
    static TextView currentSongProgress;
    static TextView totalSongDuration;
    Button openListOfPlaylists;
    static int previousSongInShuffle;
    static Context contextOfThisActivity;
    ImageButton searchBarButton;
    EditText searchBarText;
    static LastPlayedSongDatabase lastSongDB;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mToolbar=(Toolbar)findViewById(R.id.plr_toolbar);
        contextOfThisActivity=getApplicationContext();
        openListOfPlaylists = (Button) findViewById(R.id.customplaylist);
        albumCoverImage = (ImageView) findViewById(R.id.preview);
        shuffleButton = (ImageButton) findViewById(R.id.shufflebutton);
        repeatButton = (ImageButton) findViewById(R.id.repeatbutton);
        playButton = (ImageButton) findViewById(R.id.playbutton);
        stopButton = (ImageButton) findViewById(R.id.stopbutton);
        forwardButton = (ImageButton) findViewById(R.id.forwardbutton);
        backwardButton = (ImageButton) findViewById(R.id.prevbutton);
        loopButton = (ImageButton) findViewById(R.id.loopbutton);
        currentSongProgress = (TextView) findViewById(R.id.timegone);
        totalSongDuration = (TextView) findViewById(R.id.totaltime);
        Button openPlaylist = (Button) findViewById(R.id.lib);
        Button openFeed = (Button) findViewById(R.id.feed);
        menuButton = (Button) findViewById(R.id.menu);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        songName = (TextView) findViewById(R.id.songname);
        artistName = (TextView) findViewById(R.id.artistname);
        searchBarButton =  (ImageButton) findViewById(R.id.searchicon);
        searchBarText = (EditText) findViewById(R.id.search);
        lastSongDB = new LastPlayedSongDatabase(contextOfThisActivity);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);

//        <ImageButton
//        android:layout_width="25sp"
//        android:layout_height="23sp"
//        android:layout_marginTop="3dp"
//        android:src="@drawable/back"
//        android:scaleType="fitCenter"
//        android:background="@drawable/border"
//        android:id="@+id/back"
//        android:layout_below="@+id/plr_toolbar"
//                />
//
//
//    <TextView
//        android:layout_width="match_parent"
//        android:layout_height="wrap_content"
//        android:text=" Musyc"
//        android:textSize="20sp"
//        android:textColor="@android:color/white"
//        android:layout_toRightOf="@+id/back"
//        android:layout_below="@+id/plr_toolbar"
//                />
        ImageLoaderClass imageLoaderClass = new ImageLoaderClass();
        if(SongLoader.hasLoadedOnce) imageLoaderClass.loadAndPlaceImage(SongLoader.songsList.get(PlaybackManagerClass.songIndex).getAlbumID(), albumCoverImage);
        songName.setText(PlaybackManagerClass.songTitle);
        artistName.setText(PlaybackManagerClass.artistsTitle);
        if(PlaybackManagerClass.isPlaying) playButton.setImageResource(R.drawable.pause);    //Updates playButton/pause button if player activity is just strted


        seekBar.setOnSeekBarChangeListener(this);


//        ImageButton back = (ImageButton) findViewById(R.id.back);
//        back.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                finish();     //Exits app
//            }
//        }); //backButton button


        searchBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = searchBarText.getText().toString();
                if(name==null || name.equals(""))Toast.makeText(PlayerHubActivity.this, "Please enter song to search", Toast.LENGTH_LONG).show();
                else
                {
                    SearchActivity.searchingName=name;
                    searchBarText.setText("");
                    searchBarText.setHint("Search by song name");
                    Intent intent = new Intent(PlayerHubActivity.this, SearchActivity.class);
                    startActivity(intent);
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(SongLoader.songsList.isEmpty())
                {
                    Toast.makeText(PlayerHubActivity.this, "No songs available", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(PlaybackManagerClass.isPlaying)
                    {
                        PlaybackManagerClass.isPlaying=false;
                        playButton.setImageResource(R.drawable.play);     //If song was playing, pauses and updates icon
                        PlaybackManagerClass.pauseSong();
                    }
                    else
                    {
                        playButton.setImageResource(R.drawable.pause);    //If song was paused, resumes and updates icon and notification
                        PlaybackManagerClass.isPlaying=true;
                        NotificationManager.songState="Now Playing";
                        NotificationManager.updateNotification(SongLoader.songsList.get(PlaybackManagerClass.songIndex).getTitle());
                        if(PlaybackManagerClass.hasStarted) PlaybackManagerClass.mediaPlayer.start();
                        else
                            PlaybackManagerClass.playSong(PlaybackManagerClass.songIndex);
                    }
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(PlaybackManagerClass.isPlaying)
                {
                    PlaybackManagerClass.isPlaying=false;
                    playButton.setImageResource(R.drawable.play);     //Stops song, sets progress to zero and updates icons
                    PlaybackManagerClass.stopSong();
                    seekBar.setProgress(0);
                    currentSongProgress.setText("0:00");
                    NotificationManager.destroyNotification();
                }
            }
        });

        loopButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                PlaybackManagerClass.looping();
                if(!PlaybackManagerClass.isLooping)Toast.makeText(PlayerHubActivity.this, "Not looping yet", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(PlayerHubActivity.this, "Looping", Toast.LENGTH_LONG).show();
            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if(!SongLoader.songsList.isEmpty()) PlaybackManagerClass.nextSong();
                else
                    Toast.makeText(PlayerHubActivity.this, "No songs available", Toast.LENGTH_SHORT).show();
            }
        });

        backwardButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if(!SongLoader.songsList.isEmpty()) PlaybackManagerClass.previousSong();
                else
                    Toast.makeText(PlayerHubActivity.this, "No songs available", Toast.LENGTH_SHORT).show();
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if(PlaybackManagerClass.isRepeatOn)    //turns off repeat
                {
                    PlaybackManagerClass.isRepeatOn = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();    //Updates icon and enables repeating
                    repeatButton.setImageResource(R.drawable.repeat);
                }
                else
                {
                    PlaybackManagerClass.isRepeatOn = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();     //Updates icon, enables shuffling and
                    PlaybackManagerClass.isShuffleOn = false;                                                    //disables repeating
                    repeatButton.setImageResource(R.drawable.repeat_clicked);
                    shuffleButton.setImageResource(R.drawable.shuffle);
                }
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if(PlaybackManagerClass.isShuffleOn)
                {
                    PlaybackManagerClass.isShuffleOn = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();   //Enables shuffling and updates icon
                    shuffleButton.setImageResource(R.drawable.shuffle);
                }
                else
                {
                    previousSongInShuffle = PlaybackManagerClass.songIndex;
                    PlaybackManagerClass.isShuffleOn = true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();    //Enables shuffling, disables repeat and
                    PlaybackManagerClass.isRepeatOn = false;                                                     //updates icons
                    shuffleButton.setImageResource(R.drawable.shuffle__clicked);
                    repeatButton.setImageResource(R.drawable.repeat);
                }
            }
        });



        openPlaylist.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(PlayerHubActivity.this, PlaylistActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);      //If listOfPlaylists activity has not yet been started, starts activity
                startActivityIfNeeded(i, 0);
                PlaylistActivity.listViewForPlaylist.setAdapter(SongLoader.songsAdapterClass);
            }
        });


        menuButton.setOnClickListener(new View.OnClickListener()      //longClickPopupMenu menuButton open hoy
        {
            @Override
            public void onClick(View view)
            {
                popupMenuButton = new PopupMenu(PlayerHubActivity.this, menuButton);
                popupMenuButton.getMenuInflater().inflate(R.menu.popup_menu, popupMenuButton.getMenu());    //Opens longClickPopupMenu menuButton
                popupMenuButton.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {

                        if(item.getTitle().equals("Add to Playlist"))
                        {
                            ListOfPlaylistsActivity.hasComeToAddToExistingPlaylist =true;
                            Intent i = new Intent(PlayerHubActivity.this, ListOfPlaylistsActivity.class);        //Opens custom listOfPlaylists activity
                            startActivity(i);
                        }
                        return true;
                    }
                });
                popupMenuButton.show();
            }
        });

        if(SongLoader.songsList!=null)
        {
            lastSongDB.init(LastPlayedSongDatabase.db);
            if(lastSongDB.getLastSong()<SongLoader.songsList.size()) PlaybackManagerClass.songIndex=lastSongDB.getLastSong();
            else
                PlaybackManagerClass.songIndex=0;
            if(!SongLoader.songsList.isEmpty())
            {
                PlayerHubActivity.songName.setText(SongLoader.songsList.get(PlaybackManagerClass.songIndex).getTitle());
                PlayerHubActivity.artistName.setText(SongLoader.songsList.get(PlaybackManagerClass.songIndex).getArtist());
                PlaylistActivity.songsTitle.setText(SongLoader.songsList.get(PlaybackManagerClass.songIndex).getTitle());
                PlaylistActivity.artistTitle.setText(SongLoader.songsList.get(PlaybackManagerClass.songIndex).getArtist());
            }
            else
            {
                Toast.makeText(PlayerHubActivity.this, "No songs available", Toast.LENGTH_SHORT).show();
                PlayerHubActivity.songName.setText("No Songs");
                PlayerHubActivity.artistName.setText("");
                PlaylistActivity.songsTitle.setText("No Songs");
                PlaylistActivity.artistTitle.setText("");
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        PlaybackManagerClass.seeking();
    }

    static boolean isFinished=false;
    @Override
    protected void onDestroy() {
        PlaylistActivity.finishing();
        isFinished=true;
        super.onDestroy();
        PlaybackManagerClass.stopSong();
        NotificationManager.notifMan = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);   //Gets system notification service
        NotificationManager.notifMan.cancelAll();
        NotificationManager.destroyNotification();

    }
}
