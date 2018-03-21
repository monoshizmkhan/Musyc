package com.inc.musyc.musyc.ActivitiesAndFragments.SocialHub;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inc.musyc.musyc.Global.Infostatic;
import com.inc.musyc.musyc.JsontoJava.Follow;
import com.inc.musyc.musyc.R;
import com.inc.musyc.musyc.Utils.*;

//shows your follow list
public class FollowFragment extends Fragment {

    //private var
    private RecyclerView mFollowList;
    private DatabaseReference mFollowDatabase;
    private DatabaseReference mUsersDatabase;
    private View mMainView;
    private FirebaseRecyclerAdapter<Follow, FollowViewHolder> mFollowRecyclerViewAdapter;

    public FollowFragment() {
        // Required
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {

        //init
        mMainView = inflater.inflate(R.layout.fragment_follow, container, false);
        mFollowList = (RecyclerView) mMainView.findViewById(R.id.follow_list);
        mFollowDatabase = FirebaseDatabase.getInstance().getReference().child("follow").child(Infostatic.uid);
        mFollowDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("uidtoinfo");
        mUsersDatabase.keepSynced(true);
        mFollowList.setHasFixedSize(true);
        mFollowList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;

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
            protected void populateViewHolder(final FollowViewHolder followViewHolder, Follow follow, int position) {

                final String ThisUserId=follow.getId();

                mUsersDatabase.child(ThisUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        followViewHolder.setName(dataSnapshot.child("username").getValue().toString());
                        followViewHolder.setIntro(dataSnapshot.child("intro").getValue().toString());
                        followViewHolder.setImage(dataSnapshot.child("image").getValue().toString(), getContext());

                        followViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent Profile=new Intent(getContext(),ProfileActivity.class);
                                Profile.putExtra("uid", ThisUserId );
                                startActivity(Profile);
                            }
                        });
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mFollowList.setAdapter(mFollowRecyclerViewAdapter);

    }
}
