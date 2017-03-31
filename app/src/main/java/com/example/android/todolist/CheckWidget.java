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



            widgetView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            Intent Main_intent = new Intent(context, MainActivity.class);
            PendingIntent Main_pendingIntent = PendingIntent.getActivity(context, 0, Main_intent, 0);
            widgetView.setOnClickPendingIntent(R.id.widget_relative, Main_pendingIntent);

            Intent ic_add_intent = new Intent(context, AddTaskActivity.class);
            PendingIntent add_pendingIntent = PendingIntent.getActivity(context, 0, ic_add_intent, 0);
            widgetView.setOnClickPendingIntent(R.id.ic_add, add_pendingIntent);

            Intent ic_history_intent = new Intent(context, Archive.class);
            PendingIntent history_pendingIntent = PendingIntent.getActivity(context, 0, ic_history_intent, 0);
            widgetView.setOnClickPendingIntent(R.id.ic_history, history_pendingIntent);




            // Set up the collection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, widgetView);
            } else {
                setRemoteAdapterV11(context, widgetView);
            }


            Intent clickIntentTemplate = new Intent(context, AddTaskActivity.class);

            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            widgetView.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
            widgetView.setEmptyView(R.id.widget_list, R.id.widget_empty);

            appWidgetManager.updateAppWidget(appWidgetId, widgetView);

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);


        Toast.makeText(context, intent.getAction(), Toast.LENGTH_LONG).show();

        if (intent.getAction().equals("ACTION_DATA_UPDATED")) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, CheckWidget.class));


            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);

        }


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
