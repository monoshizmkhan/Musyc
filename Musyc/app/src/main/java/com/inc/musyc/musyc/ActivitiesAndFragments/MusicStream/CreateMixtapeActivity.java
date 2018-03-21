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
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;

/*
    Creats a mixtape with image title and description
    leter user can add song from MixtapeListane activity menu
 */
public class CreateMixtapeActivity extends AppCompatActivity {

    //private var
    private Toolbar mToolbar;
    private TextInputLayout mTitle,mDescription;
    private Button mPostbt;
    private FloatingActionButton  mImagebt;
    private static final int IMG_PICK=121;
    private Uri mImageUri;
    private ProgressBar mProgress;
    private StorageReference mUploadimage;
    private String mImageurl;
    private DatabaseReference mRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createmixtape);

        //init
        mToolbar =(Toolbar)findViewById(R.id.createmictape_toolbar);
        mProgress=(ProgressBar)findViewById(R.id.createmixtape_progress);
        mProgress.setVisibility(View.GONE);
        mTitle=(TextInputLayout)findViewById(R.id.createmixtape_title);
        mDescription=(TextInputLayout)findViewById(R.id.createmixtape_des);
        mPostbt=(Button)findViewById(R.id.createmixtapebt_post);
        mImagebt=(FloatingActionButton) findViewById(R.id.createmixtapebt_img);
        mUploadimage= FirebaseStorage.getInstance().getReference();
        mRecord=FirebaseDatabase.getInstance().getReference();
        //init UI
        initUI();
    }

    private void initUI()
    {
        //init toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("  Create Mixtape");
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);

        //image button
        mImagebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imagechange= new Intent();
                imagechange.setType("image/*");
                imagechange.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(imagechange,"Select Image"),IMG_PICK);
            }
        });

        //post button saves data to firebase
        mPostbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                windowOff();

                mUploadimage.child("post_music").child(Infostatic.uid).putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            mImageurl=task.getResult().getDownloadUrl().toString();
                            final String up=mRecord.child("posts").child(Infostatic.uid).push().getKey();
                            if(mImageurl==null)mImageurl="default";
                            String title=mTitle.getEditText().getText().toString();
                            String description=mDescription.getEditText().getText().toString();


                            Map notificationData = new HashMap();
                            notificationData.put("title", title);
                            notificationData.put("description", description );
                            notificationData.put("image", mImageurl);
                            notificationData.put("id", up);
                            notificationData.put("cnt", 0);

                            FirebaseDatabase.getInstance().getReference().child("mixtapes").child(Infostatic.uid).child(up).updateChildren(notificationData, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                    if(databaseError == null){

                                        windowOn();
                                        finish();

                                    } else {
                                        String error = databaseError.getMessage();
                                        Toast.makeText(CreateMixtapeActivity.this, error, Toast.LENGTH_SHORT).show();
                                        windowOn();
                                    }
                                }
                            });
                        }
                        else
                        {
                            windowOn();
                            Toast.makeText(CreateMixtapeActivity.this,"Error!",Toast.LENGTH_SHORT).show();
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
            Toast.makeText(CreateMixtapeActivity.this,"Sucessful!",Toast.LENGTH_SHORT).show();
            CropImage.activity(imguri).setAspectRatio(2,1).start(this);

        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();

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
