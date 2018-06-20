package com.android.bookstore.data;

import android.provider.BaseColumns;

/**
 * Created by user on 6/17/2018.
 */

public final class BookStoreContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it a private constructor.
    private BookStoreContract() {}

    /**
     * Inner class that defines constant values for the book_store database table.
     * Each entry in the table represents a single pet.
     */
    public static final class BookStoreEntry implements BaseColumns {

        // Name of database table for the books
        public final static String TABLE_NAME = "book";

        /**
         * Unique ID number for the book (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME ="name";

        /**
         * The price of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRICE = "price";

        /**
         * The quantity of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_QUANTITY = "quantity";

        /**
         * The name of the supplier.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";

        /**
         * The phone number of the supplier.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_SUPPLIER_PHONE = "supplier_phone";

    }
}
