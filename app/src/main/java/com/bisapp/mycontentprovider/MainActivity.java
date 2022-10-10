package com.bisapp.mycontentprovider;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.startup.AppInitializer;

import com.bisapp.mycontentprovider.startup_setup.LibraryAInitializer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    private String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    private boolean hasPermissions = false;
    private final static int RC_CALL_APP_PERM = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestPerms();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

      /*  Uri singleUri = ContentUris.withAppendedId(UserDictionary.Words.CONTENT_URI, 1);
        queryWordData();*/
      initializeComponents();

    }

    private void initializeComponents(){
        AppInitializer.getInstance(this).initializeComponent(LibraryAInitializer.class);
    }

    @AfterPermissionGranted(RC_CALL_APP_PERM)
    private void requestPerms(){
        if (EasyPermissions.hasPermissions(this,permissions)) {
            hasPermissions = true;
        } else {
            //if permission is denied
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.enable_storage_access),
                    RC_CALL_APP_PERM,
                    permissions
            );
        }
    }

    private void deleteWordData() {

        // Defines selection criteria for the rows you want to delete
        String selectionClause = UserDictionary.Words.APP_ID + " LIKE ?";
        String[] selectionArgs = {"user"};

        // Defines a variable to contain the number of rows deleted
        int rowsDeleted = 0;

        // Deletes the words that match the selection criteria
        rowsDeleted = getContentResolver().delete(
                UserDictionary.Words.CONTENT_URI,   // the user dictionary content URI
                selectionClause,                   // the column to select on
                selectionArgs                      // the value to compare to
        );

    }

    private void updateWordData() {
        // Defines an object to contain the new values to insert
        ContentValues updateValues = new ContentValues();

        // Defines selection criteria for the rows you want to update
        String selectionClause = UserDictionary.Words.LOCALE + " LIKE ?";
        String[] selectionArgs = {"en_%"};

        // Defines a variable to contain the number of updated rows
        int rowsUpdated = 0;

        /*
         * Sets the updated value and updates the selected words.
         */
        updateValues.putNull(UserDictionary.Words.LOCALE);

        rowsUpdated = getContentResolver().update(
                UserDictionary.Words.CONTENT_URI,   // the user dictionary content URI
                updateValues,                      // the columns to update
                selectionClause,                   // the column to select on
                selectionArgs                      // the value to compare to
        );

    }

    private void insertWordData() {
        // Defines an object to contain the new values to insert
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserDictionary.Words.APP_ID, UUID.randomUUID().toString());
        contentValues.put(UserDictionary.Words.WORD, "new Word");
        contentValues.put(UserDictionary.Words.LOCALE, Locale.getDefault().toString());
        contentValues.put(UserDictionary.Words.FREQUENCY, "8");

        Uri newUri = getContentResolver().insert(
                UserDictionary.Words.CONTENT_URI, // the user dictionary content URI
                contentValues   // the values to insert
        );

        // this gives the last id inserted
        long id = ContentUris.parseId(UserDictionary.Words.CONTENT_URI);

    }

    private void queryWordData() {
        // A "projection" defines the columns that will be returned for each row
        String[] projections = {
                UserDictionary.Words._ID, // A contract class constant for _ID column name
                UserDictionary.Words.WORD,  // A contract class constant for WORD column name
                UserDictionary.Words.APP_ID, // A contract class constant for APP_ID column name
                UserDictionary.Words.LOCALE  // A contract class constant for LOCALE column name
        };

        //word to query from the UI
        EditText searchWord = new EditText(getApplicationContext());
        String searchItem = searchWord.getText().toString();

        // Defines a string to contain the selection clause
        String selectionClause = null;

        // Initializes an array to contain selection arguments
        String[] selectionArgs = {""};

        //pre-process the input to check if it is a valid input
        if (searchItem == null) {
            selectionClause = null;
            selectionArgs[0] = "";
        } else {
            selectionClause = UserDictionary.Words.WORD + " = ? ";
            selectionArgs[0] = searchItem;
        }

        try (Cursor cursor = getContentResolver().query(UserDictionary.Words.CONTENT_URI, projections, selectionClause, selectionArgs, "ASC")) {
            if (cursor == null) {
                //do something here
            } else if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    // retrieve your data here
                    String ID = cursor.getString(cursor.getColumnIndex(UserDictionary.Words._ID));
                    String word = cursor.getString(cursor.getColumnIndex(UserDictionary.Words.WORD));
                    String appID = cursor.getString(cursor.getColumnIndex(UserDictionary.Words.APP_ID));
                    String locale = cursor.getString(cursor.getColumnIndex(UserDictionary.Words.LOCALE));
                    cursor.moveToNext();
                }
            }

           /* new SimpleCursorAdapter(
                    getApplicationContext(),
                    R.layout.activity_main,
                    cursor,
                    new String[]{},
                    new int[]{},
                    0);*/

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}