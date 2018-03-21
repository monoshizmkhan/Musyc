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
import com.inc.musyc.musyc.ActivitiesAndFragments.MusicStream.SinglePostViewActivity;
import com.inc.musyc.musyc.JsontoJava.Post;
import com.inc.musyc.musyc.Global.Infostatic;
import com.inc.musyc.musyc.R;
import com.inc.musyc.musyc.Utils.*;


public class FeedFragment extends Fragment {

    private RecyclerView mFeedList;
    private DatabaseReference mFeedDatabase;
    private View mMainView;
    private FirebaseRecyclerAdapter<Post, FeedViewHolder> mFeedRecyclerViewAdapter;

    public FeedFragment() {
        //Required
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //Initialization
        mMainView = inflater.inflate(R.layout.fragment_feed, container, false);
        mFeedList = (RecyclerView) mMainView.findViewById(R.id.feed_list);
        mFeedDatabase = FirebaseDatabase.getInstance().getReference().child("follow").child(Infostatic.uid);//follow list
        mFeedDatabase.keepSynced(true);//Persistence
        mFeedList.setHasFixedSize(true);
        mFeedList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;
    }



    @Override
    public void onStart() {
        super.onStart();

        mFeedRecyclerViewAdapter = new FirebaseRecyclerAdapter<Post, FeedViewHolder>(

                Post.class,
                R.layout.postview_layout,
                FeedViewHolder.class,
                mFeedDatabase


        ) {
            @Override
            protected void populateViewHolder(final FeedViewHolder feedViewHolder, final Post post, int position) {


                FirebaseDatabase.getInstance().getReference().child("posts").child(post.getId()).child("latest").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String LatestPostId=dataSnapshot.getValue().toString();

                        FirebaseDatabase.getInstance().getReference().child("posts").child(post.getId()).child(LatestPostId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                post.setArtist(dataSnapshot.child("artist").getValue().toString());
                                post.setTitle(dataSnapshot.child("title").getValue().toString());
                                post.setImage(dataSnapshot.child("image").getValue().toString());
                                post.setPost(dataSnapshot.child("post").getValue().toString());
                                post.setMusic(dataSnapshot.child("music").getValue().toString());
                                post.setName(dataSnapshot.child("name").getValue(String.class));
                                post.setPostid(LatestPostId);

                                feedViewHolder.setTitle(post.getTitle());
                                feedViewHolder.setArtist(post.getArtist());
                                feedViewHolder.setImage(post.getImage(),getContext());
                                feedViewHolder.setName(post.getName());
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

                feedViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(post.getTitle()==null)return;
                        if(post.getMusic()==null)return;
                        if(post.getImage()==null)return;

                        Intent SinglePostview=new Intent(getContext(),SinglePostViewActivity.class);

                        //Init
                        SinglePostview.putExtra("title",post.getTitle());
                        SinglePostview.putExtra("artist",post.getArtist());
                        SinglePostview.putExtra("post",post.getPost());
                        SinglePostview.putExtra("image",post.getImage());
                        SinglePostview.putExtra("music",post.getMusic());
                        SinglePostview.putExtra("postid",post.getPostid());
                        SinglePostview.putExtra("id",""+post.getId());
                        SinglePostview.putExtra("name",post.getName());


                        startActivity(SinglePostview);
                    }
                });

            }
        };

        mFeedList.setAdapter(mFeedRecyclerViewAdapter);


    }





}

