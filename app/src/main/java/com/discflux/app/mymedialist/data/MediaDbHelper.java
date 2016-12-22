package com.discflux.app.mymedialist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.discflux.app.mymedialist.data.MediaContract.MediaEntry;

/**
 * Created by Phil on 11/12/2016.
 */
public class MediaDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "media.db";

    public MediaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MEDIA_TABLE = "CREATE TABLE " + MediaEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                MediaEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                MediaEntry.COLUMN_TYPE + " INT NOT NULL, " +
                MediaEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MediaEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                MediaEntry.COLUMN_AUTHOR + " TEXT, " +
                MediaEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +

                MediaEntry.COLUMN_URL + " TEXT NOT NULL, " +
                MediaEntry.COLUMN_RATING + " REAL NOT NULL, " +
                MediaEntry.COLUMN_COMPLETION + " BOOL NOT NULL)";

                // Set up the location column as a foreign key to location table.
                //" FOREIGN KEY (" + MediaEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                //LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +

                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                //"UNIQUE (" + MediaEntry.COLUMN_DATE + ", " +
                //MediaEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MEDIA_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MediaEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
