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

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.discflux.app.mymedialist.data.MediaContract.MediaEntry;

/*
    Note: This is not a complete set of tests of the Sunshine ContentProvider, but it does test
    that at least the basic functionality has been implemented correctly.
    Students: Uncomment the tests in this class as you implement the functionality in your
    ContentProvider to make sure that you've implemented things reasonably correctly.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.
       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
       the delete functionality in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MediaEntry.CONTENT_URI,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                MediaEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Media table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
       This helper function deletes all records from both database tables using the database
       functions only.  This is designed to be used to reset the state of the database until the
       delete functionality is available in the ContentProvider.
     */
    public void deleteAllRecordsFromDB() {
        MediaDbHelper dbHelper = new MediaDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(MediaEntry.TABLE_NAME, null, null);
        db.close();
    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
        Students: Uncomment this test to make sure you've correctly registered the MediaProvider.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // MediaProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MediaProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MediaProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MediaContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MediaContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MediaProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
            This test doesn't touch the database.  It verifies that the ContentProvider returns
            the correct type for each type of URI that it can handle.
            Students: Uncomment this test to verify that your implementation of GetType is
            functioning correctly.
         */
    public void testGetType() {
        // content://com.discflux.app.mymedialist/media/
        String type = mContext.getContentResolver().getType(MediaEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/media
        assertEquals("Error: the MediaEntry CONTENT_URI should return MediaEntry.CONTENT_TYPE",
                MediaEntry.CONTENT_TYPE, type);

        String title = "The Big Sub";
        int mediaType = 100;
        // content://com.example.android.sunshine.app/media/100/TheBigSub
        type = mContext.getContentResolver().getType(
                MediaEntry.buildMediaTitle(mediaType, title));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/media
        assertEquals("Error: the MediaEntry CONTENT_URI with type and title should return MediaEntry.CONTENT_ITEM_TYPE",
                MediaEntry.CONTENT_ITEM_TYPE, type);

        // content://com.example.android.sunshine.app/media/200
        type = mContext.getContentResolver().getType(
                MediaEntry.buildMediaType(mediaType));
        // vnd.android.cursor.item/com.example.android.sunshine.app/media/200
        assertEquals("Error: the MediaEntry CONTENT_URI with location and date should return MediaEntry.CONTENT_ITEM_TYPE",
                MediaEntry.CONTENT_TYPE, type);

    }


    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if the basic media query functionality
        given in the ContentProvider is working correctly.
     */
    public void testBasicMediaQuery() {
        // insert our test records into the database
        MediaDbHelper dbHelper = new MediaDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Fantastic.  Now that we have a location, add some media!
        ContentValues mediaValues = com.discflux.app.mymedialist.data.TestUtilities.createMediaValues();

        long mediaRowId = db.insert(MediaEntry.TABLE_NAME, null, mediaValues);
        assertTrue("Unable to Insert MediaEntry into the Database", mediaRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor mediaCursor = mContext.getContentResolver().query(
                MediaEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        com.discflux.app.mymedialist.data.TestUtilities.validateCursor("testBasicMediaQuery", mediaCursor, mediaValues);
    }

    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if your location queries are
        performing correctly.
     */
    public void testBasicMediaQueries() {
        // insert our test records into the database
        MediaDbHelper dbHelper = new MediaDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = com.discflux.app.mymedialist.data.TestUtilities.createMediaValues();
        long locationRowId = com.discflux.app.mymedialist.data.TestUtilities.insertMedia(mContext);

        // Test the basic content provider query
        Cursor mediaCursor = mContext.getContentResolver().query(
                MediaEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        com.discflux.app.mymedialist.data.TestUtilities.validateCursor("testBasicMediaQueries, media query", mediaCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Location Query did not properly set NotificationUri",
                    mediaCursor.getNotificationUri(), MediaEntry.CONTENT_URI);
        }
    }

    /*
        This test uses the provider to insert and then update the data. Uncomment this test to
        see if your update location is functioning correctly.
     */
    public void testUpdateMedia() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createMediaValues();

        Uri mediaUri = mContext.getContentResolver().insert(MediaEntry.CONTENT_URI, values);
        long mediaRowId = ContentUris.parseId(mediaUri);

        // Verify we got a row back.
        assertTrue(mediaRowId != -1);
        Log.d(LOG_TAG, "New row id: " + mediaRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(MediaEntry._ID, mediaRowId);
        updatedValues.put(MediaEntry.COLUMN_TITLE, "Santa's Village");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor mediaCursor = mContext.getContentResolver().query(MediaEntry.CONTENT_URI, null, null, null, null);

        com.discflux.app.mymedialist.data.TestUtilities.TestContentObserver tco = com.discflux.app.mymedialist.data.TestUtilities.getTestContentObserver();
        mediaCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MediaEntry.CONTENT_URI, updatedValues, MediaEntry._ID + "= ?",
                new String[] { Long.toString(mediaRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        mediaCursor.unregisterContentObserver(tco);
        mediaCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MediaEntry.CONTENT_URI,
                null,   // projection
                MediaEntry._ID + " = " + mediaRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        com.discflux.app.mymedialist.data.TestUtilities.validateCursor("testUpdateMedia.  Error validating media entry update.",
                cursor, updatedValues);

        cursor.close();
    }


    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the insert functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testInsertReadProvider() {
        ContentValues testValues = com.discflux.app.mymedialist.data.TestUtilities.createMediaValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        com.discflux.app.mymedialist.data.TestUtilities.TestContentObserver tco = com.discflux.app.mymedialist.data.TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MediaEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(MediaEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MediaEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        com.discflux.app.mymedialist.data.TestUtilities.validateCursor("testInsertReadProvider. Error validating MediaEntry.",
                cursor, testValues);

        // Fantastic.  Now that we have a location, add some media!
        ContentValues mediaValues = com.discflux.app.mymedialist.data.TestUtilities.createMediaValues();
        // The TestContentObserver is a one-shot class
        tco = com.discflux.app.mymedialist.data.TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(MediaEntry.CONTENT_URI, true, tco);

        Uri mediaInsertUri = mContext.getContentResolver()
                .insert(MediaEntry.CONTENT_URI, mediaValues);
        assertTrue(mediaInsertUri != null);

        // Did our content observer get called?  Students:  If this fails, your insert media
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor mediaCursor = mContext.getContentResolver().query(
                MediaEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        com.discflux.app.mymedialist.data.TestUtilities.validateCursor("testInsertReadProvider. Error validating MediaEntry insert.",
                mediaCursor, mediaValues);

    }

    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the delete functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our media delete.
        com.discflux.app.mymedialist.data.TestUtilities.TestContentObserver mediaObserver = com.discflux.app.mymedialist.data.TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MediaEntry.CONTENT_URI, true, mediaObserver);

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        mediaObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(mediaObserver);
    }


    /*static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertMediaValues(long locationRowId) {
        long currentTestDate = com.discflux.app.mymedialist.data.TestUtilities.TEST_DATE;
        long millisecondsInADay = 1000*60*60*24;
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, currentTestDate+= millisecondsInADay ) {
            ContentValues mediaValues = new ContentValues();
            mediaValues.put(MediaContract.MediaEntry.COLUMN_LOC_KEY, locationRowId);
            mediaValues.put(MediaContract.MediaEntry.COLUMN_DATE, currentTestDate);
            mediaValues.put(MediaContract.MediaEntry.COLUMN_DEGREES, 1.1);
            mediaValues.put(MediaContract.MediaEntry.COLUMN_HUMIDITY, 1.2 + 0.01 * (float) i);
            mediaValues.put(MediaContract.MediaEntry.COLUMN_PRESSURE, 1.3 - 0.01 * (float) i);
            mediaValues.put(MediaContract.MediaEntry.COLUMN_MAX_TEMP, 75 + i);
            mediaValues.put(MediaContract.MediaEntry.COLUMN_MIN_TEMP, 65 - i);
            mediaValues.put(MediaContract.MediaEntry.COLUMN_SHORT_DESC, "Asteroids");
            mediaValues.put(MediaContract.MediaEntry.COLUMN_WIND_SPEED, 5.5 + 0.2 * (float) i);
            mediaValues.put(MediaContract.MediaEntry.COLUMN_WEATHER_ID, 321);
            returnContentValues[i] = mediaValues;
        }
        return returnContentValues;
    }*/

    // Student: Uncomment this test after you have completed writing the BulkInsert functionality
    // in your provider.  Note that this test will work with the built-in (default) provider
    // implementation, which just inserts records one-at-a-time, so really do implement the
    // BulkInsert ContentProvider function.
    /*public void testBulkInsert() {
        // first, let's create a location value
        ContentValues testValues = com.discflux.app.mymedialist.data.TestUtilities.createNorthPoleLocationValues();
        Uri locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        com.discflux.app.mymedialist.data.TestUtilities.validateCursor("testBulkInsert. Error validating LocationEntry.",
                cursor, testValues);

        // Now we can bulkInsert some media.  In fact, we only implement BulkInsert for media
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = createBulkInsertMediaValues(locationRowId);

        // Register a content observer for our bulk insert.
        com.discflux.app.mymedialist.data.TestUtilities.TestContentObserver mediaObserver = com.discflux.app.mymedialist.data.TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MediaEntry.CONTENT_URI, true, mediaObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(MediaEntry.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        mediaObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(mediaObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        cursor = mContext.getContentResolver().query(
                MediaEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                MediaEntry.COLUMN_DATE + " ASC"  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            com.discflux.app.mymedialist.data.TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating MediaEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }*/
}