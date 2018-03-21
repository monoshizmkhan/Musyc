package com.inc.musyc.musyc.ActivitiesAndFragments.MusicStream;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inc.musyc.musyc.ActivitiesAndFragments.SocialHub.MymixtapesActivity;
import com.inc.musyc.musyc.Global.Infostatic;
import com.inc.musyc.musyc.JsontoJava.Follow;
import com.inc.musyc.musyc.R;
import com.inc.musyc.musyc.Utils.FollowViewHolder;

public class SyncMainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FloatingActionButton mAddbt;
    private RecyclerView mFollowList;
    private DatabaseReference mFollowDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mMyDatabase;
    private Button mMyparty;
    public static String mTitle,mImg,mId;
    private FirebaseRecyclerAdapter<Follow, FollowViewHolder> mFollowRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_main);

        mToolbar=(Toolbar)findViewById(R.id.syncmain_toolbar);
        mAddbt=(FloatingActionButton)findViewById(R.id.syncmain_add);
        mMyparty=(Button)findViewById(R.id.syncmain_mypartybt);
        mFollowList = (RecyclerView) findViewById(R.id.syncmain_list);
        mTitle="default";
        mImg="default";
        mId="default";
        mFollowDatabase = FirebaseDatabase.getInstance().getReference().child("follow").child(Infostatic.uid);
        mFollowDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("uidtoinfo");
        mUsersDatabase.keepSynced(true);
        mMyDatabase = FirebaseDatabase.getInstance().getReference().child("uidtoinfo").child(Infostatic.uid).child("party");
        mFollowList.setHasFixedSize(true);
        mFollowList.setLayoutManager(new LinearLayoutManager(SyncMainActivity.this));

        mMyDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    mTitle=dataSnapshot.child("title").getValue().toString();
                    if(dataSnapshot.child("image").exists())mImg=dataSnapshot.child("image").getValue().toString();
                    mId=dataSnapshot.child("id").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        initUI();
    }

    private void initUI()
    {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("  MUSYC APPS");
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);

        mAddbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent choose = new Intent(SyncMainActivity.this, MymixtapesActivity.class);
                choose.putExtra("isChoose","true");
                choose.putExtra("uid",Infostatic.uid);
                startActivity(choose);
            }
        });

        mMyparty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mId.equals("default"))
                {
                    Toast.makeText(SyncMainActivity.this,"No Current Party!",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent socialmain=new Intent(SyncMainActivity.this,SyncPlaylistActivity.class);
                socialmain.putExtra("title",mTitle);
                socialmain.putExtra("image",mImg);
                socialmain.putExtra("id",mId);
                socialmain.putExtra("uid",Infostatic.uid);
                startActivity(socialmain);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        //firebase recycler view
        mFollowRecyclerViewAdapter = new FirebaseRecyclerAdapter<Follow, FollowViewHolder>(

                Follow.class,
                R.layout.singleuser_layout,
                FollowViewHolder.class,
                mFollowDatabase


        ) {
            @Override
            protected void populateViewHolder(final FollowViewHolder followViewHolder, final Follow follow, int position) {

                final String ThisUserId=follow.getId();

                mUsersDatabase.child(ThisUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        followViewHolder.setName(dataSnapshot.child("username").getValue().toString());
                        if(dataSnapshot.child("party").exists())
                        {
                            followViewHolder.setIntro("A party is going on @"+dataSnapshot.child("username").getValue().toString()+"'s!");
                            follow.setIsParty("true");
                            follow.setTitle(dataSnapshot.child("party").child("title").getValue().toString());
                            if(dataSnapshot.child("party").child("image").exists())follow.setImage(dataSnapshot.child("party").child("image").getValue().toString());
                            follow.setPartyid(dataSnapshot.child("party").child("id").getValue().toString());
                        }
                        else
                        {
                            followViewHolder.setIntro("No party is going on right now.");
                            follow.setIsParty("false");
                        }
                        followViewHolder.setImage(dataSnapshot.child("image").getValue().toString(), SyncMainActivity.this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                followViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(follow.getIsParty().equals("false"))return;
                        Intent socialmain=new Intent(SyncMainActivity.this,SyncPlaylistActivity.class);
                        socialmain.putExtra("title",follow.getTitle());
                        socialmain.putExtra("image",follow.getImage());
                        socialmain.putExtra("id",follow.getPartyid());
                        socialmain.putExtra("uid",follow.getId());
                        startActivity(socialmain);
                    }
                });

            }
        };

        mFollowList.setAdapter(mFollowRecyclerViewAdapter);

    }
}
