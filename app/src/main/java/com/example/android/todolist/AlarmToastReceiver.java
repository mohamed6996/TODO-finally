package com.example.android.todolist;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.Date;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Created by reale on 24/11/2016.
 */

public class AlarmToastReceiver extends BroadcastReceiver {

    static Uri mCurrentUri;


    @Override
    public void onReceive(Context context, Intent intent) {
        // Toast.makeText(context,"THIS IS MY ALARM", Toast.LENGTH_LONG).show();

        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        mCurrentUri = intent.getData();


        Intent intent1 = new Intent(context, AddTaskActivity.class);
        intent1.setData(mCurrentUri);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(intent1);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, FLAG_UPDATE_CURRENT);

        Intent checkedIntent = new Intent(context, CheckedReceiver.class);
        PendingIntent checkedPendingIntent = PendingIntent.getBroadcast(context, 0, checkedIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);


        builder.setAutoCancel(true)  // goes away when clicked
                .setWhen(System.currentTimeMillis())  // arrange them
                .setSmallIcon(R.mipmap.ic_launcher_web)
                .setContentTitle(title)
                .setContentText(description)
                .setTicker(title)
                .setContentIntent(pendingIntent);

        if (MainActivity.isVibrate) {
            builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
            if (MainActivity.alert != null) {
                builder.setSound(Uri.parse(MainActivity.alert));
            } else {
                builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);

            }

        } else {
            builder.setDefaults(Notification.DEFAULT_LIGHTS);

            if (MainActivity.alert != null) {
                builder.setSound(Uri.parse(MainActivity.alert));
            } else {
                builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
            }
        }


        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title)
                .bigText(description);

        builder.setStyle(bigTextStyle);

        int m = (int) (new Date().getTime() / 1000L % Integer.MAX_VALUE);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(m, builder.build());


    }


}
