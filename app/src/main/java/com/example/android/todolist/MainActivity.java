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


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.example.android.todolist.data.TaskContract;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, ListItemClickListner {

    // Constants for logging and referring to a unique loader
    private static final int TASK_LOADER_ID = 0;

    public static boolean isVibrate;
    public static boolean isCount;
    public static String alert;
    // Member variables for the adapter and RecyclerView
    private CustomCursorAdapter mAdapter;
    //  private RecyclerViewAdapter mAdapter;

    RecyclerView mRecyclerView;
    LinearLayoutManager linearLayoutManager;

    int id;
    public static List pending;
    Toolbar toolbar;

    private FloatingActionMenu menuRed;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;

    TextView emptyView;
    Cursor cursor;

    TextView count;
    Typeface courgette;

    private String[] emptyTexts = {
            "No things is good things.",
            "Everything is done.",
            "Genius only means hard-working all one’s life.",
            "Reading makes a full man, conference a ready man, and writing an exact man.",
            "And those who were seen dancing were thought to be insane by those who could not hear the music.",
            "The only limit to our realization of tomorrow will be our doubts of today.	",
            "There is no doubt that good things will come, and when it comes, it will be a surprise. ",
            "Reality is merely an illusion, albeit a very persistent one.",
            "The first and greatest victory is to conquer yourself, to be conquered by yourself is of all things most " +
                    "shameful and vile.",
            "A pessimist sees the difficulty in every opportunity, an optimist sees the opportunity in every difficulty.",
            "There is nothing noble in being superior to some other man. The true nobility is in being superior to your " +
                    "previous self. ",
            "A man is not old as long as he is seeking something. A man is not old until regrets take the place of dreams.",
            "I was not looking for my dreams to interpret my life, but rather for my life to interpret my dreams. "
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        courgette = Typeface.createFromAsset(getAssets(), "Courgette-Regular.ttf");

        emptyView = (TextView) findViewById(R.id.emptyview);

        menuRed = (FloatingActionMenu) findViewById(R.id.menu);
        fab1 = (FloatingActionButton) findViewById(R.id.menu_item);
        fab2 = (FloatingActionButton) findViewById(R.id.menu_item_2);

        menuRed.setClosedOnTouchOutside(true);


        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  Toast.makeText(MainActivity.this,"hi",Toast.LENGTH_LONG).show();
                menuRed.close(true);
                Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(addTaskIntent);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuRed.close(true);
                Intent addTaskIntent = new Intent(MainActivity.this, Archive.class);
                addTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(addTaskIntent);
            }
        });

        pending = new ArrayList();

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setTypeface(courgette);

        toolbar.inflateMenu(R.menu.menu_settings);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(MainActivity.this, Settings.class));
                return true;
            }
        });


        count = (TextView) findViewById(R.id.count);


        // Set the RecyclerView to its corresponding view
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewTasks);
        linearLayoutManager = new LinearLayoutManager(this);


        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new CustomCursorAdapter(this, this);

        mRecyclerView.setAdapter(mAdapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                id = (int) viewHolder.itemView.getTag();


                final CustomCursorAdapter adapter = (CustomCursorAdapter) mRecyclerView.getAdapter();
                //   int position = viewHolder.getAdapterPosition();

                if (!pending.contains(id)) {
                    pending.add(id);
                    adapter.pendingRemoval(id);
                } else {
                    //  pending.remove(id);

                }

                getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, MainActivity.this);

            }

        }).attachToRecyclerView(mRecyclerView);

        mRecyclerView.addItemDecoration(new SpaceItemDecoration(7));


        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // re-queries for all tasks
        SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        MainActivity.alert = sharedPreferences.getString("notifications_new_message_ringtone", "");

        MainActivity.isVibrate = sharedPreferences.getBoolean("notifications_new_message_vibrate", true);
        MainActivity.isCount = sharedPreferences.getBoolean("task_count", true);


        pending.clear();
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }



    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_TITLE,
                TaskContract.TaskEntry.COLUMN_DESCRIPTION,
                TaskContract.TaskEntry.COLUMN_TIME,
                TaskContract.TaskEntry.COLUMN_COLOR_POSITION

        };

        return new android.support.v4.content.CursorLoader(this, TaskContract.TaskEntry.CONTENT_URI,
                projection,
                null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        this.cursor = data;

        if (isCount) {
            count.setVisibility(View.VISIBLE);
            count.setText("" + data.getCount());
        } else {
            count.setVisibility(View.GONE);
        }

        if (data.getCount() > 0) {
            emptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

        } else {
            mRecyclerView.setVisibility(View.GONE);
            String random =emptyTexts[ new Random().nextInt(emptyTexts.length)] ;
            emptyView.setText(random);
            emptyView.setTypeface(courgette);
            emptyView.setVisibility(View.VISIBLE);
        }
        mAdapter.swapCursor(data);

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(int position, int id) {

        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        String stringId = Integer.toString(id);
        Uri uri = TaskContract.TaskEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();
        intent.setData(uri);
        startActivity(intent);

    }


}

