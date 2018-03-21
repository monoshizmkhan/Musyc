package com.inc.musyc.musyc.ActivitiesAndFragments.SocialHub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inc.musyc.musyc.Global.Infostatic;
import com.inc.musyc.musyc.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/*
    Other user's prifele view.
    let you see their info and lets you add them in your follow list
 */

public class ProfileActivity extends AppCompatActivity {

    //private var
    private TextView mUsername;
    private Button mFollow;
    private Button mDelete;
    private TextView mDes;
    private TextView mIntro;
    private ImageView mImage;
    private DatabaseReference mData,mData2,mData3;
    private String mUid;
    private ProgressBar mProgressbar;
    private FloatingActionButton mMixtape;
    private  int st=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Init
        mUsername=(TextView)findViewById(R.id.profile_name);
        mDes=(TextView)findViewById(R.id.profile_des);
        mIntro=(TextView)findViewById(R.id.profile_intro);
        mFollow=(Button)findViewById(R.id.profile_bt_follow);
        mDelete=(Button)findViewById(R.id.profile_bt_delete);
        mImage=(ImageView) findViewById(R.id.profile_image);
        mUid=getIntent().getStringExtra("uid");
        mData= FirebaseDatabase.getInstance().getReference().child("uidtoinfo").child(mUid);
        mData2= FirebaseDatabase.getInstance().getReference().child("follow").child(Infostatic.uid).child(mUid);
        mData3= FirebaseDatabase.getInstance().getReference().child("follower").child(Infostatic.uid).child(mUid);
        mProgressbar=(ProgressBar)findViewById(R.id.profile_pb_progress);
        mMixtape=(FloatingActionButton)findViewById(R.id.profile_mixtapes);
        //UI build
        initUI();

    }



    private void initUI()
    {
        //show or hide button depanding on relation status
        mFollow.setVisibility(View.GONE);
        mFollow.setClickable(false);
        mDelete.setVisibility(View.GONE);
        mDelete.setClickable(false);
        windowOff();

        //Toast.makeText(ProfileActivity.this,mUid+" "+Infostatic.uid,Toast.LENGTH_LONG).show();

        mMixtape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MyMixtapes=new Intent(ProfileActivity.this,MymixtapesActivity.class);
                MyMixtapes.putExtra("isChoose","false");
                MyMixtapes.putExtra("uid",mUid);
                startActivity(MyMixtapes);
            }
        });
        //Loads user data and init follow/delete button////////////////////////////
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("username").getValue().toString();
                String intro = dataSnapshot.child("intro").getValue().toString();
                String des = dataSnapshot.child("des").getValue().toString();
                final String image=dataSnapshot.child("image").getValue().toString();
                mUsername.setText(name);
                mIntro.setText(intro);
                mDes.setText(des);
                loadimage(image);

                //init data and follow/delete button////////////////////////////////////////////
                if(!mUid.equals(Infostatic.uid))
                {
                    mData2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                st=0;
                                mFollow.setText("unfollow");
                                mFollow.setVisibility(View.VISIBLE);
                                mFollow.setClickable(true);
                            }
                            else
                            {
                                mFollow.setVisibility(View.VISIBLE);
                                mFollow.setClickable(true);
                            }
                            mData3.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists())
                                    {
                                        mDelete.setVisibility(View.VISIBLE);
                                        mDelete.setClickable(true);
                                    }
                                    mProgressbar.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else
                {
                    windowOn();
                }


                //Delete buton init//////////////////////////////////////////////////////////////////
                mDelete.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        windowOff();

                        Map unfriendMap = new HashMap();
                        unfriendMap.put("follower/" + Infostatic.uid + "/" + mUid, null);
                        unfriendMap.put("follow/" + mUid + "/" + Infostatic.uid, null);
                        FirebaseDatabase.getInstance().getReference().updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if(databaseError == null){

                                    mDelete.setVisibility(View.INVISIBLE);
                                    mDelete.setEnabled(false);
                                    windowOn();

                                } else {
                                    String error = databaseError.getMessage();
                                    Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                                    windowOn();
                                }
                            }
                        });
                    }
                });

                //follow button init/////////////////////////////////////////////////////
                mFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        windowOff();
                        //if not following///////////////////////////
                        if(st==0)
                        {
                            Map unfriendMap = new HashMap();
                            unfriendMap.put("follower/" + mUid + "/" + Infostatic.uid, null);
                            unfriendMap.put("follow/" + Infostatic.uid+ "/" + mUid, null);

                            FirebaseDatabase.getInstance().getReference().updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                    if(databaseError == null){
                                        st=1;
                                        mFollow.setText("follow");
                                        windowOn();

                                    } else {
                                        String error = databaseError.getMessage();
                                        Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                                        windowOn();
                                    }
                                }
                            });
                        }
                        else
                        {
                            //following/////////////////////
                            DatabaseReference newNotificationref = FirebaseDatabase.getInstance().getReference().child("notifications").child(mUid).push();
                            String newNotificationId = newNotificationref.getKey();
                            Long time=System.currentTimeMillis();
                            String tt=(new java.util.Date(time)).toString();
                            HashMap<String, String> notificationData = new HashMap<>();
                            notificationData.put("fromid", Infostatic.uid);
                            notificationData.put("title", "New Follower!");
                            notificationData.put("body", Infostatic.name+" is following you!");
                            notificationData.put("type", "follow");
                            notificationData.put("time", tt);
                            Map friendMap = new HashMap();
                            friendMap.put("follower/" + mUid + "/" + Infostatic.uid+"/id", Infostatic.uid);
                            friendMap.put("follow/" + Infostatic.uid+ "/" + mUid+"/id",mUid);
                            friendMap.put("notifications/" + mUid + "/" + newNotificationId, notificationData);

                            FirebaseDatabase.getInstance().getReference().updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                    if(databaseError == null){
                                        st=0;
                                        mFollow.setText("unfollow");
                                        windowOn();

                                    } else {
                                        String error = databaseError.getMessage();
                                        Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                                        windowOn();
                                    }
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //loads image////////////
    private  void loadimage(final String image){
        if(image!=null && image.length()>0 && !image.equals("default"))
        {
            Picasso.with(ProfileActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_avatar).into(mImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mImage);
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
