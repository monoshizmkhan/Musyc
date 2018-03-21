package com.inc.musyc.musyc.ActivitiesAndFragments.MusicStream;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.inc.musyc.musyc.OfflineMusicPlayer.PlaybackManagerClass;
import com.inc.musyc.musyc.OfflineMusicPlayer.VideosLoaderActivity;
import com.inc.musyc.musyc.R;

public class SyncKariokyMainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private LinearLayout mSyncbt;
    private LinearLayout mKr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_karioky_main);

        mToolbar=(Toolbar)findViewById(R.id.SKM_toolbar);
        mSyncbt=(LinearLayout)findViewById(R.id.SKM_LL1);
        mKr=(LinearLayout)findViewById(R.id.SKM_LL2);
        initUI();
    }

    private void initUI()
    {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("  MUSYC APPS");
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);

        mSyncbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SyncMain = new Intent(SyncKariokyMainActivity.this, SyncMainActivity.class);
                startActivity(SyncMain);
            }
        });

        mKr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //NotificationManager.destroyNotification();     //Removes current notification
                PlaybackManagerClass.pauseSong();                    //Pauses audio playback
                Intent intent = new Intent(SyncKariokyMainActivity.this, VideosLoaderActivity.class);
                startActivityIfNeeded(intent, 0);
            }
        });
    }
}
