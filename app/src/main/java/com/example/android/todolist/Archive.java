package com.example.android.todolist;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.todolist.data.TaskContract;

import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;

public class Archive extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, ListItemClickListner {

    // Constants for logging and referring to a unique loader
    private static final int ARCHIVE_LOADER_ID = 1;

    // Member variables for the adapter and RecyclerView
    private ArchiveAdapter archiveAdapter;

    RecyclerView recyclerViewArchive;
    LinearLayoutManager linearLayoutManager;

    Snackbar mSnackbar;
    Cursor mCursor;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        toolbar = (Toolbar) findViewById(R.id.archive_toolbar);
        toolbar.setTitle("Archive");

        toolbar.inflateMenu(R.menu.menu_editor);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    // Respond to a click on the "Insert dummy data" menu option
                    case R.id.update:

                        if (mCursor.getCount() == 0) {
                            new PromptDialog(Archive.this)
                                    .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                                    .setAnimationEnable(true)
                                    .setTitleText("")
                                    .setContentText("Archive is already empty ☺")
                                    .setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
                                        @Override
                                        public void onClick(PromptDialog dialog) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        } else {

                            ColorDialog colorDialog = new ColorDialog(Archive.this);
                            colorDialog.setTitle("delete all archive !");
                            colorDialog.setAnimationEnable(true);
                            colorDialog.setPositiveListener("delete", new ColorDialog.OnPositiveListener() {
                                @Override
                                public void onClick(ColorDialog colorDialog) {
                                    getContentResolver().delete(TaskContract.TaskArchiveEntry.CONTENT_URI, null, null);
                                    mSnackbar = Snackbar.make(recyclerViewArchive, "all tasks deleted", Snackbar.LENGTH_LONG);
                                    mSnackbar.show();
                                    colorDialog.dismiss();

                                }
                            });

                            colorDialog.setNegativeListener("cancel", new ColorDialog.OnNegativeListener() {
                                @Override
                                public void onClick(ColorDialog colorDialog) {
                                    colorDialog.dismiss();

                                }
                            });

                            colorDialog.show();


                            return true;
                        }

                }
                return true;

            }
        });

        recyclerViewArchive = (RecyclerView) findViewById(R.id.recyclerviewarchive);
        linearLayoutManager = new LinearLayoutManager(this);

        recyclerViewArchive.setLayoutManager(linearLayoutManager);

        archiveAdapter = new ArchiveAdapter(this, this);
        // mAdapter = new RecyclerViewAdapter(this, this);
        recyclerViewArchive.setAdapter(archiveAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                int id = (int) viewHolder.itemView.getTag();


                String stringId = Integer.toString(id);
                Uri uri = TaskContract.TaskArchiveEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();
                getContentResolver().delete(uri, null, null);

                if (mSnackbar != null) {
                    mSnackbar.dismiss();
                }
                mSnackbar = Snackbar.make(viewHolder.itemView, "deleted", Snackbar.LENGTH_SHORT);
                mSnackbar.show();


                getSupportLoaderManager().restartLoader(ARCHIVE_LOADER_ID, null, Archive.this);

            }
        }).attachToRecyclerView(recyclerViewArchive);

        // for RV decoration
        // add divider between items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewArchive.getContext(),
                linearLayoutManager.getOrientation());
        recyclerViewArchive.addItemDecoration(dividerItemDecoration);

        recyclerViewArchive.addItemDecoration(new SpaceItemDecoration(5));


        getSupportLoaderManager().initLoader(ARCHIVE_LOADER_ID, null, this);

    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                TaskContract.TaskArchiveEntry._ID,
                TaskContract.TaskArchiveEntry.COLUMN_TITLE_ARCHIVE,
                TaskContract.TaskArchiveEntry.COLUMN_DESCRIPTION_ARCHIVE
        };

        return new android.support.v4.content.CursorLoader(this, TaskContract.TaskArchiveEntry.CONTENT_URI,
                projection,
                null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        this.mCursor = data;
        archiveAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        archiveAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(int position, int id) {
        Intent intent = new Intent(Archive.this, AddTaskActivity.class);
        intent.putExtra("FROM_ARCHIVE", true);
        String stringId = Integer.toString(id);
        Uri uri = TaskContract.TaskArchiveEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();
        intent.setData(uri);
        startActivity(intent);
    }

}
