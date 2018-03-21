package com.inc.musyc.musyc.OfflineMusicPlayer;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.Toast;

import com.inc.musyc.musyc.R;

import java.io.IOException;
import java.util.Random;

//Controls audio plaback

public class PlaybackManagerClass extends Activity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener
{
    static MediaPlayer mediaPlayer = new MediaPlayer(); //Initializes media player
    static int songIndex;
    static boolean isShuffleOn =false, isRepeatOn =false, isPlaying=false, hasPlaybackStarted =false, isLooping=false;
    static boolean hasLoopBegun =false, hasLoopEnded =false;
    static int loopBeginPoint=0, loopEndPoint=0;
    static Handler seekbarThreadMaintainer, loopThreadMaintainer;
    static Utilities converterForSeekbar;
    static boolean isPlaylist=false;
    static String songTitle="";
    static String artistsTitle="";
    static EachSongFormat currentEachSongFormat;
    static boolean hasStarted=false;


    public static void playRecordedSong(String songPath)
    {
        mediaPlayer.reset();
        try
        {
            mediaPlayer.setDataSource(songPath);
            mediaPlayer.prepare();
        }
        catch(IOException e){}
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();
            }
        });
    }

    public static void playSong(int songIndex)      //Passes song index from song list in listOfPlaylists
    {
        hasStarted=true;
        ImageLoaderClass imageLoaderClass = new ImageLoaderClass();
        if(!isPlaylist) currentEachSongFormat =SongLoader.songsList.get(songIndex); //If user is playing songs from a playlist
                                                                                    //then loads from that playlist instead
                                                                                    //of loading from all songs' storage
        else
        {
            songIndex=Integer.parseInt(EachPlaylistActivity.songIndexes.get(EachPlaylistActivity.songSelected));
            currentEachSongFormat =SongLoader.songsList.get(songIndex);
        }
        try
        {
            mediaPlayer.reset();    //Resets media player
            mediaPlayer.setDataSource(currentEachSongFormat.getPath());   //Sets data source to the current song's path, using the song index
            mediaPlayer.prepare();  //Prepares media player for playback
            mediaPlayer.start();    //Starts playback
            songTitle = currentEachSongFormat.getTitle();     //Updates song name
            artistsTitle = currentEachSongFormat.getArtist(); //Updates artist name

            PlayerHubActivity.lastSongDB.updateLastSong(songIndex);
            if(!currentEachSongFormat.getArtist().contains("unknown"))
                imageLoaderClass.loadAndPlaceImage(currentEachSongFormat.getAlbumID(), PlayerHubActivity.albumCoverImage);  //Updates the album
                                                                                                                          // albumCoverImage

            PlaylistActivity.songsTitle.setText(songTitle);         //Updates song title and artist name in other activities
            PlaylistActivity.artistTitle.setText(artistsTitle);
            PlayerHubActivity.songName.setText(songTitle);
            PlayerHubActivity.artistName.setText(artistsTitle);
            if(EachPlaylistActivity.songName!=null) EachPlaylistActivity.songName.setText(songTitle);
            if(EachPlaylistActivity.artistName!=null) EachPlaylistActivity.artistName.setText(artistsTitle);
            if(ListOfPlaylistsActivity.songName!=null) ListOfPlaylistsActivity.songName.setText(songTitle);
            if(ListOfPlaylistsActivity.artistName!=null) ListOfPlaylistsActivity.artistName.setText(artistsTitle);

            PlaylistActivity.playButton.setImageResource(R.drawable.pause);   //Updates the buttons on the activities to show play/pause button state
            PlayerHubActivity.playButton.setImageResource(R.drawable.pause);
            if(EachPlaylistActivity.playButton!=null) EachPlaylistActivity.playButton.setImageResource(R.drawable.pause);
            if(ListOfPlaylistsActivity.playButton!=null) ListOfPlaylistsActivity.playButton.setImageResource(R.drawable.pause);

            isPlaying=true;

            PlayerHubActivity.seekBar.setProgress(0); //Readies the seekbar
            PlayerHubActivity.seekBar.setMax(100);

            hasPlaybackStarted =true;

            Toast.makeText(PlayerHubActivity.contextOfThisActivity, "Playing "+ songTitle, Toast.LENGTH_SHORT).show();
            Toast.makeText(PlaylistActivity.contextOfThisActivity, "Playing "+ songTitle, Toast.LENGTH_SHORT).show();

            if(!hasPlaybackStarted) NotificationManager.startNotification(songTitle);   //Sends notification manager the current song name
            else                                                                        //Notification manager then updates/sends the notification
                NotificationManager.updateNotification(songTitle);
            NotificationManager.songState="Now Playing";
            updateProgressBar();        //Update seekbar
            if(isLooping)looping();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }





    @Override
    public void onCompletion(MediaPlayer mediaPlayer)
    {
        mediaPlayer.setOnCompletionListener(this);
        if(isRepeatOn)
        {
            playSong(songIndex);    //If repeatButton is turned on, plays the same song again.
        }
        else if(isShuffleOn)
        {
            Random rand = new Random();
            songIndex = rand.nextInt((SongLoader.songsList.size() - 1));    //If shuffleButton is turned on, plays a random song next, within the limit
            playSong(songIndex);
        }
        else if(!isRepeatOn && !isShuffleOn)
        {
            if(songIndex<=(SongLoader.songsList.size()-1))
            {
                songIndex++;
                playSong(songIndex);    //Else, if it's not the last song, then plays the next song.
            }
            else
            {
                songIndex=0;
                playSong(songIndex);    //If it is the last song, plays the first song and starts over.
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b)
    {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        seeking();      //If the seekbar is touched, it seeks to that selectedSongPosition
    }

    public static void updateProgressBar()
    {
        seekbarThreadMaintainer = new Handler();
        converterForSeekbar = new Utilities();
        if(mUpdateTimeTask!=null) seekbarThreadMaintainer.postDelayed(mUpdateTimeTask, 100);    //Runs the thread every second, updating the seekbar
    }
    public static void loopingProcess()
    {
        loopThreadMaintainer = new Handler();
        loopThreadMaintainer.removeCallbacks(loopingThread);
        if(isLooping)
        {
            mediaPlayer.seekTo(converterForSeekbar.secondsToMiliseconds(loopBeginPoint));   //Seeks to loop beginning point
            loopThreadMaintainer.postDelayed(loopingThread, 100);   //Runs the looping thread every second if looping is on
        }
    }

    private static Runnable mUpdateTimeTask = new Runnable()
    {
        public void run()
        {
            long totalDuration = mediaPlayer.getDuration();             //Gets song total duration
            long currentDuration = mediaPlayer.getCurrentPosition();    //Gets current selectedSongPosition on song
            PlayerHubActivity.totalSongDuration.setText(""+ converterForSeekbar.milliSecondsToTimer(totalDuration));//Updates the textview showing the total duration and
            PlayerHubActivity.currentSongProgress.setText(""+ converterForSeekbar.milliSecondsToTimer(currentDuration));//current positions of the song.
            int progress = (int)(converterForSeekbar.getProgressPercentage(currentDuration, totalDuration));//Calculates the current progress of the song
            PlayerHubActivity.seekBar.setProgress(progress);  //Sets the seekbar progress percentage accordingly
            if(currentDuration>=totalDuration)nextSong();   //If the song doesn't move on the next song after completion by itself, this calls
                                                            //to start next song
            seekbarThreadMaintainer.postDelayed(this, 50); //Update seekbar after a second each time
        }
    };

    static Runnable loopingThread = new Runnable() {
        @Override
        public void run(){
            if(converterForSeekbar.milliSecondsToSeconds(mediaPlayer.getCurrentPosition())>=loopEndPoint && isLooping)
            {
                mediaPlayer.seekTo(converterForSeekbar.secondsToMiliseconds(loopBeginPoint));   //If looping is turned on, then
            }                                                                                   //Seeks to loop begin point every time
            loopThreadMaintainer.postDelayed(this, 100);                                        //the seek reaches loop end point
        }
    };

    public static void nextSong()
    {
        if(isRepeatOn)
        {
            playSong(songIndex);                //If repeat is on, plays the same song again
        }
        else if(isPlaylist)                     //If user is listening to a playlist, picks next song from that playlist
        {
            if(isShuffleOn)
            {
                Random rand = new Random();
                EachPlaylistActivity.songSelected=rand.nextInt((EachPlaylistActivity.songIndexes.size()-1));
                songIndex=EachPlaylistActivity.songSelected;
                playSong(songIndex);
            }
            else
            {
                if (EachPlaylistActivity.songSelected < EachPlaylistActivity.songIndexes.size() - 1)
                    EachPlaylistActivity.songSelected++;
                else
                    EachPlaylistActivity.songSelected = 0;
                songIndex = Integer.parseInt(EachPlaylistActivity.songIndexes.get(EachPlaylistActivity.songSelected));
                currentEachSongFormat = SongLoader.songsList.get(songIndex);
                playSong(songIndex);
            }
        }
        else if(isShuffleOn)
        {
            Random rand = new Random();
            songIndex = rand.nextInt((SongLoader.songsList.size() - 1));    //If shuffleButton is turned on, picks a random next song
            playSong(songIndex);
        }
        else if(songIndex<(SongLoader.songsList.size() - 1))     //Else, if it's not the last song, then plays the next song
        {
            songIndex++;
            playSong(songIndex);
        }
        else
        {
            songIndex=0;
            playSong(songIndex);        //Else, if it is the last song, then plays the first song
        }
    }

    public static void previousSong()
    {
        if(isPlaylist)      //Picks previous song in playlist
        {
            if(EachPlaylistActivity.songSelected>0) EachPlaylistActivity.songSelected--;
            else
                EachPlaylistActivity.songSelected= EachPlaylistActivity.songIndexes.size()-1;
            songIndex=Integer.parseInt(EachPlaylistActivity.songIndexes.get(EachPlaylistActivity.songSelected));
            currentEachSongFormat =SongLoader.songsList.get(songIndex);
            playSong(songIndex);
        }
        else if(isShuffleOn)
        {
            songIndex= PlayerHubActivity.previousSongInShuffle;  //If shuffleButton was on, then plays the previous song that was played
            playSong(songIndex);
        }
        else if(songIndex > 0)    //If it is not the first song in the listOfPlaylists, then plays the song in it's previous index
        {
            songIndex--;
            playSong(songIndex);
        }
        else                        //If it is the first song in the listOfPlaylists, then plays the last song in the listOfPlaylists
        {
            songIndex=SongLoader.songsList.size()-1;
            playSong(songIndex);
        }
    }

    public static void pauseSong()
    {
        mediaPlayer.pause();    //Pauses song and updates the notification and pause buttons
        isPlaying=false;
        NotificationManager.songState="Now Paused";
        if(NotificationManager.hasIssuedNotificationOnce)
        {
            NotificationManager.updateNotification(SongLoader.songsList.get(songIndex).getTitle());    //Updates the notification
            PlaylistActivity.playButton.setImageResource(R.drawable.play);      //Updates play/pause button icons
            if(EachPlaylistActivity.playButton!=null) EachPlaylistActivity.playButton.setImageResource(R.drawable.play);
            if(ListOfPlaylistsActivity.playButton!=null) ListOfPlaylistsActivity.playButton.setImageResource(R.drawable.play);
        }

    }

    public static void stopSong()
    {
        isPlaying=false;
        mediaPlayer.stop(); //Stops the song
        mediaPlayer.seekTo(0);
        PlayerHubActivity.currentSongProgress.setText("0:00");
        PlayerHubActivity.seekBar.setProgress(0);
        hasStarted=false;
    }

    public static void seeking()    //Maintains the thread for the seekbar operations
    {
        if(!hasStarted)
        {
            playSong(songIndex);
            pauseSong();
        }
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = converterForSeekbar.progressToTimer(PlayerHubActivity.seekBar.getProgress(), totalDuration);
        mediaPlayer.seekTo(currentPosition);    //Converts current selectedSongPosition and total duration ratio to miliseconds and seeks to that selectedSongPosition
        if(!mediaPlayer.isPlaying()) mediaPlayer.start(); //If song was paused, starts playing after seeking
        PlayerHubActivity.playButton.setImageResource(R.drawable.pause);       //Updates button icons
        PlaylistActivity.playButton.setImageResource(R.drawable.pause);
    }

    public static void looping()
    {
        if(!hasLoopBegun && !hasLoopEnded && !isLooping)    //If looping was off and no start time was picked,
        {                                                   //then selects current selectedSongPosition as loopButton beginning time
            loopBeginPoint = converterForSeekbar.milliSecondsToSeconds(mediaPlayer.getCurrentPosition());
            hasLoopBegun =true;
            PlayerHubActivity.loopButton.setImageResource(R.drawable.loop_clicked_1);
            Toast.makeText(PlayerHubActivity.contextOfThisActivity, "Loop start point selected", Toast.LENGTH_SHORT);
        }
        else if(hasLoopBegun && !hasLoopEnded)              //If loopButton beginning time was selected, selects current selectedSongPosition as loopButton end time
        {
            loopEndPoint = converterForSeekbar.milliSecondsToSeconds(mediaPlayer.getCurrentPosition());
            hasLoopEnded =true;
            isLooping=true;
            loopingProcess();
            PlayerHubActivity.loopButton.setImageResource(R.drawable.loop_clicked_2);
            Toast.makeText(PlayerHubActivity.contextOfThisActivity, "Loop end point selected", Toast.LENGTH_SHORT);
            Toast.makeText(PlayerHubActivity.contextOfThisActivity, "Now Looping", Toast.LENGTH_SHORT);
        }
        else    //Turns off looping
        {
            hasLoopBegun =false;
            hasLoopEnded =false;
            isLooping=false;
            PlayerHubActivity.loopButton.setImageResource(R.drawable.loop);
            //Toast.makeText(PlayerHubActivity.contextOfThisActivity, "Looping off", Toast.LENGTH_SHORT);
        }
    }

}
