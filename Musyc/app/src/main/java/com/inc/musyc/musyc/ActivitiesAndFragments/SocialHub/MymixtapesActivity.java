package com.inc.musyc.musyc.ActivitiesAndFragments.SocialHub;

import android.content.Intent;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.inc.musyc.musyc.ActivitiesAndFragments.MusicStream.CreateMixtapeActivity;
import com.inc.musyc.musyc.ActivitiesAndFragments.MusicStream.MixtapelistenActivity;
import com.inc.musyc.musyc.ActivitiesAndFragments.MusicStream.SyncMainActivity;
import com.inc.musyc.musyc.Global.Infostatic;
import com.inc.musyc.musyc.JsontoJava.Mixtape;
import com.inc.musyc.musyc.R;
import com.inc.musyc.musyc.Utils.*;

import java.util.HashMap;
import java.util.Map;

/*
    Mymixtapelist shows all the mxtapes i have made.
    a mixtape is a playlist
 */

public class MymixtapesActivity extends AppCompatActivity {

    //private var
    private Toolbar mToolbar;
    private FloatingActionButton mAddbutton;
    private RecyclerView mMixtapeList;
    private DatabaseReference mMixtapeDatabase;
    private DatabaseReference mUpdate;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private boolean isChoose;
    private FirebaseRecyclerAdapter<Mixtape,MixtapeViewHolder> MixtapesRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mymixtapes);
        if(getIntent().getStringExtra("isChoose").toString().equals("true"))isChoose=true;
        else isChoose=false;
        //init
        mToolbar =(Toolbar)findViewById(R.id.mymixtapes_toolbar);
        mMixtapeList = (RecyclerView)findViewById(R.id.mymixtape_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = getIntent().getStringExtra("uid").toString();
        if(mCurrent_user_id==null)mCurrent_user_id=Infostatic.uid;
        mMixtapeDatabase = FirebaseDatabase.getInstance().getReference().child("mixtapes").child(mCurrent_user_id);
        mMixtapeDatabase.keepSynced(true);
        mAddbutton=(FloatingActionButton)findViewById(R.id.mymxtapesbt_add);
        mUpdate=FirebaseDatabase.getInstance().getReference().child("uidtoinfo").child(Infostatic.uid).child("party");
        //init UI
        initUI();
    }


    private void initUI()
    {
        //UI recycle view
        mMixtapeList.setHasFixedSize(true);
        mMixtapeList.setLayoutManager(new LinearLayoutManager(MymixtapesActivity.this));
        if(isChoose)mAddbutton.setVisibility(View.GONE);
        //init tolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("  Mixtapes");
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);

        //init plalist add button
        mAddbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isChoose)return;
                Intent socialmain=new Intent(MymixtapesActivity.this,CreateMixtapeActivity.class);
                startActivity(socialmain);
            }
        });
    }

    //adroid lifecycle /////////////////////////////
    @Override
    public void onStart() {
        super.onStart();

        //Recycle View Init
        MixtapesRecyclerViewAdapter = new FirebaseRecyclerAdapter<Mixtape,MixtapeViewHolder>(
                Mixtape.class,
                R.layout.mixtape_layout,
                MixtapeViewHolder.class,
                mMixtapeDatabase
        ) {
            @Override
            protected void populateViewHolder(MixtapeViewHolder viewHolder, final Mixtape model, int position) {

                if(model.getTitle()!=null)viewHolder.setTitle(model.getTitle());
                else viewHolder.setTitle("Liked song");
                if(model.getDescription()!=null)viewHolder.setDescription(model.getDescription());
                else viewHolder.setDescription("Songs you might like.");
                viewHolder.setImage(model.getImage(),MymixtapesActivity.this);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isChoose)
                        {
                            Map partyMap = new HashMap();
                            if(model.getTitle()!=null)partyMap.put("title", model.getTitle());
                            else partyMap.put("title", model.getTitle());
                            partyMap.put("image", model.getImage());
                            partyMap.put("id", model.getId());
                            SyncMainActivity.mTitle=model.getTitle();
                            SyncMainActivity.mImg=model.getImage();
                            SyncMainActivity.mId=model.getId();
                            mUpdate.updateChildren(partyMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                    if(databaseError == null){
                                        Toast.makeText(MymixtapesActivity.this,model.getTitle()+" was selected.",Toast.LENGTH_SHORT).show();
                                    } else {
                                        String error = databaseError.getMessage();
                                    }
                                }
                            });
                            FirebaseDatabase.getInstance().getReference().child("party").child(Infostatic.uid).child("current").setValue(0);
                            return;
                        }
                        Intent socialmain=new Intent(MymixtapesActivity.this,MixtapelistenActivity.class);
                       if(model.getTitle()!=null) socialmain.putExtra("title",model.getTitle());
                        else socialmain.putExtra("title",model.getTitle());
                        socialmain.putExtra("image",model.getImage());
                        if(model.getId()!=null)socialmain.putExtra("id",model.getId());
                        else socialmain.putExtra("id","liked_songs");
                        socialmain.putExtra("uid",mCurrent_user_id);
                        startActivity(socialmain);
                    }
                });
            }
        };

        mMixtapeList.setAdapter(MixtapesRecyclerViewAdapter);
    }
}
