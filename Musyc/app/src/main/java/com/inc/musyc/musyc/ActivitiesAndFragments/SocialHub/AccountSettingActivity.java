package com.inc.musyc.musyc.ActivitiesAndFragments.SocialHub;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.inc.musyc.musyc.Global.Infostatic;
import com.inc.musyc.musyc.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

/*
    User's account settings.
    lets you change image,pass,intro
 */

public class AccountSettingActivity extends AppCompatActivity {

    //private var
    private TextView mName;
    private TextView mIntro;
    private TextView mDes;
    private Button mChangeIntro;
    private Button mChangeImage;
    private final int IMG_PICK=1;
    private FirebaseStorage mStorage;
    private ProgressBar mProgressbar;
    private CircleImageView mImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        //init
        mName=(TextView)findViewById(R.id.acsat_name);
        mIntro=(TextView)findViewById(R.id.acsat_intro);
        mDes=(TextView)findViewById(R.id.acsat_description);
        mDes.setVisibility(View.GONE);
        mChangeIntro =(Button)findViewById(R.id.acsat_bt_intro);
        mChangeImage =(Button)findViewById(R.id.acsat_bt_image);
        mStorage=FirebaseStorage.getInstance();
        mProgressbar=(ProgressBar)findViewById(R.id.acsat_pb_progress);
        mImage=(CircleImageView)findViewById(R.id.acsat_image);
        //loads profile image
        loadimage();
        //init UI
        initUI();
    }

    private void initUI()
    {
        //sets text fields
        mName.setText(Infostatic.name);
        mIntro.setText(Infostatic.intro);
        mDes.setText(Infostatic.des);

        //change intro button
        mChangeIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ChangeIntro=new Intent(AccountSettingActivity.this,ChangeIntroActivity.class);
                startActivity(ChangeIntro);
            }
        });

        //change image button
        mChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           /*     CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AccountSettingActivity.this);*/

                Intent ImageChange= new Intent();
                ImageChange.setType("image/*");
                ImageChange.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(ImageChange,"Select Image"),IMG_PICK);
            }
        });
    }

    //Android lifecycle///////////////////////////////////////////////
    @Override
    protected void onResume() {

        mName.setText(Infostatic.name);
        mIntro.setText(Infostatic.intro);
        mDes.setText(Infostatic.des);
        super.onResume();
    }

    //data return and upload
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMG_PICK && resultCode==RESULT_OK)
        {
            Uri imguri=data.getData();
            Toast.makeText(AccountSettingActivity.this,"Sucessful!",Toast.LENGTH_SHORT).show();
            CropImage.activity(imguri).setAspectRatio(1,1).start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                windowOff();
                Uri resultUri = result.getUri();

                mStorage.getReference().child("Profile_image").child(Infostatic.uid).putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            final String Url=task.getResult().getDownloadUrl().toString();

                            FirebaseDatabase.getInstance().getReference().child("uidtoinfo").child(Infostatic.uid).child("image").setValue(Url).addOnCompleteListener(
                                    new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Infostatic.img=Url;
                                                windowOn();
                                                Toast.makeText(AccountSettingActivity.this,"Success!",Toast.LENGTH_SHORT).show();
                                                loadimage();

                                            }
                                            else
                                            {
                                                windowOn();
                                                Toast.makeText(AccountSettingActivity.this,"Try Again!",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                            );

                        }
                        else
                        {
                            windowOn();
                            Toast.makeText(AccountSettingActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    //loads image from online with a placeholder image and network persistence
    public void  loadimage(){
        if(Infostatic.img.length()>0 && !Infostatic.img.equals("default"))
        {
            Picasso.with(AccountSettingActivity.this).load(Infostatic.img).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_avatar).into(mImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(AccountSettingActivity.this).load(Infostatic.img).placeholder(R.drawable.default_avatar).into(mImage);
                }
            });
        }

    }

    //window and progressbar util///////////////////////////////
    private void windowOff()
    {
        //shows progressbar
        //turns window touch off
        mProgressbar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void windowOn()
    {
        //hide progressbar
        //turns window touch on
        mProgressbar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
