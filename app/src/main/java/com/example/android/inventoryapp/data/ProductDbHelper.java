package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;
/**
 * Created by bplewis5 on 7/26/16.
 */
public class ProductDbHelper extends SQLiteOpenHelper{


    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "product.db";

    private static final String TEXT_TYPE = " TEXT NOT NULL";
    private static final String COMMA_SEP = ", ";
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" +
                    ProductEntry._ID + " INTEGER PRIMARY KEY, " +
                    ProductEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    ProductEntry.COLUMN_SUPPLIER + TEXT_TYPE + COMMA_SEP +
                    ProductEntry.COLUMN_QUANTITY + " INTEGER NOT NULL" + COMMA_SEP +
                    ProductEntry.COLUMN_PRICE + " REAL NOT NULL" + COMMA_SEP +
                    ProductEntry.COLUMN_IMAGE_URI + " BLOB NOT NULL);";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE);
        onCreate(sqLiteDatabase);
    }
}
