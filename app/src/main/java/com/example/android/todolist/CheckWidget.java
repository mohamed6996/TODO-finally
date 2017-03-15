package com.example.android.todolist;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Toast;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class CheckWidget extends AppWidgetProvider {

    RemoteViews widgetView;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int index = 0; index < appWidgetIds.length; index++) {
            // There may be multiple widgets active, so update all of them

            int appWidgetId = appWidgetIds[index];

        /*    Intent widgetIntent = new Intent(context, CheckWidget.class);
            widgetIntent.setData(AlarmToastReceiver.mCurrentUri);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, widgetIntent, PendingIntent.FLAG_UPDATE_CURRENT);*/

            widgetView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            // Set up the collection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, widgetView);
            } else {
                setRemoteAdapterV11(context, widgetView);
            }

            //     widgetView.setOnClickPendingIntent(R.id.img, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, widgetView);
            //      appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, android.R.layout.simple_list_item_1);

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

     /*   Intent intent1 = new Intent(context, AddTaskActivity.class);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(intent1);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, FLAG_UPDATE_CURRENT);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            Toast.makeText(context, "failed" , Toast.LENGTH_LONG).show();
        }*/
        //   if (intent.getAction().equalsIgnoreCase("ACTION_DATA_UPDATED")) {
        Toast.makeText(context, intent.getAction(), Toast.LENGTH_LONG).show();

        if (intent.getAction().equals("ACTION_DATA_UPDATED")) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, CheckWidget.class));

            //  appWidgetManager.updateAppWidget(appWidgetIds, widgetView );

            //  appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, android.R.layout.simple_list_item_1);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);

        }
        //  }
        //  onUpdate(context,appWidgetManager,appWidgetIds);
     /*
        for (int index = 0; index < appWidgetIds.length; index++) {

            int appWidgetId = appWidgetIds[index];
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, android.R.layout.simple_list_item_1);
            onUpdate(context,appWidgetManager,appWidgetIds);

        }*/

    }


    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, WidgetService.class));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private static void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_list,
                new Intent(context, WidgetService.class));
    }
}
