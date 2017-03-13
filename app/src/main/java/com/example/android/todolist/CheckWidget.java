package com.example.android.todolist;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Toast;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class CheckWidget extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Intent intent1 = new Intent(context, AddTaskActivity.class);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(intent1);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, FLAG_UPDATE_CURRENT);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            Toast.makeText(context, "failed" , Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int index = 0; index < appWidgetIds.length; index++) {
            // There may be multiple widgets active, so update all of them

            int appWidgetId = appWidgetIds[index];

            Intent widgetIntent = new Intent(context, CheckWidget.class);
            widgetIntent.setData(AlarmToastReceiver.mCurrentUri);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, widgetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            widgetView.setOnClickPendingIntent(R.id.img, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, widgetView);


        }
    }
}
