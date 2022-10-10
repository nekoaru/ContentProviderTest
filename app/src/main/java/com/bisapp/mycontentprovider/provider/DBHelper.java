package com.bisapp.mycontentprovider.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Education"; //This is the name of the database
    public static final String BOOK_TABLE_NAME = "Book"; //This is database table, we are going to create
    public static final int DATABASE_VERSION = 1; // This is the current version of the database


    private static final String CREATE_BOOK_TABLE = "CREATE TABLE "+ BOOK_TABLE_NAME +
            " ( _id INTEGER PRIMARY KEY, "+
            " name TEXT NOT NULL,"+
            " type TEXT NOT NULL," +
            " date_created datetime default current_timestamp);";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ BOOK_TABLE_NAME);
        onCreate(db);
    }

}
