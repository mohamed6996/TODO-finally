package com.example.android.todolist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import com.example.android.todolist.data.TaskContract;

import java.text.SimpleDateFormat;


/**
 * Created by lenovo on 3/14/2017.
 */


public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    Context mContext;
    Intent intent;

    private Cursor mCursor;

    public WidgetDataProvider(Context context, Intent intent) {
        this.mContext = context;
        this.intent = intent;
    }


    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {

        if (mCursor != null) {
            mCursor.close();
        }
        final long identityToken = Binder.clearCallingIdentity();

        mCursor = mContext.getContentResolver().query(
                TaskContract.TaskEntry.CONTENT_URI,
                null, null, null, null);

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_row_item);


        int idIndex = mCursor.getColumnIndex(TaskContract.TaskEntry._ID);
        int titleIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TITLE);
        int descriptionIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION);
        int timeIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TIME);


        mCursor.moveToPosition(position); // get to the right location in the mCursor


        int id = mCursor.getInt(idIndex);
        String title = mCursor.getString(titleIndex);
        String description = mCursor.getString(descriptionIndex);
        long picked_hour = mCursor.getLong(timeIndex);

        view.setTextViewText(R.id.widget_title_id, title);
        view.setTextViewText(R.id.widget_content_id, description);

        if (picked_hour != 0) {
            SimpleDateFormat format1 = new SimpleDateFormat("E h:mm  a");
            String formated_time = format1.format(picked_hour);
            view.setTextViewText(R.id.widget_time_chosen, formated_time);
        }


        final Intent fillInIntent = new Intent();

        String stringId = Integer.toString(id);
        Uri uri = TaskContract.TaskEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();
        fillInIntent.setData(uri);
        view.setOnClickFillInIntent(R.id.widget_row_item, fillInIntent);
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.widget_row_item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (mCursor.moveToPosition(position))
            return mCursor.getLong(0);
        return position;
        // return 0;  also true
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
