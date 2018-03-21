package com.inc.musyc.musyc.ActivitiesAndFragments.MusicStream;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.inc.musyc.musyc.Global.Infostatic;
import com.inc.musyc.musyc.R;

import java.util.HashMap;
import java.util.Map;

/*
    Adds song to mixtape with a title and music URL
 */

public class AddSongToMixtapeActivity extends AppCompatActivity {


    //private var
    private Toolbar mToolbar;
    private TextInputLayout mTitle;
    private Button mPostbt;
    private FloatingActionButton mMusicbt;
    private static final int MUSIC_PICK=122;
    private Uri mMusicUri;
    private ProgressBar mProgress;
    private StorageReference mUploadMusic;
    private String mMusicUrl,mMixtapeid;
    private DatabaseReference mRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addsongtomixtape);

        //init var
        mToolbar =(Toolbar)findViewById(R.id.addsongtomixtape_toolbar);
        mProgress=(ProgressBar)findViewById(R.id.addsongtomixtape_progress);
        mProgress.setVisibility(View.GONE);
        mTitle=(TextInputLayout)findViewById(R.id.addsongtomixtape_title);
        mPostbt=(Button)findViewById(R.id.addsongtomixtape_post);
        mMusicbt=(FloatingActionButton) findViewById(R.id.addsongtomixtape_music);
        mUploadMusic= FirebaseStorage.getInstance().getReference();
        mRecord= FirebaseDatabase.getInstance().getReference();
        mMixtapeid=getIntent().getStringExtra("mMixtapeid");

        //init UI
        initUI();

    }

    private void initUI()
    {
        //init toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" Add Song From Mobile");
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);

        //Choose music from mobile
        mMusicbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MusicChange= new Intent();
                MusicChange.setType("audio/*");
                MusicChange.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(MusicChange,"Select Image"),MUSIC_PICK);
            }
        });

        //Posts music ans saves data to firebase
        mPostbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                windowOff();
                mUploadMusic.child("post_music").child(Infostatic.uid).putFile(mMusicUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            mMusicUrl =task.getResult().getDownloadUrl().toString();
                            final String Upload=mRecord.child("mixtapes").child(Infostatic.uid).child(mMixtapeid).child("songs").push().getKey();
                            String mUploadTitle=mTitle.getEditText().getText().toString();

                            Map notificationData = new HashMap();
                            notificationData.put("title", mUploadTitle);
                            notificationData.put("music", mMusicUrl);
                            notificationData.put("cnt", 0);

                            mRecord.child("mixtapes").child(Infostatic.uid).child(mMixtapeid).child("songs").child(Upload).updateChildren(notificationData, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                                    if(databaseError == null){
                                        windowOn();
                                        finish();

                                    } else {

                                        String error = databaseError.getMessage();
                                        Toast.makeText(AddSongToMixtapeActivity.this, error, Toast.LENGTH_SHORT).show();
                                        windowOn();
                                    }
                                }
                            });

                        }
                        else
                        {
                            windowOn();
                            Toast.makeText(AddSongToMixtapeActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    //data return
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==MUSIC_PICK && resultCode==RESULT_OK)
        {
            mMusicUri =data.getData();
        }

    }


    //window and progressbar util///////////////////////////////
    private void windowOff()
    {

        //shows progressbar
        //turns window touch off
        mProgress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void windowOn()
    {

        //hide progressbar
        //turns window touch on
        mProgress.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
