package com.inc.musyc.musyc.ActivitiesAndFragments.SocialHub;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.inc.musyc.musyc.JsontoJava.Notifications;
import com.inc.musyc.musyc.Global.Infostatic;
import com.inc.musyc.musyc.R;
import com.inc.musyc.musyc.Utils.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;


public class NotificationFragment extends Fragment {

    private View mMainView;
    private RecyclerView mNotificationList;
    private final List<Notifications> mNotfications=new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private NotificationAdapter mAdapter;
    private DatabaseReference mRootref;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_notification, container, false);

       //recycle view Init
        mAdapter = new NotificationAdapter(mNotfications);
        mNotificationList = (RecyclerView) mMainView.findViewById(R.id.notification_list);
        mLinearLayout = new LinearLayoutManager(getContext());
        //reverse recycle view
        mLinearLayout.setReverseLayout(true);
        mLinearLayout.setStackFromEnd(true);
        mNotificationList.setHasFixedSize(true);
        mNotificationList.setLayoutManager(mLinearLayout);

        //Recycle view source
        mRootref= FirebaseDatabase.getInstance().getReference().child("notifications").child(Infostatic.uid);

        mNotificationList.setAdapter(mAdapter);

        //recycle view loader
        loadNotification();

        return mMainView;
    }

    private void loadNotification()
    {
        mRootref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Notifications notification = dataSnapshot.getValue(Notifications.class);

                mNotfications.add(notification);
                mAdapter.notifyDataSetChanged();
                mNotificationList.smoothScrollToPosition(mAdapter.getItemCount()-1);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

}
