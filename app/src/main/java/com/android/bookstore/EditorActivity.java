package com.android.bookstore;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.content.CursorLoader;
import android.net.Uri;
import android.app.LoaderManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.bookstore.data.BookStoreContract.BookStoreEntry;

import com.android.bookstore.data.BookStoreContract;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;

    /**
     * Content URI for the existing book (null if it's a new book)
     */
    private Uri mCurrentBookUri;

    /**
     * EditText field to enter the book's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the book's price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the book's quantity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the book's supplier name
     */
    private EditText mSupplierNameEditText;

    /**
     * EditText field to enter the book's supplier phone number
     */
    private EditText mSupplierPhoneNumberEditText;

    /**
     * Buttons
     **/
    private Button increaseQuantityButton;
    private Button decreaseQuantityButton;
    private Button orderFromSupplierButton;
    private Button deleteBookButton;

    /**
     * Boolean flag that keeps track of whether the book has been edited (true) or not (false)
     */
    private boolean mBookHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mBookHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book or editing an existing one.
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_product_name);
        mPriceEditText = findViewById(R.id.edit_product_price);
        mQuantityEditText = findViewById(R.id.edit_book_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_supplier_name);
        mSupplierPhoneNumberEditText = findViewById(R.id.edit_supplier_phone);

        increaseQuantityButton = findViewById(R.id.increase_quantity);
        decreaseQuantityButton = findViewById(R.id.decrease_quantity);
        orderFromSupplierButton = findViewById(R.id.order_from_supplier);
        deleteBookButton = findViewById(R.id.delete_button);

        // If the intent DOES NOT contain a book content URI, then we know that we are
        // creating a new book.
        if (mCurrentBookUri == null) {
            // This is a new book, so change the app bar to say "Add a Book"
            setTitle(getString(R.string.title_add_book));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();

            //Hide buttons
            increaseQuantityButton.setVisibility(View.GONE);
            decreaseQuantityButton.setVisibility(View.GONE);
            orderFromSupplierButton.setVisibility(View.GONE);
            deleteBookButton.setVisibility(View.GONE);
        } else {
            // Otherwise this is an existing book, so change app bar to say "Edit Book"
            setTitle(getString(R.string.title_edit_book));

            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);

            //Show buttons
            increaseQuantityButton.setVisibility(View.VISIBLE);
            decreaseQuantityButton.setVisibility(View.VISIBLE);
            orderFromSupplierButton.setVisibility(View.VISIBLE);
            deleteBookButton.setVisibility(View.VISIBLE);
        }

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);
    }

    /**
     * Get user input from editor and save book into database.
     */
    private void saveBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneNumberString = mSupplierPhoneNumberEditText.getText().toString().trim();

        //If any of required fields are empty
        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.name_cannot_be_empty),
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, getString(R.string.price_cannot_be_empty),
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(this, getString(R.string.supplier_name_cannot_be_empty),
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (TextUtils.isEmpty(supplierPhoneNumberString)) {
            Toast.makeText(this, getString(R.string.supplier_phone_number_cannot_be_empty),
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //If quantity is empty
        if (TextUtils.isEmpty(quantityString)) {
            quantityString = "0";
        }

        //Prepare quantity & supplier phone number
        Long quantity = Long.parseLong(quantityString);
        Long supplierPhoneNumber = Long.parseLong(supplierPhoneNumberString);

        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierPhoneNumberString)) {
            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and book attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(BookStoreEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(BookStoreEntry.COLUMN_PRICE, priceString);
        values.put(BookStoreEntry.COLUMN_QUANTITY, quantity);
        values.put(BookStoreEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(BookStoreEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneNumber);

        // Determine if this is a new or existing book by checking if mCurrentBookUri is null or not
        if (mCurrentBookUri == null) {
            // This is a NEW book, so insert a new book into the provider,
            // returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(BookStoreEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.insert_book_failed),
                        Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.insert_book_successfully),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            // Otherwise this is an EXISTING book, so update the book with content URI: mCurrentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.update_book_failed),
                        Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.update_book_successfully),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save book to database
                saveBook();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                deleteBook();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all book attributes, define a projection that contains
        // all columns from the book table
        String[] projection = {
                BookStoreEntry._ID,
                BookStoreEntry.COLUMN_PRODUCT_NAME,
                BookStoreEntry.COLUMN_PRICE,
                BookStoreEntry.COLUMN_QUANTITY,
                BookStoreEntry.COLUMN_SUPPLIER_NAME,
                BookStoreEntry.COLUMN_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,  // Parent activity context
                mCurrentBookUri,       // Query the content URI for the current book
                projection,            // Columns to include in the resulting Cursor
                null,         // No selection clause
                null,      // No selection arguments
                null);        // Default sort order
    }

    public void updateQuantity(int quantity, boolean decrease) {
        // Check if deCrease or increase quantity
        if (decrease) {
            quantity--;
        } else {
            quantity++;
        }
        // Check if we have a current uri for the existing book
        if (mCurrentBookUri != null) {
            if (quantity >= 0) {
                ContentValues values = new ContentValues();
                values.put(BookStoreContract.BookStoreEntry.COLUMN_QUANTITY, quantity);
                // Get the number of rows
                int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

                if (rowsAffected == 0) {
                    // Show message that something went wrong with the increase of the quantity
                    Toast.makeText(this, getText(R.string.error_increase_quantity),
                            Toast.LENGTH_LONG).show();
                }
            } else {
                // Quantity can not be less than 0
                Toast.makeText(this, getText(R.string.quantity_less_than_zero), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String currentName = cursor.getString(nameColumnIndex);
            String currentPrice = cursor.getString(priceColumnIndex);
            final int currentQuantity = cursor.getInt(quantityColumnIndex);
            String currentSupplierName = cursor.getString(supplierNameColumnIndex);
            final String currentSupplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(currentName);
            mPriceEditText.setText(currentPrice);
            mQuantityEditText.setText(Integer.toString(currentQuantity));
            mSupplierNameEditText.setText(currentSupplierName);
            mSupplierPhoneNumberEditText.setText(currentSupplierPhoneNumber);

            //Increase button onclick listener
            increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String quantityString = mQuantityEditText.getText().toString();
                    String stringValue = quantityString.matches("") ? "0" : quantityString;
                    int quantity = Integer.parseInt(stringValue);
                    updateQuantity(quantity, false);
                }
            });

            //Increase button onclick listener
            decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String quantityString = mQuantityEditText.getText().toString();
                    if (quantityString.matches("")) {
                        mQuantityEditText.setText("0");
                    }
                    String stringValue = quantityString.matches("") ? "0" : quantityString;
                    int quantity = Integer.parseInt(stringValue);
                    updateQuantity(quantity, true);
                }
            });

            //Delete button onclick listener
            deleteBookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteBook();
                }
            });

            //Order from supplier button onclick listener
            orderFromSupplierButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", currentSupplierPhoneNumber, null));
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneNumberEditText.setText("");
    }

    /**
     * Delete action for an existing book.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Get number of deleted rows
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // check if the book was deleted or not
            if (rowsDeleted == 0) {
                // If book was not deleted display a fail message
                Toast.makeText(this, getString(R.string.delete_book_failed),
                        Toast.LENGTH_LONG).show();
            } else {
                // Display a message that the book was deleted successfully
                Toast.makeText(this, getString(R.string.delete_book_successfully),
                        Toast.LENGTH_LONG).show();
            }
        }

        finish();
    }

    @Override
    public void onBackPressed() {

        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Close activity
                        finish();
                    }
                };
    }

}
