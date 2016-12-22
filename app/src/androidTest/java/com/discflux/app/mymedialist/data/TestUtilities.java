package com.discflux.app.mymedialist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.discflux.app.mymedialist.utils.PollingCheck;

import java.util.Map;
import java.util.Set;


/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your MediaContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {
    static final String TEST_TITLE = "TheBigDeep";
    static final int TEST_TYPE = 100;

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + valueCursor.getString(idx) +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
        Students: Use this to create some default media values for your database tests.
     */
    static ContentValues createMediaValues() {
        ContentValues mediaValues = new ContentValues();
        mediaValues.put(MediaContract.MediaEntry.COLUMN_TITLE, "Alone in Berlin");
        mediaValues.put(MediaContract.MediaEntry.COLUMN_TYPE, 100);
        mediaValues.put(MediaContract.MediaEntry.COLUMN_RATING, 3.6);
        mediaValues.put(MediaContract.MediaEntry.COLUMN_DATE, 2016);
        mediaValues.put(MediaContract.MediaEntry.COLUMN_DESCRIPTION, "Good film about circumventing Gestapo on home soil. Very tragic.");
        mediaValues.put(MediaContract.MediaEntry.COLUMN_URL, "www.appletrailers.com");
        mediaValues.put(MediaContract.MediaEntry.COLUMN_COMPLETION, 0);

        return mediaValues;
    }

    /*
        Students: This is a helper method for the testMediaTable quiz. You can move your
        code from testMediaTable to here so that you can call this code from both
        testMediaTable and testMediaTable.
     */
    public static long insertMedia(Context context) {
        // First step: Get reference to writable database
        SQLiteDatabase db = new MediaDbHelper(context).getWritableDatabase();
        // Create ContentValues of what you want to insert
        ContentValues mediaValues = createMediaValues();

        // Insert ContentValues into database and get a row ID back
        long mediaRowId = db.insert(MediaContract.MediaEntry.TABLE_NAME, null, mediaValues);

        // Query the database and receive a Cursor back
        Cursor c = db.query(
                MediaContract.MediaEntry.TABLE_NAME,
                null,
                null,
                null,
                null, null,
                null
        );
        // Move the cursor to a valid database row
        assertTrue("Error: No Records returned from media query", c.moveToFirst());
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        com.discflux.app.mymedialist.data.TestUtilities.validateCurrentRecord("Error: Failed to validate current record " + c.getString(0), c, mediaValues);

        // move cursor to ensure only one record in database
        assertFalse("Error: More than one record returned from media query", c.moveToNext());

        // Finally, close the cursor and database
        c.close();
        db.close();
        return mediaRowId;
    }

/*    *//*
        Students: You can uncomment this helper function once you have finished creating the
        LocationEntry part of the MediaContract.
     *//*
    static ContentValues createNorthPoleLocationValues() {
        // Create a new map of values, where column names are the keys
        ContentValues mediaValues = new ContentValues();
        mediaValues.put(MediaContract.LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION);
        mediaValues.put(MediaContract.LocationEntry.COLUMN_CITY_NAME, "North Pole");
        mediaValues.put(MediaContract.LocationEntry.COLUMN_COORD_LAT, 64.7488);
        mediaValues.put(MediaContract.LocationEntry.COLUMN_COORD_LONG, -147.353);

        return mediaValues;
    }

    *//*
        Students: You can uncomment this function once you have finished creating the
        LocationEntry part of the MediaContract as well as the MediaDbHelper.
     *//*
    static long insertNorthPoleLocationValues(Context context) {
        // insert our test records into the database
        MediaDbHelper dbHelper = new MediaDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues mediaValues = TestUtilities.createNorthPoleLocationValues();

        long locationRowId;
        locationRowId = db.insert(MediaContract.LocationEntry.TABLE_NAME, null, mediaValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }*/

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.
        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}