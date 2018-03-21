package com.inc.musyc.musyc.ActivitiesAndFragments.MusicStream;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;

/*
    create single song post
 */

public class CreatePostActivity extends AppCompatActivity {

    //private var
    private TextInputLayout mTitle,mArtist,mPost;
    private Button mImagebt,mMusicbt,mPostbt;
    private String mImageurl,mMusicurl,mTitles,mArtists,mPosts;
    private Uri mImageuri,mMusicuri;
    private DatabaseReference mRecord;
    private StorageReference mUploadimage,mUploadmusic;
    private ProgressBar mProgress;

    //data return code
    private static final int IMG_PICK=111,MUSIC_PICK=222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpost);

        //init var
        mTitle=(TextInputLayout) findViewById(R.id.createpost_title);
        mArtist=(TextInputLayout) findViewById(R.id.createpost_artist);
        mPost=(TextInputLayout) findViewById(R.id.createpost_post);
        mImagebt=(Button)findViewById(R.id.createpost_bt_img);
        mMusicbt=(Button)findViewById(R.id.createpost_bt_music);
        mPostbt=(Button)findViewById(R.id.createpost_bt_post);
        mProgress=(ProgressBar)findViewById(R.id.createpost_progress);
        mUploadimage= FirebaseStorage.getInstance().getReference();
        mUploadmusic=FirebaseStorage.getInstance().getReference();
        mRecord=FirebaseDatabase.getInstance().getReference();

        // /window is on
        windowOn();
        //UI init
        initUI();
    }

    private void initUI()
    {
        //add image button
        mImagebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ImageChange= new Intent();
                ImageChange.setType("image/*");
                ImageChange.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(ImageChange,"Select Image"),IMG_PICK);
            }
        });

        //add music button
        mMusicbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent musicchange= new Intent();
                musicchange.setType("audio/*");
                musicchange.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(musicchange,"Select Music"),MUSIC_PICK);
            }
        });

        //post button. saves post to firebase
        mPostbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mTitle.getEditText().getText().toString().length()==0 ||  mArtist.getEditText().getText().toString().length()==0 )
                {

                    Toast.makeText(CreatePostActivity.this,"Please add title and Artist!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mPost.getEditText().getText().toString().length()==0)
                {
                    Toast.makeText(CreatePostActivity.this,"Your post is Empty!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mMusicuri==null || mImageuri==null)
                {
                    Toast.makeText(CreatePostActivity.this,"Please add image and music!",Toast.LENGTH_SHORT).show();
                    return;
                }

                //window if off
                windowOff();

                mUploadimage.child("post_mage").child(Infostatic.uid).putFile(mImageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            mImageurl=task.getResult().getDownloadUrl().toString();

                            mUploadmusic.child("post_music").child(Infostatic.uid).putFile(mMusicuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        mMusicurl=task.getResult().getDownloadUrl().toString();
                                        final String UploadId=mRecord.child("posts").child(Infostatic.uid).push().getKey();
                                        if(mImageurl==null)mImageurl="default";
                                        if(mMusicurl==null)mMusicurl="default";
                                        mTitles=mTitle.getEditText().getText().toString();
                                        mArtists=mArtist.getEditText().getText().toString();
                                        mPosts=mPost.getEditText().getText().toString();

                                        Map notificationData = new HashMap();

                                        notificationData.put(UploadId+"/title", mTitles);
                                        notificationData.put(UploadId+"/artist", mArtists);
                                        notificationData.put(UploadId+"/post", mPosts);
                                        notificationData.put(UploadId+"/image", mImageurl);
                                        notificationData.put(UploadId+"/music", mMusicurl);
                                        notificationData.put(UploadId+"/name", Infostatic.name);
                                        notificationData.put("latest", UploadId);


                                        FirebaseDatabase.getInstance().getReference().child("posts").child(Infostatic.uid).updateChildren(notificationData, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                                if(databaseError == null){
                                                    windowOn();
                                                    finish();

                                                } else {

                                                    String error = databaseError.getMessage();
                                                    Toast.makeText(CreatePostActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    windowOn();
                                                }
                                            }
                                        });

                                    }
                                    else
                                    {
                                        windowOn();
                                        Toast.makeText(CreatePostActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                        else
                        {
                            windowOn();
                            Toast.makeText(CreatePostActivity.this,"Error!",Toast.LENGTH_SHORT).show();
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

        if(requestCode==IMG_PICK && resultCode==RESULT_OK)
        {
            Uri imguri=data.getData();
            Toast.makeText(CreatePostActivity.this,"Sucessful!",Toast.LENGTH_SHORT).show();
            CropImage.activity(imguri).setAspectRatio(2,1).start(this);
        }
        else if(requestCode==MUSIC_PICK && resultCode==RESULT_OK)
        {
            mMusicuri=data.getData();
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageuri = result.getUri();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
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



