package com.inc.musyc.musyc.ActivitiesAndFragments.SocialHub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.inc.musyc.musyc.JsontoJava.Singleuser;
import com.inc.musyc.musyc.R;
import com.inc.musyc.musyc.Utils.UserViewHolder;

/*
    Demo activity for debugging purpose
    later it will be change to search activity
 */

public class DemoActivity extends AppCompatActivity {

    //private var
    private Toolbar mToolbar;
    private RecyclerView mRview;
    private DatabaseReference mUserdata;
    private Query mSearch;
    private TextView mResulttext;
    private FirebaseRecyclerAdapter<Singleuser,UserViewHolder> UserListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        String search=getIntent().getStringExtra("query").toString();
        //String search="moumita";
        //init
        //mUserdata=FirebaseDatabase.getInstance().getReference().child("uidtoinfo");
        mToolbar=(Toolbar)findViewById(R.id.demo_toolbar);
        mRview=(RecyclerView)findViewById(R.id.demo_list);
        mResulttext=(TextView)findViewById(R.id.demo_result);
        mSearch=FirebaseDatabase.getInstance().getReference().child("uidtoinfo").orderByChild("username").equalTo(search);
        //init UI
        initUI();
    }

    private void initUI()
    {
        //toolbar init
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("  Search Result");
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);
        //recycle view init
        mRview.setHasFixedSize(true);
        mRview.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        //firebase recycle view adaptar
        UserListAdapter=
        new FirebaseRecyclerAdapter<Singleuser, UserViewHolder>(
                Singleuser.class,
                R.layout.singleuser_layout,
                UserViewHolder.class,
                mSearch
        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, Singleuser model, int position) {

                viewHolder.setName(model.getUsername());
                viewHolder.setIntro(model.getIntro());
                viewHolder.setImage(model.getImage(),getApplicationContext());
                final String uid=getRef(position).getKey();
                mResulttext.setText("1 Result Found!");

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent Profile=new Intent(DemoActivity.this,ProfileActivity.class);
                        Profile.putExtra("uid",uid);
                        startActivity(Profile);
                    }
                });
            }
        };
        mRview.setAdapter(UserListAdapter);
    }


}
