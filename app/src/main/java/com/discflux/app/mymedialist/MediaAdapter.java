package com.discflux.app.mymedialist;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link MediaAdapter} exposes a list of media items
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class MediaAdapter extends CursorAdapter {

    public MediaAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor

        String complete = cursor.getString(MoviesFragment.COL_MEDIA_COMPLETION);
        if (complete.equals(0)) {
            complete = "Incomplete";
        } else {
            complete = "Complete";
        }

        return cursor.getString(MoviesFragment.COL_MEDIA_TITLE) +
                " - " + cursor.getString(MoviesFragment.COL_MEDIA_DATE) +
                " - " + complete;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_media, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        TextView tv = (TextView) view.findViewById(R.id.list_item_media_tv);
        tv.setText(convertCursorRowToUXFormat(cursor));
    }
}