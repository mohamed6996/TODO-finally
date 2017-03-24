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

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.LoaderManager;


import com.example.android.todolist.data.TaskContract;

import com.thebluealliance.spectrum.SpectrumPalette;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import cn.refactor.lib.colordialog.PromptDialog;


public class AddTaskActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener, LoaderManager.LoaderCallbacks<Cursor>, SpectrumPalette.OnColorSelectedListener {

    ContentValues contentValues;
    FloatingActionButton fabTime;
    //   Button add_btn;
    FloatingActionButton add_btn, delete_btn;

    EditText edt_title, edt_description;
    TextView formated_time;
    int year, monthOfYear, dayOfMonth;
    int hourOfDay, minute, second;
    long time;
    int color_posiotion;
    int colorPositionIndex;

    Uri mCurrentUri;
    boolean isTaskUri;
    boolean isColorPicked;
    int id;

    private Animation mScaleAnim;

    Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        SpectrumPalette spectrumPalette = (SpectrumPalette) findViewById(R.id.palette);
        spectrumPalette.setOnColorSelectedListener(this);



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        add_btn = (FloatingActionButton) findViewById(R.id.addButton);
        delete_btn = (FloatingActionButton) findViewById(R.id.fab_delete);
        formated_time = (TextView) findViewById(R.id.pickedTime);
        edt_title = (EditText) findViewById(R.id.editTextTaskDescription);
        edt_description = (EditText) findViewById(R.id.TaskDescription);


        contentValues = new ContentValues();



        //   /tasks/1
        //  /tasks

        delete_btn.setVisibility(View.INVISIBLE);


        Intent intent = getIntent();
        mCurrentUri = intent.getData();


        if (mCurrentUri == null) {
            setTitle("add task");
            //  Toast.makeText(AddTaskActivity.this, "" + mCurrentUri, Toast.LENGTH_LONG).show();
            add_btn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_send));

        } else {
            setTitle("update task");
            add_btn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_done_black_24dp));
            delete_btn.setVisibility(View.VISIBLE);


            int row = Integer.valueOf(mCurrentUri.getLastPathSegment());
            String match = TaskContract.TaskEntry.CONTENT_URI.getPath() + "/" + row;


            if (mCurrentUri.getPath().equals(match)) {
                isTaskUri = true;
                getSupportLoaderManager().initLoader(0, null, this);

            } else {
                getSupportLoaderManager().initLoader(1, null, this);

            }


        }


        fabTime = (FloatingActionButton) findViewById(R.id.time_picked);
        fabTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddTaskActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                dpd.setVersion(DatePickerDialog.Version.VERSION_1);
                dpd.setThemeDark(true);
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentUri == null) {
                    onClickAddTask();

                } else {
                    updateTask();
                    sendBroadcast(new Intent("ACTION_DATA_UPDATED"));
                    finish();
                }
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // notify app widget to be updated
                sendBroadcast(new Intent("ACTION_DATA_UPDATED"));

                String stringId = Integer.toString(id);
                Uri uri = TaskContract.TaskEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();
                getContentResolver().delete(uri, null, null);

                finish();
            }
        });


    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_TITLE,
                TaskContract.TaskEntry.COLUMN_DESCRIPTION,
                TaskContract.TaskEntry.COLUMN_TIME

        };

        return new android.support.v4.content.CursorLoader(this, mCurrentUri,
                null,
                null, null, null);

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update the data that the adapter uses to create ViewHolders
        // mAdapter.swapCursor(data);

        String title, description;
        int titleIndex = 0, descriptionIndex = 0, idIndex;
        if (cursor.moveToFirst()) {


            if (isTaskUri) {
                idIndex = cursor.getColumnIndex(TaskContract.TaskEntry._ID);
                titleIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TITLE);
                descriptionIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION);
                colorPositionIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_COLOR_POSITION);

            } else {
                idIndex = cursor.getColumnIndex(TaskContract.TaskArchiveEntry._ID);
                titleIndex = cursor.getColumnIndex(TaskContract.TaskArchiveEntry.COLUMN_TITLE_ARCHIVE);
                descriptionIndex = cursor.getColumnIndex(TaskContract.TaskArchiveEntry.COLUMN_DESCRIPTION_ARCHIVE);
            }


            // Determine the values of the wanted data
            id = cursor.getInt(idIndex);
            title = cursor.getString(titleIndex);
            description = cursor.getString(descriptionIndex);
            int color_pos = cursor.getInt(colorPositionIndex);

            selectColor(color_pos);


            edt_title.setText(title);
            edt_description.setText(description);
            //    Toast.makeText(AddTaskActivity.this, "" + title + description, Toast.LENGTH_LONG).show();
        }


    }



    public void selectColor(int position) {
        int[] rainbow = this.getResources().getIntArray(R.array.rainbow);
        if (position == -12627531){
            toolbar.setBackgroundColor(rainbow[0]);
        }
        if (position == -4251852){
            toolbar.setBackgroundColor(rainbow[1]);
        }
        if (position == -10000274){
            toolbar.setBackgroundColor(rainbow[2]);
        }
        if (position == -6324577){
            toolbar.setBackgroundColor(rainbow[3]);
        }
        if (position == -14899982){
            toolbar.setBackgroundColor(rainbow[4]);
        }
        if (position == -12547018){
            toolbar.setBackgroundColor(rainbow[5]);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // mAdapter.swapCursor(null);
        edt_title.setText("");
        edt_description.setText("");

    }


    public void onClickAddTask() {

        String input = edt_title.getText().toString();
        if (input.length() == 0) {
            new PromptDialog(this)
                    .setDialogType(PromptDialog.DIALOG_TYPE_WRONG)
                    .setAnimationEnable(true)
                    .setTitleText("Warning")
                    .setContentText("You must enter a title")
                    .setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
                        @Override
                        public void onClick(PromptDialog dialog) {
                            dialog.dismiss();
                         //   return;
                        }
                    }).show();

            return;
        }


        String description_input = edt_description.getText().toString();
      /*  if (input.length() == 0) {
            return;
        }*/


        // Put the task description and selected mPriority into the ContentValues
        contentValues.put(TaskContract.TaskEntry.COLUMN_TITLE, input);
        contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, description_input);
        contentValues.put(TaskContract.TaskEntry.COLUMN_TIME, time);
        if (isColorPicked) {
            contentValues.put(TaskContract.TaskEntry.COLUMN_COLOR_POSITION, color_posiotion);
        }

        Uri uri = getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues);

        archiveData(input, description_input);

        if (uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }

        sendBroadcast(new Intent("ACTION_DATA_UPDATED"));
       finish();

    }

    public void archiveData(String title, String description) {
        ContentValues contentValues = new ContentValues();

        // Put the task description and selected mPriority into the ContentValues
        contentValues.put(TaskContract.TaskArchiveEntry.COLUMN_TITLE_ARCHIVE, title);
        contentValues.put(TaskContract.TaskArchiveEntry.COLUMN_DESCRIPTION_ARCHIVE, description);

        getContentResolver().insert(TaskContract.TaskArchiveEntry.CONTENT_URI, contentValues);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.update:
                //  updateTask();
                //  finish();
                Intent intent = new Intent(this, Archive.class);
                startActivity(intent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void updateTask() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String title_str = edt_title.getText().toString();
        String description_str = edt_description.getText().toString();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_TITLE, title_str);
        values.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, description_str);


        // Determine if this is a new or existing pet by checking if mCurrentPetUri is null or not
        if (mCurrentUri == null) {
            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "successful",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "successful",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;

        time_picker();

    }

    public void time_picker() {
        Calendar n = Calendar.getInstance();
        TimePickerDialog t = TimePickerDialog.newInstance(AddTaskActivity.this, n.get(Calendar.HOUR_OF_DAY),
                n.get(Calendar.MINUTE),
                true);
        t.show(getFragmentManager(), "timepickerdialog");
        t.setVersion(TimePickerDialog.Version.VERSION_1);
        t.setThemeDark(true);

    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.second = second;

        setCalender();

    }

    public void setCalender() {
        Calendar curunt_calender = Calendar.getInstance();
        curunt_calender.setTimeInMillis(System.currentTimeMillis());

        final Calendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        cal.set(Calendar.HOUR_OF_DAY, this.hourOfDay);
        cal.set(Calendar.MINUTE, this.minute);
        cal.set(Calendar.SECOND, this.second);

        time = cal.getTimeInMillis();
        SimpleDateFormat format1 = new SimpleDateFormat("E h:mm  a");
        formated_time.setText(format1.format(cal.getTime()));


    }



    @Override
    public void onColorSelected(@ColorInt int color) {
     //   Toast.makeText(this, "" + color, Toast.LENGTH_LONG).show();

        this.color_posiotion = color;

        isColorPicked = true;
    }
}
