package com.android.bookstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.bookstore.data.BookStoreContract.BookStoreEntry;
/**
 * Created by user on 6/17/2018.
 *
 * Database helper for book store app. Manages database creation and version management.
 */

public class BookStoreDbHelper extends SQLiteOpenHelper {

    // Name of the database file
    private static final String DATABASE_NAME = "book_store.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link BookStoreDbHelper}.
     *
     * @param context of the app
     */
    public BookStoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the book table
        String createTable =  "CREATE TABLE " + BookStoreEntry.TABLE_NAME + " ("
                + BookStoreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookStoreEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + BookStoreEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + BookStoreEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + BookStoreEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL DEFAULT 0,"
                + BookStoreEntry.COLUMN_SUPPLIER_PHONE + " INTEGER);";

        // Execute the SQL statement
        db.execSQL(createTable);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}