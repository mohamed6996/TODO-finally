/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.todolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.todolist.data.TaskContract;

import java.util.HashMap;

import static com.example.android.todolist.MainActivity.pending;


public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.TaskViewHolder> {

    final private ListItemClickListner mOnClickListner;

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<Integer, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be


    private Cursor mCursor;
    private Context mContext;

    String title, description;
    int id;
    long picked_hour;


    public CustomCursorAdapter(Context mContext, ListItemClickListner listner) {
        this.mContext = mContext;
        this.mOnClickListner = listner;
    }


    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.task_layout, parent, false);

        return new TaskViewHolder(view);
    }


    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {

        // Indices for the _id, description, and priority columns
        int idIndex = mCursor.getColumnIndex(TaskContract.TaskEntry._ID);
        int titleIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TITLE);
        int descriptionIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION);
        int timeIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TIME);


        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        id = mCursor.getInt(idIndex);
        title = mCursor.getString(titleIndex);
        description = mCursor.getString(descriptionIndex);
        picked_hour = mCursor.getLong(timeIndex);


        if (pending.contains(id)) {

            holder.regularLayout.setVisibility(View.GONE);
            holder.swipeLayout.setVisibility(View.VISIBLE);

        } else {
            holder.regularLayout.setVisibility(View.VISIBLE);
            holder.swipeLayout.setVisibility(View.GONE);
            holder.taskDescriptionView.setText(title + description);
        }

        //Set values
        holder.itemView.setTag(id);
        holder.taskDescriptionView.setText(title + description);


        long time = System.currentTimeMillis();

        if (picked_hour >= time) {

            setAlarm(picked_hour);
        }


    }

    private void setAlarm(long time) {


        Intent intent = new Intent(mContext, AlarmToastReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        String stringId = Integer.toString(id);
        Uri uri = TaskContract.TaskEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();
        intent.setData(uri);

        final int _id = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, _id, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(mContext.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC, time, pendingIntent);


    }


    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }


    public void pendingRemoval(final int id) {

        //   notifyItemChanged(id);

        Runnable pendingRemovalRunnable = new Runnable() {
            @Override
            public void run() {
                remove(id);

            }
        };
        handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT); // remove item after 3 seconds
        pendingRunnables.put(id, pendingRemovalRunnable);


    }

    public void remove(int id) {


        String stringId = Integer.toString(id);
        Uri uri = TaskContract.TaskEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();
        mContext.getContentResolver().delete(uri, null, null);


    }


    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


    // Inner class for creating ViewHolders
    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public LinearLayout regularLayout;
        public LinearLayout swipeLayout;

        TextView taskDescriptionView;
        //  TextView priorityView;


        public TaskViewHolder(View itemView) {
            super(itemView);
            regularLayout = (LinearLayout) itemView.findViewById(R.id.regularLayout);
            swipeLayout = (LinearLayout) itemView.findViewById(R.id.swipeLayout);
            taskDescriptionView = (TextView) itemView.findViewById(R.id.taskDescription);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            mOnClickListner.onListItemClick(getAdapterPosition(), (Integer) itemView.getTag());

        }
    }
}