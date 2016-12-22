package com.discflux.app.mymedialist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.discflux.app.mymedialist.data.MediaContract;

/**
 * Created by Phil on 12/12/2016.
 */
public class EditActivity extends AppCompatActivity {

    Button mSaveButton, mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSaveButton = (Button) findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                String title = ((EditText) findViewById(R.id.title_et)).getText().toString();
                intent.setData(Uri.parse(title));
                setResult(RESULT_OK, intent);

                insertMediaEntry(title);
                finish();
            }
        });

        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

    private void insertMediaEntry(String title) {
        int type = 100;
        String date = ((EditText) findViewById(R.id.date_et)).getText().toString();
        String description = ((EditText) findViewById(R.id.description_et)).getText().toString();

        ContentValues values = new ContentValues();
        values.put(MediaContract.MediaEntry.COLUMN_TITLE, title);
        values.put(MediaContract.MediaEntry.COLUMN_TYPE, type);
        values.put(MediaContract.MediaEntry.COLUMN_DATE, date);
        values.put(MediaContract.MediaEntry.COLUMN_DESCRIPTION, description);
        values.put(MediaContract.MediaEntry.COLUMN_URL, "");
        values.put(MediaContract.MediaEntry.COLUMN_COMPLETION, 0);
        values.put(MediaContract.MediaEntry.COLUMN_RATING, 0);

        if (type == 300) {
            String author = ((EditText) findViewById(R.id.author_et)).getText().toString();
            values.put(MediaContract.MediaEntry.COLUMN_AUTHOR, author);
        }

        // add a validation step to prevent duplicate titles
        if (validateEntry(values))
            this.getContentResolver().insert(MediaContract.MediaEntry.CONTENT_URI, values);
        else
            Log.e(this.getClass().getSimpleName(), "Readding duplicate title/year to database!");
    }

    private Boolean validateEntry(ContentValues values) {
        String type = values.getAsString(MediaContract.MediaEntry.COLUMN_TYPE);
        String title = values.getAsString(MediaContract.MediaEntry.COLUMN_TITLE);
        String selection = MediaContract.MediaEntry.COLUMN_TITLE + " = ? AND " + MediaContract.MediaEntry.COLUMN_TYPE + " = ?";
        String[] selectionArgs = {title, type};
        // search conflicting title and date
        Cursor cursor = this.getContentResolver().query(MediaContract.MediaEntry.buildMediaTitle(Integer.parseInt(type), title), null, selection, selectionArgs, null, null);

        return !cursor.moveToFirst();
    }
}
