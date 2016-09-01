package com.example.qbclct.netwrkcn;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by QBCLCT on 20/7/16.
 */
public class FeedReaderContract {
        // To prevent someone from accidentally instantiating the contract class,
        // give it an empty constructor.
        public FeedReaderContract() {}

        /* Inner class that defines the table contents */
        public static abstract class FeedEntry implements BaseColumns {
            public static final String TABLE_NAME = "entry";
            public static final String _ID = "entryid";
            public static final String COLUMN_NAME_DATE = "date";
            public static final String COLUMN_NAME_TEXT = "text";
            public static final String COLUMN_NAME_SUBTITLE = "subtitle";
            private static final String TEXT_TYPE = " TEXT";
            private static final String COMMA_SEP = ",";
            public static final String SQL_CREATE_ENTRIES =
                    "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                            FeedEntry._ID + " INTEGER PRIMARY KEY," +
                            FeedEntry.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                            FeedEntry.COLUMN_NAME_TEXT + TEXT_TYPE +  " )";

            public static final String SQL_DELETE_ENTRIES =
                    "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
        }

}
