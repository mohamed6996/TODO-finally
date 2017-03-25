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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.todolist.data.TaskContract;
import com.facebook.stetho.Stetho;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, ListItemClickListner {

    // Constants for logging and referring to a unique loader
    private static final int TASK_LOADER_ID = 0;

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

    TextView empty;
    ImageView emptyView;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      //  empty = (TextView) findViewById(R.id.emptyview);
        emptyView = (ImageView) findViewById(R.id.emptyview);


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
                startActivity(addTaskIntent);
            }
        });

        Stetho.initializeWithDefaults(getApplicationContext());
        pending = new ArrayList();

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        toolbar.setTitle("Checked");

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
        pending.clear();
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);

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
       //   Toast.makeText(MainActivity.this,""+ data.getCount(),Toast.LENGTH_LONG).show();
        if (data.getCount() > 0) {
            emptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

        }
        else {
            mRecyclerView.setVisibility(View.GONE);

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

