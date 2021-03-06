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

package com.example.android.todolist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.todolist.data.TaskContract.TaskEntry;
import com.example.android.todolist.data.TaskContract.TaskArchiveEntry;


public class TaskDbHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "tasksDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 2;


    // Constructor
    TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
                TaskEntry._ID + " INTEGER PRIMARY KEY, " +
                TaskEntry.COLUMN_TITLE + " TEXT, " +
                TaskEntry.COLUMN_DESCRIPTION + " TEXT, " +
                TaskEntry.COLUMN_TIME + " INTEGER, " +
                TaskEntry.COLUMN_COLOR_POSITION + " INTEGER ); ";

        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE_ARCHIVE = "CREATE TABLE " + TaskArchiveEntry.TABLE_NAME + " (" +
                TaskArchiveEntry._ID + " INTEGER PRIMARY KEY, " +
                TaskArchiveEntry.COLUMN_TITLE_ARCHIVE + " TEXT, " +
                TaskArchiveEntry.COLUMN_DESCRIPTION_ARCHIVE + " TEXT );";



        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_ARCHIVE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TaskArchiveEntry.TABLE_NAME);

        onCreate(db);
    }
}
