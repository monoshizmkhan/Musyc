package com.inc.musyc.musyc.OfflineMusicPlayer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.inc.musyc.musyc.R;

import java.util.ArrayList;

public class EachPlaylistActivity extends AppCompatActivity
{
    static String listName="";
    static ArrayList<String> songIndexes;
    PopupMenu longClickPopupMenu;
    static ImageButton menuButton, playButton, previousButton, forwardButton;
    static TextView songName, artistName;
    ListView listViewForPlaylist;
    ArrayList<EachSongFormat> songList;
    static int songSelected;
    static Context contextForThisActivity;
    int selectedSongPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_playlist);

        ImageButton back = (ImageButton) findViewById(R.id.back);       //Initialization
        menuButton = (ImageButton) findViewById(R.id.menu);
        listViewForPlaylist = (ListView) findViewById(R.id.playlist);
        contextForThisActivity=getApplicationContext();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();       //Setting up back button
            }
        });

        playButton = (ImageButton) findViewById(R.id.play);
        forwardButton = (ImageButton) findViewById(R.id.next);
        previousButton = (ImageButton) findViewById(R.id.prev);
        songName = (TextView) findViewById(R.id.songname);
        artistName = (TextView) findViewById(R.id.artistname);
        Button fullPlaylist = (Button) findViewById(R.id.all);

        fullPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(EachPlaylistActivity.this, PlaylistActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(intent, 0);
            }
        });

        if(PlaybackManagerClass.isPlaying)playButton.setImageResource(R.drawable.pause);    //Update song and artist name
        if(songName!=null) songName.setText(PlayerHubActivity.songName.getText());          //And play/pause buttons
        if(artistName!=null) artistName.setText(PlayerHubActivity.artistName.getText());


        menuButton.setOnClickListener(new View.OnClickListener()  //pop up menuButton open hoy
        {
            @Override
            public void onClick(View view) {
                longClickPopupMenu = new PopupMenu(EachPlaylistActivity.this, menuButton);
                longClickPopupMenu.getMenuInflater().inflate(R.menu.popup_menu, longClickPopupMenu.getMenu());
                longClickPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Karaoke"))   //Opens Karaoke app
                        {
                            longClickPopupMenu.dismiss();
                            NotificationManager.destroyNotification();     //Removes current notification
                            PlaybackManagerClass.pauseSong();                    //Pauses audio playback
                            Intent intent = new Intent(EachPlaylistActivity.this, VideosLoaderActivity.class);
                            startActivityIfNeeded(intent, 0);
                            return true;
                        }
                        else if (item.getTitle().equals("Add to Playlist")) { //Opens custom listOfPlaylists activity
                            ListOfPlaylistsActivity.hasComeToAddToExistingPlaylist = true;
                            Intent i = new Intent(EachPlaylistActivity.this, ListOfPlaylistsActivity.class);
                            startActivity(i);
                        }
                        return true;
                    }
                });
                longClickPopupMenu.show();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PlaybackManagerClass.isPlaying) {
                    playButton.setImageResource(R.drawable.play);
                    PlaybackManagerClass.pauseSong();
                } else {
                    playButton.setImageResource(R.drawable.pause);
                    PlaybackManagerClass.mediaPlayer.start();
                    PlaybackManagerClass.isPlaying = true;
                    NotificationManager.songState="Now Playing";
                    NotificationManager.updateNotification(SongLoader.songsList.get(PlaybackManagerClass.songIndex).getTitle());
                }
            }
        });


        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                PlaybackManagerClass.nextSong();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                PlaybackManagerClass.previousSong();
            }
        });

        songIndexes = new ArrayList<>();
        songList = new ArrayList<>();
        getList();          //Load playlist onto listview

        listViewForPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                songSelected=position;
                PlaybackManagerClass.isPlaylist=true;
                PlaybackManagerClass.songIndex=Integer.parseInt(songIndexes.get(position).toString());
                PlaybackManagerClass.playSong(PlaybackManagerClass.songIndex);      //Plays selected song
            }
        });

        listViewForPlaylist.setLongClickable(true);
        listViewForPlaylist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int positionList, long l) {
                selectedSongPosition =positionList;
                Toast.makeText(EachPlaylistActivity.this, "Long Clicking", Toast.LENGTH_LONG).show();
                listViewForPlaylist.setClickable(false);
                popup();
                listViewForPlaylist.setClickable(true);         //Opens popup window to delete song
                return false;
            }
        });

    }

    void getList()
    {
        EachPlaylistDatabase eachPlaylistDatabase = new EachPlaylistDatabase(this, listName);
        songIndexes= eachPlaylistDatabase.getList();       //Load playlist data from database
        int index=0;
        if(songIndexes==null) Toast.makeText(EachPlaylistActivity.this, "No songs available", Toast.LENGTH_SHORT).show();
        else
        {
            while(index!=songIndexes.size())        //Loads complete song data to playlist view using index from database
            {
                songList.add(new EachSongFormat(SongLoader.songsList.get(Integer.parseInt(songIndexes.get(index))).getID(), SongLoader.songsList.get(Integer.parseInt(songIndexes.get(index))).getTitle(), SongLoader.songsList.get(Integer.parseInt(songIndexes.get(index))).getArtist(), SongLoader.songsList.get(Integer.parseInt(songIndexes.get(index))).getPath(), SongLoader.songsList.get(Integer.parseInt(songIndexes.get(index))).getAlbumID()));
                index++;
            }
            SongsAdapterClass playListAdapter = new SongsAdapterClass(this, songList);
            listViewForPlaylist.setAdapter(playListAdapter);
        }
    }

    void popup()
    {
        final String selectedSongName=songIndexes.get(selectedSongPosition).toString();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        TextView title = new TextView(this);
        title.setText("Select Action");
        alertDialogBuilder.setView(title);
        title.setTextSize(17);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton("Delete song from playlist", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EachPlaylistDatabase eachPlaylistDatabase = new EachPlaylistDatabase(contextForThisActivity, listName);
                PlaybackManagerClass.stopSong();
                PlaybackManagerClass.isPlaylist=false;
                PlaybackManagerClass.songIndex++;
                eachPlaylistDatabase.removeFromList(selectedSongName);        //Stop playing current song and remove the
                songIndexes = new ArrayList<>();                        //long clicked song from list, then updates the
                songList = new ArrayList<>();                           //playlist.
                getList();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        try
        {
            alertDialog.show();
        }
        catch(IllegalStateException e)
        {
            Toast.makeText(EachPlaylistActivity.this, "Could not execute long press", Toast.LENGTH_LONG).show();
        }
    }
}
