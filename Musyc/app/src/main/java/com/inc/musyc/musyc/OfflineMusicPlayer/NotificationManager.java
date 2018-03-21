package com.inc.musyc.musyc.OfflineMusicPlayer;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.inc.musyc.musyc.R;

//Starts, updates and cancels notifications

public class NotificationManager
{
    static NotificationCompat.Builder mbuilder;
    static ImageButton notifPlay, notifPrev, notifNext;
    static ImageView notifCover;
    static View view;
    static android.app.NotificationManager notifMan;
    static LayoutInflater inflater;
    static RemoteViews remViews;
    static TextView notifTitle;
    static boolean hasIssuedNotificationOnce=false;
    static String songState="Now Playing";

    public static void startNotification(String songName)
    {
        hasIssuedNotificationOnce=true;
        inflater = (LayoutInflater) PlayerHubActivity.contextOfThisActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);     //Get inflater and view
                                                                                                                        //of the activity for
        view = inflater.inflate(R.layout.notifications, null);                                                          //notification system


        notifPlay = (ImageButton) view.findViewById(R.id.playnotif);        //Notification big view buttons,
        notifPrev = (ImageButton) view.findViewById(R.id.prevnotif);        //EachSongFormat name and album albumCoverImage
        notifNext = (ImageButton) view.findViewById(R.id.nextnotif);
        notifCover = (ImageView) view.findViewById(R.id.notifcover);
        notifTitle = (TextView) view.findViewById(R.id.notifTitle);

        mbuilder = new NotificationCompat.Builder(view.getContext());       //Builds notification
        mbuilder.setContentTitle(songState);                                //Sets notification title to "Playing" if a song is playing, else sets to
                                                                            //"Paused"
        mbuilder.setContentText(songName).setSmallIcon(R.drawable.play).setColor(777);  //Sets song name as notification text,
                                                                                        //and sets playButton icon and background.

        remViews = new RemoteViews(view.getContext().getPackageName(), R.layout.notifications); //Sets remoteviews to big notification layout

        notifMan = (android.app.NotificationManager) view.getContext().getSystemService(Context.NOTIFICATION_SERVICE);  //Sets notification service

        Intent intent = new Intent(view.getContext(), PlayerHubActivity.class);        //Opens PlayerHubActivity class if notification is clicked from elsewhere
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);             //Instead of opening a new activity, it brings the PlayerHubActivity activity to front
        PendingIntent pendInt = PendingIntent.getActivity(view.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(PlaybackManagerClass.isPlaying) PlayerHubActivity.playButton.setImageResource(R.drawable.pause);//If new PlayerHubActivity activity is opened, then the Play/Pause button
        else                                                                              //is set according to the actual playing state
            PlayerHubActivity.playButton.setImageResource(R.drawable.play);

        mbuilder.setContentIntent(pendInt);                                 //Sets the pending intent for clicking on the notification


        synchronized (notifMan)
        {
            notifMan.notify();                          //Builds and sends notification
            notifMan.notify(001, mbuilder.build());     //Gives the notification an ID which is updated everytime a change occurs
        }                                               //so new updates are not received everytime a change occurs, rather the existing one is
    }                                                   //updated.

    public static void updateNotification(String songName)      //Sets the new information and redos the above instructions to update notification
    {
        hasIssuedNotificationOnce=true;
        inflater = (LayoutInflater) PlayerHubActivity.contextOfThisActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.notifications, null);
        mbuilder = new NotificationCompat.Builder(view.getContext());
        mbuilder.setContentTitle(songState).setContentText(songName).setSmallIcon(R.drawable.play).setColor(777);
        notifPlay = (ImageButton) view.findViewById(R.id.playnotif);
        notifPrev = (ImageButton) view.findViewById(R.id.prevnotif);
        notifNext = (ImageButton) view.findViewById(R.id.nextnotif);
        notifCover = (ImageView) view.findViewById(R.id.notifcover);
        notifTitle = (TextView) view.findViewById(R.id.notifTitle);
        notifMan = (android.app.NotificationManager) PlayerHubActivity.contextOfThisActivity.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(view.getContext(), PlayerHubActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendInt = PendingIntent.getActivity(view.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mbuilder.setContentIntent(pendInt);
        if(PlaybackManagerClass.isPlaying) PlayerHubActivity.playButton.setImageResource(R.drawable.pause);
        else
            PlayerHubActivity.playButton.setImageResource(R.drawable.play);

        remViews = new RemoteViews(view.getContext().getPackageName(), R.layout.notifications);
        synchronized (notifMan)
        {
            notifMan.notify();
            notifMan.notify(001, mbuilder.build());
        }
    }
    public static void destroyNotification()
    {
        if(hasIssuedNotificationOnce)
        {
            notifMan.cancel(001);               //Cancels the notifications with the tag assigned
            notifMan.cancelAll();               //Cancels all notifications assigned by the application
        }

    }
}
