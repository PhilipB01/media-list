package com.discflux.app.mymedialist;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.discflux.app.mymedialist.data.MediaContract;

/**
 * Created by Phil on 28/12/2016.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private MediaAdapter mAdapter;
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
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.content_list, container, false);

        mAdapter = new MediaAdapter(getActivity(), null, 0);
        /*mAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, mStringArray);*/

        ListView listView = (ListView) rootView.findViewById(R.id.listview);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    int mediaType = cursor.getInt(COL_MEDIA_TYPE);
                    String title = cursor.getString(COL_MEDIA_TITLE);
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(MediaContract.MediaEntry.buildMediaTitle(
                                    mediaType, title
                            ));
                    startActivity(intent);

                    //detailIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = MediaContract.MediaEntry.buildMediaType(100);
        String sortOrder = MediaContract.MediaEntry.COLUMN_TITLE  + " ASC";
        return new CursorLoader(getActivity(),
                uri,
                MEDIA_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
