package com.android.bookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.bookstore.data.BookStoreContract.BookStoreEntry;
import com.android.bookstore.data.BookStoreDbHelper;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static int BOOK_LOADER = 0;

    private BookStoreAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView bookListView = (ListView) findViewById(R.id.list);

        // Empty view will be used in case of no data in the inventory
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        mCursorAdapter = new BookStoreAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);

        getSupportLoaderManager().initLoader(BOOK_LOADER, null, this);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentUri = ContentUris.withAppendedId(BookStoreEntry.CONTENT_URI, id);

                intent.setData(currentUri);
                startActivity(intent);
            }
        });

        // Create addProduct button to open EditorActivity and create a new product
        FloatingActionButton addProduct = findViewById(R.id.add_product);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.delete_all_entries:
                deleteAllEntries();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to delete all books in the database.
     */
    private void deleteAllEntries() {
        int noRows = getContentResolver().delete(BookStoreEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", noRows + " rows deleted from book database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_main.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BookStoreEntry._ID,
                BookStoreEntry.COLUMN_PRODUCT_NAME,
                BookStoreEntry.COLUMN_PRICE,
                BookStoreEntry.COLUMN_QUANTITY,
                BookStoreEntry.COLUMN_SUPPLIER_NAME,
                BookStoreEntry.COLUMN_SUPPLIER_PHONE};
        return new CursorLoader(this, BookStoreEntry.CONTENT_URI, projection, null, null, null);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }


    public void sellProduct(int bookID, int quantity) {
        //Decrease quantity
        quantity--;

        //Check if book is available in the bookstore
        if (quantity >= 0) {
            ContentValues values = new ContentValues();
            values.put(BookStoreEntry.COLUMN_QUANTITY, quantity);
            Uri updateUri = ContentUris.withAppendedId(BookStoreEntry.CONTENT_URI, bookID);
            getContentResolver().update(updateUri, values, null, null);

        } else {
            Toast.makeText(this, getText(R.string.not_available_product), Toast.LENGTH_LONG).show();
        }
    }
}
