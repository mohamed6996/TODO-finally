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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.support.v4.app.LoaderManager;
import android.widget.Toolbar;


import com.example.android.todolist.data.TaskContract;
import com.ribell.colorpickerview.ColorPickerView;
import com.ribell.colorpickerview.interfaces.ColorPickerViewListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class AddTaskActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener, LoaderManager.LoaderCallbacks<Cursor>, ColorPickerViewListener {

    ContentValues contentValues;
    FloatingActionButton fabTime;
 //   Button add_btn;
      FloatingActionButton add_btn;

    EditText edt_title, edt_description;
    int year, monthOfYear, dayOfMonth;
    int hourOfDay, minute, second;
    long time;
    int color_posiotion;

    Uri mCurrentUri;
    boolean isTaskUri;
    boolean isColorPicked;
    ColorPickerView colorPickerView;

    Toolbar toolbar ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
      //  toolbar = (Toolbar) findViewById(R.id.toolbar);

        contentValues = new ContentValues();

        colorPickerView = (ColorPickerView) findViewById(R.id.gridview);
        colorPickerView.setListener(this);

        //  colorPickerView.setBorderColor(getResources().getColor(R.color.mdtp_red));
        colorPickerView.setBorderColorSelected(getResources().getColor(R.color.border_selected));

        edt_title = (EditText) findViewById(R.id.editTextTaskDescription);
        edt_description = (EditText) findViewById(R.id.TaskDescription);

        //   /tasks/1
        //  /tasks

        Intent intent = getIntent();
        mCurrentUri = intent.getData();


        if (mCurrentUri == null) {
            setTitle("add task");
            //  Toast.makeText(AddTaskActivity.this, "" + mCurrentUri, Toast.LENGTH_LONG).show();

        } else {
            setTitle("update task");

            int row = Integer.valueOf(mCurrentUri.getLastPathSegment());
            String match = TaskContract.TaskEntry.CONTENT_URI.getPath() + "/" + row;


            if (mCurrentUri.getPath().equals(match)) {
                isTaskUri = true;
                getSupportLoaderManager().initLoader(0, null, this);

            } else {
                getSupportLoaderManager().initLoader(1, null, this);

            }


        }


        add_btn = (FloatingActionButton) findViewById(R.id.addButton);

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
                onClickAddTask();
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
        int titleIndex = 0, descriptionIndex = 0;
        if (cursor.moveToFirst()) {

            if (isTaskUri) {
                titleIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TITLE);
                descriptionIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION);
            } else {
                titleIndex = cursor.getColumnIndex(TaskContract.TaskArchiveEntry.COLUMN_TITLE_ARCHIVE);
                descriptionIndex = cursor.getColumnIndex(TaskContract.TaskArchiveEntry.COLUMN_DESCRIPTION_ARCHIVE);
            }


            // Determine the values of the wanted data
            title = cursor.getString(titleIndex);
            description = cursor.getString(descriptionIndex);

            edt_title.setText(title);
            edt_description.setText(description);
            Toast.makeText(AddTaskActivity.this, "" + title + description, Toast.LENGTH_LONG).show();
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
      /*  if (input.length() == 0) {
            return;
        }*/


        String description_input = edt_description.getText().toString();
      /*  if (input.length() == 0) {
            return;
        }*/


        // Put the task description and selected mPriority into the ContentValues
        contentValues.put(TaskContract.TaskEntry.COLUMN_TITLE, input);
        contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, description_input);
        contentValues.put(TaskContract.TaskEntry.COLUMN_TIME, time);
        if (isColorPicked) {
            contentValues.put(TaskContract.TaskEntry.COLUMN_COLOR_POSITION, color_posiotion + 1);
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
        values.put(TaskContract.TaskEntry.COLUMN_TIME, title_str);
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


    }


    @Override
    public void onColorPickerClick(int colorPosition) {
        this.color_posiotion = colorPosition;

        isColorPicked = true;
    }
}
