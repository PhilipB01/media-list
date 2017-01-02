package com.discflux.app.mymedialist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.discflux.app.mymedialist.data.MediaContract;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private Button mTrailerButton, mCancelButton;
    private TextView mHeaderTv, mDateTv, mDirectorTv, mRatingTv, mDescriptionTv;
    private CheckBox mWatchedCheck;
    private RatingBar mRatingBar;
    private String mTrailerUrl;

    private static final int LOADER_ID = 0;

    private static final String[] MEDIA_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MediaContract.MediaEntry._ID,
            MediaContract.MediaEntry.COLUMN_TYPE,
            MediaContract.MediaEntry.COLUMN_TITLE,
            MediaContract.MediaEntry.COLUMN_AUTHOR,
            MediaContract.MediaEntry.COLUMN_DATE,
            MediaContract.MediaEntry.COLUMN_URL,
            MediaContract.MediaEntry.COLUMN_DESCRIPTION,
            MediaContract.MediaEntry.COLUMN_RATING,
            MediaContract.MediaEntry.COLUMN_COMPLETION
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_MEDIA_ID = 0;
    static final int COL_MEDIA_TYPE = 1;
    static final int COL_MEDIA_TITLE = 2;
    static final int COL_MEDIA_AUTHOR = 3;
    static final int COL_MEDIA_DATE = 4;
    static final int COL_MEDIA_URL = 5;
    static final int COL_MEDIA_DESCRIPTION = 6;
    static final int COL_MEDIA_RATING = 7;
    static final int COL_MEDIA_COMPLETION = 8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHeaderTv = (TextView) findViewById(R.id.header_tv);
        mDateTv = (TextView) findViewById(R.id.date_et);
        mDirectorTv = (TextView) findViewById(R.id.director_et);
        mRatingTv = (TextView) findViewById(R.id.rating_et);
        mDescriptionTv = (TextView) findViewById(R.id.description_et);

        mWatchedCheck = (CheckBox) findViewById(R.id.watched_check);

        mRatingBar = (RatingBar) findViewById(R.id.rating_bar);

        mTrailerButton = (Button) findViewById(R.id.trailer_button);

        mCancelButton = (Button) findViewById(R.id.watch_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getIntent();
        if (intent == null) {
            return null;
        }

        return new CursorLoader(
                this,
                intent.getData(),
                MEDIA_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "On Load Finished");
        if (!data.moveToFirst()) {
            return;
        }
        mHeaderTv.setText(data.getString(COL_MEDIA_TITLE));
        mDateTv.setText(data.getString(COL_MEDIA_DATE));
        mDirectorTv.setText(data.getString(COL_MEDIA_AUTHOR));
        mRatingTv.setText(data.getString(COL_MEDIA_RATING));
        mDescriptionTv.setText(data.getString(COL_MEDIA_DESCRIPTION));
        mTrailerUrl = data.getString(COL_MEDIA_URL);

        mWatchedCheck.setChecked(data.getInt(COL_MEDIA_COMPLETION) > 0);

        mTrailerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "trailer url:" + mTrailerUrl);
            }
        });

        final String id = data.getString(COL_MEDIA_ID);
        mWatchedCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ContentValues values = new ContentValues();
                values.put(MediaContract.MediaEntry.COLUMN_COMPLETION, isChecked);
                getContentResolver().update(
                        MediaContract.MediaEntry.CONTENT_URI,
                        values,
                        "_id = ?",
                        new String[]{"" + id});
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
