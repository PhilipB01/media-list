/*
 * Copyright (C) 2014 The Android Open Source Project
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
package com.discflux.app.mymedialist.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;


public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MediaDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Media
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.
        Note that this only tests that the Media table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MediaContract.MediaEntry.TABLE_NAME);

        mContext.deleteDatabase(MediaDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MediaDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MediaContract.MediaEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> mediaColumnHashSet = new HashSet<String>();
        mediaColumnHashSet.add(MediaContract.MediaEntry._ID);
        mediaColumnHashSet.add(MediaContract.MediaEntry.COLUMN_AUTHOR);
        mediaColumnHashSet.add(MediaContract.MediaEntry.COLUMN_COMPLETION);
        mediaColumnHashSet.add(MediaContract.MediaEntry.COLUMN_DATE);
        mediaColumnHashSet.add(MediaContract.MediaEntry.COLUMN_DESCRIPTION);
        mediaColumnHashSet.add(MediaContract.MediaEntry.COLUMN_RATING);
        mediaColumnHashSet.add(MediaContract.MediaEntry.COLUMN_TITLE);
        mediaColumnHashSet.add(MediaContract.MediaEntry.COLUMN_TYPE);
        mediaColumnHashSet.add(MediaContract.MediaEntry.COLUMN_URL);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            mediaColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required media
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required media entry columns",
                mediaColumnHashSet.isEmpty());
        db.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createMediaValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testMediaTable() {
        // First insert the media, and then use the mediaRowId to insert
        // the media item. Make sure to cover as many failure cases as you can.
        //long mediaRowId = insertMedia();
        // Instead of rewriting all of the code we've already written in testMediaTable
        // we can move this code to insertMedia and then call insertMedia from both
        // tests. Why move it? We need the code to return the ID of the inserted media
        // and our testMediaTable can only return void because it's a test.
        //assertTrue("Error: Could not insert media values into db", mediaRowId!=-1);

        // First step: Get reference to writable database
        SQLiteDatabase db = new MediaDbHelper(this.mContext).getWritableDatabase();

        // Create ContentValues of what you want to insert
        // (you can use the createMediaValues TestUtilities function if you wish)
        ContentValues testValues = com.discflux.app.mymedialist.data.TestUtilities.createMediaValues();

        // Insert ContentValues into database and get a row ID back
        db.insert(MediaContract.MediaEntry.TABLE_NAME, null, testValues);

        // Query the database and receive a Cursor back
        Cursor cursor = db.query(MediaContract.MediaEntry.TABLE_NAME, null, null, null, null, null, null);

        // Move the cursor to a valid database row
        assertTrue("Error: No valid entries returned from media table", cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        com.discflux.app.mymedialist.data.TestUtilities.validateCurrentRecord("Database media values do not match expected values", cursor, testValues);

        // Finally, close the cursor and database
        cursor.close();
        db.close();
    }

}