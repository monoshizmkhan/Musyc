package com.inc.musyc.musyc.ActivitiesAndFragments.MusicStream;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cleveroad.audiowidget.AudioWidget;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.inc.musyc.musyc.JsontoJava.Mixtapesongs;
import com.inc.musyc.musyc.R;
import com.inc.musyc.musyc.Utils.*;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Formatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SyncPlaylistActivity extends AppCompatActivity {


    //exoplayer//////////////////////////////////////////
    private SimpleExoPlayer exoPlayer;
    private ExoPlayer.EventListener eventListener;
    private SeekBar seekPlayerProgress;
    private Handler handler;
    private ImageButton btnPlay;
    private TextView txtCurrentTime, txtEndTime;
    private boolean isPlaying = false;
    private AudioWidget audioWidget;
    private static final int OVERLAY_PERMISSION_REQ_CODE=5;
    private static final String TAG = "PlayListActivity";
    private String mSongulrs[];
    private int mTotalsong=1;
    private String mTitle,mMixtapeid,mImage;
    private ImageView mImageView;
    private ImageButton mLikebt;
    private Toolbar mtoolbar;
    private RecyclerView mMixtapesongsList;
    private DatabaseReference mMixtapesongsDatabase,mSyncDatabase;
    private int mCurrentsong;
    private boolean nextClicked=true;
    private int mStateended=0;
    private FirebaseRecyclerAdapter<Mixtapesongs,MixtapeSongsViewHolder> feedRecyclerViewAdapter;
    private ImageButton mNextbt,mPrevbt;
    private String mPartyhostid;
    private Long mTime;
    private Long estimatedServerTimeMs;
    private boolean onpage=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_playlist);

        //Init
        mImageView=(ImageView)findViewById(R.id.syncplaylist_imageview);
        mTitle=getIntent().getStringExtra("title");
        mImage=getIntent().getStringExtra("image");
        mMixtapeid=getIntent().getStringExtra("id");
        mPartyhostid=getIntent().getStringExtra("uid");
        mCurrentsong=0;;
        mMixtapesongsList = (RecyclerView)findViewById(R.id.syncplaylist_list);
        mMixtapesongsDatabase = FirebaseDatabase.getInstance().getReference().child("mixtapes").child(mPartyhostid).child(mMixtapeid).child("songs");
        mSyncDatabase = FirebaseDatabase.getInstance().getReference().child("party").child(mPartyhostid);
        mMixtapesongsDatabase.keepSynced(true);
        mSongulrs=new String[1111];
        estimatedServerTimeMs=(long)0;
        onpage=true;
        //UI build
        mMixtapesongsList.setHasFixedSize(true);
        mMixtapesongsList.setLayoutManager(new LinearLayoutManager(SyncPlaylistActivity.this));
        mtoolbar=(Toolbar)findViewById(R.id.syncplaylist_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("  "+mTitle);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);

        //Load Image////////////////////////////////////////
        loadImage();

        //initWidget
        initWidget();
        tracktime();
        mSyncDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int k=dataSnapshot.child("current").getValue(Integer.class);
                mTime=dataSnapshot.child("time").getValue(Long.class);
                changesongto(k);
                tracktime();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private boolean waitfor() throws InterruptedException {
        if(estimatedServerTimeMs==0)return true;
        long delta=5000-(System.currentTimeMillis()+estimatedServerTimeMs-mTime);
        if(delta<=0)return true;
        TimeUnit.MILLISECONDS.sleep(delta);
       // Toast.makeText(SyncPlaylistActivity.this,"waited "+delta,Toast.LENGTH_SHORT).show();
        return true;
    }
    private void tracktime()
    {
        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                double offset = snapshot.getValue(Double.class);
                long c = (long)offset;
                if(estimatedServerTimeMs>0)estimatedServerTimeMs=(c+estimatedServerTimeMs)/2;
                else estimatedServerTimeMs=c;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }
    private void changesongto(int position)
    {
        if(exoPlayer==null)return;
        MixtapeSongsViewHolder a =(MixtapeSongsViewHolder) mMixtapesongsList.findViewHolderForAdapterPosition(mCurrentsong);
        a.unsetColor();
        setPlayPause(false);
        exoPlayer.stop();
        mCurrentsong=position;
        nextPlayer(0);
    }
    private void loadImage()
    {
        //loads image from url with placeholder image and network persistence
        if(mImage!=null && mImage.length()>0 && !mImage.equals("default"))
        {
            Picasso.with(this).load(mImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.defaultmusic).into(mImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(SyncPlaylistActivity.this).load(mImage).placeholder(R.drawable.defaultmusic).into(mImageView);
                }
            });
        }
    }

    private void initWidget()
    {
        //Audio Widget permisson required /////////////////////////////
        audioWidget = new AudioWidget.Builder(getApplicationContext()).build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        }
        else
        {
            audioWidget.show(100,100);
        }

        //Audio Widget Cntroll////////////////////
        audioWidget.controller().onControlsClickListener(new AudioWidget.OnControlsClickListener() {
            @Override
            public boolean onPlaylistClicked() {
                // playlist icon clicked
                // return false to collapse widget, true to stay in expanded state
                Intent socialmain = new Intent(SyncPlaylistActivity.this, DumbActivity.class);
                startActivity(socialmain);
                return false;
            }

            @Override
            public void onPreviousClicked() {
                // previous track button clicked
                nextPlayerSync(-1);
            }

            @Override
            public boolean onPlayPauseClicked() {
                finish();

                // return true to change playback state of widget and play button click animation (in collapsed state)
                return false;
            }

            @Override
            public void onNextClicked() {
                // next track button clicked
                nextPlayerSync(1);
                tracktime();

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

    //data source init
    private void prepareExoPlayerFromURL(Uri uri){

        TrackSelector trackSelector = new DefaultTrackSelector();

        LoadControl loadControl = new DefaultLoadControl();


        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), null);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource audioSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
        initExoplayerController();
        exoPlayer.addListener(eventListener);

        exoPlayer.prepare(audioSource);
        initMediaControls();
    }

    //init exoplayer controller
    private void initExoplayerController()
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
                        setZero();
                        if(mStateended==1)
                        {
                            nextPlayerSync(1);
                            mStateended++;
                            if(mStateended>10)mStateended=2;
                        }

                        break;
                    case ExoPlayer.STATE_READY:

                        mStateended=1;
                        Log.i(TAG,"ExoPlayer ready! pos: "+exoPlayer.getCurrentPosition()
                                +" max: "+stringForTime((int)exoPlayer.getDuration()));

                        try {
                            if(onpage && waitfor())setProgress();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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

    //Media Button Controll//////////////////////////////////////////////////////////////////////////
    private void initMediaControls() {
        initPlayButton();
        initSeekBar();
        initTxtTime();
    }

    //init player button
    private void initPlayButton() {
       // btnPlay = (ImageButton) findViewById(R.id.syncplaylist_bt_play);
       // btnPlay.requestFocus();
//        btnPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setPlayPause(!isPlaying);
//            }
//        });
        //btnPlay.setEnabled(false);
       // btnPlay.setVisibility(View.GONE);
        mNextbt=(ImageButton)findViewById(R.id.syncplaylist_bt_next);
        mPrevbt=(ImageButton)findViewById(R.id.syncplaylist_bt_prev);
        mPrevbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPlayerSync(-1);
            }
        });
        mNextbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPlayerSync(1);
            }
        });
    }

    private void nextPlayerSync(int i)
    {
        mTotalsong=feedRecyclerViewAdapter.getItemCount();
        if(mTotalsong==0)return;
        tracktime();
        mSyncDatabase.child("current").setValue((mCurrentsong+i+mTotalsong)%mTotalsong);
        mSyncDatabase.child("time").setValue(ServerValue.TIMESTAMP);


    }
    //next song
    private void nextPlayer(int i)
    {
        if(exoPlayer==null)return ;
        mTotalsong=feedRecyclerViewAdapter.getItemCount();
        //Toast.makeText(SyncPlaylistActivity.this,"Hello Done",Toast.LENGTH_SHORT).show();
        MixtapeSongsViewHolder a =(MixtapeSongsViewHolder) mMixtapesongsList.findViewHolderForAdapterPosition(mCurrentsong);
        a.unsetColor();
        setPlayPause(false);
        exoPlayer.stop();
        setZero();
        mTotalsong=feedRecyclerViewAdapter.getItemCount();
        mCurrentsong=(mCurrentsong+i)%mTotalsong;
        a =(MixtapeSongsViewHolder) mMixtapesongsList.findViewHolderForAdapterPosition(mCurrentsong);
        a.setColor();
        prepareExoPlayerFromURL(Uri.parse(mSongulrs[mCurrentsong]));
        nextClicked=true;
    }

    //prev song
    private void prevPlayer()
    {
        if(exoPlayer==null)return ;
        MixtapeSongsViewHolder a =(MixtapeSongsViewHolder) mMixtapesongsList.findViewHolderForAdapterPosition(mCurrentsong);
        a.unsetColor();
        setPlayPause(false);
        exoPlayer.stop();
        setZero();
        mTotalsong=feedRecyclerViewAdapter.getItemCount();
        mCurrentsong=(mCurrentsong-1+mTotalsong)%mTotalsong;
        a =(MixtapeSongsViewHolder) mMixtapesongsList.findViewHolderForAdapterPosition(mCurrentsong);
        a.setColor();
        prepareExoPlayerFromURL(Uri.parse(mSongulrs[mCurrentsong]));
        nextClicked=true;
    }

    //play pause action
    private void setPlayPause(boolean play){
        if(exoPlayer==null)return;
        isPlaying = play;

        exoPlayer.setPlayWhenReady(play);
        if(!isPlaying){
            //Toast.makeText(SyncPlaylistActivity.this,"Hq",Toast.LENGTH_LONG).show();
            audioWidget.controller().pause();
            //btnPlay.setImageResource(android.R.drawable.ic_media_play);
        }else{
            setProgress();
            audioWidget.controller().start();
            //btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        }
    }
    private void setMute(boolean play)
    {
        if(exoPlayer==null)return;
        isPlaying = play;
        if(!isPlaying){
            audioWidget.controller().pause();
            btnPlay.setImageResource(android.R.drawable.ic_media_play);
        }else{

            audioWidget.controller().start();
            btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    //Music Player SeekBar//////////////////////////////////////////////////////////////////////////

    //init textview
    private void initTxtTime() {
        txtCurrentTime = (TextView) findViewById(R.id.syncplaylist_timecurrent);
        txtEndTime = (TextView) findViewById(R.id.syncplaylist_endtime);
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

    //set seekbar progress
    private void setProgress() {
        if(exoPlayer==null)return ;
        if(!isPlaying)
        {
            if(nextClicked)
            {
                setPlayPause(true);
                nextClicked=false;
            }
            return;
        }
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

    //set time to zero
    public void setZero()
    {
        txtCurrentTime.setText("00:00");
        txtEndTime.setText("00:00");
    }

    //init seekbar
    private void initSeekBar() {
        seekPlayerProgress = (SeekBar) findViewById(R.id.syncplaylist_mp);
        seekPlayerProgress.requestFocus();
        seekPlayerProgress.setEnabled(false);

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


    //Android Lifecycle/////////////////////////////////////////////////////////////////////////
    @Override
    protected void onResume() {
        super.onResume();
        onpage=true;
        if(audioWidget==null)return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        }
        else
        {
            audioWidget.show(100,100);
        }

    }

    @Override
    protected void onStop() {
        if(exoPlayer!=null)exoPlayer.stop();
        onpage=false;
        super.onStop();
    }

    @Override
    protected void onStart() {
        onpage=true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        }
        else
        {
            audioWidget.show(100,100);
        }
        feedRecyclerViewAdapter = new FirebaseRecyclerAdapter<Mixtapesongs,MixtapeSongsViewHolder>(

                Mixtapesongs.class,
                R.layout.mixtapesongs_layout,
                MixtapeSongsViewHolder.class,
                mMixtapesongsDatabase


        ) {
            @Override
            protected void populateViewHolder(final MixtapeSongsViewHolder viewHolder, final Mixtapesongs model, final int position) {
                viewHolder.setTitle((position+1)+". "+model.getTitle());

                mSongulrs[position]=model.getMusic();
                if(position==mCurrentsong)
                {
                    viewHolder.setColor();
                    prepareExoPlayerFromURL(Uri.parse(mSongulrs[mCurrentsong]));
                }
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       /* MixtapeSongsViewHolder a =(MixtapeSongsViewHolder) mMixtapesongsList.findViewHolderForAdapterPosition(mCurrentsong);
                        a.unsetColor();
                        setPlayPause(false);
                        exoPlayer.stop();
                        setZero();
                        mCurrentsong=position;
                        viewHolder.setColor();

                        //prepareExoPlayerFromURL(Uri.parse(model.getMusic()));
                        nextClicked=true;*/
                        try
                        {
                            mTotalsong=feedRecyclerViewAdapter.getItemCount();
                            nextPlayerSync((position-mCurrentsong));
                        }catch (Exception e)
                        {

                        }
                    }
                });

            }


        };
        mTotalsong=feedRecyclerViewAdapter.getItemCount();
       // Toast.makeText(SyncPlaylistActivity.this,""+mTotalsong,Toast.LENGTH_SHORT).show();
        mMixtapesongsList.setAdapter(feedRecyclerViewAdapter);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if(exoPlayer!=null)exoPlayer.stop();
        onpage=false;
        if(audioWidget!=null)audioWidget.hide();
        super.onDestroy();
    }


    //Android Menu Activity///////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.mixtapes_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.mixtapesedit_addfrommobile)
        {
            Intent AddSongOffLine=new Intent(SyncPlaylistActivity.this,AddSongToMixtapeActivity.class);
            AddSongOffLine.putExtra("mMixtapeid",mMixtapeid);
            startActivity(AddSongOffLine);
        }
        return true;
    }

    //Android on return result//////////////////////////////////////////////////////////////////////////
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
