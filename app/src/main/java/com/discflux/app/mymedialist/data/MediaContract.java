package com.discflux.app.mymedialist.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class MediaContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.discflux.app.mymedialist";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MEDIA = "media";

    /* Inner class that defines the table contents of the weather table */
    public static final class MediaEntry implements BaseColumns {

        public static final String TABLE_NAME = "media";

        public static final String COLUMN_TYPE = "type";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_AUTHOR = "author";

        public static final String COLUMN_DATE = "date";

        public static final String COLUMN_URL = "url";

        public static final String COLUMN_DESCRIPTION = "description";

        public static final String COLUMN_RATING = "rating";

        public static final String COLUMN_COMPLETION = "complete";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MEDIA).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEDIA;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEDIA;


        public static Uri buildMediaUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMediaTitle(int mediaType, String mediaTitle) {

            return CONTENT_URI.buildUpon().appendPath(Integer.toString(mediaType)).appendPath(mediaTitle).build();
        }

        public static Uri buildMediaType(int mediaType) {

            return CONTENT_URI.buildUpon().appendPath(Long.toString(mediaType)).build();
        }

        public static String getMediaTitleFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }


        // 100 - film
        // 200 - tv series
        // 300 - book
        // 400 - game
        // 500 - music
        public static int getMediaTypeFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }
}
