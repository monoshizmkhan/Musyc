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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.inc.musyc.musyc.R;

import java.util.ArrayList;
import java.util.List;

//Shows custom playlists
//Lets creation of new playlists

public class ListOfPlaylistsActivity extends AppCompatActivity
{
    static ListView listOfPlaylists;
    static ImageButton menuButton, playButton, previousButton, forwardButton;
    static TextView songName, artistName;
    Button createNewPlaylist;
    static boolean hasComeToAddToExistingPlaylist =false;
    static Context contextForThisActivity;
    int position;
    String selectedPlaylistName;
    PopupMenu popupMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_lists);

        contextForThisActivity=this.getApplicationContext();
        ImageButton backButton = (ImageButton) findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        Button viewAllSongs = (Button) findViewById(R.id.all);
        viewAllSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ListOfPlaylistsActivity.this, PlaylistActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(i, 0);
            }
        });

        Button libraryOfAllSongs = (Button) findViewById(R.id.lib);
        libraryOfAllSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ListOfPlaylistsActivity.this, PlaylistActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(i, 0);
            }
        });

        songName = (TextView) findViewById(R.id.songname);
        artistName = (TextView) findViewById(R.id.artistname);
        playButton = (ImageButton) findViewById(R.id.play);
        forwardButton = (ImageButton) findViewById(R.id.next);
        previousButton = (ImageButton) findViewById(R.id.prev);

        if(PlaybackManagerClass.isPlaying)playButton.setImageResource(R.drawable.pause);
        if(PlayerHubActivity.songName!=null)songName.setText(PlayerHubActivity.songName.getText());
        if(PlayerHubActivity.artistName!=null) artistName.setText(PlayerHubActivity.artistName.getText());

        menuButton = (ImageButton) findViewById(R.id.menu);
        menuButton.setOnClickListener(new View.OnClickListener()  //pop up menuButton open hoy
        {
            @Override
            public void onClick(View view) {
                popupMenuButton = new PopupMenu(ListOfPlaylistsActivity.this, menuButton);
                popupMenuButton.getMenuInflater().inflate(R.menu.popup_menu, popupMenuButton.getMenu());
                popupMenuButton.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Add to Playlist")) { //Opens custom Playlists activity
                            ListOfPlaylistsActivity.hasComeToAddToExistingPlaylist = true;
                            Intent i = new Intent(ListOfPlaylistsActivity.this, ListOfPlaylistsActivity.class);
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

        listOfPlaylists = (ListView) findViewById(R.id.playlist);
        ListOfPlaylistsLoader.getPlaylistsList();

        listOfPlaylists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                if(hasComeToAddToExistingPlaylist)
                {
                    EachPlaylistDatabase eachPlaylistDatabase = new EachPlaylistDatabase(contextForThisActivity, ListOfPlaylistsLoader.listOfPlaylists.get(position).toString());
                    eachPlaylistDatabase.addToList(""+ PlaybackManagerClass.songIndex);
                    Toast.makeText(contextForThisActivity, SongLoader.songsList.get(PlaybackManagerClass.songIndex).getTitle()+" successfully added to "+ListOfPlaylistsLoader.listOfPlaylists.get(position).toString(), Toast.LENGTH_SHORT).show();
                    hasComeToAddToExistingPlaylist=false;
                }   //If user opens the list of playlists activity to add a song to a playlist, the playlist names act like buttons
                else
                {
                    EachPlaylistActivity.listName=ListOfPlaylistsLoader.listOfPlaylists.get(position).toString();
                    Intent intent = new Intent(ListOfPlaylistsActivity.this, EachPlaylistActivity.class);
                    startActivityIfNeeded(intent, 0);
                }   //Else, clicking on the playlist names open those playlists
            }
        });

        listOfPlaylists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int positionList, long l)
            {
                position=positionList;
                Toast.makeText(ListOfPlaylistsActivity.this, "Long clicking playlist list", Toast.LENGTH_SHORT).show();
                listOfPlaylists.setClickable(false);
                longClickPopup();                           //Opens popupmenu to delete playlist or add current song to it
                listOfPlaylists.setClickable(true);
                return false;
            }
        });

        createNewPlaylist = (Button) findViewById(R.id.newplay);
        createNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                popup();
            }
        });
    }

    void longClickPopup()
    {
        selectedPlaylistName = ListOfPlaylistsLoader.listOfPlaylists.get(position).toString();
        Toast.makeText(contextForThisActivity, selectedPlaylistName, Toast.LENGTH_SHORT).show();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        TextView title = new TextView(this);
        title.setText("Select Action");
        title.setTextSize(17);
        alertDialogBuilder.setView(title);
        alertDialogBuilder.setPositiveButton("Add current song to Playlist", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                EachPlaylistDatabase eachPlaylistDatabase = new EachPlaylistDatabase(contextForThisActivity, selectedPlaylistName);
                String temp=selectedPlaylistName;
                String divider[] = temp.split(" ");
                if(divider.length>1)
                {
                    Toast.makeText(ListOfPlaylistsActivity.this, "Please don't use space and symbols , . ? / > < ( ) { } [ ] | ", Toast.LENGTH_SHORT).show();
                }   //Stops from opening a database table with a space or symbols in it
                selectedPlaylistName=temp;
                eachPlaylistDatabase.addToList(""+ PlaybackManagerClass.songIndex);
            }
        });
        alertDialogBuilder.setCancelable(true).setPositiveButton("Delete Playlist", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                ListOfPlaylistsDatabase listOfPlaylistsDatabase = new ListOfPlaylistsDatabase(contextForThisActivity);
                listOfPlaylistsDatabase.removePlaylist(selectedPlaylistName);
                ListOfPlaylistsLoader.listOfPlaylists = new ArrayList<>();
                ListOfPlaylistsLoader.getPlaylistsList();       //Deletes playlist, then updates the playlist list
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        try
        {
            alertDialog.show();
        }
        catch(IllegalStateException e){}
    }

    void popup()    //Shows a popup dialog to take new listOfPlaylists name as input and create that listOfPlaylists, adding the current song into it
    {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final EditText et = new EditText(this);
        alertDialogBuilder.setView(et);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setTitle("Enter Playlist name");
        alertDialogBuilder.setCancelable(true).setPositiveButton("Enter", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                String temp = et.getText().toString();
                String divider[] = temp.split(" ");
                if(divider.length>1)
                {
                    Toast.makeText(ListOfPlaylistsActivity.this, "Don't use space and symbols such as , . / ? ( ) [ ] { } | etc", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ListOfPlaylistsLoader.listOfPlaylists.add(temp);
                    ListOfPlaylistsLoader.playlistDB.createNewPlaylist(temp);
                    EachPlaylistDatabase eachPlaylistDatabase = new EachPlaylistDatabase(contextForThisActivity, temp);
                    eachPlaylistDatabase.addToList(""+ PlaybackManagerClass.songIndex);
                    Toast.makeText(contextForThisActivity, "New Playlist : "+temp+" created", Toast.LENGTH_SHORT).show();
                    ListOfPlaylistsLoader.listOfPlaylists = new ArrayList<>();  //Creates new playlist, adds current playing song to it
                    ListOfPlaylistsLoader.getPlaylistsList();                   //Updates playlist list
                }

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        try
        {
            alertDialog.show();
        }
        catch(IllegalStateException e){}
    }
}
