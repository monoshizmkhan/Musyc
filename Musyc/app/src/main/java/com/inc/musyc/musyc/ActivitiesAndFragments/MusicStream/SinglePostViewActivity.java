package com.inc.musyc.musyc.ActivitiesAndFragments.MusicStream;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.cleveroad.audiowidget.AudioWidget;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inc.musyc.musyc.Global.Infostatic;
import com.inc.musyc.musyc.R;
import com.inc.musyc.musyc.Utils.DumbActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.lang.Integer.parseInt;


public class SinglePostViewActivity extends AppCompatActivity {


    private SimpleExoPlayer exoPlayer;
    private ExoPlayer.EventListener eventListener;
    private SeekBar seekPlayerProgress;
    private Handler handler;
    private ImageButton btnPlay;
    private TextView txtCurrentTime, txtEndTime;
    private boolean isPlaying = false;
    private AudioWidget audioWidget;
    private static final int OVERLAY_PERMISSION_REQ_CODE=5;
    private static final String TAG = "Single Postview";
    private String StreamURL ="";
    private DatabaseReference mData;
    private String mTitle,mArtist,mLyrics,mImage;
    private ImageView mImageView;
    private TextView mTitleView,mArtistView,mLyricsView,mLikesview,mNameview;
    private ImageButton mLikebt;
    private int mLikes;
    private boolean isLiked=false;
    private String id,postid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_single_post_view);
        mLyricsView=(TextView)findViewById(R.id.singlepostview_lyricsview);
        mLyricsView=(TextView)findViewById(R.id.singlepostview_lyricsview);
        mLyricsView.setMovementMethod(new ScrollingMovementMethod());
        mTitleView=(TextView)findViewById(R.id.singlepostview_title);
        mArtistView=(TextView)findViewById(R.id.singlepostview_artist);
        mImageView=(ImageView)findViewById(R.id.singlepostview_imageview);
        mLikebt=(ImageButton)findViewById(R.id.singlepostview_bt_like);

        //init data////////////////////////////////////////////////////
        mTitle=getIntent().getStringExtra("title");
        mArtist=getIntent().getStringExtra("artist");
        mLyrics=getIntent().getStringExtra("post");
        mImage=getIntent().getStringExtra("image");
        StreamURL =getIntent().getStringExtra("music");
        id=getIntent().getStringExtra("id");
        postid=getIntent().getStringExtra("postid");
        mLikesview=(TextView)findViewById(R.id.singlepostview_likes);
        mLikes=0;
        FirebaseDatabase.getInstance().getReference().child("posts").child(id).child(postid).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())return;
                if(dataSnapshot.child(Infostatic.uid).exists()) {
                    isLiked = true;
                    mLikebt.setImageResource(R.drawable.ic_action_name2);
                    //Toast.makeText(SinglePostViewActivity.this,"YO",Toast.LENGTH_SHORT).show();
                }
                mLikes=(int)dataSnapshot.getChildrenCount();
                mLikesview.setText(mLikes+" Likes");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Init UI////////////////////////////////////////////////////////////////

        //init like bt
        initLikebt();
        //set texts
        mTitleView.setText("Title: "+mTitle);
        mArtistView.setText("Artis: "+mArtist);
        mLikesview.setText(mLikes+" Likes");
        mLyricsView.setText(mLyrics);
        //loads image
        loadImage();
        //Widget Controll
        widgetInit();
        //init exoplayer and loads songs
        prepareExoPlayerFromURL(Uri.parse(StreamURL));
    }

    private void initLikebt()
    {
        mLikebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SinglePostViewActivity.this,"Like",Toast.LENGTH_SHORT).show();
                if(isLiked==false)
                {
                    HashMap<String, String> notificationData = new HashMap<>();
                    Long time=System.currentTimeMillis();
                    String tt=(new java.util.Date(time)).toString();
                    notificationData.put("fromid", Infostatic.uid);
                    notificationData.put("title", "New Likes!");
                    notificationData.put("body", Infostatic.name+" Likes your post!\n "+mTitle+".");
                    notificationData.put("type", "likes");
                    notificationData.put("postid", postid);
                    notificationData.put("time", tt);
                    Map friendMap = new HashMap();

                    HashMap<String, String> likedsong = new HashMap<>();
                    likedsong.put("title", mTitle);
                    likedsong.put("music", StreamURL);
                    likedsong.put("cnt", "0");

                    DatabaseReference newNotificationref = FirebaseDatabase.getInstance().getReference().child("notifications").child(id).push();
                    String newNotificationId = newNotificationref.getKey();
                    friendMap.put("posts/" + id + "/" +postid+"/likes"+"/"+ Infostatic.uid, "true");
                    friendMap.put("notifications/" + id + "/" + newNotificationId, notificationData);
                    friendMap.put("mixtapes/" + Infostatic.uid + "/liked_songs/songs/" + postid, likedsong);

                    FirebaseDatabase.getInstance().getReference().updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if(databaseError == null){
                                isLiked=true;
                                mLikes++;
                                mLikesview.setText(mLikes+" Likes");
                                mLikebt.setImageResource(R.drawable.ic_action_name2);

                            } else {
                                String error = databaseError.getMessage();
                                Toast.makeText(SinglePostViewActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else
                {
                    Toast.makeText(SinglePostViewActivity.this,"Like",Toast.LENGTH_SHORT).show();
                    Map friendMap = new HashMap();
                    friendMap.put("posts/" + id + "/" + postid + "/likes" + "/" + Infostatic.uid, null);
                    friendMap.put("mixtapes/" + Infostatic.uid + "/liked_songs/songs/" + postid, null);
                    FirebaseDatabase.getInstance().getReference().updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if (databaseError == null) {
                                isLiked = false;
                                mLikes--;
                                mLikesview.setText(mLikes + " Likes");
                                mLikebt.setImageResource(R.drawable.ic_action_name);

                            } else {
                                String error = databaseError.getMessage();
                                Toast.makeText(SinglePostViewActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });
    }
    private void loadImage()
    {
        if(mImage!=null && mImage.length()>0 && !mImage.equals("default"))
        {
            Picasso.with(this).load(mImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_avatar).into(mImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(SinglePostViewActivity.this).load(mImage).placeholder(R.drawable.default_avatar).into(mImageView);
                }
            });
        }
    }
    private void widgetInit()
    {
        audioWidget = new AudioWidget.Builder(getApplicationContext()).build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        }
        else
        {
            audioWidget.show(100,100);
        }
        audioWidget.controller().onControlsClickListener(new AudioWidget.OnControlsClickListener() {
            @Override
            public boolean onPlaylistClicked() {
                // playlist icon clicked
                // return false to collapse widget, true to stay in expanded state
                Intent socialmain = new Intent(SinglePostViewActivity.this, DumbActivity.class);
                startActivity(socialmain);
                return false;
            }

            @Override
            public void onPreviousClicked() {
                // previous track button clicked
                //nextPlayer();
            }

            @Override
            public boolean onPlayPauseClicked() {
                setPlayPause(!isPlaying);
                // return true to change playback state of widget and play button click animation (in collapsed state)
                return true;
            }

            @Override
            public void onNextClicked() {
                // next track button clicked
                //nextPlayer();

            }

            @Override
            public void onAlbumClicked() {
                // album cover clicked
            }

            @Override
            public void onPlaylistLongClicked() {
                // playlist button long clicked
            }

            @Override
            public void onPreviousLongClicked() {
                // previous track button long clicked
            }

            @Override
            public void onPlayPauseLongClicked() {
                // play/pause button long clicked
            }

            @Override
            public void onNextLongClicked() {
                // next track button long clicked
            }

            @Override
            public void onAlbumLongClicked() {
                // album cover long clicked
            }
        });
    }

    //Stream data/////////////////////////
    private void prepareExoPlayerFromURL(Uri uri){

        TrackSelector trackSelector = new DefaultTrackSelector();

        LoadControl loadControl = new DefaultLoadControl();

        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), null);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource audioSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);

        exoplayerControllerInit();
        exoPlayer.addListener(eventListener);

        exoPlayer.prepare(audioSource);
        initMediaControls();
    }

    private void exoplayerControllerInit()
    {
        eventListener = new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
                Log.i(TAG,"onTimelineChanged");
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.i(TAG,"onTracksChanged");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.i(TAG,"onLoadingChanged");
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.i(TAG,"onPlayerStateChanged: playWhenReady = "+String.valueOf(playWhenReady)
                        +" playbackState = "+playbackState);
                switch (playbackState){
                    case ExoPlayer.STATE_ENDED:
                        Log.i(TAG,"Playback ended!");
                        //Stop playback and return to start position
                        setPlayPause(false);
                        exoPlayer.seekTo(0);
                        break;
                    case ExoPlayer.STATE_READY:
                        Log.i(TAG,"ExoPlayer ready! pos: "+exoPlayer.getCurrentPosition()
                                +" max: "+stringForTime((int)exoPlayer.getDuration()));

                        setProgress();
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        Log.i(TAG,"Playback buffering!");
                        break;
                    case ExoPlayer.STATE_IDLE:
                        Log.i(TAG,"ExoPlayer idle!");
                        break;
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.i(TAG,"onPlaybackError: "+error.getMessage());
            }

            @Override
            public void onPositionDiscontinuity() {
                Log.i(TAG,"onPositionDiscontinuity");
            }
        };

    }
    //Media Controll init
    private void initMediaControls() {
        initPlayButton();
        initSeekBar();
        initTxtTime();
    }

    private void initPlayButton() {
        btnPlay = (ImageButton) findViewById(R.id.singlepostview_bt_play);
        btnPlay.requestFocus();
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 setPlayPause(!isPlaying);
            }
        });
    }


    private void nextPlayer()
    {
        if(isPlaying) setPlayPause(!isPlaying);
        exoPlayer.stop();
        prepareExoPlayerFromURL(Uri.parse(StreamURL));
    }

    private void setPlayPause(boolean play){

       isPlaying = play;

        exoPlayer.setPlayWhenReady(play);
        if(!isPlaying){
           // Toast.makeText(SinglePostViewActivity.this,"Hq",Toast.LENGTH_LONG).show();
            audioWidget.controller().pause();
            btnPlay.setImageResource(android.R.drawable.ic_media_play);
        }else{
            setProgress();
            audioWidget.controller().start();
            btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    //Seekbar init/////////////////////////////////////////////////
    private void initTxtTime() {
        txtCurrentTime = (TextView) findViewById(R.id.singlepostview_timecurrent);
        txtEndTime = (TextView) findViewById(R.id.singlepostview_endtime);
    }

    private String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds =  timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void setProgress() {
        if(!isPlaying)return;
        seekPlayerProgress.setProgress(0);
        seekPlayerProgress.setMax((int) exoPlayer.getDuration()/1000);
        txtCurrentTime.setText(stringForTime((int)exoPlayer.getCurrentPosition()));
        txtEndTime.setText(stringForTime((int)exoPlayer.getDuration()));

        if(handler == null)handler = new Handler();
        //Make sure you update Seekbar on UI thread
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (exoPlayer != null && isPlaying) {
                    seekPlayerProgress.setMax((int) exoPlayer.getDuration()/1000);
                    int mCurrentPosition = (int) exoPlayer.getCurrentPosition() / 1000;
                    seekPlayerProgress.setProgress(mCurrentPosition);
                    txtCurrentTime.setText(stringForTime((int)exoPlayer.getCurrentPosition()));
                    txtEndTime.setText(stringForTime((int)exoPlayer.getDuration()));

                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void initSeekBar() {
        seekPlayerProgress = (SeekBar) findViewById(R.id.singlepostview_mp);
        seekPlayerProgress.requestFocus();

        seekPlayerProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    // We're not interested in programmatically generated changes to
                    // the progress bar's position.
                    return;
                }

                exoPlayer.seekTo(progress*1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekPlayerProgress.setMax(0);
        seekPlayerProgress.setMax((int) exoPlayer.getDuration()/1000);

    }

    //Android lifecycle/////////////////
    @Override
    protected void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        }
        else
        {
            audioWidget.show(100,100);
        }

        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        }
        else
        {
            audioWidget.show(100,100);
        }
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if(exoPlayer!=null)exoPlayer.stop();
        audioWidget.hide();
        super.onDestroy();
    }

    //Return values
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                // now you can show audio widget
                audioWidget.show(100,100);
            }
        }
    }
}
