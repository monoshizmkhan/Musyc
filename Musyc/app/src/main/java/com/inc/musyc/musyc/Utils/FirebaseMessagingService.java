package com.inc.musyc.musyc.Utils;

/**
 * Created by saad on 10/3/17.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.inc.musyc.musyc.Global.Infostatic;
import com.inc.musyc.musyc.R;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private Infostatic infostatic;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //builds notification
        String notification_title = remoteMessage.getNotification().getTitle();
        String notification_message = remoteMessage.getNotification().getBody();
        String click_action = remoteMessage.getNotification().getClickAction();
        infostatic=new Infostatic();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(notification_title)
                        .setContentText(notification_message);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);


       // notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
       /// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if(infostatic.isfirst)
        {
            //app not online
            Intent resultIntent = new Intent(click_action);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);
        }
        else
        {
            //app online
            Intent resultIntent = new Intent(getApplicationContext(),DumbActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);
        }


        mBuilder.setAutoCancel(true);

        int mNotificationId = (int) System.currentTimeMillis();

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());


    }
}