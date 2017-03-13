package com.example.android.todolist;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class CheckedReceiver extends BroadcastReceiver {

    Uri uri;

    @Override
    public void onReceive(Context context, Intent intent) {

        uri = AlarmToastReceiver.mCurrentUri;
        context.getContentResolver().delete(uri, null, null);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }
}
