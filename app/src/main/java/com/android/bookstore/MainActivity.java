package com.android.bookstore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.bookstore.data.BookStoreContract.BookStoreEntry;
import com.android.bookstore.data.BookStoreDbHelper;

public class MainActivity extends AppCompatActivity {

    // Database helper that will provide us access to the database
    private BookStoreDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new BookStoreDbHelper(this);

        final Button button = findViewById(R.id.insert_book);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                insertData();
                finish();
                startActivity(getIntent());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        queryData();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the book_store database.
     */
    private void queryData() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BookStoreEntry._ID,
                BookStoreEntry.COLUMN_PRODUCT_NAME,
                BookStoreEntry.COLUMN_PRICE,
                BookStoreEntry.COLUMN_QUANTITY,
                BookStoreEntry.COLUMN_SUPPLIER_NAME,
                BookStoreEntry.COLUMN_SUPPLIER_PHONE};

        // Perform a query on the book table
        Cursor cursor = db.query(
                BookStoreEntry.TABLE_NAME,    // The table to query
                projection,                   // The columns to return
                null,                  // The columns for the WHERE clause
                null,               // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                    // Don't filter by row groups
                null);                  // The sort order

        TextView bookCountView = (TextView) findViewById(R.id.view_books);

        try {
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in the table layout.
            bookCountView.setText(R.string.book_table_contains + cursor.getCount() + R.string.books);

            int nameColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_SUPPLIER_PHONE);
            TableLayout tableLayout = (TableLayout) findViewById(R.id.book_table);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value
                // at the current row the cursor is on.
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                int currentSupplierPhone = cursor.getInt(supplierPhoneColumnIndex);
                // Create a table row
                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                TextView nameTextView = new TextView(this);
                TextView priceTextView = new TextView(this);
                TextView quantityTextView = new TextView(this);
                TextView supplierNameTextView = new TextView(this);
                TextView supplierPhoneTextView = new TextView(this);

                nameTextView.setText(currentName);
                priceTextView.setText(currentPrice + "$");
                quantityTextView.setText(String.valueOf(currentQuantity));
                supplierNameTextView.setText(currentSupplierName);
                supplierPhoneTextView.setText(String.valueOf(currentSupplierPhone));
                // Add all the text views in table row
                row.addView(nameTextView);
                row.addView(priceTextView);
                row.addView(quantityTextView);
                row.addView(supplierNameTextView);
                row.addView(supplierPhoneTextView);
                tableLayout.addView(row);
            }

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertData() {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and Harry Potter's book attributes are the values.
        ContentValues values = new ContentValues();
        values.put(BookStoreEntry.COLUMN_PRICE, "20"); // Price in $
        values.put(BookStoreEntry.COLUMN_PRODUCT_NAME, "Harry Potter");
        values.put(BookStoreEntry.COLUMN_QUANTITY, 12);
        values.put(BookStoreEntry.COLUMN_SUPPLIER_NAME, "Test");
        values.put(BookStoreEntry.COLUMN_SUPPLIER_PHONE, 12343647);

        // Insert a new row for Harry Potter in the database, returning the ID of that new row.
        db.insert(BookStoreEntry.TABLE_NAME, null, values);
    }

}
