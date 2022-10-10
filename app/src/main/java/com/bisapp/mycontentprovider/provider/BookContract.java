package com.bisapp.mycontentprovider.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class BookContract {

    /**
     * Authority string for this provider.
     */
    public static final String AUTHORITY = "com.bisapp.mycontentprovider";
    /**
     * The content:// style URL for this provider
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    /**
     * Contains the user defined books.
     */
    public static class BookColumn implements BaseColumns {
        /**
         * The name column.
         * <p>TYPE: TEXT</p>
         */
        public static final String NAME = "name";
        /**
         * The type column.
         * <p>TYPE: TEXT</p>
         */
        public static final String TYPE = "type";
        /**
         * The date_created column.
         * <p>TYPE: DATETIME</p>
         */
        public static final String DATE_CREATED = "date_created";
        public static final String _ID = BaseColumns._ID;
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BookContract.CONTENT_URI, "book");

        /**
         * The mime type of the directory of items.
         **/
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/vnd.com.bisapp.book";

        /**
         * The mime type of the single items.
         **/
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/vnd.com.bisapp.book";

        /**
         * The default sort order for
         * queries containing _ID fields.
         */
        public static final String SORT_ORDER_DEFAULT =
                _ID + " ASC";
    }
}
