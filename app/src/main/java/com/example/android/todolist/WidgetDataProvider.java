package com.example.android.todolist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.example.android.todolist.data.TaskContract;

import java.util.ArrayList;
import java.util.List;

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
                android.R.layout.simple_list_item_1);

        int titleIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TITLE);
        int descriptionIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION);


        mCursor.moveToPosition(position); // get to the right location in the mCursor


        String title = mCursor.getString(titleIndex);
        String description = mCursor.getString(descriptionIndex);

        view.setTextViewText(android.R.id.text1, title);
        view.setTextColor(android.R.id.text1, Color.BLUE);
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), android.R.layout.simple_list_item_1);
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
