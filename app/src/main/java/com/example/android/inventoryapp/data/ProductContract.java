package com.example.android.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Created by bplewis5 on 7/26/16.
 */
public final class ProductContract {

    public ProductContract() {}

    public static abstract class ProductEntry implements BaseColumns{
        public static final String TABLE_NAME = "product_entry";
        public static final String COLUMN_NAME = "product_entry";
        public static final String COLUMN_SUPPLIER = "product_entry";
        public static final String COLUMN_QUANTITY = "product_entry";
        public static final String COLUMN_PRICE = "product_entry";

    }

}
