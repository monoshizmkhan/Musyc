package com.inc.musyc.musyc.ActivitiesAndFragments.MusicStream;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.inc.musyc.musyc.ActivitiesAndFragments.MainSettings.MainSettingsActivity;
import com.inc.musyc.musyc.ActivitiesAndFragments.SocialHub.SocialMainActivity;
import com.inc.musyc.musyc.ActivitiesAndFragments.loginsignup.StartActivity;
import com.inc.musyc.musyc.Global.Infostatic;
import com.inc.musyc.musyc.OfflineMusicPlayer.PlaylistActivity;
import com.inc.musyc.musyc.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/*
    First activity thats open when the app start
    you can nevigate to any where from here
 */

public class AppMainActivity extends AppCompatActivity {

    //private variables
    private Button mLogin;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Toolbar mToolbar;
    private ImageButton mSocial;
    private ImageButton mAppset;
    private ImageButton mMusicplayer;
    private ImageButton mSyncKarioky;
    private ProgressBar mProgressbar;
    private TextView mName;
    private CircleImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_main);

        //init var
        mAuth=FirebaseAuth.getInstance();
        mLogin=(Button)findViewById(R.id.appmain_login);
        mToolbar=(Toolbar)findViewById(R.id.appmain_toolbar);
        mName=(TextView)findViewById(R.id.appmain_name);
        mSocial=(ImageButton)findViewById(R.id.appmain_bt_social);
        mAppset=(ImageButton)findViewById(R.id.appmain_appset);
        mSyncKarioky=(ImageButton)findViewById(R.id.appmain_app);
        mProgressbar=(ProgressBar) findViewById(R.id.appmain_pb_progress);
        mDatabase=FirebaseDatabase.getInstance().getReference();
        mImage=(CircleImageView)findViewById(R.id.appmain_image);
        mMusicplayer=(ImageButton)findViewById(R.id.appmain_bt_music);

        //Initialize login activity///////////////
        initlogin();
        //init UI////////////////////////////////
        initUI();

    }

    private void initUI()
    {
        mMusicplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppMainActivity.this, PlaylistActivity.class);    //Opens the PlayerHubActivity activity on startup
                startActivity(intent);
            }
        });
        //init progressbar
        mProgressbar.setVisibility(View.GONE);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("  MUSYC HOME");
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);
        mDatabase.keepSynced(true);

        //button for feed
        mSocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Infostatic.islogedin)return;
                Intent socialmain = new Intent(AppMainActivity.this, SocialMainActivity.class);
                socialmain.putExtra("f", 0);
                startActivity(socialmain);
            }
        });

        //button for appsetting
        mAppset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settings = new Intent(AppMainActivity.this, MainSettingsActivity.class);
                startActivity(settings);

            }
        });

        //button for sync/karioky main page

        mSyncKarioky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Infostatic.islogedin)return;
                Intent synckarioky = new Intent(AppMainActivity.this, SyncKariokyMainActivity.class);
                startActivity(synckarioky);
            }
        });

        //button for notification and login
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Infostatic.isfirst=false;
                if(Infostatic.islogedin)
                {
                    Intent socialmain = new Intent(AppMainActivity.this, SocialMainActivity.class);
                    socialmain.putExtra("f", 2);
                    startActivity(socialmain);
                }
                else {
                    Intent Start = new Intent(AppMainActivity.this, StartActivity.class);
                    startActivity(Start);
                    //Toast.makeText(AppMainActivity.this, "", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void initlogin()
    {
        //checkin for login
        if(mAuth.getCurrentUser()!=null )
        {
            //gets current users data from firebase and saves it in infosatic
            windowOff();
            Infostatic.uid=mAuth.getCurrentUser().getUid();
            Infostatic.email=mAuth.getCurrentUser().getEmail();
            mDatabase.child("uidtoinfo").child(Infostatic.uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()) {

                        //updating infostatic
                        Infostatic.token= FirebaseInstanceId.getInstance().getToken();
                        Infostatic.name = dataSnapshot.child("username").getValue().toString();
                        Infostatic.intro = dataSnapshot.child("intro").getValue().toString();
                        Infostatic.des = dataSnapshot.child("des").getValue().toString();
                        Infostatic.img=dataSnapshot.child("image").getValue().toString();
                        Infostatic.not=dataSnapshot.child("cnt").getValue().toString();
                        FirebaseDatabase.getInstance().getReference().child("uidtoinfo").child(Infostatic.uid).child("token").setValue(Infostatic.token);
                        //Toast.makeText(AppMainActivity.this, "Success!", Toast.LENGTH_LONG).show();
                        Infostatic.islogedin=true;
                        //Infostatic.isfirst=false;
                        loadImage();
                        mName.setText(Infostatic.name);
                        mLogin.setText(""+Infostatic.not+" Notifications");
                        windowOn();
                    }
                    else
                    {
                        mName.setText("Offline");
                        mLogin.setText("Log In");
                        windowOn();
                        mLogin.setClickable(true);
                        mImage.setImageResource(R.drawable.default_avatar);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        else
        {

            Infostatic.islogedin=false;
            mName.setText("Offline");
            mLogin.setText("Log In");
            windowOn();
            mLogin.setClickable(true);
            mImage.setImageResource(R.drawable.default_avatar);
            FirebaseAuth.getInstance().signOut();
        }
    }

    private void loadImage()
    {
        //load image from online with a placeholder image and network persistence

        if(Infostatic.img.length()>0 && !Infostatic.img.equals("default"))
        {
            Picasso.with(AppMainActivity.this).load(Infostatic.img).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_avatar).into(mImage, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError() {
                    Picasso.with(AppMainActivity.this).load(Infostatic.img).placeholder(R.drawable.default_avatar).into(mImage);
                }
            });
        }
    }

    //Android Lifecycle Activity///////////////////////////////////

    @Override
    protected void onResume() {
        initlogin();
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        if(Infostatic.islogedin==false)mAuth.signOut();

        super.onDestroy();
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
