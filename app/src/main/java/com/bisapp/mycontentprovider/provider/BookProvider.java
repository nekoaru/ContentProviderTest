package com.bisapp.mycontentprovider.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BookProvider extends ContentProvider {

    private static final int ITEM_LIST = 1; // check if the URI is for all items
    private static final int ITEM_ID = 2; // check if the URI is for a row of item

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(BookContract.AUTHORITY, "book", ITEM_LIST);
        URI_MATCHER.addURI(BookContract.AUTHORITY, "book/#", ITEM_ID);
    }

    private DBHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        //initialize our database helper here
        dbHelper = new DBHelper(getContext());
        database = dbHelper.getWritableDatabase();

        //Please, do not do complex task here lest
        // your content provider will be very slow
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(DBHelper.BOOK_TABLE_NAME);

        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = BookContract.BookColumn.SORT_ORDER_DEFAULT;
                }
                break;
            case ITEM_ID:
                sqLiteQueryBuilder.appendWhere(
                        BookContract.BookColumn._ID + " = "
                                + uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException("Unsupported Uri: " + uri);
        }

        Cursor cursor = sqLiteQueryBuilder.query(database, projection, selection,
                selectionArgs, null, null, sortOrder);

        int count = cursor.getCount();
        if (cursor.getCount() > 0) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case ITEM_ID:
                return BookContract.BookColumn.CONTENT_ITEM_TYPE;
            case ITEM_LIST:
                return BookContract.BookColumn.CONTENT_DIR_TYPE;
            default:
                throw new IllegalArgumentException("No Uri exist for " + uri);

        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        //checking that the Uri is dir base type
        if (URI_MATCHER.match(uri) == ITEM_ID)
            throw new IllegalArgumentException("Unsupported Uri for insertion: " + uri);

        long id = database.insert(DBHelper.BOOK_TABLE_NAME, null, values);

        //check if new data inserted
        if (id > 0) {
            Uri newRowUri = ContentUris.withAppendedId(uri, id);

            //notify all listeners of the change
            getContext().getContentResolver().notifyChange(newRowUri, null);
            return newRowUri;
        }

        throw new SQLException("Problem inserting into this Uri: " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int deletedCount = 0;
        switch (URI_MATCHER.match(uri)){
            case ITEM_LIST:
                deletedCount = database.delete(DBHelper.BOOK_TABLE_NAME,selection,selectionArgs);
                break;
            case ITEM_ID:
                String where = BookContract.BookColumn._ID +
                        " = " + uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection)){
                    where += " AND " + selection;
                }
                deletedCount = database.delete(DBHelper.BOOK_TABLE_NAME,where,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported Uri for deletion: "+ uri);
        }

        if (deletedCount > 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return deletedCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int updatedCount = 0;
        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                updatedCount = database.update(DBHelper.BOOK_TABLE_NAME, values, selection, selectionArgs);
                break;
            case ITEM_ID:
                String id = uri.getLastPathSegment();
                String where = BookContract.BookColumn._ID +
                        " = " + id;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updatedCount = database.update(DBHelper.BOOK_TABLE_NAME, values, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported Uri: " + uri);

        }

        // notify all listeners of changes:
        if (updatedCount > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return updatedCount;
    }


}
