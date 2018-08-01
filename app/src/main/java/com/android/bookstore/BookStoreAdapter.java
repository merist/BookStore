package com.android.bookstore;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.android.bookstore.data.BookStoreContract.BookStoreEntry;

/**
 * Created by user on 7/20/2018.
 */

public class BookStoreAdapter extends CursorAdapter {

    private final MainActivity activity;
    /**
     * Constructs a new {@link BookStoreAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */

    public BookStoreAdapter(MainActivity context, Cursor c) {
        super(context, c, 0);
        this.activity = context;
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);

        // Find the columns of book attributes that we're interested in
        final int idColumnIndex = cursor.getColumnIndex(BookStoreEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_QUANTITY);

        // Read the book attributes from the Cursor for the current book
        String bookName = cursor.getString(nameColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);

        // Update the TextViews with the attributes for the current book
        nameTextView.setText(bookName);
        priceTextView.setText(price);
        quantityTextView.setText(quantity);

        // BookId and quantity needed on click of sell button to decrease the quantity of the current book
        final int bookID = cursor.getInt(idColumnIndex);
        final int bookQuantity = cursor.getInt(quantityColumnIndex);

        // Sell button
        Button sellButton = view.findViewById(R.id.sell_button);
        // Onclick of sell button the quantity will decrease
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.sellProduct(bookID, bookQuantity);
            }
        });
    }
}