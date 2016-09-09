package com.example.sachinf.roadsoft;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by sachinf on 8/26/2016.
 */
public class NotificationHandler {
    private static Notification.Builder builder;
    public String title = "Roadsoft";
    public String text = "Monitoring Location";

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int iconId = R.mipmap.ic_launcher;

    //----------------------------------------- NOTIFICATION --------------------------------------------------------//



    public void makeNotification(Context context, boolean onGoing) {
        //PREVENT RANDOM KILLS
        NotificationManager notificationManger =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (onGoing) {
            Bitmap bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), iconId),
                    context.getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_width),
                    context.getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_height),
                    true);
            Intent intent = new Intent(context, MapsActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 01, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder = new Notification.Builder(context);
            builder.setContentTitle(title);
            builder.setContentText(text);
            builder.setContentIntent(pendingIntent);
            builder.setSmallIcon(iconId);
            builder.setLargeIcon(bm);
            builder.setPriority(Notification.PRIORITY_HIGH);
            builder.setOngoing(onGoing);
            builder.setAutoCancel(false);
            Notification notification = builder.build();
            notificationManger.notify(01, notification);
        }
        else{
            notificationManger.cancel(01);
        }
    }//END makeNotification()


}
