package com.discflux.app.mymedialist.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Phil on 11/12/2016.
 */
public class MediaProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MediaDbHelper mOpenHelper;

    static final int MEDIA = 100;
    static final int MEDIA_WITH_TYPE_AND_TITLE = 101;
    static final int MEDIA_WITH_TYPE = 102;

/*    private static final SQLiteQueryBuilder sMediaQueryBuilder;

    static{
        sMediaQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
         sWeatherByLocationSettingQueryBuilder.setTables(
                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.LocationEntry.TABLE_NAME +
                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
"." + WeatherContract.LocationEntry._ID);
    }*/

    //media.title = ? AND media.type = ?
    private static final String sMediaTitleSelection =
            MediaContract.MediaEntry.TABLE_NAME +
                    "." + MediaContract.MediaEntry.COLUMN_TITLE + " = ? AND " +
                    MediaContract.MediaEntry.TABLE_NAME + "." +
                        MediaContract.MediaEntry.COLUMN_TYPE + " = ?";

    //media.type = ?
    private static final String sMediaTypeSelection =
            MediaContract.MediaEntry.TABLE_NAME +
                    "." + MediaContract.MediaEntry.COLUMN_TYPE + " = ? ";


    private Cursor getMediaByTitle(Uri uri, String[] projection, String sortOrder) {
        String title = MediaContract.MediaEntry.getMediaTitleFromUri(uri);
        int mediaType = MediaContract.MediaEntry.getMediaTypeFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(
                MediaContract.MediaEntry.TABLE_NAME,
                projection,
                sMediaTitleSelection,
                new String[]{title, Integer.toString(mediaType)},
                null,
                null,
                sortOrder);
    }


    private Cursor getMediaByType(Uri uri, String[] projection, String sortOrder) {
        int type = MediaContract.MediaEntry.getMediaTypeFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(
                MediaContract.MediaEntry.TABLE_NAME,
                projection,
                sMediaTitleSelection,
                new String[]{Integer.toString(type)},
                null,
                null,
                sortOrder);
    }

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MediaContract.CONTENT_AUTHORITY;

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.
        matcher.addURI(authority, MediaContract.PATH_MEDIA, MEDIA);
        matcher.addURI(authority, MediaContract.PATH_MEDIA + "/#/*", MEDIA_WITH_TYPE_AND_TITLE);
        matcher.addURI(authority, MediaContract.PATH_MEDIA + "/#", MEDIA_WITH_TYPE);

        // 3) Return the new matcher!
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MediaDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MEDIA:
                return MediaContract.MediaEntry.CONTENT_TYPE;
            case MEDIA_WITH_TYPE:
                return MediaContract.MediaEntry.CONTENT_TYPE;
            case MEDIA_WITH_TYPE_AND_TITLE:
                return MediaContract.MediaEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            // "media/*"
            case MEDIA_WITH_TYPE: {
                retCursor = getMediaByType(uri, projection, sortOrder);
                break;
            }
            // "media/*"
            case MEDIA_WITH_TYPE_AND_TITLE: {
                retCursor = getMediaByTitle(uri, projection, sortOrder);
                break;
            }
            // "media/*/*"
            case MEDIA: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MediaContract.MediaEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MEDIA: {
                long _id = db.insert(MediaContract.MediaEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MediaContract.MediaEntry.buildMediaUri(_id);
                else throw new android.database.SQLException("Failed to insert row into" + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch(match) {
            case MEDIA:
                count = db.delete(MediaContract.MediaEntry.TABLE_NAME, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (selection == null) selection = "1";

        switch(match) {
            case MEDIA:
                rowsDeleted = db.update(MediaContract.MediaEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEDIA:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MediaContract.MediaEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
