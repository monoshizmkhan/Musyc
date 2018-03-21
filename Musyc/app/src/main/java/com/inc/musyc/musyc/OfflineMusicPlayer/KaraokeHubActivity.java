package com.inc.musyc.musyc.OfflineMusicPlayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.inc.musyc.musyc.R;

import java.text.SimpleDateFormat;
import java.util.Date;

//Used to play/pause and stop video

public class KaraokeHubActivity extends AppCompatActivity
{
    ImageButton backButton, startRecordingButton, stopRecordingButton;
    static VideoView karaokeVideoView;
    boolean hasVideoStarted=false, isVideoPaused=false;
    static String videoPath;
    static String videoName;
    static TextView videoNamePreview;
    ImageView videoPreview;
    String recordingName;
    static Context contextForThisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karaoke_hub);
        contextForThisActivity=getApplicationContext();
        backButton = (ImageButton) findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                karaokeVideoView.stopPlayback();
                finish();
            }
        });     //goes back to the previous activity

        karaokeVideoView = (VideoView) findViewById(R.id.video);
        startRecordingButton = (ImageButton) findViewById(R.id.recordButton);
        stopRecordingButton = (ImageButton) findViewById(R.id.stopRecordingButton);
        videoNamePreview = (TextView) findViewById(R.id.videoName);
        videoPreview = (ImageView) findViewById(R.id.videoPreview);

        videoNamePreview.setText(videoName);        //sets video title under the video
        videoPreview.setVisibility(View.VISIBLE);   //set video thumbnail visible
        videoPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecordingButton.performClick();//starts recording if user taps on video
            }
        });

        startRecordingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    if(!hasVideoStarted)
                    {
                        Toast.makeText(KaraokeHubActivity.this,"asd",Toast.LENGTH_SHORT).show();
                        videoPreview.setVisibility(View.INVISIBLE);     //sets thumbnail invisible and starts video
                        VideoPlaybackManagerClass.playVideo(videoPath); //starts video using the video path provided by videoPlayer class
                        hasVideoStarted=true;                           //notifies the start of the video
                        recordingName = videoName +" "+ new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                        //Appends video name and current date-time to use as the name for
                        //the recording being saved.
                        VoiceRecorderManagerClass.startRecording(recordingName);
                        //Starts recording audio.
                    }
                    else
                    {
                        stopRecordingButton.performClick();             //Stops recording video if the video is tapped on mid-recording
                    }
                }
                catch(Exception e){
                    Toast.makeText(KaraokeHubActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });

        stopRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hasVideoStarted)
                {
                    karaokeVideoView.seekTo(0);       //stops video
                    karaokeVideoView.stopPlayback();
                    hasVideoStarted=false;
                    startRecordingButton.setImageResource(R.drawable.record);
                    videoPreview.setVisibility(View.VISIBLE);
                    VoiceRecorderManagerClass.stopRecording();  //Prepares for recording again
//                    PlaybackManagerClass.playRecordedSong(VoiceRecorderManagerClass.provideRecordingPath());
                                                                //Plays the recorded audio
                    Toast.makeText(KaraokeHubActivity.this, "Recording saved as "+recordingName, Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(KaraokeHubActivity.this, "Please start recording first", Toast.LENGTH_SHORT).show();
            }
        });

        karaokeVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopRecordingButton.performClick();             //Stops recording and saves it when video is complete
            }
        });
    }

    @Override
    protected void onDestroy() {
            super.onDestroy();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if(hasVideoStarted)
        {
            stopRecordingButton.performClick();
        }
        else super.onBackPressed();

    }

}
