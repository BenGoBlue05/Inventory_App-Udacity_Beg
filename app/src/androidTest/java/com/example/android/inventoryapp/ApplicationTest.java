package com.example.android.inventoryapp;

import android.app.Application;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;

import com.example.android.inventoryapp.data.ProductContract;
import com.example.android.inventoryapp.data.ProductDbHelper;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    private final String LOG_TAG = ApplicationTest.class.getSimpleName();
//    public ContentValues cv0 = createContentValues("basketball", "spalding", 13, 23.50);
//    public ContentValues cv1 = createContentValues("baseball", "Wilson", 34, 5.60);
//    public ContentValues cv2 = createContentValues("soccer ball", "Nike", 45, 22.62);
//    private ProductDbHelper mOpenHelper = new ProductDbHelper(getContext());
    public ApplicationTest() {
        super(Application.class);
    }


    public void testAdd(){
        ProductDbHelper helper = new ProductDbHelper(mContext);
        SQLiteDatabase db = helper.getWritableDatabase();
        long id = db.insert(ProductContract.ProductEntry.TABLE_NAME, null,
                createContentValues("basketball", "Spalding", 23, 23.19));
        assertTrue(id != -1);
    }

    public ContentValues createContentValues(String name, String supplier, int quantity, double price) {
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_NAME, name);
        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER, supplier);
        values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, quantity);
        values.put(ProductContract.ProductEntry.COLUMN_PRICE, price);
        return values;
    }



}