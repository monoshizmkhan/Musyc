package com.inc.musyc.musyc.Utils;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.inc.musyc.musyc.JsontoJava.Notifications;
import com.inc.musyc.musyc.ActivitiesAndFragments.SocialHub.ProfileActivity;
import com.inc.musyc.musyc.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by saad on 10/3/17.
 * Notification recycle view adapter
 */

public class NotificationAdapter extends RecyclerView.Adapter< NotificationAdapter.NotificationViewHolder> {
    private List<Notifications> mNotficationList;

    //Notification List ////////////////////////
    public NotificationAdapter(List<Notifications> mNotficationList) {

        this.mNotficationList = mNotficationList;

    }


    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_layout ,parent, false);

        return new NotificationViewHolder(v);

    }

    //inflate notification view for different type of notification

    public class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView notificationBody;
        public CircleImageView notificationImg;
        public  TextView notificationTitle;
        public TextView notificationTime;
        public String fromid,type,postid;
        private  Context context;
        public NotificationViewHolder(View view) {
            super(view);
            context=view.getContext();
            notificationBody = (TextView) view.findViewById(R.id.notification_body);
            notificationImg = (CircleImageView) view.findViewById(R.id.notification_img);
            notificationTitle = (TextView) view.findViewById(R.id.notification_title);
            notificationTime=(TextView) view.findViewById(R.id.notification_time);
            fromid="";
            view.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {

            if(type.equals("likes"))return;
            Intent socialmain=new Intent(view.getContext(),ProfileActivity.class);
            socialmain.putExtra("uid", fromid );
            context.startActivity(socialmain);
        }
    }

    @Override
    public void onBindViewHolder(final NotificationViewHolder viewHolder, int i) {

        Notifications c = mNotficationList.get(i);
        viewHolder.fromid = c.getFromid();
        viewHolder.type = c.getType();
        viewHolder.postid = c.getPostid();
        viewHolder.notificationBody.setText(c.getBody());
        viewHolder.notificationTime.setText(c.getTime());
        viewHolder.notificationTitle.setText(c.getTitle());
        if(viewHolder.type.equals("likes"))viewHolder.notificationImg.setImageResource(R.drawable.like);
        else{
            viewHolder.notificationImg.setImageResource(R.drawable.follow);
        }
        //img add here
    }

    @Override
    public int getItemCount() {
        return mNotficationList.size();
    }
}
